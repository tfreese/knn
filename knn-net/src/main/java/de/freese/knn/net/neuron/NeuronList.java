/**
 * Created: 17.09.2016
 */

package de.freese.knn.net.neuron;

import de.freese.base.core.collection.stream.SplitableArray;
import de.freese.knn.net.layer.ILayer;

/**
 * Liste der Neuronen eines {@link ILayer}.
 *
 * @author Thomas Freese
 */
public class NeuronList extends SplitableArray<INeuron>
{
    /**
     * Erstellt ein neues {@link NeuronList} Object.
     *
     * @param array {@link INeuron}[]
     */
    public NeuronList(final INeuron[] array)
    {
        super(array);
    }

    /**
     * Erstellt ein neues {@link NeuronList} Object.
     *
     * @param array {@link INeuron}[]
     * @param indexStart int
     * @param indexEnd int
     */
    public NeuronList(final INeuron[] array, final int indexStart, final int indexEnd)
    {
        super(array, indexStart, indexEnd);
    }

    /**
     * @see de.freese.base.core.collection.stream.SplitableArray#subList(int, int)
     */
    @Override
    public NeuronList subList(final int fromIndex, final int toIndex)
    {
        return new NeuronList(getArray(), fromIndex, Math.min(getArray().length - 1, toIndex));
    }
}
