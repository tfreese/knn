/**
 * Created: 02.10.2011
 */

package de.freese.knn.net.math.executor;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.IValueInitializer;

/**
 * Initialisiert die BIAS-Gewichte der Neuronen eines Layers.
 * 
 * @author Thomas Freese
 */
class InitializeTask implements Runnable
{
	/**
	 * 
	 */
	private final CountDownLatch latch;

	/**
	 * 
	 */
	private final ILayer layer;

	/**
	 * 
	 */
	private final IValueInitializer valueInitializer;

	/**
	 * Erstellt ein neues {@link InitializeTask} Object.
	 * 
	 * @param latch {@link CountDownLatch}
	 * @param valueInitializer {@link IValueInitializer}
	 * @param layer {@link ILayer}
	 */
	InitializeTask(final CountDownLatch latch, final IValueInitializer valueInitializer,
			final ILayer layer)
	{
		super();

		this.latch = latch;
		this.valueInitializer = valueInitializer;
		this.layer = layer;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		AbstractKnnMath.initialize(Collections.singletonList(this.layer), this.valueInitializer);

		this.latch.countDown();
	}
}