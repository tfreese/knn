// Created: 05.11.2020
package de.freese.knn.net.math.disruptor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.utils.NamedThreadFactory;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Jeder Handler verarbeitet ein einzelnes Neuron.
 *
 * @author Thomas Freese
 */
public class KnnMathDisruptorPerNeuron extends AbstractKnnMath {
    /**
     * @author Thomas Freese
     */
    private static final class RunnableEvent {
        private Runnable runnable;
    }

    /**
     * @author Thomas Freese
     */
    private static final class RunnableHandler implements EventHandler<RunnableEvent> {
        private final int ordinal;
        private final int parallelism;

        RunnableHandler() {
            this(-1, -1);
        }

        RunnableHandler(final int parallelism, final int ordinal) {
            super();

            this.parallelism = parallelism;
            this.ordinal = ordinal;
        }

        @Override
        public void onEvent(final RunnableEvent event, final long sequence, final boolean endOfBatch) throws Exception {
            // Load-Balancing auf die Handler über die Sequence.
            // Sonst würden alle Handler gleichzeitig eine Sequence bearbeiten.
            if (this.ordinal == -1 || this.ordinal == (sequence % this.parallelism)) {
                event.runnable.run();

                event.runnable = null;
            }
        }
    }

    private final Disruptor<RunnableEvent> disruptor;

    /**
     * @param parallelism int; must be a power of 2
     */
    public KnnMathDisruptorPerNeuron(final int parallelism) {
        super(parallelism);

        // bufferSize must be a power of 2
        // 32 << 4 = 512<br>
        // 24 << 4 = 256<br>
        // 16 << 4 = 256<br>
        // 8 << 4 = 128<br>
        // 4 << 4 = 64<br>
        // 2 << 4 = 32<br>
        final int ringBufferSize = Integer.highestOneBit(parallelism) << 4;

        if (Integer.bitCount(ringBufferSize) != 1) {
            throw new IllegalArgumentException("bufferSize must be a power of 2");
        }

        this.disruptor = new Disruptor<>(RunnableEvent::new, ringBufferSize, new NamedThreadFactory("knn-disruptor-%d"));

        final EventHandler<RunnableEvent>[] handlers = new RunnableHandler[parallelism];

        for (int i = 0; i < handlers.length; i++) {
            handlers[i] = new RunnableHandler(parallelism, i);
        }

        this.disruptor.handleEventsWith(handlers);

        this.disruptor.start();
    }

    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        final CountDownLatch latch = new CountDownLatch(layer.getSize());
        final RingBuffer<RunnableEvent> ringBuffer = getDisruptor().getRingBuffer();

        layer.getNeurons().forEach(neuron -> {
            final long sequence = ringBuffer.next();

            try {
                final RunnableEvent event = ringBuffer.get(sequence);

                event.runnable = () -> {
                    backward(neuron, errors, layerErrors);
                    latch.countDown();
                };
            }
            finally {
                ringBuffer.publish(sequence);
            }
        });

        waitForLatch(latch);

        visitor.setErrors(layer, layerErrors);
    }

    @Override
    public void close() {
        // Nur notwendig, wenn die Event-Publizierung noch nicht abgeschlossen ist.
        // this.disruptor.halt();

        try {
            this.disruptor.shutdown(5, TimeUnit.SECONDS);
        }
        catch (Exception ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor) {
        final double[] inputs = visitor.getLastOutputs();
        final double[] outputs = new double[layer.getSize()];

        final CountDownLatch latch = new CountDownLatch(layer.getSize());
        final RingBuffer<RunnableEvent> ringBuffer = getDisruptor().getRingBuffer();

        layer.getNeurons().forEach(neuron -> {
            final long sequence = ringBuffer.next();

            try {
                final RunnableEvent event = ringBuffer.get(sequence);

                event.runnable = () -> {
                    forward(neuron, inputs, outputs);
                    latch.countDown();
                };
            }
            finally {
                ringBuffer.publish(sequence);
            }
        });

        waitForLatch(latch);

        visitor.setOutputs(layer, outputs);
    }

    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers) {
        for (Layer layer : layers) {
            initialize(layer, valueInitializer);
        }
    }

    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum, final BackwardVisitor visitor) {
        final double[] leftOutputs = visitor.getOutputs(leftLayer);
        final double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        final double[] rightErrors = visitor.getErrors(rightLayer);

        final CountDownLatch latch = new CountDownLatch(leftLayer.getSize());
        final RingBuffer<RunnableEvent> ringBuffer = getDisruptor().getRingBuffer();

        leftLayer.getNeurons().forEach(neuron -> {
            final long sequence = ringBuffer.next();

            try {
                final RunnableEvent event = ringBuffer.get(sequence);

                event.runnable = () -> {
                    refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);
                    latch.countDown();
                };
            }
            finally {
                ringBuffer.publish(sequence);
            }
        });

        waitForLatch(latch);
    }

    private Disruptor<RunnableEvent> getDisruptor() {
        return this.disruptor;
    }

    /**
     * Blockiert den aktuellen Thread, bis der Latch auf 0 ist.
     */
    private void waitForLatch(final CountDownLatch latch) {
        try {
            latch.await();
        }
        catch (RuntimeException rex) {
            throw rex;
        }
        catch (InterruptedException ex) {
            getLogger().error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }
}
