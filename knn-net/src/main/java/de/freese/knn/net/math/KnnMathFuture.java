/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit einem {@link Future}.
 *
 * @author Thomas Freese
 */
public class KnnMathFuture extends AbstractKnnMathAsync
{
    /**
     * Erstellt ein neues {@link KnnMathFuture} Object.
     */
    public KnnMathFuture()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link KnnMathFuture} Object.
     *
     * @param executorService {@link ExecutorService}
     */
    public KnnMathFuture(final ExecutorService executorService)
    {
        super(executorService);
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

        Future<?> future = getExecutorService().submit(() -> partitions.get(0).forEach(neuron -> backward(neuron, errors, layerErrors)));
        partitions.get(1).forEach(neuron -> backward(neuron, errors, layerErrors));

        waitForFuture(future);

        visitor.setErrors(layer, layerErrors);
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

        Future<?> future = getExecutorService().submit(() -> partitions.get(0).forEach(neuron -> forward(neuron, inputs, outputs)));
        partitions.get(1).forEach(neuron -> forward(neuron, inputs, outputs));

        waitForFuture(future);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @see de.freese.knn.net.math.AbstractKnnMathAsync#getPartitions(de.freese.knn.net.neuron.NeuronList)
     */
    @Override
    protected List<NeuronList> getPartitions(final NeuronList neurons)
    {
        int middle = neurons.size() / getPoolSize();

        NeuronList nl1 = neurons.subList(0, middle);
        NeuronList nl2 = neurons.subList(middle, neurons.size());

        return List.of(nl1, nl2);
    }

    /**
     * @see de.freese.knn.net.math.AbstractKnnMathAsync#getPoolSize()
     */
    @Override
    protected int getPoolSize()
    {
        return 2;
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
            getExecutorService().execute(() -> {
                initialize(layer, valueInitializer);

                latch.countDown();
            });
        }

        waitForLatch(latch);
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

        Future<?> future = getExecutorService()
                .submit(() -> partitions.get(0).forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)));
        partitions.get(1).forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));

        waitForFuture(future);
    }
}
