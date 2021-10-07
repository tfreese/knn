// Created: 23.05.2016
package de.freese.knn.net.math.reactor;

import java.util.concurrent.Executors;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.utils.KnnThreadFactory;
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
public final class KnnMathReactor extends AbstractKnnMath
{
    /**
     *
     */
    private final Scheduler scheduler;

    /**
     * Erstellt ein neues {@link KnnMathReactor} Object.
     *
     * @param parallelism int
     */
    public KnnMathReactor(final int parallelism)
    {
        super(parallelism);

        // Siehe JavaDoc von Schedulers
        // #boundedElastic()}: Optimized for longer executions, an alternative for blocking tasks where the number of active tasks (and threads) is capped
        // #parallel()}: Optimized for fast {@link Runnable} non-blocking executions
        // #fromExecutorService(ExecutorService)} to create new instances around {@link java.util.concurrent.Executors}

        // Recht langsam
        // this.scheduler = Schedulers.newBoundedElastic(parallelism, Integer.MAX_VALUE, "knn-scheduler-");

        // Recht langsam
        // this.scheduler = Schedulers.newParallel("knn-scheduler-", parallelism);

        // Recht schnell
        this.scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(parallelism, new KnnThreadFactory("knn-scheduler-")));
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
            .parallel(getParallelism())
            .runOn(getScheduler())
            .doOnNext(neuron -> backward(neuron, errors, layerErrors))
            .sequential()
            .blockLast()
            ;
        // @formatter:on

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#close()
     */
    @Override
    public void close()
    {
        getScheduler().dispose();
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
            .parallel(getParallelism())
            .runOn(getScheduler())
            .doOnNext(neuron -> forward(neuron, inputs, outputs))
            .sequential()
            .blockLast()
            ;
        // @formatter:on

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @return {@link Scheduler}
     */
    private Scheduler getScheduler()
    {
        return this.scheduler;
    }

    // /**
    // * @see de.freese.knn.net.math.KnnMath#getNetError(double[], double[])
    // */
    // @Override
    // public double getNetError(final double[] outputs, final double[] outputTargets)
    // {
    // // Flux.create((final FluxSink<Integer> fluxSink) -> IntStream.range(0, outputs.length).forEach(fluxSink::next))
    // // Flux.fromStream(IntStream.range(0, outputs.length).boxed())
    // //
    // // Siehe auch MathFlux für math. Operatoren !
    //
//        // @formatter:off
//        double error = Flux.range(0, outputs.length)
//            .parallel(getParallelism()
//            .runOn(getScheduler())
//            .map(i -> getNetError(i, outputs, outputTargets))
//            .reduce((error1, error2) -> error1 + error2)
//            .block()
//            ;
//        // @formatter:on
    //
    // error /= 2.0D;
    //
    // return error;
    // }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        // @formatter:off
        Flux.fromArray(layers)
            .parallel(getParallelism())
            .runOn(getScheduler())
            .doOnNext(layer -> initialize(layer, valueInitializer))
            .sequential()
            .blockLast()
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
            .parallel(getParallelism())
            .runOn(getScheduler())
            .doOnNext(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors))
            .sequential()
            .blockLast()
            ;
        // @formatter:on
    }

    // /**
    // * @see de.freese.knn.net.math.KnnMath#setOutputError(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
    // */
    // @Override
    // public void setOutputError(final Layer layer, final BackwardVisitor visitor)
    // {
    // final double[] outputs = visitor.getOutputs(layer);
    // final double[] errors = new double[outputs.length];
    //
//        // @formatter:off
//        //Flux.create((final FluxSink<Integer> fluxSink) -> IntStream.range(0, outputs.length).forEach(fluxSink::next))
//        //Flux.fromStream(IntStream.range(0, outputs.length).boxed())
//        Flux.range(0, outputs.length)
//            .parallel(getParallelism()
//            .runOn(getScheduler())
//            .doOnNext(i -> setOutputError(i, outputs, errors, visitor))
//            .sequential()
//            .blockLast()
//            ;
//        // @formatter:on
    //
    // visitor.setErrors(layer, errors);
    // }
}
