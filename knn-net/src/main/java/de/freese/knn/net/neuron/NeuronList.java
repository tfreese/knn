/**
 * Created: 17.09.2016
 */

package de.freese.knn.net.neuron;

import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.util.stream.DefaultSplitableArray;

/**
 * Liste der Neuronen eines {@link Layer}.
 *
 * @author Thomas Freese
 */
public class NeuronList extends DefaultSplitableArray<Neuron>
{
    /**
     * Erstellt ein neues {@link NeuronList} Object.
     *
     * @param array {@link Neuron}[]
     */
    public NeuronList(final Neuron[] array)
    {
        super(array);
    }

    /**
     * Erstellt ein neues {@link NeuronList} Object.
     *
     * @param array {@link Neuron}[]
     * @param indexStart int
     * @param indexEnd int
     */
    public NeuronList(final Neuron[] array, final int indexStart, final int indexEnd)
    {
        super(array, indexStart, indexEnd);
    }

    /**
     * @see de.freese.knn.net.util.stream.DefaultSplitableArray#subList(int, int)
     */
    @Override
    public NeuronList subList(final int fromIndex, final int toIndex)
    {
        return new NeuronList(getArray(), fromIndex, Math.min(getArray().length - 1, toIndex));
    }
}
