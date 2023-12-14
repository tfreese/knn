// Created: 02.10.2011
package de.freese.knn.net.math.queueWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit QueueWorkers.
 *
 * @author Thomas Freese
 */
public final class KnnMathQueueWorker extends AbstractKnnMath {
    /**
     * @author Thomas Freese
     */
    private static final class QueueWorker extends Thread {
        private static final Logger LOGGER = LoggerFactory.getLogger(QueueWorker.class);

        private final BlockingQueue<Runnable> queue;

        private boolean stopped;

        private QueueWorker(final BlockingQueue<Runnable> queue) {
            super();

            this.queue = Objects.requireNonNull(queue, "queue required");
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                if (this.stopped) {
                    break;
                }

                try {
                    final Runnable runnable = this.queue.take();

                    runnable.run();
                }
                catch (InterruptedException iex) {
                    // Ignore
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }

            LOGGER.debug("{}: terminated", getName());
        }

        void stopWorker() {
            this.stopped = true;

            interrupt();
        }
    }

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    private final List<QueueWorker> workers = new ArrayList<>();

    public KnnMathQueueWorker(final int parallelism) {
        super(parallelism);

        for (int i = 1; i <= (parallelism); i++) {
            final QueueWorker worker = new QueueWorker(this.queue);
            worker.setName(worker.getClass().getSimpleName() + "-" + i);
            worker.setDaemon(false);

            this.workers.add(worker);
            worker.start();
        }
    }

    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        final List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        final List<RunnableFuture<Void>> futures = new ArrayList<>(partitions.size());

        for (NeuronList partition : partitions) {
            final RunnableFuture<Void> future = new FutureTask<>(() -> partition.forEach(neuron -> backward(neuron, errors, layerErrors)), null);

            futures.add(future);
            getQueue().add(future);
        }

        waitForFutures(futures);

        visitor.setErrors(layer, layerErrors);
    }

    @Override
    public void close() {
        this.workers.forEach(QueueWorker::stopWorker);

        this.workers.clear();
        getQueue().clear();
    }

    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor) {
        final double[] inputs = visitor.getLastOutputs();
        final double[] outputs = new double[layer.getSize()];

        final List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        final List<RunnableFuture<Void>> futures = new ArrayList<>(partitions.size());

        for (NeuronList partition : partitions) {
            final RunnableFuture<Void> future = new FutureTask<>(() -> partition.forEach(neuron -> forward(neuron, inputs, outputs)), null);

            futures.add(future);
            getQueue().add(future);
        }

        waitForFutures(futures);

        visitor.setOutputs(layer, outputs);
    }

    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers) {
        final List<RunnableFuture<Void>> futures = new ArrayList<>(layers.length);

        for (Layer layer : layers) {
            final RunnableFuture<Void> future = new FutureTask<>(() -> initialize(layer, valueInitializer), null);

            futures.add(future);
            getQueue().add(future);
        }

        waitForFutures(futures);
    }

    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum, final BackwardVisitor visitor) {
        final double[] leftOutputs = visitor.getOutputs(leftLayer);
        final double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        final double[] rightErrors = visitor.getErrors(rightLayer);

        final List<NeuronList> partitions = getPartitions(leftLayer.getNeurons(), getParallelism());
        final List<RunnableFuture<Void>> futures = new ArrayList<>(partitions.size());

        for (NeuronList partition : partitions) {
            final RunnableFuture<Void> future = new FutureTask<>(() -> partition.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)), null);

            futures.add(future);
            getQueue().add(future);
        }

        waitForFutures(futures);
    }

    private BlockingQueue<Runnable> getQueue() {
        return this.queue;
    }

    /**
     * Warten bis der Task fertig ist.
     */
    private void waitForFuture(final Future<?> future) {
        try {
            future.get();
        }
        catch (InterruptedException | ExecutionException ex) {
            getLogger().error(ex.getMessage(), ex);
        }
    }

    /**
     * Warten bis alle Tasks fertig sind.
     */
    private void waitForFutures(final List<? extends Future<Void>> futures) {
        for (Future<Void> future : futures) {
            waitForFuture(future);
        }
    }
}
