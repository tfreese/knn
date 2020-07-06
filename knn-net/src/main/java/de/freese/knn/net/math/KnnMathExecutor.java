/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem {@link ExecutorService}-Framework.
 *
 * @author Thomas Freese
 */
public class KnnMathExecutor extends AbstractKnnMathAsync
{
    /**
     * Erstellt ein neues {@link KnnMathExecutor} Object.
     */
    public KnnMathExecutor()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link KnnMathExecutor} Object.
     *
     * @param executorService {@link ExecutorService}
     */
    public KnnMathExecutor(final ExecutorService executorService)
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
        CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions)
        {
            getExecutorService().execute(() -> {
                partition.forEach(neuron -> backward(neuron, errors, layerErrors));

                latch.countDown();
            });
        }

        waitForLatch(latch);

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
        CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions)
        {
            getExecutorService().execute(() -> {
                partition.forEach(neuron -> forward(neuron, inputs, outputs));

                latch.countDown();
            });
        }

        waitForLatch(latch);

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
        CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions)
        {
            getExecutorService().execute(() -> {
                partition.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));

                latch.countDown();
            });
        }

        waitForLatch(latch);
    }
}
