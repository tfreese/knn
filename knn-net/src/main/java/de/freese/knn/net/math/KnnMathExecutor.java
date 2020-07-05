/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.utils.KnnThreadQueueThreadFactory;
import de.freese.knn.net.utils.KnnUtils;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem {@link ExecutorService}-Framework.
 *
 * @author Thomas Freese
 */
public class KnnMathExecutor extends AbstractKnnMath implements AutoCloseable
{
    /**
     *
     */
    private boolean createdExecutor = false;

    /**
     *
     */
    private final ExecutorService executorService;

    /**
     * Erstellt ein neues {@link KnnMathExecutor} Object.
     */
    public KnnMathExecutor()
    {
        super();

        // this.executorService = Executors.newCachedThreadPool(new KnnThreadQueueThreadFactory());
        // this.executorService = Executors.newWorkStealingPool(KnnUtils.DEFAULT_POOL_SIZE);
        this.executorService = Executors.newFixedThreadPool(getPoolSize(), new KnnThreadQueueThreadFactory());

        this.createdExecutor = true;
    }

    /**
     * Erstellt ein neues {@link KnnMathExecutor} Object.
     *
     * @param executorService {@link ExecutorService}
     */
    public KnnMathExecutor(final ExecutorService executorService)
    {
        super();

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
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
        CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions)
        {
            // BackwardTask task = new BackwardTask(latch, partition, errors, layerErrors);
            // this.executorService.execute(task);

            this.executorService.execute(() -> {
                partition.forEach(neuron -> backward(neuron, errors, layerErrors));

                latch.countDown();
            });
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
        if (this.createdExecutor)
        {
            KnnUtils.shutdown(this.executorService, getLogger());
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
        catch (RuntimeException rex)
        {
            throw rex;
        }
        catch (Throwable th)
        {
            throw new RuntimeException(th);
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

        List<NeuronList> partitions = getPartitions(layer.getNeurons());
        CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions)
        {
            // ForwardTask task = new ForwardTask(latch, partition, inputs, outputs);
            // this.executorService.execute(task);
            this.executorService.execute(() -> {
                partition.forEach(neuron -> forward(neuron, inputs, outputs));

                latch.countDown();
            });
        }

        doLatchAwait(latch);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        CountDownLatch latch = new CountDownLatch(layers.length);

        for (Layer layer : layers)
        {
            // InitializeTask task = new InitializeTask(latch, valueInitializer, layer);
            // this.executorService.execute(task);
            this.executorService.execute(() -> {
                initialize(layer, valueInitializer);

                latch.countDown();
            });
        }

        doLatchAwait(latch);
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
        CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions)
        {
            // RefreshWeightsTask task = new RefreshWeightsTask(latch, partition, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);
            // this.executorService.execute(task);
            this.executorService.execute(() -> {
                partition.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));

                latch.countDown();
            });
        }

        doLatchAwait(latch);
    }
}
