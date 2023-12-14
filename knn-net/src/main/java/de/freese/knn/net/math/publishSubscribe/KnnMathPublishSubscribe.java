// Created: 02.10.2011
package de.freese.knn.net.math.publishSubscribe;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.SubmissionPublisher;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem {@link Flow}-Framework.
 *
 * @author Thomas Freese
 */
public final class KnnMathPublishSubscribe extends AbstractKnnMath {
    // /**
    // * @author Thomas Freese
    // */
    // private static final class NeuronSubscriber implements Subscriber<NeuronList>
    // {
    // private final Consumer<NeuronList> consumer;
    // private final CountDownLatch latch;
    //
    // private Subscription subscription;
    //
    // private NeuronSubscriber(final CountDownLatch latch, final Consumer<NeuronList> consumer)
    // {
    // super();
    //
    // this.latch = Objects.requireNonNull(latch, "latch required");
    // this.consumer = Objects.requireNonNull(consumer, "consumer required");
    // }
    //
    // @Override
    // public void onComplete()
    // {
    // this.latch.countDown();
    // }
    //
    // @Override
    // public void onError(final Throwable t)
    // {
    // // Empty
    // }
    //
    // @Override
    // public void onNext(final NeuronList list)
    // {
    // this.consumer.accept(list);
    //
    // this.subscription.request(1); // Nächstes Element anfordern.
    // }
    //
    // @Override
    // public void onSubscribe(final Subscription subscription)
    // {
    // this.subscription = subscription;
    // this.subscription.request(1); // Erstes Element anfordern.
    // // subscription.request(Long.MAX_VALUE); // Alle Elemente anfordern.
    // }
    // }

    private final Executor executor;

    public KnnMathPublishSubscribe(final int parallelism, final Executor executor) {
        super(parallelism);

        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        final List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        CompletableFuture<Void> future = null;

        try (SubmissionPublisher<NeuronList> publisher = new SubmissionPublisher<>(getExecutor(), Flow.defaultBufferSize())) {
            future = publisher.consume(list -> list.forEach(neuron -> backward(neuron, errors, layerErrors)));

            partitions.forEach(publisher::submit);
        }

        waitForFuture(future);

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
        CompletableFuture<Void> future = null;

        try (SubmissionPublisher<NeuronList> publisher = new SubmissionPublisher<>(getExecutor(), Flow.defaultBufferSize())) {
            future = publisher.consume(list -> list.forEach(neuron -> forward(neuron, inputs, outputs)));

            partitions.forEach(publisher::submit);
        }

        waitForFuture(future);

        visitor.setOutputs(layer, outputs);
    }

    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers) {
        final CountDownLatch latch = new CountDownLatch(layers.length);

        for (Layer layer : layers) {
            // InitializeTask task = new InitializeTask(latch, valueInitializer, layer);
            // this.executorService.execute(task);
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
        CompletableFuture<Void> future = null;

        try (SubmissionPublisher<NeuronList> publisher = new SubmissionPublisher<>(getExecutor(), Flow.defaultBufferSize())) {
            future = publisher.consume(list -> list.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)));

            partitions.forEach(publisher::submit);
        }

        waitForFuture(future);
    }

    private Executor getExecutor() {
        return this.executor;
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
