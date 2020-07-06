/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem {@link CompletionService}.
 *
 * @author Thomas Freese
 */
public class KnnMathCompletionService extends AbstractKnnMathAsync
{
    /**
     *
     */
    private final CompletionService<Void> completionService;

    /**
     * Erstellt ein neues {@link KnnMathCompletionService} Object.
     */
    public KnnMathCompletionService()
    {
        super();

        this.completionService = new ExecutorCompletionService<>(getExecutorService());
    }

    /**
     * Erstellt ein neues {@link KnnMathCompletionService} Object.
     *
     * @param executorService {@link ExecutorService}
     */
    public KnnMathCompletionService(final ExecutorService executorService)
    {
        super(executorService);

        this.completionService = new ExecutorCompletionService<>(executorService);
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

        for (NeuronList partition : partitions)
        {
            this.completionService.submit(() -> partition.forEach(neuron -> backward(neuron, errors, layerErrors)), null);
        }

        waitForCompletionService(this.completionService, partitions.size());

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

        for (NeuronList partition : partitions)
        {
            this.completionService.submit(() -> partition.forEach(neuron -> forward(neuron, inputs, outputs)), null);
        }

        waitForCompletionService(this.completionService, partitions.size());

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        for (Layer layer : layers)
        {
            this.completionService.submit(() -> initialize(layer, valueInitializer), null);
        }

        waitForCompletionService(this.completionService, layers.length);
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

        for (NeuronList partition : partitions)
        {
            this.completionService.submit(
                    () -> partition.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)), null);
        }

        waitForCompletionService(this.completionService, partitions.size());
    }
}
