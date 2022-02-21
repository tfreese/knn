// Created: 05.11.2020
package de.freese.knn.net.math.disruptor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;

import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.utils.KnnThreadFactory;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Jeder Handler verarbeitet ein einzelnes Neuron.
 *
 * @author Thomas Freese
 */
public class KnnMathDisruptorPerNeuron extends AbstractKnnMath
{
    /**
     * @author Thomas Freese
     */
    private static class RunnableEvent
    {
        /**
        *
        */
        Runnable runnable;
    }
    /**
     * @author Thomas Freese
     */
    private static class RunnableHandler implements EventHandler<RunnableEvent>, WorkHandler<RunnableEvent>
    {
        /**
         *
         */
        private final int ordinal;
        /**
         *
         */
        private final int parallelism;

        /**
         * Erstellt ein neues {@link RunnableHandler} Object.
         *
         */
        RunnableHandler()
        {
            this(-1,-1);
        }

        /**
         * Erstellt ein neues {@link RunnableHandler} Object.
         *
         * @param parallelism int
         * @param ordinal int
         */
        RunnableHandler(final int parallelism, final int ordinal)
        {
            super();

            this.parallelism = parallelism;
            this.ordinal = ordinal;
        }

        /**
         * @see com.lmax.disruptor.WorkHandler#onEvent(java.lang.Object)
         */
        @Override
        public void onEvent(final RunnableEvent event) throws Exception
        {
            event.runnable.run();

            event.runnable = null;
        }

        /**
         * @see com.lmax.disruptor.EventHandler#onEvent(java.lang.Object, long, boolean)
         */
        @Override
        public void onEvent(final RunnableEvent event, final long sequence, final boolean endOfBatch) throws Exception
        {
            // Load-Balancing auf die Handler über die Sequence.
            // Sonst würden alle Handler gleichzeitig eine Sequence bearbeiten.
            if ((this.ordinal == -1) || (this.ordinal == (sequence % this.parallelism)))
            {
                onEvent(event);
            }
        }
    }

    /**
     *
     */
    private final Disruptor<RunnableEvent> disruptor;

    /**
     * Erstellt ein neues {@link KnnMathDisruptorPerNeuron} Object.
     *
     * @param parallelism int; must be a power of 2
     */
    public KnnMathDisruptorPerNeuron(final int parallelism)
    {
        super(parallelism);

        // bufferSize must be a power of 2
        // 32 << 4 = 512<br>
        // 24 << 4 = 256<br>
        // 16 << 4 = 256<br>
        // 8 << 4 = 128<br>
        // 4 << 4 = 64<br>
        // 2 << 4 = 32<br>
        int ringBufferSize = Integer.highestOneBit(parallelism) << 4;

        if (Integer.bitCount(ringBufferSize) != 1)
        {
            throw new IllegalArgumentException("bufferSize must be a power of 2");
        }

        this.disruptor = new Disruptor<>(RunnableEvent::new, ringBufferSize, new KnnThreadFactory("knn-disruptor-"));

//        EventHandler<RunnableEvent>[] handlers = new RunnableHandler[parallelism];
//
//        for (int i = 0; i < handlers.length; i++)
//        {
//            handlers[i] = new RunnableHandler(parallelism, i);
//        }
//
//        this.disruptor.handleEventsWith(handlers);

        WorkHandler<RunnableEvent>[] workers = new RunnableHandler[parallelism];

        for (int i = 0; i < workers.length; i++)
        {
            workers[i] = new RunnableHandler();
        }

        this.disruptor.handleEventsWithWorkerPool(workers);

        this.disruptor.start();
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#backward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor)
    {
        double[] errors = visitor.getLastErrors();
        double[] layerErrors = new double[layer.getSize()];

        CountDownLatch latch = new CountDownLatch(layer.getSize());
        RingBuffer<RunnableEvent> ringBuffer = getDisruptor().getRingBuffer();

        layer.getNeurons().forEach(neuron -> {
            long sequence = ringBuffer.next();

            try
            {
                RunnableEvent event = ringBuffer.get(sequence);

                event.runnable = () -> {
                    backward(neuron, errors, layerErrors);
                    latch.countDown();
                };
            }
            finally
            {
                ringBuffer.publish(sequence);
            }
        });

        waitForLatch(latch);

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#close()
     */
    @Override
    public void close()
    {
        // Nur notwending, wenn die Event-Publizierung noch nicht abgeschlossen ist.
        // this.disruptor.halt();

        try
        {
            this.disruptor.shutdown(5, TimeUnit.SECONDS);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#forward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor)
    {
        double[] inputs = visitor.getLastOutputs();
        double[] outputs = new double[layer.getSize()];

        CountDownLatch latch = new CountDownLatch(layer.getSize());
        RingBuffer<RunnableEvent> ringBuffer = getDisruptor().getRingBuffer();

        layer.getNeurons().forEach(neuron -> {
            long sequence = ringBuffer.next();

            try
            {
                RunnableEvent event = ringBuffer.get(sequence);

                event.runnable = () -> {
                    forward(neuron, inputs, outputs);
                    latch.countDown();
                };
            }
            finally
            {
                ringBuffer.publish(sequence);
            }
        });

        waitForLatch(latch);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @return {@link Disruptor}
     */
    private Disruptor<RunnableEvent> getDisruptor()
    {
        return this.disruptor;
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        for (Layer layer : layers)
        {
            initialize(layer, valueInitializer);
        }
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#refreshLayerWeights(de.freese.knn.net.layer.Layer, de.freese.knn.net.layer.Layer, double, double,
     *      de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum,
                                    final BackwardVisitor visitor)
    {
        double[] leftOutputs = visitor.getOutputs(leftLayer);
        double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        double[] rightErrors = visitor.getErrors(rightLayer);

        CountDownLatch latch = new CountDownLatch(leftLayer.getSize());
        RingBuffer<RunnableEvent> ringBuffer = getDisruptor().getRingBuffer();

        leftLayer.getNeurons().forEach(neuron -> {
            long sequence = ringBuffer.next();

            try
            {
                RunnableEvent event = ringBuffer.get(sequence);

                event.runnable = () -> {
                    refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);
                    latch.countDown();
                };
            }
            finally
            {
                ringBuffer.publish(sequence);
            }
        });

        waitForLatch(latch);
    }

    /**
     * Blockiert den aktuellen Thread, bis der Latch auf 0 ist.
     *
     * @param latch {@link CountDownLatch}
     */
    private void waitForLatch(final CountDownLatch latch)
    {
        try
        {
            latch.await();
        }
        catch (RuntimeException rex)
        {
            throw rex;
        }
        catch (Throwable th)
        {
            throw new RuntimeException(th);
        }
    }
}
