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
public class KnnMathDisruptorPartitionPerHandler extends AbstractKnnMath
{
    // /**
    // * @author Thomas Freese
    // */
    // private static class JoiningHandler implements EventHandler<MathEvent>
    // {
    // /**
    // *
    // */
    // private CountDownLatch latch;
    //
    // /**
    // * @see com.lmax.disruptor.EventHandler#onEvent(java.lang.Object, long, boolean)
    // */
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
    private static class MathEvent
    {
        /**
         *
         */
        final Runnable[] runnables;

        /**
         * Erstellt ein neues {@link MathEvent} Object.
         *
         * @param parallelism int
         */
        MathEvent(final int parallelism)
        {
            super();

            this.runnables = new Runnable[parallelism];
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class MathEventHandler implements EventHandler<MathEvent>
    {
        /**
         *
         */
        private final int ordinal;

        /**
         * Erstellt ein neues {@link MathEventHandler} Object.
         *
         * @param ordinal int
         */
        MathEventHandler(final int ordinal)
        {
            super();

            this.ordinal = ordinal;
        }

        /**
         * @see com.lmax.disruptor.EventHandler#onEvent(java.lang.Object, long, boolean)
         */
        @Override
        public void onEvent(final MathEvent event, final long sequence, final boolean endOfBatch) throws Exception
        {
            event.runnables[this.ordinal].run();

            event.runnables[this.ordinal] = null;
        }
    }

    /**
     *
     */
    private final Disruptor<MathEvent> disruptor;
    // /**
    // *
    // */
    // private final JoiningHandler joiningHandler;

    /**
     * Erstellt ein neues {@link KnnMathDisruptorPartitionPerHandler} Object.
     *
     * @param parallelism int; must be a power of 2
     */
    public KnnMathDisruptorPartitionPerHandler(final int parallelism)
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

        this.disruptor = new Disruptor<>(() -> new MathEvent(parallelism), ringBufferSize, new KnnThreadFactory("knn-disruptor-"));

        MathEventHandler[] handlers = new MathEventHandler[parallelism];

        for (int i = 0; i < handlers.length; i++)
        {
            handlers[i] = new MathEventHandler(i);
        }

        // this.joiningHandler = new JoiningHandler();

        this.disruptor.handleEventsWith(handlers);// .then(this.joiningHandler);
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

        List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        CountDownLatch latch = new CountDownLatch(partitions.size());

        publish(ordinal -> {
            NeuronList partition = partitions.get(ordinal);

            return () -> {
                partition.forEach(neuron -> backward(neuron, errors, layerErrors));
                latch.countDown();
            };
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

        List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        CountDownLatch latch = new CountDownLatch(partitions.size());

        publish(ordinal -> {
            NeuronList partition = partitions.get(ordinal);

            return () -> {
                partition.forEach(neuron -> forward(neuron, inputs, outputs));

                latch.countDown();
            };
        });

        waitForLatch(latch);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @return {@link Disruptor}<MathEvent>
     */
    private Disruptor<MathEvent> getDisruptor()
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
     * @param functionRunnables {@link IntFunction}
     */
    private void publish(final IntFunction<Runnable> functionRunnables)
    {
        RingBuffer<MathEvent> ringBuffer = getDisruptor().getRingBuffer();

        long sequence = ringBuffer.next();

        try
        {
            MathEvent event = ringBuffer.get(sequence);

            for (int i = 0; i < getParallelism(); i++)
            {
                event.runnables[i] = functionRunnables.apply(i);
            }
        }
        finally
        {
            ringBuffer.publish(sequence);
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

        List<NeuronList> partitions = getPartitions(leftLayer.getNeurons(), getParallelism());
        CountDownLatch latch = new CountDownLatch(partitions.size());

        publish(ordinal -> {
            NeuronList partition = partitions.get(ordinal);

            return () -> {
                partition.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));

                latch.countDown();
            };
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
