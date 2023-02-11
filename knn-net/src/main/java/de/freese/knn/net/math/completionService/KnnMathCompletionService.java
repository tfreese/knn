// Created: 02.10.2011
package de.freese.knn.net.math.completionService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem {@link CompletionService}.
 *
 * @author Thomas Freese
 */
public final class KnnMathCompletionService extends AbstractKnnMath {
    private final CompletionService<Void> completionService;

    private final Executor executor;

    public KnnMathCompletionService(final int parallelism, final Executor executor) {
        super(parallelism);

        this.executor = Objects.requireNonNull(executor, "executor required");

        this.completionService = new ExecutorCompletionService<>(executor);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#backward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
        double[] errors = visitor.getLastErrors();
        double[] layerErrors = new double[layer.getSize()];

        List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());

        for (NeuronList partition : partitions) {
            getCompletionService().submit(() -> partition.forEach(neuron -> backward(neuron, errors, layerErrors)), null);
        }

        waitForCompletionService(getCompletionService(), partitions.size());

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#close()
     */

    @Override
    public void close() {
        // Externen Executor nicht schliessen.
        // KnnUtils.shutdown(getExecutor(), getLogger());
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#forward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor) {
        double[] inputs = visitor.getLastOutputs();
        double[] outputs = new double[layer.getSize()];

        List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());

        for (NeuronList partition : partitions) {
            getCompletionService().submit(() -> partition.forEach(neuron -> forward(neuron, inputs, outputs)), null);
        }

        waitForCompletionService(getCompletionService(), partitions.size());

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers) {
        for (Layer layer : layers) {
            getCompletionService().submit(() -> initialize(layer, valueInitializer), null);
        }

        waitForCompletionService(getCompletionService(), layers.length);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#refreshLayerWeights(de.freese.knn.net.layer.Layer, de.freese.knn.net.layer.Layer, double, double,
     * de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum, final BackwardVisitor visitor) {
        double[] leftOutputs = visitor.getOutputs(leftLayer);
        double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        double[] rightErrors = visitor.getErrors(rightLayer);

        List<NeuronList> partitions = getPartitions(leftLayer.getNeurons(), getParallelism());

        for (NeuronList partition : partitions) {
            getCompletionService().submit(() -> partition.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)), null);
        }

        waitForCompletionService(getCompletionService(), partitions.size());
    }

    /**
     * Warten bis alle Tasks fertig sind.
     */
    void waitForCompletionService(final CompletionService<?> completionService, final int count) {
        for (int i = 0; i < count; i++) {
            try {
                completionService.take();
            }
            catch (InterruptedException ex) {
                getLogger().error(ex.getMessage(), ex);
            }
        }
    }

    private CompletionService<Void> getCompletionService() {
        return this.completionService;
    }

    @SuppressWarnings("unused")
    private Executor getExecutor() {
        return this.executor;
    }
}
