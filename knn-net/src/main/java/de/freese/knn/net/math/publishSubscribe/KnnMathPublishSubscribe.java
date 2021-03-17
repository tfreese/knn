/**
 * Created: 02.10.2011
 */
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
public final class KnnMathPublishSubscribe extends AbstractKnnMath implements AutoCloseable
{
    // /**
    // * @author Thomas Freese
    // */
    // private static final class NeuronSubscriber implements Subscriber<NeuronList>
    // {
    // /**
    // *
    // */
    // private final Consumer<NeuronList> consumer;
    //
    // /**
    // *
    // */
    // private final CountDownLatch latch;
    //
    // /**
    // *
    // */
    // private Subscription subscription;
    //
    // /**
    // * Erstellt ein neues {@link NeuronSubscriber} Object.
    // *
    // * @param latch {@link CountDownLatch}
    // * @param consumer {@link Consumer}
    // */
    // private NeuronSubscriber(final CountDownLatch latch, final Consumer<NeuronList> consumer)
    // {
    // super();
    //
    // this.latch = Objects.requireNonNull(latch, "latch required");
    // this.consumer = Objects.requireNonNull(consumer, "consumer required");
    // }
    //
    // /**
    // * @see java.util.concurrent.Flow.Subscriber#onComplete()
    // */
    // @Override
    // public void onComplete()
    // {
    // this.latch.countDown();
    // }
    //
    // /**
    // * @see java.util.concurrent.Flow.Subscriber#onError(java.lang.Throwable)
    // */
    // @Override
    // public void onError(final Throwable t)
    // {
    // // Empty
    // }
    //
    // /**
    // * @see java.util.concurrent.Flow.Subscriber#onNext(java.lang.Object)
    // */
    // @Override
    // public void onNext(final NeuronList list)
    // {
    // this.consumer.accept(list);
    //
    // this.subscription.request(1); // Nächstes Element anfordern.
    // }
    //
    // /**
    // * @see java.util.concurrent.Flow.Subscriber#onSubscribe(java.util.concurrent.Flow.Subscription)
    // */
    // @Override
    // public void onSubscribe(final Subscription subscription)
    // {
    // this.subscription = subscription;
    // this.subscription.request(1); // Erstes Element anfordern.
    // // subscription.request(Long.MAX_VALUE); // Alle Elemente anfordern.
    // }
    // }

    /**
    *
    */
    private Executor executor;

    /**
     * Erstellt ein neues {@link KnnMathPublishSubscribe} Object.
     *
     * @param parallelism int
     * @param executor {@link Executor}
     */
    public KnnMathPublishSubscribe(final int parallelism, final Executor executor)
    {
        super(parallelism);

        this.executor = Objects.requireNonNull(executor, "executor required");
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#backward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor)
    {
        double[] errors = visitor.getLastErrors();
        double[] layerErrors = new double[layer.getSize()];

        List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        CompletableFuture<Void> future = null;

        try (SubmissionPublisher<NeuronList> publisher = new SubmissionPublisher<>(getExecutor(), Flow.defaultBufferSize()))
        {
            future = publisher.consume(list -> list.forEach(neuron -> backward(neuron, errors, layerErrors)));

            partitions.forEach(publisher::submit);
        }

        waitForFuture(future);

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception
    {
        // Externen Executor nicht schliessen.
        // KnnUtils.shutdown(getExecutor(), getLogger());
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#forward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor)
    {
        double[] inputs = visitor.getLastOutputs();
        double[] outputs = new double[layer.getSize()];

        List<NeuronList> partitions = getPartitions(layer.getNeurons(), getParallelism());
        CompletableFuture<Void> future = null;

        try (SubmissionPublisher<NeuronList> publisher = new SubmissionPublisher<>(getExecutor(), Flow.defaultBufferSize()))
        {
            future = publisher.consume(list -> list.forEach(neuron -> forward(neuron, inputs, outputs)));

            partitions.forEach(publisher::submit);
        }

        waitForFuture(future);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @return {@link Executor}
     */
    private Executor getExecutor()
    {
        return this.executor;
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        CountDownLatch latch = new CountDownLatch(layers.length);

        for (Layer layer : layers)
        {
            // InitializeTask task = new InitializeTask(latch, valueInitializer, layer);
            // this.executorService.execute(task);
            getExecutor().execute(() -> {
                initialize(layer, valueInitializer);

                latch.countDown();
            });
        }

        waitForLatch(latch);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#refreshLayerWeights(de.freese.knn.net.layer.Layer, de.freese.knn.net.layer.Layer, double, double,
     *      de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum,
                                    final BackwardVisitor visitor)
    {
        double[] leftOutputs = visitor.getOutputs(leftLayer);
        double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        double[] rightErrors = visitor.getErrors(rightLayer);

        List<NeuronList> partitions = getPartitions(leftLayer.getNeurons(), getParallelism());
        CompletableFuture<Void> future = null;

        try (SubmissionPublisher<NeuronList> publisher = new SubmissionPublisher<>(getExecutor(), Flow.defaultBufferSize()))
        {
            future = publisher
                    .consume(list -> list.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors)));

            partitions.forEach(publisher::submit);
        }

        waitForFuture(future);
    }

    /**
     * Warten bis der Task fertig ist.
     *
     * @param future {@link Future}
     */
    private void waitForFuture(final Future<?> future)
    {
        try
        {
            future.get();
        }
        catch (InterruptedException | ExecutionException ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * Blockiert den aktuellen Thread, bis der Latch auf 0 ist.
     *
     * @param latch {@link CountDownLatch}
     */
    private void waitForLatch(final CountDownLatch latch)
    {
        try
        {
            latch.await();
        }
        catch (RuntimeException rex)
        {
            throw rex;
        }
        catch (Throwable th)
        {
            throw new RuntimeException(th);
        }
    }
}
