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
	 * 
	 */
	private int from, to;

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

	ForkJoinForwardTask(final List<INeuron> neurons, final double[] inputs, final double[] outputs, int from, int to)
	{
		this(neurons, inputs, outputs)
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
			
			ForkJoinForwardTask task1 = new ForkJoinForwardTask(this.neurons, this.inputs, this.outputs, from, middle);
			ForkJoinForwardTask task2 = new ForkJoinForwardTask(this.neurons, this.inputs, this.outputs, from + middle, to);

			invokeAll(task1, task2);
		}
	}
}
