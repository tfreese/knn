/**
 * Created: 17.09.2016
 */

package de.freese.knn.net.neuron;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import de.freese.knn.net.layer.Layer;

/**
 * Liste der Neuronen eines {@link Layer}.
 *
 * @author Thomas Freese
 */
public class NeuronList implements Iterable<Neuron>
{
    /**
     * @author Thomas Freese
     */
    private final class NeuronIterator implements Iterator<Neuron>
    {
        /**
         *
         */
        private int position;

        /**
         * Erzeugt eine neue Instanz von {@link NeuronIterator}
         */
        private NeuronIterator()
        {
            super();
        }

        /**
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext()
        {
            return this.position < size();
        }

        /**
         * @see java.util.Iterator#next()
         */
        @Override
        public Neuron next()
        {
            if (hasNext())
            {
                Neuron neuron = get(this.position);
                this.position++;

                return neuron;
            }

            throw new NoSuchElementException("Array index: " + this.position);
        }

        /**
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove()
        {
            // set(this.position, null);
            throw new UnsupportedOperationException("remove() method is not supported");
        }
    }

    /**
     *
     */
    private final int fromIndex;

    /**
     *
     */
    private final Neuron[] neurons;

    /**
     *
     */
    private final int toIndex;

    /**
     * Erstellt ein neues {@link NeuronList} Object.
     *
     * @param size int
     */
    public NeuronList(final int size)
    {
        this(new Neuron[size]);
    }

    /**
     * Erstellt ein neues {@link NeuronList} Object.
     *
     * @param neurons {@link Neuron}[]
     */
    public NeuronList(final Neuron[] neurons)
    {
        this(neurons, 0, neurons.length);
    }

    /**
     * Erstellt ein neues {@link NeuronList} Object.
     *
     * @param neurons {@link Neuron}[]
     * @param fromIndex int; inklusive
     * @param toIndex int; exklusive
     */
    public NeuronList(final Neuron[] neurons, final int fromIndex, final int toIndex)
    {
        super();

        this.neurons = neurons;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    /**
     * @param index int
     * @return {@link Neuron}
     */
    public Neuron get(final int index)
    {
        return this.neurons[index + this.fromIndex];
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<Neuron> iterator()
    {
        return new NeuronIterator();
    }

    /**
     * @return {@link Stream}
     */
    public Stream<Neuron> parallelStream()
    {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * @param index int
     * @param neuron {@link Neuron}
     */
    public void set(final int index, final Neuron neuron)
    {
        this.neurons[index + this.fromIndex] = neuron;
    }

    /**
     * @return int
     */
    public int size()
    {
        return this.toIndex - this.fromIndex;
    }

    /**
     * @see java.lang.Iterable#spliterator()
     */
    @Override
    public Spliterator<Neuron> spliterator()
    {
        return Arrays.spliterator(this.neurons, this.fromIndex, this.toIndex);
    }

    /**
     * @return {@link Stream}
     */
    public Stream<Neuron> stream()
    {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * @param fromIndex int; inklusive
     * @param toIndex int; exklusive
     * @return {@link NeuronList}
     */
    public NeuronList subList(final int fromIndex, final int toIndex)
    {
        return new NeuronList(this.neurons, fromIndex, toIndex);
    }
}
