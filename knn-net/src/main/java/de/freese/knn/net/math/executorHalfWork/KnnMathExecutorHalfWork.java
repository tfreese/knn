/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math.executorHalfWork;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit einem {@link Future}.<br>
 * Hier wird jedoch die Häfte der Arbeit im aktuellem Thread verarbeitet.
 *
 * @author Thomas Freese
 */
public final class KnnMathExecutorHalfWork extends AbstractKnnMath implements AutoCloseable
{
    /**
    *
    */
    private ExecutorService executorService;

    /**
     * Erstellt ein neues {@link KnnMathExecutorHalfWork} Object.
     *
     * @param executorService {@link ExecutorService}
     */
    public KnnMathExecutorHalfWork(final ExecutorService executorService)
    {
        // Die Arbeit wird zwischen diesem und einem anderen Thread aufgeteilt.
        super(2);

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

        List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());

        Future<?> future = getExecutorService().submit(() -> partitions.get(0).forEach(neuron -> backward(neuron, errors, layerErrors)));

        // In diesem Thread.
        partitions.get(1).forEach(neuron -> backward(neuron, errors, layerErrors));

        waitForFuture(future);

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception
    {
        // Externen ExecutorService nicht schliessen.
        // KnnUtils.shutdown(getExecutorService(), getLogger());
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

        Future<?> future = getExecutorService().submit(() -> partitions.get(0).forEach(neuron -> forward(neuron, inputs, outputs)));

        // In diesem Thread.
        partitions.get(1).forEach(neuron -> forward(neuron, inputs, outputs));

        waitForFuture(future);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @return {@link ExecutorService}
     */
    private ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    /**
     * @see de.freese.knn.net.math.AbstractKnnMath#getPartitions(de.freese.knn.net.neuron.NeuronList, int)
     */
    @Override
    protected List<NeuronList> getPartitions(final NeuronList neurons, final int parallelism)
    {
        int middle = neurons.size() / parallelism;

        NeuronList nl1 = neurons.subList(0, middle);
        NeuronList nl2 = neurons.subList(middle, neurons.size());

        return List.of(nl1, nl2);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        int middle = layers.length / getParallelism();

        List<Layer> layerList = Arrays.asList(layers);

        List<Layer> list1 = layerList.subList(0, middle);
        List<Layer> list2 = layerList.subList(middle, layers.length);

        Future<?> future = getExecutorService().submit(() -> list1.forEach(layer -> initialize(layer, valueInitializer)));

        // In diesem Thread.
        list2.forEach(layer -> initialize(layer, valueInitializer));

        waitForFuture(future);
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

        Future<?> future = getExecutorService()
                .submit(() -> partitions.get(0).forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)));

        // In diesem Thread.
        partitions.get(1).forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));

        waitForFuture(future);
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
}
