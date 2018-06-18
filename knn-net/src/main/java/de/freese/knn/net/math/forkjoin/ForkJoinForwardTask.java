/**
 * Created: 04.04.2012
 */
package de.freese.knn.net.math.forkjoin;

import java.util.concurrent.RecursiveAction;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.neuron.NeuronList;

/**
 * Mathematik für die Ausgangswerte eines Layers.
 *
 * @author Thomas Freese
 */
class ForkJoinForwardTask extends RecursiveAction// RecursiveTask<double[]>
{
    /**
     *
     */
    private static final long serialVersionUID = 4449740515431715497L;

    /**
     *
     */
    private int from = 0;

    /**
     *
     */
    private final double[] inputs;

    /**
     *
     */
    private final NeuronList neurons;

    /**
     *
     */
    private final double[] outputs;

    /**
     *
     */
    private int to = 0;

    /**
     * Erstellt ein neues {@link ForkJoinForwardTask} Object.
     *
     * @param neurons {@link NeuronList}
     * @param inputs double[]
     * @param outputs double[]
     */
    ForkJoinForwardTask(final NeuronList neurons, final double[] inputs, final double[] outputs)
    {
        this(neurons, inputs, outputs, 0, neurons.size());
    }

    /**
     * Erstellt ein neues {@link ForkJoinForwardTask} Object.
     *
     * @param neurons {@link NeuronList}
     * @param inputs double[]
     * @param outputs double[]
     * @param from int
     * @param to int
     */
    private ForkJoinForwardTask(final NeuronList neurons, final double[] inputs, final double[] outputs, final int from, final int to)
    {
        super();

        this.neurons = neurons;
        this.inputs = inputs;
        this.outputs = outputs;
        this.from = from;
        this.to = to;
    }

    /**
     * @see java.util.concurrent.RecursiveAction#compute()
     */
    @Override
    protected void compute()
    {
        if ((this.to - this.from) < 20)
        {
            NeuronList n = this.neurons.subList(this.from, this.to);

            n.forEach(neuron -> AbstractKnnMath.forward(neuron, this.inputs, this.outputs));
        }
        else
        {
            int middle = (this.from + this.to) / 2;

            ForkJoinForwardTask task1 = new ForkJoinForwardTask(this.neurons, this.inputs, this.outputs, this.from, middle);
            ForkJoinForwardTask task2 = new ForkJoinForwardTask(this.neurons, this.inputs, this.outputs, middle, this.to);

            invokeAll(task1, task2);
        }
    }
}
