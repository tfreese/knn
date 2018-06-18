/**
 * Created: 17.07.2011
 */

package de.freese.knn.net.neuron;

import de.freese.knn.net.function.IFunction;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;
import de.freese.knn.net.matrix.Matrix;

/**
 * Neuron des Netzes.
 * 
 * @author Thomas Freese
 */
public class Neuron implements INeuron
{
	/**
	 * 
	 */
	private double inputBIAS = 0.0D;

	/**
	 * 
	 */
	private final ILayer layer;

	/**
	 * 
	 */
	private final int layerIndex;

	/**
	 * Erstellt ein neues {@link Neuron} Object.
	 * 
	 * @param layer {@link ILayer}
	 * @param layerIndex int
	 */
	public Neuron(final ILayer layer, final int layerIndex)
	{
		super();

		this.layer = layer;
		this.layerIndex = layerIndex;
	}

	/**
	 * @see de.freese.knn.net.neuron.INeuron#getFunction()
	 */
	@Override
	public IFunction getFunction()
	{
		return this.layer.getFunction();
	}

	/**
	 * @see de.freese.knn.net.neuron.INeuron#getInputBIAS()
	 */
	@Override
	public double getInputBIAS()
	{
		return this.inputBIAS;
	}

	/**
	 * Liefert die Eingangsmatrix des Layers.<br>
	 * Der {@link InputLayer} hat keine Eingangsmatrix !
	 * 
	 * @return {@link Matrix}
	 */
	protected Matrix getInputMatrix()
	{
		return this.layer.getInputMatrix();
	}

	/**
	 * @see de.freese.knn.net.neuron.INeuron#getInputSize()
	 */
	@Override
	public int getInputSize()
	{
		Matrix matrix = getInputMatrix();

		if (matrix != null)
		{
			return matrix.getWeights().length;
		}

		return 0;
	}

	/**
	 * @see de.freese.knn.net.neuron.INeuron#getInputWeight(int)
	 */
	@Override
	public double getInputWeight(final int index)
	{
		Matrix matrix = getInputMatrix();

		if (matrix != null)
		{
			int layerIndex = getLayerIndex();

			return matrix.getWeights()[index][layerIndex];
		}

		return 0.0D;
	}

	/**
	 * @see de.freese.knn.net.neuron.INeuron#getLayerIndex()
	 */
	@Override
	public int getLayerIndex()
	{
		return this.layerIndex;
	}

	/**
	 * Liefert die Ausgangsmatrix des Layers.<br>
	 * Der {@link OutputLayer} hat keine Ausgangsmatrix !
	 * 
	 * @return {@link Matrix}
	 */
	protected Matrix getOutputMatrix()
	{
		return this.layer.getOutputMatrix();
	}

	/**
	 * @see de.freese.knn.net.neuron.INeuron#getOutputSize()
	 */
	@Override
	public int getOutputSize()
	{
		Matrix matrix = getOutputMatrix();

		if (matrix != null)
		{
			return matrix.getWeights()[0].length;
		}

		return 0;
	}

	/**
	 * @see de.freese.knn.net.neuron.INeuron#getOutputWeight(int)
	 */
	@Override
	public double getOutputWeight(final int index)
	{
		Matrix matrix = getOutputMatrix();

		if (matrix != null)
		{
			int layerIndex = getLayerIndex();

			return matrix.getWeights()[layerIndex][index];
		}

		return 0.0D;
	}

	/**
	 * @see de.freese.knn.net.neuron.INeuron#setInputBIAS(double)
	 */
	@Override
	public void setInputBIAS(final double value)
	{
		this.inputBIAS = value;
	}

	/**
	 * @see de.freese.knn.net.neuron.INeuron#setInputWeight(int, double)
	 */
	@Override
	public void setInputWeight(final int index, final double weight)
	{
		Matrix matrix = getInputMatrix();

		if (matrix != null)
		{
			int layerIndex = getLayerIndex();

			matrix.getWeights()[index][layerIndex] = weight;
		}
	}

	/**
	 * @see de.freese.knn.net.neuron.INeuron#setOutputWeight(int, double)
	 */
	@Override
	public void setOutputWeight(final int index, final double weight)
	{
		Matrix matrix = getOutputMatrix();

		if (matrix != null)
		{
			int layerIndex = getLayerIndex();

			matrix.getWeights()[layerIndex][index] = weight;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Neuron: ");
		sb.append("layerIndex=").append(this.layerIndex);
		sb.append("/").append(this.layer.getSize() - 1);

		return sb.toString();
	}
}
