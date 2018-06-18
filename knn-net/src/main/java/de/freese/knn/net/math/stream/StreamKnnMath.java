/**
 * Created on 23.05.2016 17:18:14
 */
package de.freese.knn.net.math.stream;

import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import de.freese.base.core.collection.stream.spliterator.SplitableArraySpliterator;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.IValueInitializer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit der Java8 Streaming-API.
 *
 * @author Thomas Freese
 */
public class StreamKnnMath extends AbstractKnnMath
{
    /**
     * Erstellt ein neues Object.
     */
    public StreamKnnMath()
    {
        super();
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#backward(de.freese.knn.net.layer.ILayer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void backward(final ILayer layer, final BackwardVisitor visitor)
    {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        // @formatter:off
        layer.getNeurons()
                .parallelStream()
                .forEach(neuron -> backward(neuron, errors, layerErrors));
        // @formatter:on

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#forward(de.freese.knn.net.layer.ILayer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final ILayer layer, final ForwardVisitor visitor)
    {
        final double[] inputs = visitor.getLastOutputs();
        final double[] outputs = new double[layer.getSize()];

        // @formatter:off
        layer.getNeurons()
                .parallelStream()
                .forEach(neuron -> forward(neuron, inputs, outputs));
        // @formatter:on

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#getNetError(double[], double[])
     */
    @Override
    public double getNetError(final double[] outputs, final double[] outputTargets)
    {
        // @formatter:off
        double error = IntStream.range(0, outputs.length)
                .parallel()
                .mapToDouble(i -> getNetError(i, outputs, outputTargets))
                .sum();
        // @formatter:on

        error /= 2.0D;

        return error;

    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#initialize(de.freese.knn.net.matrix.IValueInitializer, de.freese.knn.net.layer.ILayer[])
     */
    @Override
    public void initialize(final IValueInitializer valueInitializer, final ILayer[] layers)
    {
        // @formatter:off
        StreamSupport.stream(new SplitableArraySpliterator<>(layers), true)
                .parallel()
                .forEach(layer -> initialize(layer, valueInitializer));
        // @formatter:on
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#refreshLayerWeights(de.freese.knn.net.layer.ILayer, de.freese.knn.net.layer.ILayer, double, double,
     *      de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void refreshLayerWeights(final ILayer leftLayer, final ILayer rightLayer, final double teachFactor, final double momentum,
                                    final BackwardVisitor visitor)
    {
        final double[] leftOutputs = visitor.getOutputs(leftLayer);
        final double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        final double[] rightErrors = visitor.getErrors(rightLayer);

        // @formatter:off
        leftLayer.getNeurons()
                .parallelStream()
                .forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));
        // @formatter:on
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#setOutputError(de.freese.knn.net.layer.ILayer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void setOutputError(final ILayer layer, final BackwardVisitor visitor)
    {
        final double[] outputs = visitor.getOutputs(layer);
        final double[] errors = new double[outputs.length];

        // @formatter:off
        IntStream.range(0, outputs.length)
                .parallel()
                .forEach(i -> setOutputError(i, outputs, errors, visitor));
        // @formatter:on

        visitor.setErrors(layer, errors);
    }
}
