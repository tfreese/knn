// Created: 02.10.2011
package de.freese.knn.net.math.forkjoin;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.Neuron;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem ForkJoin-Framework.
 *
 * @author Thomas Freese
 */
public final class KnnMathForkJoin extends AbstractKnnMath {
    private final ForkJoinPool forkJoinPool;

    public KnnMathForkJoin(final ForkJoinPool forkJoinPool) {
        super();

        this.forkJoinPool = Objects.requireNonNull(forkJoinPool, "forkJoinPool required");
    }

    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        final ForkJoinBackwardTask task = new ForkJoinBackwardTask(this, layer.getNeurons(), errors, layerErrors);

        getForkJoinPool().invoke(task);

        visitor.setErrors(layer, layerErrors);
    }

    @Override
    public void backward(final Neuron neuron, final double[] errors, final double[] layerErrors) {
        super.backward(neuron, errors, layerErrors);
    }

    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor) {
        final double[] inputs = visitor.getLastOutputs();
        final double[] outputs = new double[layer.getSize()];

        final ForkJoinForwardTask task = new ForkJoinForwardTask(this, layer.getNeurons(), inputs, outputs);

        getForkJoinPool().invoke(task);

        visitor.setOutputs(layer, outputs);
    }

    @Override
    public void forward(final Neuron neuron, final double[] inputs, final double[] outputs) {
        super.forward(neuron, inputs, outputs);
    }

    @Override
    public void initialize(final Layer layer, final ValueInitializer valueInitializer) {
        super.initialize(layer, valueInitializer);
    }

    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers) {
        final ForkJoinInitializeTask task = new ForkJoinInitializeTask(this, layers, valueInitializer);

        getForkJoinPool().invoke(task);
    }

    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum, final BackwardVisitor visitor) {
        final double[] leftOutputs = visitor.getOutputs(leftLayer);
        final double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        final double[] rightErrors = visitor.getErrors(rightLayer);

        final ForkJoinRefreshWeightsTask task = new ForkJoinRefreshWeightsTask(this, leftLayer.getNeurons(), teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);

        getForkJoinPool().invoke(task);
    }

    @Override
    public void refreshLayerWeights(final Neuron neuron, final double teachFactor, final double momentum, final double[] leftOutputs, final double[][] deltaWeights, final double[] rightErrors) {
        super.refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);
    }

    private ForkJoinPool getForkJoinPool() {
        return this.forkJoinPool;
    }
}
