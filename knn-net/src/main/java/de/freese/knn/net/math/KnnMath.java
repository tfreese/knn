// Created: 04.04.2012
package de.freese.knn.net.math;

import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * @author Thomas Freese
 */
public interface KnnMath {
    /**
     * Mathematik für die Eingangsfehler eines Layers.
     */
    void backward(Layer layer, BackwardVisitor visitor);

    default void close() {
        // Empty
    }

    /**
     * Mathematik für die Ausgangswerte eines Layers.
     */
    void forward(Layer layer, ForwardVisitor visitor);

    /**
     * Liefert den aktuellen Netzfehler.
     */
    double getNetError(double[] outputs, double[] outputTargets);

    /**
     * Initialisiert die BIAS-Gewichte der Neuronen eines Layers.
     */
    void initialize(ValueInitializer valueInitializer, Layer[] layers);

    /**
     * Aktualisiert die Gewichte eines Layers aus den Fehlern und Ausgangswerten des nachfolgenden Layers.
     *
     * @param momentum double, Anteil der vorherigen Gewichtsveränderung
     */
    void refreshLayerWeights(Layer leftLayer, Layer rightLayer, double teachFactor, double momentum, BackwardVisitor visitor);

    /**
     * Liefert den Ausgabefehler nach dem Gradientenabstiegsverfahren.
     */
    void setOutputError(Layer layer, BackwardVisitor visitor);
}
