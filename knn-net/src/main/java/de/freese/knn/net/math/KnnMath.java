/**
 * Created: 04.04.2012
 */
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
     *
     * @param layer {@link Layer}
     * @param visitor {@link BackwardVisitor}
     */
    public void backward(final Layer layer, final BackwardVisitor visitor);

    /**
     * Mathematik für die Ausgangswerte eines Layers.
     *
     * @param layer {@link Layer}
     * @param visitor {@link ForwardVisitor}
     */
    public void forward(final Layer layer, final ForwardVisitor visitor);

    /**
     * Liefert den aktuellen Netzfehler.
     *
     * @param outputs double[]
     * @param outputTargets double[]
     * @return double
     */
    public double getNetError(final double[] outputs, final double[] outputTargets);

    /**
     * Initialisiert die BIAS-Gewichte der Neuronen eines Layers.
     *
     * @param valueInitializer {@link ValueInitializer}
     * @param layers {@link Layer}[]
     */
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers);

    /**
     * Aktualisiert die Gewichte eines Layers aus den Fehlern und Ausgangswerten des nachfolgenden Layers.
     *
     * @param leftLayer {@link Layer}
     * @param rightLayer {@link Layer}
     * @param teachFactor double, Lernfaktor
     * @param momentum double, Anteil der vorherigen Gewichtsveränderung
     * @param visitor {@link BackwardVisitor}
     */
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum,
            final BackwardVisitor visitor);

    /**
     * Liefert den Ausgabefehler nach dem Gradientenabstiegsverfahren.
     *
     * @param layer {@link Layer}
     * @param visitor {@link BackwardVisitor}
     */
    public void setOutputError(final Layer layer, final BackwardVisitor visitor);
}
