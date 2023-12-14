// Created: 05.11.2020
package de.freese.knn.net.math.disruptor;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.utils.KnnThreadFactory;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Jeder Handler verarbeitet eine SubList der Neuronen.
 *
 * @author Thomas Freese
 */
public class KnnMathDisruptorPerPartition extends AbstractKnnMath {
    // /**
    // * @author Thomas Freese
    // */
    // private static final class JoiningHandler implements EventHandler<MathEvent>
    // {
    // private CountDownLatch latch;
    //
    // @Override
    // public void onEvent(final MathEvent event, final long sequence, final boolean endOfBatch) throws Exception
    // {
    // LoggerFactory.getLogger(JoiningHandler.class).info("onEvent");
    //
    // this.latch.countDown();
    // }
    // }

    /**
     * @author Thomas Freese
     */
    private static final class MathEvent {
        private final Runnable[] runnables;

        MathEvent(final int parallelism) {
            super();

            this.runnables = new Runnable[parallelism];
        }
    }

    /**
     * @author Thomas Freese
     */
    private static final class MathHandler implements EventHandler<MathEvent> {
        private final int ordinal;

        MathHandler(final int ordinal) {
            super();

            this.ordinal = ordinal;
        }

        @Override
        public void onEvent(final MathEvent event, final long sequence, final boolean endOfBatch) throws Exception {
            event.runnables[this.ordinal].run();

            event.runnables[this.ordinal] = null;
        }
    }

    private final Disruptor<MathEvent> disruptor;

    // private final JoiningHandler joiningHandler;

    /**
     * @param parallelism int; must be a power of 2
     */
    public KnnMathDisruptorPerPartition(final int parallelism) {
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

        this.disruptor = new Disruptor<>(() -> new MathEvent(parallelism), ringBufferSize, new KnnThreadFactory("knn-disruptor-"));

        final MathHandler[] handlers = new MathHandler[parallelism];

        for (int i = 0; i < handlers.length; i++) {
            handlers[i] = new MathHandler(i);
        }

        // this.joiningHandler = new JoiningHandler();

        this.disruptor.handleEventsWith(handlers);// .then(this.joiningHandler);
        this.disruptor.start();
    }

    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        final List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        final CountDownLatch latch = new CountDownLatch(partitions.size());

        publish(ordinal -> {
            final NeuronList partition = partitions.get(ordinal);

            return () -> {
                partition.forEach(neuron -> backward(neuron, errors, layerErrors));
                latch.countDown();
            };
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

        final List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        final CountDownLatch latch = new CountDownLatch(partitions.size());

        publish(ordinal -> {
            final NeuronList partition = partitions.get(ordinal);

            return () -> {
                partition.forEach(neuron -> forward(neuron, inputs, outputs));

                latch.countDown();
            };
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

        final List<NeuronList> partitions = getPartitions(leftLayer.getNeurons(), getParallelism());
        final CountDownLatch latch = new CountDownLatch(partitions.size());

        publish(ordinal -> {
            final NeuronList partition = partitions.get(ordinal);

            return () -> {
                partition.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));

                latch.countDown();
            };
        });

        waitForLatch(latch);
    }

    private Disruptor<MathEvent> getDisruptor() {
        return this.disruptor;
    }

    private void publish(final IntFunction<Runnable> functionRunnables) {
        final RingBuffer<MathEvent> ringBuffer = getDisruptor().getRingBuffer();

        final long sequence = ringBuffer.next();

        try {
            final MathEvent event = ringBuffer.get(sequence);

            for (int i = 0; i < getParallelism(); i++) {
                event.runnables[i] = functionRunnables.apply(i);
            }
        }
        finally {
            ringBuffer.publish(sequence);
        }
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
        catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }
}
