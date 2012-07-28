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
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute()
	{
		if (this.neurons.size() < 20)
		{
			AbstractKnnMath.refreshLayerWeights(this.neurons, this.teachFactor, this.momentum,
					this.leftOutputs, this.deltaWeights, this.rightErrors);
		}
		else
		{
			int middle = this.neurons.size() / 2;
			List<INeuron> list1 = this.neurons.subList(0, middle);
			List<INeuron> list2 = this.neurons.subList(middle, this.neurons.size());

			ForkJoinRefreshWeightsTask task1 =
					new ForkJoinRefreshWeightsTask(list1, this.teachFactor, this.momentum,
							this.leftOutputs, this.deltaWeights, this.rightErrors);
			ForkJoinRefreshWeightsTask task2 =
					new ForkJoinRefreshWeightsTask(list2, this.teachFactor, this.momentum,
							this.leftOutputs, this.deltaWeights, this.rightErrors);

			invokeAll(task1, task2);
		}
	}
}
