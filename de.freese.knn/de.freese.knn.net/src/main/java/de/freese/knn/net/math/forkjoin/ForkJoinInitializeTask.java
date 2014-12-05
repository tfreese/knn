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
	 * 
	 */
	 private int from, to;

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

	private ForkJoinInitializeTask(final List<ILayer> layers, final IValueInitializer valueInitializer, int from, int to)
	{
		this(layers, valueInitializer);
		this.from = from;
		this.to = to;
	}
	/**
	 * @see java.util.concurrent.RecursiveAction#compute()
	 */
	@Override
	protected void compute()
	{
		if (to - from == 1)
		{
			AbstractKnnMath.initialize(this.layers.get(to - from), this.valueInitializer);
		}
		else
		{
			int middle = from + to / 2;
			ForkJoinInitializeTask task1 = new ForkJoinInitializeTask(this.layers, this.valueInitializer, from, middle);
			ForkJoinInitializeTask task2 = new ForkJoinInitializeTask(this.layers, this.valueInitializer, from + middle, to);

			invokeAll(task1, task2);
		}
	}
}
