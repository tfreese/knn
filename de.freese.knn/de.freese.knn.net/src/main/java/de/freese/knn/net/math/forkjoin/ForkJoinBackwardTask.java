/**
 * Created: 04.04.2012
 */

package de.freese.knn.net.math.forkjoin;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.neuron.INeuron;

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
	private final List<INeuron> neurons;

	/**
	 * 
	 */
	private final double[] errors;

	/**
	 * 
	 */
	private final double[] layerErrors;

	/**
	 * 
	 */
	private int from, to;

	/**
	 * Erstellt ein neues {@link ForkJoinBackwardTask} Object.
	 * 
	 * @param neurons {@link List}
	 * @param errors double[]
	 * @param layerErrors double[]
	 */
	ForkJoinBackwardTask(final List<INeuron> neurons, final double[] errors,
			final double[] layerErrors)
	{
		super();

		this.neurons = neurons;
		this.errors = errors;
		this.layerErrors = layerErrors;
	}


	private ForkJoinBackwardTask(final List<INeuron> neurons, final double[] errors,
			final double[] layerErrors, int from, int to)
	{
		this(neurons, errors, layerErrors);
		this.from = from;
		this.to = to;
	}

	/**
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute()
	{
		if ((to - from) < 20)
		{
			List<INeuron> neurons = new ArrauList<INeuron>();
			for (int i = from; i < to; i++) {
				neurons.add(this.neurons.get(i));
			}
			AbstractKnnMath.backward(neurons, this.errors, this.layerErrors);
		}
		else
		{
			int middle = from + to / 2;
			ForkJoinBackwardTask task1 =
					new ForkJoinBackwardTask(this.neurons, this.errors, this.layerErrors, from, middle);
			ForkJoinBackwardTask task2 =
					new ForkJoinBackwardTask(this.neurons, this.errors, this.layerErrors, from + middle, to);

			invokeAll(task1, task2);
		}
	}
}
