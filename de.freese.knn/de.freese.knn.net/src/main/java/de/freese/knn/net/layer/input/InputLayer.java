/**
 * 06.06.2008
 */
package de.freese.knn.net.layer.input;

import java.util.Collections;
import java.util.List;

import de.freese.knn.net.layer.hidden.LinearLayer;
import de.freese.knn.net.neuron.INeuron;
import de.freese.knn.net.neuron.InputNeuron;

/**
 * EingangsLayer.
 * 
 * @author Thomas Freese
 */
public class InputLayer extends LinearLayer
{
	/**
	 * Creates a new {@link InputLayer} object.
	 * 
	 * @param size int
	 */
	public InputLayer(final int size)
	{
		super(size);
	}

	/**
	 * @see de.freese.knn.net.layer.AbstractLayer#createNeurons(java.util.List, int)
	 */
	@Override
	protected List<INeuron> createNeurons(final List<INeuron> neurons, final int size)
	{
		for (int i = 0; i < size; i++)
		{
			INeuron neuron = new InputNeuron(this, i);
			neurons.add(neuron);
		}

		// Keine Nachtraegliche Aenderung mehr moeglich.
		return Collections.unmodifiableList(neurons);
	}
}
