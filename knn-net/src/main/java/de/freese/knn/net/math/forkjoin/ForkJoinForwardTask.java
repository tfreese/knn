// Created: 04.04.2012
package de.freese.knn.net.math.forkjoin;

import java.io.Serial;
import java.util.concurrent.RecursiveAction;

import de.freese.knn.net.neuron.NeuronList;

/**
 * Mathematik für die Ausgangswerte eines Layers.
 *
 * @author Thomas Freese
 */
class ForkJoinForwardTask extends RecursiveAction// RecursiveTask<double[]>
{
    @Serial
    private static final long serialVersionUID = 4449740515431715497L;

    private final int from;

    private final double[] inputs;

    private final KnnMathForkJoin math;

    private final NeuronList neurons;

    private final double[] outputs;

    private final int to;

    ForkJoinForwardTask(final KnnMathForkJoin math, final NeuronList neurons, final double[] inputs, final double[] outputs)
    {
        this(math, neurons, inputs, outputs, 0, neurons.size());
    }

    private ForkJoinForwardTask(final KnnMathForkJoin math, final NeuronList neurons, final double[] inputs, final double[] outputs, final int from,
                                final int to)
    {
        super();

        this.math = math;
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

            n.forEach(neuron -> this.math.forward(neuron, this.inputs, this.outputs));
        }
        else
        {
            int middle = (this.from + this.to) / 2;

            ForkJoinForwardTask task1 = new ForkJoinForwardTask(this.math, this.neurons, this.inputs, this.outputs, this.from, middle);
            ForkJoinForwardTask task2 = new ForkJoinForwardTask(this.math, this.neurons, this.inputs, this.outputs, middle, this.to);

            invokeAll(task1, task2);
        }
    }
}
