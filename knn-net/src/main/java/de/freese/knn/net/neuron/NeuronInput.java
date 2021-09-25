// Created: 17.07.2011
package de.freese.knn.net.neuron;

import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.Layer;

/**
 * Neuron des {@link InputLayer}.<br>
 * InputNeuronen haben keine BIAS Werte.
 *
 * @author Thomas Freese
 */
public class NeuronInput extends NeuronImpl
{
    /**
     * Erstellt ein neues {@link NeuronInput} Object.
     *
     * @param layer {@link Layer}
     * @param layerIndex int
     */
    public NeuronInput(final Layer layer, final int layerIndex)
    {
        super(layer, layerIndex);
    }

    /**
     * @see de.freese.knn.net.neuron.NeuronImpl#getInputBIAS()
     */
    @Override
    public double getInputBIAS()
    {
        return 0.0D;
    }

    /**
     * @see de.freese.knn.net.neuron.NeuronImpl#setInputBIAS(double)
     */
    @Override
    public void setInputBIAS(final double value)
    {
        // Empty
    }
}
