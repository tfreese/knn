/**
 * Created: 12.06.2011
 */

package de.freese.knn.net.visitor;

import java.util.List;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;

/**
 * Durchläuft vorwärts das Netz und sammelt die Outputs der Layer ein.
 * 
 * @author Thomas Freese
 */
public class ForwardVisitor extends AbstractKnnVisitor
{
	/**
	 * 
	 */
	private double[] inputs = null;

	/**
	 * false = nur die aktuellen Outputs werden gespeichert.
	 */
	private final boolean trainingMode;

	/**
	 * Erstellt ein neues {@link ForwardVisitor} Object.
	 * 
	 * @param trainingMode boolean, false = nur die aktuellen Outputs werden gespeichert
	 */
	public ForwardVisitor(final boolean trainingMode)
	{
		super();

		this.trainingMode = trainingMode;
	}

	/**
	 * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitKNN(de.freese.knn.net.NeuralNet)
	 */
	@Override
	protected void visitKNN(final NeuralNet knn)
	{
		super.visitKNN(knn);

		List<ILayer> layers = knn.getLayer();

		// Vorwärts
		visitIterable(layers);
	}

	/**
	 * Setzt die Daten des Input-Layers.
	 * 
	 * @param inputs double[]
	 */
	public void setInputs(final double[] inputs)
	{
		this.inputs = inputs;
	}

	/**
	 * Setzt die Output-Daten des Layers.
	 * 
	 * @param layer {@link ILayer}
	 * @param outputs double[]
	 */
	public void setOutputs(final ILayer layer, final double[] outputs)
	{
		if (this.trainingMode)
		{
			getValues().put(layer, outputs);
		}

		// Aktuelle Outputs merken
		getValues().put(null, outputs);
	}

	/**
	 * Liefert die Output-Daten des Layers.
	 * 
	 * @param layer {@link ILayer}
	 * @return double[]
	 */
	double[] getOutputs(final ILayer layer)
	{
		ILayer key = this.trainingMode ? layer : null;

		return getValues().get(key);
	}

	/**
	 * Liefert die Output-Daten des letzten Layers.
	 * 
	 * @return double[]
	 */
	public double[] getLastOutputs()
	{
		return getValues().get(null);
	}

	/**
	 * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitHiddenLayer(de.freese.knn.net.layer.ILayer)
	 */
	@Override
	protected void visitHiddenLayer(final ILayer layer)
	{
		getMath().forward(layer, this);
	}

	/**
	 * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitInputLayer(de.freese.knn.net.layer.input.InputLayer)
	 */
	@Override
	protected void visitInputLayer(final InputLayer layer)
	{
		setOutputs(layer, this.inputs);
	}

	/**
	 * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitOutputLayer(de.freese.knn.net.layer.output.OutputLayer)
	 */
	@Override
	protected void visitOutputLayer(final OutputLayer layer)
	{
		getMath().forward(layer, this);
	}
}
