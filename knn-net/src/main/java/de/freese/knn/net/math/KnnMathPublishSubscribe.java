/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem {@link Flow}-Framework.
 *
 * @author Thomas Freese
 */
public class KnnMathPublishSubscribe extends AbstractKnnMathAsync
{
    /**
     * @author Thomas Freese
     */
    class NeuronSubscriber implements Subscriber<NeuronList>
    {
        /**
         *
         */
        private final Consumer<NeuronList> consumer;

        /**
         *
         */
        private final CountDownLatch latch;

        /**
         *
         */
        private Subscription subscription = null;

        /**
         * Erstellt ein neues {@link NeuronSubscriber} Object.
         *
         * @param latch {@link CountDownLatch}
         * @param consumer {@link Consumer}
         */
        private NeuronSubscriber(final CountDownLatch latch, final Consumer<NeuronList> consumer)
        {
            super();

            this.latch = latch;
            this.consumer = consumer;
        }

        /**
         * @return {@link Subscription}
         */
        protected Subscription getSubscription()
        {
            return this.subscription;
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onComplete()
         */
        @Override
        public void onComplete()
        {
            this.latch.countDown();
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onError(java.lang.Throwable)
         */
        @Override
        public void onError(final Throwable t)
        {
            // Empty
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onNext(java.lang.Object)
         */
        @Override
        public void onNext(final NeuronList list)
        {
            this.consumer.accept(list);

            this.subscription.request(1); // Nächstes Element anfordern.
        }

        /**
         * @see java.util.concurrent.Flow.Subscriber#onSubscribe(java.util.concurrent.Flow.Subscription)
         */
        @Override
        public void onSubscribe(final Subscription subscription)
        {
            this.subscription = subscription;
            this.subscription.request(1); // Erstes Element anfordern.
            // subscription.request(Long.MAX_VALUE); // Alle Elemente anfordern.
        }
    }

    /**
     * Erstellt ein neues {@link KnnMathPublishSubscribe} Object.
     */
    public KnnMathPublishSubscribe()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link KnnMathPublishSubscribe} Object.
     *
     * @param executorService {@link ExecutorService}
     */
    public KnnMathPublishSubscribe(final ExecutorService executorService)
    {
        super(executorService);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#backward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor)
    {
        double[] errors = visitor.getLastErrors();
        double[] layerErrors = new double[layer.getSize()];

        List<NeuronList> partitions = getPartitions(layer.getNeurons());
        CountDownLatch latch = new CountDownLatch(1);

        try (SubmissionPublisher<NeuronList> publisher = new SubmissionPublisher<>(getExecutorService(), Flow.defaultBufferSize()))
        {
            publisher.subscribe(new NeuronSubscriber(latch, list -> list.forEach(neuron -> backward(neuron, errors, layerErrors))));

            partitions.forEach(publisher::submit);
        }

        waitForLatch(latch);

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#forward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor)
    {
        double[] inputs = visitor.getLastOutputs();
        double[] outputs = new double[layer.getSize()];

        List<NeuronList> partitions = getPartitions(layer.getNeurons());
        CountDownLatch latch = new CountDownLatch(1);

        try (SubmissionPublisher<NeuronList> publisher = new SubmissionPublisher<>(getExecutorService(), Flow.defaultBufferSize()))
        {
            publisher.subscribe(new NeuronSubscriber(latch, list -> list.forEach(neuron -> forward(neuron, inputs, outputs))));

            partitions.forEach(publisher::submit);
        }

        waitForLatch(latch);

        visitor.setOutputs(layer, outputs);
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
            getExecutorService().execute(() -> {
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

        List<NeuronList> partitions = getPartitions(leftLayer.getNeurons());
        CountDownLatch latch = new CountDownLatch(1);

        try (SubmissionPublisher<NeuronList> publisher = new SubmissionPublisher<>(getExecutorService(), Flow.defaultBufferSize()))
        {
            publisher.subscribe(new NeuronSubscriber(latch,
                    list -> list.forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors))));

            partitions.forEach(publisher::submit);
        }

        waitForLatch(latch);
    }
}
