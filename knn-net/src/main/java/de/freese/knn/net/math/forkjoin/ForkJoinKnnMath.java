/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math.forkjoin;

import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.IValueInitializer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem Fork-Join-Framework.
 *
 * @author Thomas Freese
 */
public class ForkJoinKnnMath extends AbstractKnnMath implements AutoCloseable
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
     * Erstellt ein neues {@link ForkJoinKnnMath} Object.
     */
    public ForkJoinKnnMath()
    {
        this(new ForkJoinPool());

        this.createdPool = true;

        // if (getLogger().isDebugEnabled())
        // {
        // int tasks = 1 + (((neurons.size() + 7) >>> 3) / this.processors);
        // getLogger().debug("Layer Size= {}, Calulated Tasks: {}",
        // Integer.valueOf(neurons.size()), Integer.valueOf(tasks));
        // }
    }

    /**
     * Erstellt ein neues {@link ForkJoinKnnMath} Object.
     *
     * @param forkJoinPool {@link ForkJoinPool}
     */
    public ForkJoinKnnMath(final ForkJoinPool forkJoinPool)
    {
        super();

        this.forkJoinPool = Objects.requireNonNull(forkJoinPool, "forkJoinPool required");
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#backward(de.freese.knn.net.layer.ILayer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void backward(final ILayer layer, final BackwardVisitor visitor)
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
            getLogger().info("Shutdown ForkJoinPool");

            this.forkJoinPool.shutdown();

            while (!this.forkJoinPool.isTerminated())
            {
                try
                {
                    this.forkJoinPool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
                }
                catch (InterruptedException ex)
                {
                    getLogger().error(null, ex);
                }
            }
        }
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#forward(de.freese.knn.net.layer.ILayer, de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public void forward(final ILayer layer, final ForwardVisitor visitor)
    {
        double[] inputs = visitor.getLastOutputs();
        double[] outputs = new double[layer.getSize()];

        ForkJoinForwardTask task = new ForkJoinForwardTask(layer.getNeurons(), inputs, outputs);

        this.forkJoinPool.invoke(task);

        visitor.setOutputs(layer, outputs);
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#initialize(de.freese.knn.net.matrix.IValueInitializer, de.freese.knn.net.layer.ILayer[])
     */
    @Override
    public void initialize(final IValueInitializer valueInitializer, final ILayer[] layers)
    {
        ForkJoinInitializeTask task = new ForkJoinInitializeTask(layers, valueInitializer);

        this.forkJoinPool.invoke(task);
    }

    /**
     * @see de.freese.knn.net.math.IKnnMath#refreshLayerWeights(de.freese.knn.net.layer.ILayer, de.freese.knn.net.layer.ILayer, double, double,
     *      de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void refreshLayerWeights(final ILayer leftLayer, final ILayer rightLayer, final double teachFactor, final double momentum,
                                    final BackwardVisitor visitor)
    {
        double[] leftOutputs = visitor.getOutputs(leftLayer);
        double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        double[] rightErrors = visitor.getErrors(rightLayer);

        ForkJoinRefreshWeightsTask task = new ForkJoinRefreshWeightsTask(leftLayer.getNeurons(), teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);

        this.forkJoinPool.invoke(task);
    }
}
