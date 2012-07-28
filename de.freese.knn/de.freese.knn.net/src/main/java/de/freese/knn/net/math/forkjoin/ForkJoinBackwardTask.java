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

	/**
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute()
	{
		if (this.neurons.size() < 20)
		{
			AbstractKnnMath.backward(this.neurons, this.errors, this.layerErrors);
		}
		else
		{
			int middle = this.neurons.size() / 2;
			List<INeuron> list1 = this.neurons.subList(0, middle);
			List<INeuron> list2 = this.neurons.subList(middle, this.neurons.size());

			ForkJoinBackwardTask task1 =
					new ForkJoinBackwardTask(list1, this.errors, this.layerErrors);
			ForkJoinBackwardTask task2 =
					new ForkJoinBackwardTask(list2, this.errors, this.layerErrors);

			invokeAll(task1, task2);
		}
	}
}
