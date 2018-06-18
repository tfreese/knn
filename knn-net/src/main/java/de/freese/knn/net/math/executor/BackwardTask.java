/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math.executor;

import java.util.concurrent.CountDownLatch;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.neuron.NeuronList;

/**
 * Mathematik für die Eingangsfehler eines Layers.
 *
 * @author Thomas Freese
 */
class BackwardTask implements Runnable
{
    /**
     *
     */
    private final double[] errors;

    /**
     *
     */
    private final CountDownLatch latch;

    /**
     *
     */
    private final double[] layerErrors;

    /**
     *
     */
    private final NeuronList neurons;

    /**
     * Erstellt ein neues {@link BackwardTask} Object.
     *
     * @param latch {@link CountDownLatch}
     * @param neurons {@link NeuronList}
     * @param errors double[]
     * @param layerErrors double[]
     */
    BackwardTask(final CountDownLatch latch, final NeuronList neurons, final double[] errors, final double[] layerErrors)
    {
        super();

        this.latch = latch;
        this.neurons = neurons;
        this.errors = errors;
        this.layerErrors = layerErrors;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        this.neurons.forEach(neuron -> AbstractKnnMath.backward(neuron, this.errors, this.layerErrors));

        this.latch.countDown();
    }
}
