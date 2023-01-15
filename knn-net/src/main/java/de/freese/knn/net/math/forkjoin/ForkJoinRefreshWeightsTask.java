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

    ForkJoinRefreshWeightsTask(final KnnMathForkJoin math, final NeuronList neurons, final double teachFactor, final double momentum,
                               final double[] leftOutputs, final double[][] deltaWeights, final double[] rightErrors)
    {
        this(math, neurons, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors, 0, neurons.size());
    }

    private ForkJoinRefreshWeightsTask(final KnnMathForkJoin math, final NeuronList neurons, final double teachFactor, final double momentum,
                                       final double[] leftOutputs, final double[][] deltaWeights, final double[] rightErrors, final int from, final int to)
    {
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

    /**
     * @see java.util.concurrent.RecursiveAction#compute()
     */
    @Override
    protected void compute()
    {
        if ((this.to - this.from) < 20)
        {
            NeuronList n = this.neurons.subList(this.from, this.to);

            n.forEach(neuron -> this.math.refreshLayerWeights(neuron, this.teachFactor, this.momentum, this.leftOutputs, this.deltaWeights, this.rightErrors));
        }
        else
        {
            int middle = (this.from + this.to) / 2;

            ForkJoinRefreshWeightsTask task1 = new ForkJoinRefreshWeightsTask(this.math, this.neurons, this.teachFactor, this.momentum, this.leftOutputs,
                    this.deltaWeights, this.rightErrors, this.from, middle);
            ForkJoinRefreshWeightsTask task2 = new ForkJoinRefreshWeightsTask(this.math, this.neurons, this.teachFactor, this.momentum, this.leftOutputs,
                    this.deltaWeights, this.rightErrors, middle, this.to);

            invokeAll(task1, task2);
        }
    }
}
