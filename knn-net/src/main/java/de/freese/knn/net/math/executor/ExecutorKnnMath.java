/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.IValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem {@link ExecutorService}-Framework.
 *
 * @author Thomas Freese
 */
public class ExecutorKnnMath extends AbstractKnnMath implements AutoCloseable
{
    /**
     *
     */
    private boolean createdExecutor = false;

    /**
     *
     */
    private final Executor executor;

    /**
     *
     */
    private final int processors;

    /**
     * Erstellt ein neues {@link ExecutorKnnMath} Object.
     */
    public ExecutorKnnMath()
    {
        this(Executors.newCachedThreadPool(new KnnThreadQueueThreadFactory()));

        this.createdExecutor = true;
    }

    /**
     * Erstellt ein neues {@link ExecutorKnnMath} Object.
     *
     * @param executor {@link Executor}
     */
    public ExecutorKnnMath(final Executor executor)
    {
        super();

        this.executor = Objects.requireNonNull(executor, "executor required");
        this.processors = Runtime.getRuntime().availableProcessors();
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#backward(de.freese.knn.net.layer.ILayer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void backward(final ILayer layer, final BackwardVisitor visitor)
    {
        double[] errors = visitor.getLastErrors();
        double[] layerErrors = new double[layer.getSize()];

        List<NeuronList> partitions = getPartitions(layer.getNeurons());
        CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions)
        {
            BackwardTask task = new BackwardTask(latch, partition, errors, layerErrors);
            this.executor.execute(task);
        }

        doLatchAwait(latch);

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception
    {
        if (this.createdExecutor && (this.executor instanceof ExecutorService))
        {
            getLogger().info("Shutdown ExecutorService");

            ExecutorService executorService = (ExecutorService) this.executor;
            executorService.shutdown();

            try
            {
                // Wait a while for existing tasks to terminate.
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS))
                {
                    executorService.shutdownNow(); // Cancel currently executing tasks

                    // Wait a while for tasks to respond to being cancelled
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS))
                    {
                        System.err.println("Pool did not terminate");
                    }
                }
            }
            catch (InterruptedException iex)
            {
                // (Re-)Cancel if current thread also interrupted
                executorService.shutdownNow();

                // Preserve interrupt status
                // Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Blockiert den aktuellen Thread, bis der Latch auf 0 ist.
     *
     * @param latch {@link CountDownLatch}
     */
    private void doLatchAwait(final CountDownLatch latch)
    {
        try
        {
            latch.await();
        }
        catch (Throwable th)
        {
            if (th instanceof RuntimeException)
            {
                throw (RuntimeException) th;
            }

            throw new RuntimeException(th);
        }
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#forward(de.freese.knn.net.layer.ILayer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final ILayer layer, final ForwardVisitor visitor)
    {
        double[] inputs = visitor.getLastOutputs();
        double[] outputs = new double[layer.getSize()];

        List<NeuronList> partitions = getPartitions(layer.getNeurons());
        CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions)
        {
            ForwardTask task = new ForwardTask(latch, partition, inputs, outputs);
            this.executor.execute(task);
        }

        doLatchAwait(latch);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * Aufsplitten der Neuronen für parallele Verarbeitung.
     *
     * @param neurons {@link NeuronList}
     * @return {@link List}<NeuronList>
     */
    private List<NeuronList> getPartitions(final NeuronList neurons)
    {
        List<NeuronList> partitions = new ArrayList<>();

        if ((this.processors == 1) || (neurons.size() <= this.processors))
        {
            partitions.add(neurons);

            return partitions;
        }

        int size = neurons.size() / this.processors;
        int fromIndex = 0;

        for (int p = 0; p < (this.processors - 1); p++)
        {
            int toIndex = fromIndex + size;

            NeuronList part = neurons.subList(fromIndex, toIndex);
            partitions.add(part);

            fromIndex = toIndex;
        }

        NeuronList part = neurons.subList(fromIndex, neurons.size());
        partitions.add(part);

        return partitions;
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#initialize(de.freese.knn.net.matrix.IValueInitializer, de.freese.knn.net.layer.ILayer[])
     */
    @Override
    public void initialize(final IValueInitializer valueInitializer, final ILayer[] layers)
    {
        CountDownLatch latch = new CountDownLatch(layers.length);

        for (ILayer layer : layers)
        {
            InitializeTask task = new InitializeTask(latch, valueInitializer, layer);
            this.executor.execute(task);
        }

        doLatchAwait(latch);
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#refreshLayerWeights(de.freese.knn.net.layer.ILayer, de.freese.knn.net.layer.ILayer, double, double,
     *      de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void refreshLayerWeights(final ILayer leftLayer, final ILayer rightLayer, final double teachFactor, final double momentum,
                                    final BackwardVisitor visitor)
    {
        double[] leftOutputs = visitor.getOutputs(leftLayer);
        double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        double[] rightErrors = visitor.getErrors(rightLayer);

        List<NeuronList> partitions = getPartitions(leftLayer.getNeurons());
        CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions)
        {
            RefreshWeightsTask task = new RefreshWeightsTask(latch, partition, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);
            this.executor.execute(task);
        }

        doLatchAwait(latch);
    }
}
