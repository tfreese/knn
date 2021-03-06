/**
 * Created: 04.04.2012
 */
package de.freese.knn.net.math.forkjoin;

import java.util.concurrent.RecursiveAction;
import de.freese.knn.net.neuron.NeuronList;

/**
 * Mathematik für die Eingangsfehler eines Layers.
 *
 * @author Thomas Freese
 */
class ForkJoinBackwardTask extends RecursiveAction// RecursiveTask<double[]>
{
    /**
     *
     */
    private static final long serialVersionUID = 5074316140736114438L;

    /**
     *
     */
    private final double[] errors;

    /**
     *
     */
    private int from;

    /**
     *
     */
    private final double[] layerErrors;

    /**
     *
     */
    private final KnnMathForkJoin math;

    /**
     *
     */
    private final NeuronList neurons;

    /**
     *
     */
    private int to;

    /**
     * Erstellt ein neues {@link ForkJoinBackwardTask} Object.
     *
     * @param math {@link KnnMathForkJoin}
     * @param neurons {@link NeuronList}
     * @param errors double[]
     * @param layerErrors double[]
     */
    ForkJoinBackwardTask(final KnnMathForkJoin math, final NeuronList neurons, final double[] errors, final double[] layerErrors)
    {
        this(math, neurons, errors, layerErrors, 0, neurons.size());
    }

    /**
     * Erstellt ein neues {@link ForkJoinBackwardTask} Object.
     *
     * @param math {@link KnnMathForkJoin}
     * @param neurons {@link NeuronList}
     * @param errors double[]
     * @param layerErrors double[]
     * @param from int
     * @param to int
     */
    private ForkJoinBackwardTask(final KnnMathForkJoin math, final NeuronList neurons, final double[] errors, final double[] layerErrors, final int from,
            final int to)
    {
        super();

        this.math = math;
        this.neurons = neurons;
        this.errors = errors;
        this.layerErrors = layerErrors;
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

            n.forEach(neuron -> this.math.backward(neuron, this.errors, this.layerErrors));
        }
        else
        {
            int middle = (this.from + this.to) / 2;

            ForkJoinBackwardTask task1 = new ForkJoinBackwardTask(this.math, this.neurons, this.errors, this.layerErrors, this.from, middle);
            ForkJoinBackwardTask task2 = new ForkJoinBackwardTask(this.math, this.neurons, this.errors, this.layerErrors, middle, this.to);

            invokeAll(task1, task2);
        }
    }
}
