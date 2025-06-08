// Created: 04.04.2012
package de.freese.knn.net.math.forkjoin;

import java.io.Serial;
import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;

/**
 * Initialisiert die BIAS-Gewichte der Neuronen eines Layers.
 *
 * @author Thomas Freese
 */
class ForkJoinInitializeTask extends RecursiveAction// RecursiveTask<double[]>
{
    @Serial
    private static final long serialVersionUID = 687804634087313634L;

    private final int from;
    private final transient Layer[] layers;
    private final transient KnnMathForkJoin math;
    private final int to;
    private final transient ValueInitializer valueInitializer;

    ForkJoinInitializeTask(final KnnMathForkJoin math, final Layer[] layers, final ValueInitializer valueInitializer) {
        this(math, layers, valueInitializer, 0, layers.length);
    }

    private ForkJoinInitializeTask(final KnnMathForkJoin math, final Layer[] layers, final ValueInitializer valueInitializer, final int from, final int to) {
        super();

        this.math = math;
        this.layers = layers;
        this.valueInitializer = valueInitializer;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void compute() {
        if ((to - from) < 20) {
            final Layer[] l = Arrays.copyOfRange(layers, from, to);

            for (Layer layer : l) {
                math.initialize(layer, valueInitializer);
            }
        }
        else {
            final int middle = (from + to) / 2;

            final ForkJoinInitializeTask task1 = new ForkJoinInitializeTask(math, layers, valueInitializer, from, middle);
            final ForkJoinInitializeTask task2 = new ForkJoinInitializeTask(math, layers, valueInitializer, middle, to);

            invokeAll(task1, task2);
        }
    }
}
