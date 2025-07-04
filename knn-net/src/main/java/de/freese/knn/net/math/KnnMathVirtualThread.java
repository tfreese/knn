// Created: 23.05.2016
package de.freese.knn.net.math;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit virtuellen Threads.
 *
 * @author Thomas Freese
 */
public final class KnnMathVirtualThread extends AbstractKnnMath {
    private final ThreadFactory threadFactory;

    public KnnMathVirtualThread() {
        super();

        threadFactory = Thread.ofVirtual().factory();
    }

    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        try (ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {
            layer.getNeurons().forEach(neuron -> executorService.execute(() -> backward(neuron, errors, layerErrors)));
        }

        visitor.setErrors(layer, layerErrors);
    }

    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor) {
        final double[] inputs = visitor.getLastOutputs();
        final double[] outputs = new double[layer.getSize()];

        try (ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {
            layer.getNeurons().forEach(neuron -> executorService.execute(() -> forward(neuron, inputs, outputs)));
        }

        visitor.setOutputs(layer, outputs);
    }

    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers) {
        try (ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {
            for (Layer layer : layers) {
                executorService.execute(() -> initialize(layer, valueInitializer));
            }
        }
    }

    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum, final BackwardVisitor visitor) {
        final double[] leftOutputs = visitor.getOutputs(leftLayer);
        final double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        final double[] rightErrors = visitor.getErrors(rightLayer);

        try (ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {
            leftLayer.getNeurons().forEach(neuron -> executorService.execute(() -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)));
        }
    }
}
