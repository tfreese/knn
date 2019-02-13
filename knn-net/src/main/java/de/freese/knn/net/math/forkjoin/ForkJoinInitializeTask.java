/**
 * Created: 04.04.2012
 */
package de.freese.knn.net.math.forkjoin;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.ValueInitializer;

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
    private int from = 0;

    /**
     *
     */
    private final Layer[] layers;

    /**
     *
     */
    private int to = 0;

    /**
     *
     */
    private final ValueInitializer valueInitializer;

    /**
     * Erstellt ein neues {@link ForkJoinInitializeTask} Object.
     *
     * @param layers {@link Layer}[]
     * @param valueInitializer {@link ValueInitializer}
     */
    ForkJoinInitializeTask(final Layer[] layers, final ValueInitializer valueInitializer)
    {
        this(layers, valueInitializer, 0, layers.length);
    }

    /**
     * Erstellt ein neues {@link ForkJoinInitializeTask} Object.
     *
     * @param layers {@link Layer}[]
     * @param valueInitializer {@link ValueInitializer}
     * @param from int
     * @param to int
     */
    private ForkJoinInitializeTask(final Layer[] layers, final ValueInitializer valueInitializer, final int from, final int to)
    {
        super();

        this.layers = layers;
        this.valueInitializer = valueInitializer;
        this.from = from;
        this.to = to;
    }

    /**
     * @see java.util.concurrent.RecursiveAction#compute()
     */
    @Override
    protected void compute()
    {
        if ((this.to - this.from) < 2)
        {
            Layer[] l = Arrays.copyOfRange(this.layers, this.from, this.to);

            for (Layer layer : l)
            {
                AbstractKnnMath.initialize(layer, this.valueInitializer);
            }
        }
        else
        {
            int middle = (this.from + this.to) / 2;

            ForkJoinInitializeTask task1 = new ForkJoinInitializeTask(this.layers, this.valueInitializer, this.from, middle);
            ForkJoinInitializeTask task2 = new ForkJoinInitializeTask(this.layers, this.valueInitializer, middle, this.to);

            invokeAll(task1, task2);
        }
    }
}
