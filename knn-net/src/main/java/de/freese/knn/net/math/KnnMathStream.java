// Created: 23.05.2016
package de.freese.knn.net.math;

import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit parallelen {@link Stream}s.
 *
 * @author Thomas Freese
 */
public final class KnnMathStream extends AbstractKnnMath {
    public KnnMathStream() {
        super();
    }

    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
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

    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor) {
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

    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers) {
        // @formatter:off
        StreamSupport.stream(Arrays.spliterator(layers), true)
            .parallel()
            .forEach(layer -> initialize(layer, valueInitializer))
            ;
        // @formatter:on
    }

    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum, final BackwardVisitor visitor) {
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
}
