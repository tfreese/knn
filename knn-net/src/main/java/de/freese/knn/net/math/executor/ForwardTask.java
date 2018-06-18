/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math.executor;

import java.util.concurrent.CountDownLatch;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.neuron.NeuronList;

/**
 * Mathematik für die Ausgangswerte eines Layers.
 *
 * @author Thomas Freese
 */
class ForwardTask implements Runnable
{
    /**
     *
     */
    private final double[] inputs;

    /**
     *
     */
    private final CountDownLatch latch;

    /**
     *
     */
    private final NeuronList neurons;

    /**
     *
     */
    private final double[] outputs;

    /**
     * Erstellt ein neues {@link ForwardTask} Object.
     *
     * @param latch {@link CountDownLatch}
     * @param neurons {@link NeuronList}
     * @param inputs double[]
     * @param outputs double[]
     */
    ForwardTask(final CountDownLatch latch, final NeuronList neurons, final double[] inputs, final double[] outputs)
    {
        super();

        this.latch = latch;
        this.neurons = neurons;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        this.neurons.forEach(neuron -> AbstractKnnMath.forward(neuron, this.inputs, this.outputs));

        this.latch.countDown();
    }
}
