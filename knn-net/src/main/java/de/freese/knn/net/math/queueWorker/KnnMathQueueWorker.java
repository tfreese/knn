/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math.queueWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit QueueWorkers.
 *
 * @author Thomas Freese
 */
public final class KnnMathQueueWorker extends AbstractKnnMath
{
    /**
     * @author Thomas Freese
     */
    private static final class QueueWorker extends Thread
    {
        /**
        *
        */
        private static final Logger LOGGER = LoggerFactory.getLogger(QueueWorker.class);

        /**
         *
         */
        private final BlockingQueue<Runnable> queue;

        /**
         *
         */
        private boolean stopped;

        /**
         * Erzeugt eine neue Instanz von {@link QueueWorker}.
         *
         * @param queue {@link BlockingQueue}
         */
        private QueueWorker(final BlockingQueue<Runnable> queue)
        {
            super();

            this.queue = Objects.requireNonNull(queue, "queue required");
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            while (!Thread.interrupted())
            {
                if (this.stopped)
                {
                    break;
                }

                try
                {
                    Runnable runnable = this.queue.take();

                    runnable.run();
                }
                catch (InterruptedException iex)
                {
                    // Ignore
                }
                catch (Exception ex)
                {
                    LOGGER.error(null, ex);
                }
            }

            LOGGER.debug("{}: terminated", getName());
        }

        /**
         *
         */
        void stopWorker()
        {
            this.stopped = true;

            interrupt();
        }
    }

    /**
     *
     */
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    /**
     *
     */
    private final List<QueueWorker> workers = new ArrayList<>();

    /**
     * Erstellt ein neues {@link KnnMathQueueWorker} Object.
     *
     * @param parallelism int
     */
    public KnnMathQueueWorker(final int parallelism)
    {
        super(parallelism);

        for (int i = 1; i <= (parallelism); i++)
        {
            QueueWorker worker = new QueueWorker(this.queue);
            worker.setName(worker.getClass().getSimpleName() + "-" + i);
            worker.setDaemon(false);

            this.workers.add(worker);
            worker.start();
        }
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
        List<RunnableFuture<Void>> futures = new ArrayList<>(partitions.size());

        for (NeuronList partition : partitions)
        {
            RunnableFuture<Void> future = new FutureTask<>(() -> partition.forEach(neuron -> backward(neuron, errors, layerErrors)), null);

            futures.add(future);
            getQueue().add(future);
        }

        waitForFutures(futures);

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#close()
     */
    @Override
    public void close()
    {
        this.workers.forEach(QueueWorker::stopWorker);

        this.workers.clear();
        getQueue().clear();
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
        List<RunnableFuture<Void>> futures = new ArrayList<>(partitions.size());

        for (NeuronList partition : partitions)
        {
            RunnableFuture<Void> future = new FutureTask<>(() -> partition.forEach(neuron -> forward(neuron, inputs, outputs)), null);

            futures.add(future);
            getQueue().add(future);
        }

        waitForFutures(futures);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @return {@link BlockingQueue}<Runnable>
     */
    private BlockingQueue<Runnable> getQueue()
    {
        return this.queue;
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        List<RunnableFuture<Void>> futures = new ArrayList<>(layers.length);

        for (Layer layer : layers)
        {
            RunnableFuture<Void> future = new FutureTask<>(() -> initialize(layer, valueInitializer), null);

            futures.add(future);
            getQueue().add(future);
        }

        waitForFutures(futures);
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
        List<RunnableFuture<Void>> futures = new ArrayList<>(partitions.size());

        for (NeuronList partition : partitions)
        {
            RunnableFuture<Void> future = new FutureTask<>(
                    () -> partition.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)), null);

            futures.add(future);
            getQueue().add(future);
        }

        waitForFutures(futures);
    }

    /**
     * Warten bis der Task fertig ist.
     *
     * @param future {@link Future}
     */
    private void waitForFuture(final Future<?> future)
    {
        try
        {
            future.get();
        }
        catch (InterruptedException | ExecutionException ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * Warten bis alle Tasks fertig sind.
     *
     * @param futures {@link List}
     */
    private void waitForFutures(final List<? extends Future<Void>> futures)
    {
        for (Future<Void> future : futures)
        {
            waitForFuture(future);
        }
    }
}
