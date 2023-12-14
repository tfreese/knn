// Created: 02.10.2011
package de.freese.knn.net.math.executor;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem {@link ExecutorService}-Framework.
 *
 * @author Thomas Freese
 */
public final class KnnMathExecutor extends AbstractKnnMath {
    private final Executor executor;

    public KnnMathExecutor(final int parallelism, final Executor executor) {
        super(parallelism);

        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        final List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        final CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions) {
            getExecutor().execute(() -> {
                partition.forEach(neuron -> backward(neuron, errors, layerErrors));

                latch.countDown();
            });
        }

        waitForLatch(latch);

        visitor.setErrors(layer, layerErrors);
    }

    @Override
    public void close() {
        // Externen Executor nicht schliessen.
        // KnnUtils.shutdown(getExecutor(), getLogger());
    }

    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor) {
        final double[] inputs = visitor.getLastOutputs();
        final double[] outputs = new double[layer.getSize()];

        final List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        final CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions) {
            getExecutor().execute(() -> {
                partition.forEach(neuron -> forward(neuron, inputs, outputs));

                latch.countDown();
            });
        }

        waitForLatch(latch);

        visitor.setOutputs(layer, outputs);
    }

    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers) {
        final CountDownLatch latch = new CountDownLatch(layers.length);

        for (Layer layer : layers) {
            getExecutor().execute(() -> {
                initialize(layer, valueInitializer);

                latch.countDown();
            });
        }

        waitForLatch(latch);
    }

    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum, final BackwardVisitor visitor) {
        final double[] leftOutputs = visitor.getOutputs(leftLayer);
        final double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        final double[] rightErrors = visitor.getErrors(rightLayer);

        final List<NeuronList> partitions = getPartitions(leftLayer.getNeurons(), getParallelism());
        final CountDownLatch latch = new CountDownLatch(partitions.size());

        for (NeuronList partition : partitions) {
            getExecutor().execute(() -> {
                partition.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));

                latch.countDown();
            });
        }

        waitForLatch(latch);
    }

    private Executor getExecutor() {
        return this.executor;
    }

    /**
     * Blockiert den aktuellen Thread, bis der Latch auf 0 ist.
     */
    private void waitForLatch(final CountDownLatch latch) {
        try {
            latch.await();
        }
        catch (RuntimeException rex) {
            throw rex;
        }
        catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }
}
