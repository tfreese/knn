// Created: 04.04.2012
package de.freese.knn.net.math.forkjoin;

import java.io.Serial;
import java.util.concurrent.RecursiveAction;

import de.freese.knn.net.neuron.NeuronList;

/**
 * Mathematik für die Eingangsfehler eines Layers.
 *
 * @author Thomas Freese
 */
class ForkJoinBackwardTask extends RecursiveAction// RecursiveTask<double[]>
{
    @Serial
    private static final long serialVersionUID = 5074316140736114438L;

    private final double[] errors;
    private final int from;
    private final double[] layerErrors;
    private final transient KnnMathForkJoin math;
    private final transient NeuronList neurons;
    private final int to;

    ForkJoinBackwardTask(final KnnMathForkJoin math, final NeuronList neurons, final double[] errors, final double[] layerErrors) {
        this(math, neurons, errors, layerErrors, 0, neurons.size());
    }

    private ForkJoinBackwardTask(final KnnMathForkJoin math, final NeuronList neurons, final double[] errors, final double[] layerErrors, final int from, final int to) {
        super();

        this.math = math;
        this.neurons = neurons;
        this.errors = errors;
        this.layerErrors = layerErrors;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void compute() {
        if ((to - from) < 20) {
            final NeuronList n = neurons.subList(from, to);

            n.forEach(neuron -> math.backward(neuron, errors, layerErrors));
        }
        else {
            final int middle = (from + to) / 2;

            final ForkJoinBackwardTask task1 = new ForkJoinBackwardTask(math, neurons, errors, layerErrors, from, middle);
            final ForkJoinBackwardTask task2 = new ForkJoinBackwardTask(math, neurons, errors, layerErrors, middle, to);

            invokeAll(task1, task2);
        }
    }
}
