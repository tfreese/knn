/**
 * Created: 17.07.2011
 */

package de.freese.knn.net.neuron;

import de.freese.knn.net.layer.InputLayer;

/**
 * Neuron des {@link InputLayer}.<br>
 * InputNeuronen haben keine BIAS Werte.
 * 
 * @author Thomas Freese
 */
public class NeuronInput extends NeuronDefault
{
	/**
	 * Erstellt ein neues {@link NeuronInput} Object.
	 * 
	 * @param layer InputLayer
	 * @param layerIndex int
	 */
	public NeuronInput(final InputLayer layer, final int layerIndex)
	{
		super(layer, layerIndex);
	}

	/**
	 * @see de.freese.knn.net.neuron.NeuronDefault#getInputBIAS()
	 */
	@Override
	public double getInputBIAS()
	{
		return 0.0D;
	}

	/**
	 * @see de.freese.knn.net.neuron.NeuronDefault#setInputBIAS(double)
	 */
	@Override
	public void setInputBIAS(final double value)
	{
		// NOOP
	}
}
