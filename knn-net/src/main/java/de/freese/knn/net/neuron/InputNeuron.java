/**
 * Created: 17.07.2011
 */

package de.freese.knn.net.neuron;

import de.freese.knn.net.layer.input.InputLayer;

/**
 * Neuron des {@link InputLayer}.<br>
 * InputNeuronen haben keine BIAS Werte.
 * 
 * @author Thomas Freese
 */
public class InputNeuron extends Neuron
{
	/**
	 * Erstellt ein neues {@link InputNeuron} Object.
	 * 
	 * @param layer InputLayer
	 * @param layerIndex int
	 */
	public InputNeuron(final InputLayer layer, final int layerIndex)
	{
		super(layer, layerIndex);
	}

	/**
	 * @see de.freese.knn.net.neuron.Neuron#getInputBIAS()
	 */
	@Override
	public double getInputBIAS()
	{
		return 0.0D;
	}

	/**
	 * @see de.freese.knn.net.neuron.Neuron#setInputBIAS(double)
	 */
	@Override
	public void setInputBIAS(final double value)
	{
		// NOOP
	}
}
