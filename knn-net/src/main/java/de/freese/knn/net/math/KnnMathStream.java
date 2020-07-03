/**
 * Created on 23.05.2016 17:18:14
 */
package de.freese.knn.net.math;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit der Java8 Streaming-API.
 *
 * @author Thomas Freese
 */
public class KnnMathStream extends AbstractKnnMath
{
    /**
     * Erstellt ein neues Object.
     */
    public KnnMathStream()
    {
        super();
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#backward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor)
    {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        // @formatter:off
        layer.getNeurons()
            .parallelStream()
            .forEach(neuron -> backward(neuron, errors, layerErrors))
            ;
        // @formatter:on

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#forward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor)
    {
        final double[] inputs = visitor.getLastOutputs();
        final double[] outputs = new double[layer.getSize()];

        // @formatter:off
        layer.getNeurons()
            .parallelStream()
            .forEach(neuron -> forward(neuron, inputs, outputs))
            ;
        // @formatter:on

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#getNetError(double[], double[])
     */
    @Override
    public double getNetError(final double[] outputs, final double[] outputTargets)
    {
        // @formatter:off
        double error = IntStream.range(0, outputs.length)
                .parallel()
                .mapToDouble(i -> getNetError(i, outputs, outputTargets))
                .sum()
                ;
        // @formatter:on

        error /= 2.0D;

        return error;

    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        // @formatter:off
        StreamSupport.stream(Arrays.spliterator(layers), true)
            .parallel()
            .forEach(layer -> initialize(layer, valueInitializer))
            ;
        // @formatter:on
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#refreshLayerWeights(de.freese.knn.net.layer.Layer, de.freese.knn.net.layer.Layer, double, double,
     *      de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum,
                                    final BackwardVisitor visitor)
    {
        final double[] leftOutputs = visitor.getOutputs(leftLayer);
        final double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        final double[] rightErrors = visitor.getErrors(rightLayer);

        // @formatter:off
        leftLayer.getNeurons()
            .parallelStream()
            .forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors))
            ;
        // @formatter:on
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#setOutputError(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void setOutputError(final Layer layer, final BackwardVisitor visitor)
    {
        final double[] outputs = visitor.getOutputs(layer);
        final double[] errors = new double[outputs.length];

        // @formatter:off
        IntStream.range(0, outputs.length)
            .parallel()
            .forEach(i -> setOutputError(i, outputs, errors, visitor))
            ;
        // @formatter:on

        visitor.setErrors(layer, errors);
    }
}
