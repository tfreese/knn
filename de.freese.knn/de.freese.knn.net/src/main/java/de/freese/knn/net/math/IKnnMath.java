/**
 * Created: 04.04.2012
 */

package de.freese.knn.net.math;

import java.util.List;

import de.freese.base.model.release.IReleaseable;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.matrix.IValueInitializer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet}.
 * 
 * @author Thomas Freese
 */
public interface IKnnMath extends IReleaseable
{
	/**
	 * Mathematik für die Eingangsfehler eines Layers.
	 * 
	 * @param layer {@link ILayer}
	 * @param visitor {@link BackwardVisitor}
	 */
	public void backward(final ILayer layer, final BackwardVisitor visitor);

	/**
	 * Mathematik für die Ausgangswerte eines Layers.
	 * 
	 * @param layer {@link ILayer}
	 * @param visitor {@link ForwardVisitor}
	 */
	public void forward(final ILayer layer, final ForwardVisitor visitor);

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
	 * @param valueInitializer {@link IValueInitializer}
	 * @param layers {@link List}
	 */
	public void initialize(final IValueInitializer valueInitializer, final List<ILayer> layers);

	/**
	 * Aktualisiert die Gewichte eines Layers aus den Fehlern und Ausgangswerten des nachfolgenden
	 * Layers.
	 * 
	 * @param leftLayer {@link ILayer}
	 * @param rightLayer {@link ILayer}
	 * @param teachFactor double, Lernfaktor
	 * @param momentum double, Anteil der vorherigen Gewichtsveränderung
	 * @param visitor {@link BackwardVisitor}
	 */
	public void refreshLayerWeights(final ILayer leftLayer, final ILayer rightLayer,
									final double teachFactor, final double momentum,
									final BackwardVisitor visitor);

	/**
	 * Liefert den Ausgabefehler.
	 * 
	 * @param layer {@link ILayer}
	 * @param visitor {@link BackwardVisitor}
	 */
	public void setOutputError(final ILayer layer, final BackwardVisitor visitor);
}