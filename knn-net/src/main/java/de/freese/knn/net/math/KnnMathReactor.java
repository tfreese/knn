/**
 * Created on 23.05.2016 17:18:14
 */
package de.freese.knn.net.math;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.utils.KnnThreadQueueThreadFactory;
import de.freese.knn.net.utils.KnnUtils;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Mathematik des {@link NeuralNet} mit dem Reactor-Framework.
 *
 * @author Thomas Freese
 */
public class KnnMathReactor extends AbstractKnnMath implements AutoCloseable
{
    /**
     *
     */
    private boolean createdExecutor = false;

    /**
     *
     */
    private final Executor executor;

    /**
     *
     */
    private final Scheduler scheduler;

    /**
     * Erstellt ein neues Object.
     */
    public KnnMathReactor()
    {
        // this(Executors.newCachedThreadPool(new KnnThreadQueueThreadFactory()));
        this(Executors.newFixedThreadPool(KnnUtils.DEFAULT_POOL_SIZE, new KnnThreadQueueThreadFactory()));

        this.createdExecutor = true;
    }

    /**
     * Erstellt ein neues Object.
     *
     * @param executor {@link Executor}
     */
    public KnnMathReactor(final Executor executor)
    {
        super();

        this.executor = Objects.requireNonNull(executor, "executor required");
        this.scheduler = Schedulers.fromExecutor(executor);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#backward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor)
    {
        final double[] errors = visitor.getLastErrors();
        final double[] layerErrors = new double[layer.getSize()];

        // @formatter:off
        Flux.fromIterable(layer.getNeurons())
            .parallel(KnnUtils.DEFAULT_POOL_SIZE)
            .runOn(this.scheduler)
            .subscribe(neuron -> backward(neuron, errors, layerErrors))
            ;
        // @formatter:on

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception
    {
        this.scheduler.dispose();

        if (this.createdExecutor && (this.executor instanceof ExecutorService))
        {
            KnnUtils.shutdown((ExecutorService) this.executor, getLogger());
        }
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#forward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor)
    {
        final double[] inputs = visitor.getLastOutputs();
        final double[] outputs = new double[layer.getSize()];

        // @formatter:off
        Flux.fromIterable(layer.getNeurons())
            .parallel(KnnUtils.DEFAULT_POOL_SIZE)
            .runOn(this.scheduler)
            .subscribe(neuron -> forward(neuron, inputs, outputs))
            ;
        // @formatter:on

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#getNetError(double[], double[])
     */
    @Override
    public double getNetError(final double[] outputs, final double[] outputTargets)
    {
        // @formatter:off
        //Flux.create((final FluxSink<Integer> fluxSink) -> IntStream.range(0, outputs.length).forEach(fluxSink::next))
        //Flux.fromStream(IntStream.range(0, outputs.length).boxed())
        double error = Flux.range(0, outputs.length)
            .parallel(KnnUtils.DEFAULT_POOL_SIZE)
            .runOn(this.scheduler)
            .map(i -> getNetError(i, outputs, outputTargets))
            .reduce((error1, error2) -> error1 + error2)
            .block()
            ;

        // Siehe auch MathFlux für math. Operatoren !

        error /= 2.0D;

        return error;

    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        // @formatter:off
        Flux.fromArray(layers)
            .parallel(KnnUtils.DEFAULT_POOL_SIZE)
            .runOn(this.scheduler)
            .subscribe(layer -> initialize(layer, valueInitializer))
            ;
        // @formatter:on
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#refreshLayerWeights(de.freese.knn.net.layer.Layer, de.freese.knn.net.layer.Layer, double, double,
     *      de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum,
                                    final BackwardVisitor visitor)
    {
        final double[] leftOutputs = visitor.getOutputs(leftLayer);
        final double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        final double[] rightErrors = visitor.getErrors(rightLayer);

        // @formatter:off
        Flux.fromIterable(leftLayer.getNeurons())
            .parallel(KnnUtils.DEFAULT_POOL_SIZE)
            .runOn(this.scheduler)
            .subscribe(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors))
            ;
        // @formatter:on
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#setOutputError(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void setOutputError(final Layer layer, final BackwardVisitor visitor)
    {
        final double[] outputs = visitor.getOutputs(layer);
        final double[] errors = new double[outputs.length];

        // @formatter:off
        //Flux.create((final FluxSink<Integer> fluxSink) -> IntStream.range(0, outputs.length).forEach(fluxSink::next))
        //Flux.fromStream(IntStream.range(0, outputs.length).boxed())
        Flux.range(0, outputs.length)
            .parallel(KnnUtils.DEFAULT_POOL_SIZE)
            .runOn(this.scheduler)
            .subscribe(i -> setOutputError(i, outputs, errors, visitor))
            ;
        // @formatter:on

        visitor.setErrors(layer, errors);
    }
}
