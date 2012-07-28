/**
 * Created: 04.04.2012
 */

package de.freese.knn.net.math.forkjoin;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.IValueInitializer;

/**
 * Initialisiert die BIAS-Gewichte der Neuronen eines Layers.
 * 
 * @author Thomas Freese
 */
class ForkJoinInitializeTask extends RecursiveAction// RecursiveTask<double[]>
{
	/**
	 *
	 */
	private static final long serialVersionUID = 687804634087313634L;

	/**
	 * 
	 */
	private final List<ILayer> layers;

	/**
	 * 
	 */
	private final IValueInitializer valueInitializer;

	/**
	 * Erstellt ein neues {@link ForkJoinInitializeTask} Object.
	 * 
	 * @param layers {@link List}
	 * @param valueInitializer {@link IValueInitializer}
	 */
	ForkJoinInitializeTask(final List<ILayer> layers, final IValueInitializer valueInitializer)
	{
		super();

		this.layers = layers;
		this.valueInitializer = valueInitializer;
	}

	/**
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute()
	{
		if (this.layers.size() == 1)
		{
			AbstractKnnMath.initialize(this.layers, this.valueInitializer);
		}
		else
		{
			int middle = this.layers.size() / 2;
			List<ILayer> list1 = this.layers.subList(0, middle);
			List<ILayer> list2 = this.layers.subList(middle, this.layers.size());

			ForkJoinInitializeTask task1 = new ForkJoinInitializeTask(list1, this.valueInitializer);
			ForkJoinInitializeTask task2 = new ForkJoinInitializeTask(list2, this.valueInitializer);

			invokeAll(task1, task2);
		}
	}
}
