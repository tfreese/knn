/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math.executor;

import java.util.concurrent.CountDownLatch;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.neuron.NeuronList;

/**
 * Aktualisiert die Gewichte eines Layers aus den Fehlern und Ausgangswerten des nachfolgenden Layers.
 *
 * @author Thomas Freese
 */
class RefreshWeightsTask implements Runnable
{
    /**
     *
     */
    private final double[][] deltaWeights;

    /**
     *
     */
    private final CountDownLatch latch;

    /**
     *
     */
    private final double[] leftOutputs;

    /**
     *
     */
    private final double momentum;

    /**
     *
     */
    private final NeuronList neurons;

    /**
     *
     */
    private final double[] rightErrors;

    /**
     *
     */
    private final double teachFactor;

    /**
     * Erstellt ein neues {@link RefreshWeightsTask} Object.
     *
     * @param latch {@link CountDownLatch}
     * @param neurons {@link NeuronList}
     * @param teachFactor double
     * @param momentum double
     * @param leftOutputs double[]
     * @param deltaWeights double[]
     * @param rightErrors double[]
     */
    RefreshWeightsTask(final CountDownLatch latch, final NeuronList neurons, final double teachFactor, final double momentum, final double[] leftOutputs,
            final double[][] deltaWeights, final double[] rightErrors)
    {
        super();

        this.latch = latch;
        this.neurons = neurons;
        this.teachFactor = teachFactor;
        this.momentum = momentum;
        this.leftOutputs = leftOutputs;
        this.deltaWeights = deltaWeights;
        this.rightErrors = rightErrors;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        this.neurons.forEach(
                neuron -> AbstractKnnMath.refreshLayerWeights(neuron, this.teachFactor, this.momentum, this.leftOutputs, this.deltaWeights, this.rightErrors));

        this.latch.countDown();
    }
}
