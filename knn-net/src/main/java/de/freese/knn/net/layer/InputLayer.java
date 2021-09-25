// Created: 06.06.2008
package de.freese.knn.net.layer;

import de.freese.knn.net.function.FunctionLinear;
import de.freese.knn.net.neuron.NeuronInput;

/**
 * EingangsLayer.
 *
 * @author Thomas Freese
 */
public class InputLayer extends AbstractLayer
{
    /**
     * Creates a new {@link InputLayer} object.
     *
     * @param size int
     */
    public InputLayer(final int size)
    {
        super(size, new FunctionLinear(), NeuronInput::new);
    }
}
