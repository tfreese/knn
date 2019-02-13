/**
 * Created: 02.10.2011
 */
package de.freese.knn.net.math.executor;

import java.util.concurrent.CountDownLatch;

import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;

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
    private final Layer layer;

    /**
     *
     */
    private final ValueInitializer valueInitializer;

    /**
     * Erstellt ein neues {@link InitializeTask} Object.
     *
     * @param latch {@link CountDownLatch}
     * @param valueInitializer {@link ValueInitializer}
     * @param layer {@link Layer}
     */
    InitializeTask(final CountDownLatch latch, final ValueInitializer valueInitializer, final Layer layer)
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
        AbstractKnnMath.initialize(this.layer, this.valueInitializer);

        this.latch.countDown();
    }
}
