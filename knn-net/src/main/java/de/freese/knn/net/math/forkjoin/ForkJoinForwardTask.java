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
    private final transient KnnMathForkJoin math;
    private final transient NeuronList neurons;
    private final double[] outputs;
    private final int to;

    ForkJoinForwardTask(final KnnMathForkJoin math, final NeuronList neurons, final double[] inputs, final double[] outputs) {
        this(math, neurons, inputs, outputs, 0, neurons.size());
    }

    private ForkJoinForwardTask(final KnnMathForkJoin math, final NeuronList neurons, final double[] inputs, final double[] outputs, final int from, final int to) {
        super();

        this.math = math;
        this.neurons = neurons;
        this.inputs = inputs;
        this.outputs = outputs;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void compute() {
        if ((to - from) < 20) {
            final NeuronList n = neurons.subList(from, to);

            n.forEach(neuron -> math.forward(neuron, inputs, outputs));
        }
        else {
            final int middle = (from + to) / 2;

            final ForkJoinForwardTask task1 = new ForkJoinForwardTask(math, neurons, inputs, outputs, from, middle);
            final ForkJoinForwardTask task2 = new ForkJoinForwardTask(math, neurons, inputs, outputs, middle, to);

            invokeAll(task1, task2);
        }
    }
}
