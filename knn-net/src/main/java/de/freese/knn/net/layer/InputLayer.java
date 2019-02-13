/**
 * 06.06.2008
 */
package de.freese.knn.net.layer;

import de.freese.knn.net.function.FunctionLinear;
import de.freese.knn.net.neuron.NeuronInput;
import de.freese.knn.net.neuron.NeuronList;

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
        super(size, new FunctionLinear());
    }

    /**
     * @see de.freese.knn.net.layer.AbstractLayer#createNeurons(de.freese.knn.net.neuron.NeuronList)
     */
    @Override
    protected void createNeurons(final NeuronList neurons)
    {
        for (int i = 0; i < neurons.size(); i++)
        {
            neurons.set(i, new NeuronInput(this, i));
        }
    }
}
