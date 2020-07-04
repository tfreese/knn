/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.utils.KnnUtils;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem {@link CompletionService}.
 *
 * @author Thomas Freese
 */
public class KnnMathQueueWorker extends AbstractKnnMath implements AutoCloseable
{
    /**
     * @author Thomas Freese
     */
    private class QueueWorker extends Thread
    {
        /**
         * Erzeugt eine neue Instanz von {@link QueueWorker}.
         */
        private QueueWorker()
        {
            super();
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            while (!Thread.interrupted())
            {
                try
                {
                    Runnable runnable = KnnMathQueueWorker.this.queue.take();

                    runnable.run();
                }
                catch (InterruptedException iex)
                {
                    break;
                }
                catch (Exception ex)
                {
                    getLogger().error(null, ex);
                }
            }

            getLogger().debug("{}: terminated", getName());
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
     */
    public KnnMathQueueWorker()
    {
        super();

        for (int i = 1; i <= (KnnUtils.DEFAULT_POOL_SIZE); i++)
        {
            QueueWorker worker = new QueueWorker();
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

        List<NeuronList> partitions = getPartitions(layer.getNeurons());
        List<RunnableFuture<Void>> futures = new ArrayList<>(partitions.size());

        for (NeuronList partition : partitions)
        {
            RunnableFuture<Void> runnableFuture = new FutureTask<>(() -> partition.forEach(neuron -> backward(neuron, errors, layerErrors)), null);

            futures.add(runnableFuture);
            this.queue.add(runnableFuture);
        }

        waitForFutures(futures);

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception
    {
        this.workers.forEach(QueueWorker::interrupt);

        this.workers.clear();
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#forward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor)
    {
        double[] inputs = visitor.getLastOutputs();
        double[] outputs = new double[layer.getSize()];

        List<NeuronList> partitions = getPartitions(layer.getNeurons());
        List<RunnableFuture<Void>> futures = new ArrayList<>(partitions.size());

        for (NeuronList partition : partitions)
        {
            RunnableFuture<Void> runnableFuture = new FutureTask<>(() -> partition.forEach(neuron -> forward(neuron, inputs, outputs)), null);

            futures.add(runnableFuture);
            this.queue.add(runnableFuture);
        }

        waitForFutures(futures);

        visitor.setOutputs(layer, outputs);
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
            RunnableFuture<Void> runnableFuture = new FutureTask<>(() -> initialize(layer, valueInitializer), null);

            futures.add(runnableFuture);
            this.queue.add(runnableFuture);
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

        List<NeuronList> partitions = getPartitions(leftLayer.getNeurons());
        List<RunnableFuture<Void>> futures = new ArrayList<>(partitions.size());

        for (NeuronList partition : partitions)
        {
            RunnableFuture<Void> runnableFuture = new FutureTask<>(
                    () -> partition.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)), null);

            futures.add(runnableFuture);
            this.queue.add(runnableFuture);
        }

        waitForFutures(futures);
    }

    /**
     * Warten bis alle Tasks fertig sind.
     *
     * @param futures {@link List}
     */
    private void waitForFutures(final List<RunnableFuture<Void>> futures)
    {
        for (RunnableFuture<Void> runnableFuture : futures)
        {
            try
            {
                runnableFuture.get();
            }
            catch (InterruptedException | ExecutionException ex)
            {
                getLogger().error(null, ex);
            }
        }
    }
}
