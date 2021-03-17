/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math.forkjoin;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.Neuron;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem ForkJoin-Framework.
 *
 * @author Thomas Freese
 */
public final class KnnMathForkJoin extends AbstractKnnMath
{
    /**
     *
     */
    private final ForkJoinPool forkJoinPool;

    /**
     * Erstellt ein neues {@link KnnMathForkJoin} Object.
     *
     * @param forkJoinPool {@link ForkJoinPool}
     */
    public KnnMathForkJoin(final ForkJoinPool forkJoinPool)
    {
        super(1);

        this.forkJoinPool = Objects.requireNonNull(forkJoinPool, "forkJoinPool required");
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#backward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor)
    {
        double[] errors = visitor.getLastErrors();
        double[] layerErrors = new double[layer.getSize()];

        ForkJoinBackwardTask task = new ForkJoinBackwardTask(this, layer.getNeurons(), errors, layerErrors);

        getForkJoinPool().invoke(task);

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see de.freese.knn.net.math.AbstractKnnMath#backward(de.freese.knn.net.neuron.Neuron, double[], double[])
     */
    @Override
    public void backward(final Neuron neuron, final double[] errors, final double[] layerErrors)
    {
        super.backward(neuron, errors, layerErrors);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#forward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor)
    {
        double[] inputs = visitor.getLastOutputs();
        double[] outputs = new double[layer.getSize()];

        ForkJoinForwardTask task = new ForkJoinForwardTask(this, layer.getNeurons(), inputs, outputs);

        getForkJoinPool().invoke(task);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @see de.freese.knn.net.math.AbstractKnnMath#forward(de.freese.knn.net.neuron.Neuron, double[], double[])
     */
    @Override
    public void forward(final Neuron neuron, final double[] inputs, final double[] outputs)
    {
        super.forward(neuron, inputs, outputs);
    }

    /**
     * @return {@link ForkJoinPool}
     */
    private ForkJoinPool getForkJoinPool()
    {
        return this.forkJoinPool;
    }

    /**
     * @see de.freese.knn.net.math.AbstractKnnMath#initialize(de.freese.knn.net.layer.Layer, de.freese.knn.net.matrix.ValueInitializer)
     */
    @Override
    public void initialize(final Layer layer, final ValueInitializer valueInitializer)
    {
        super.initialize(layer, valueInitializer);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        ForkJoinInitializeTask task = new ForkJoinInitializeTask(this, layers, valueInitializer);

        getForkJoinPool().invoke(task);
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

        ForkJoinRefreshWeightsTask task =
                new ForkJoinRefreshWeightsTask(this, leftLayer.getNeurons(), teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);

        getForkJoinPool().invoke(task);
    }

    /**
     * @see de.freese.knn.net.math.AbstractKnnMath#refreshLayerWeights(de.freese.knn.net.neuron.Neuron, double, double, double[], double[][], double[])
     */
    @Override
    public void refreshLayerWeights(final Neuron neuron, final double teachFactor, final double momentum, final double[] leftOutputs,
                                    final double[][] deltaWeights, final double[] rightErrors)
    {
        super.refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);
    }
}
