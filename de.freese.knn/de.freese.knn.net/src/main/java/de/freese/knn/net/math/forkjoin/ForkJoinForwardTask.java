/**
 * Created: 04.04.2012
 */

package de.freese.knn.net.math.forkjoin;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.neuron.INeuron;

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
	private final List<INeuron> neurons;

	/**
	 * 
	 */
	private final double[] inputs;

	/**
	 * 
	 */
	private final double[] outputs;

	/**
	 * Erstellt ein neues {@link ForkJoinForwardTask} Object.
	 * 
	 * @param neurons {@link List}
	 * @param inputs double[]
	 * @param outputs double[]
	 */
	ForkJoinForwardTask(final List<INeuron> neurons, final double[] inputs, final double[] outputs)
	{
		super();

		this.neurons = neurons;
		this.inputs = inputs;
		this.outputs = outputs;
	}

	/**
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute()
	{
		if (this.neurons.size() < 20)
		{
			AbstractKnnMath.forward(this.neurons, this.inputs, this.outputs);
		}
		else
		{
			int middle = this.neurons.size() / 2;
			List<INeuron> list1 = this.neurons.subList(0, middle);
			List<INeuron> list2 = this.neurons.subList(middle, this.neurons.size());

			ForkJoinForwardTask task1 = new ForkJoinForwardTask(list1, this.inputs, this.outputs);
			ForkJoinForwardTask task2 = new ForkJoinForwardTask(list2, this.inputs, this.outputs);

			invokeAll(task1, task2);
		}
	}
}
