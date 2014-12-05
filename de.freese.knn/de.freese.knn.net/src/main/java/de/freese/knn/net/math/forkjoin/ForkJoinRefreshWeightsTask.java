/**
 * Created: 04.04.2012
 */

package de.freese.knn.net.math.forkjoin;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.neuron.INeuron;

/**
 * Aktualisiert die Gewichte eines Layers aus den Fehlern und Ausgangswerten des nachfolgenden
 * Layers.
 * 
 * @author Thomas Freese
 */
class ForkJoinRefreshWeightsTask extends RecursiveAction// RecursiveTask<double[]>
{
	/**
	 *
	 */
	private static final long serialVersionUID = 7164427207684076313L;

	/**
	 * 
	 */
	private final double[][] deltaWeights;

	/**
	 * 
	 */
	private final double[] leftOutputs;

	/**
	 * 
	 */
	private final double momentum;

	/**
	 * 
	 */
	private final List<INeuron> neurons;

	/**
	 * 
	 */
	private final double[] rightErrors;

	/**
	 * 
	 */
	private final double teachFactor;
	
	/**
	 * 
	 */
	 private int from, to;

	/**
	 * Erstellt ein neues {@link ForkJoinRefreshWeightsTask} Object.
	 * 
	 * @param neurons {@link List}
	 * @param teachFactor double
	 * @param momentum double
	 * @param leftOutputs double[]
	 * @param deltaWeights double[]
	 * @param rightErrors double[]
	 */
	ForkJoinRefreshWeightsTask(final List<INeuron> neurons, final double teachFactor,
			final double momentum, final double[] leftOutputs, final double[][] deltaWeights,
			final double[] rightErrors)
	{
		super();

		this.neurons = neurons;
		this.teachFactor = teachFactor;
		this.momentum = momentum;
		this.leftOutputs = leftOutputs;
		this.deltaWeights = deltaWeights;
		this.rightErrors = rightErrors;
	}

	/**
	 * Erstellt ein neues {@link ForkJoinRefreshWeightsTask} Object.
	 * 
	 * @param neurons {@link List}
	 * @param teachFactor double
	 * @param momentum double
	 * @param leftOutputs double[]
	 * @param deltaWeights double[]
	 * @param rightErrors double[]
	 * @param from int
	 * @param to int
	 */
	private ForkJoinRefreshWeightsTask(final List<INeuron> neurons, final double teachFactor,
			final double momentum, final double[] leftOutputs, final double[][] deltaWeights,
			final double[] rightErrors, final int from, final int to)
	{
		this(neurons, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors);
		this.from = from;
		this.to = to;
	}

	/**
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute()
	{
		if (to - from < 20)
		{
			List<INeuron> neurons = new ArrauList<INeuron>();
			for (int i = from; i < to; i++) {
				neurons.add(this.neurons.get(i));
			}
			AbstractKnnMath.refreshLayerWeights(neurons, this.teachFactor, this.momentum,
					this.leftOutputs, this.deltaWeights, this.rightErrors);
		}
		else
		{
			int middle = from + to / 2;
			ForkJoinRefreshWeightsTask task1 =
					new ForkJoinRefreshWeightsTask(this.neurons, this.teachFactor, this.momentum,
							this.leftOutputs, this.deltaWeights, this.rightErrors, from, middle);
			ForkJoinRefreshWeightsTask task2 =
					new ForkJoinRefreshWeightsTask(this.neurons, this.teachFactor, this.momentum,
							this.leftOutputs, this.deltaWeights, this.rightErrors, from + middle, to);

			invokeAll(task1, task2);
		}
	}
}
