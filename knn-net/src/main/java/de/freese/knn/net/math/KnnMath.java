// Created: 04.04.2012
package de.freese.knn.net.math;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet}.
 *
 * @author Thomas Freese
 */
public interface KnnMath
{
    /**
     * Mathematik für die Eingangsfehler eines Layers.
     */
    void backward(final Layer layer, final BackwardVisitor visitor);

    default void close()
    {
        // Empty
    }

    /**
     * Mathematik für die Ausgangswerte eines Layers.
     */
    void forward(final Layer layer, final ForwardVisitor visitor);

    /**
     * Liefert den aktuellen Netzfehler.
     */
    double getNetError(final double[] outputs, final double[] outputTargets);

    /**
     * Initialisiert die BIAS-Gewichte der Neuronen eines Layers.
     */
    void initialize(final ValueInitializer valueInitializer, final Layer[] layers);

    /**
     * Aktualisiert die Gewichte eines Layers aus den Fehlern und Ausgangswerten des nachfolgenden Layers.
     *
     * @param momentum double, Anteil der vorherigen Gewichtsveränderung
     */
    void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum, final BackwardVisitor visitor);

    /**
     * Liefert den Ausgabefehler nach dem Gradientenabstiegsverfahren.
     */
    void setOutputError(final Layer layer, final BackwardVisitor visitor);
}
