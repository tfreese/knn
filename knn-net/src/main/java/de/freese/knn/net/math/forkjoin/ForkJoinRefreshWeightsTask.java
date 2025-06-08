// Created: 04.04.2012
package de.freese.knn.net.math.forkjoin;

import java.io.Serial;
import java.util.concurrent.RecursiveAction;

import de.freese.knn.net.neuron.NeuronList;

/**
 * Aktualisiert die Gewichte eines Layers aus den Fehlern und Ausgangswerten des nachfolgenden Layers.
 *
 * @author Thomas Freese
 */
class ForkJoinRefreshWeightsTask extends RecursiveAction// RecursiveTask<double[]>
{
    @Serial
    private static final long serialVersionUID = 7164427207684076313L;

    private final double[][] deltaWeights;
    private final int from;
    private final double[] leftOutputs;
    private final transient KnnMathForkJoin math;
    private final double momentum;
    private final transient NeuronList neurons;
    private final double[] rightErrors;
    private final double teachFactor;
    private final int to;

    //    @SuppressWarnings("java:S107")
    ForkJoinRefreshWeightsTask(final KnnMathForkJoin math, final NeuronList neurons, final double teachFactor, final double momentum, final double[] leftOutputs,
                               final double[][] deltaWeights, final double[] rightErrors) {
        this(math, neurons, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors, 0, neurons.size());
    }

    //    @SuppressWarnings("java:S107")
    private ForkJoinRefreshWeightsTask(final KnnMathForkJoin math, final NeuronList neurons, final double teachFactor, final double momentum, final double[] leftOutputs,
                                       final double[][] deltaWeights, final double[] rightErrors, final int from, final int to) {
        super();

        this.math = math;
        this.neurons = neurons;
        this.teachFactor = teachFactor;
        this.momentum = momentum;
        this.leftOutputs = leftOutputs;
        this.deltaWeights = deltaWeights;
        this.rightErrors = rightErrors;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void compute() {
        if ((to - from) < 20) {
            final NeuronList n = neurons.subList(from, to);

            n.forEach(neuron -> math.refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));
        }
        else {
            final int middle = (from + to) / 2;

            final ForkJoinRefreshWeightsTask task1 = new ForkJoinRefreshWeightsTask(math, neurons, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors, from, middle);
            final ForkJoinRefreshWeightsTask task2 = new ForkJoinRefreshWeightsTask(math, neurons, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors, middle, to);

            invokeAll(task1, task2);
        }
    }
}
