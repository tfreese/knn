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
import de.freese.knn.net.utils.KnnUtils;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem Fork-Join-Framework.
 *
 * @author Thomas Freese
 */
public class KnnMathForkJoin extends AbstractKnnMath implements AutoCloseable
{
    /**
     *
     */
    private boolean createdPool = false;

    /**
     *
     */
    private final ForkJoinPool forkJoinPool;

    /**
     * Erstellt ein neues {@link KnnMathForkJoin} Object.
     */
    public KnnMathForkJoin()
    {
        // this(ForkJoinPool.commonPool());
        this(new ForkJoinPool(KnnUtils.DEFAULT_POOL_SIZE));

        this.createdPool = true;

        // if (getLogger().isDebugEnabled())
        // {
        // int tasks = 1 + (((neurons.size() + 7) >>> 3) / this.processors);
        // getLogger().debug("Layer Size= {}, Calulated Tasks: {}",
        // Integer.valueOf(neurons.size()), Integer.valueOf(tasks));
        // }
    }

    /**
     * Erstellt ein neues {@link KnnMathForkJoin} Object.
     *
     * @param forkJoinPool {@link ForkJoinPool}
     */
    public KnnMathForkJoin(final ForkJoinPool forkJoinPool)
    {
        super();

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

        ForkJoinBackwardTask task = new ForkJoinBackwardTask(layer.getNeurons(), errors, layerErrors);

        this.forkJoinPool.invoke(task);

        visitor.setErrors(layer, layerErrors);
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception
    {
        if (this.createdPool)
        {
            KnnUtils.shutdown(this.forkJoinPool, getLogger());
        }
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#forward(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor)
    {
        double[] inputs = visitor.getLastOutputs();
        double[] outputs = new double[layer.getSize()];

        ForkJoinForwardTask task = new ForkJoinForwardTask(layer.getNeurons(), inputs, outputs);

        this.forkJoinPool.invoke(task);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#initialize(de.freese.knn.net.matrix.ValueInitializer, de.freese.knn.net.layer.Layer[])
     */
    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers)
    {
        ForkJoinInitializeTask task = new ForkJoinInitializeTask(layers, valueInitializer);

        this.forkJoinPool.invoke(task);
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

        ForkJoinRefreshWeightsTask task = new ForkJoinRefreshWeightsTask(leftLayer.getNeurons(), teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);

        this.forkJoinPool.invoke(task);
    }
}
