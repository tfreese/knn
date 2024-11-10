// Created: 02.10.2011
package de.freese.knn.net.math;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit einem {@link Future}.<br>
 * Hier wird jedoch die Hälfte der Arbeit im aktuellen Thread verarbeitet.
 *
 * @author Thomas Freese
 */
public final class KnnMathExecutorHalfWork extends AbstractKnnMath {
    private final ExecutorService executorService;

    public KnnMathExecutorHalfWork(final ExecutorService executorService) {
        // Die Arbeit wird zwischen diesem und einem anderen Thread aufgeteilt.
        super(2);

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
    }

    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        final List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());

        final Future<?> future = getExecutorService().submit(() -> partitions.getFirst().forEach(neuron -> backward(neuron, errors, layerErrors)));

        // In diesem Thread.
        partitions.get(1).forEach(neuron -> backward(neuron, errors, layerErrors));

        waitForFuture(future);

        visitor.setErrors(layer, layerErrors);
    }

    @Override
    public void close() {
        // Externen ExecutorService nicht schliessen.
        // KnnUtils.shutdown(getExecutorService(), getLogger());
    }

    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor) {
        final double[] inputs = visitor.getLastOutputs();
        final double[] outputs = new double[layer.getSize()];

        final List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());

        final Future<?> future = getExecutorService().submit(() -> partitions.getFirst().forEach(neuron -> forward(neuron, inputs, outputs)));

        // In diesem Thread.
        partitions.get(1).forEach(neuron -> forward(neuron, inputs, outputs));

        waitForFuture(future);

        visitor.setOutputs(layer, outputs);
    }

    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers) {
        final int middle = layers.length / getParallelism();

        final List<Layer> layerList = Arrays.asList(layers);

        final List<Layer> list1 = layerList.subList(0, middle);
        final List<Layer> list2 = layerList.subList(middle, layers.length);

        final Future<?> future = getExecutorService().submit(() -> list1.forEach(layer -> initialize(layer, valueInitializer)));

        // In diesem Thread.
        list2.forEach(layer -> initialize(layer, valueInitializer));

        waitForFuture(future);
    }

    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum, final BackwardVisitor visitor) {
        final double[] leftOutputs = visitor.getOutputs(leftLayer);
        final double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        final double[] rightErrors = visitor.getErrors(rightLayer);

        final List<NeuronList> partitions = getPartitions(leftLayer.getNeurons(), getParallelism());

        final Future<?> future = getExecutorService().submit(
                () -> partitions.getFirst().forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)));

        // In diesem Thread.
        partitions.get(1).forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));

        waitForFuture(future);
    }

    @Override
    protected List<NeuronList> getPartitions(final NeuronList neurons, final int parallelism) {
        final int middle = neurons.size() / parallelism;

        final NeuronList nl1 = neurons.subList(0, middle);
        final NeuronList nl2 = neurons.subList(middle, neurons.size());

        return List.of(nl1, nl2);
    }

    private ExecutorService getExecutorService() {
        return this.executorService;
    }

    /**
     * Warten bis der Task fertig ist.
     */
    private void waitForFuture(final Future<?> future) {
        try {
            future.get();
        }
        catch (InterruptedException ex) {
            getLogger().error(ex.getMessage(), ex);

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }
}
