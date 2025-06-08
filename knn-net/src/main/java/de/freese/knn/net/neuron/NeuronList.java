// Created: 17.09.2016
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
public class NeuronList implements Iterable<Neuron> {
    /**
     * @author Thomas Freese
     */
    private final class NeuronIterator implements Iterator<Neuron> {
        private int position;

        @Override
        public boolean hasNext() {
            return position < size();
        }

        @Override
        public Neuron next() {
            if (hasNext()) {
                final Neuron neuron = get(position);
                position++;

                return neuron;
            }

            throw new NoSuchElementException("Array index: " + position);
        }

        @Override
        public void remove() {
            // set(position, null);
            throw new UnsupportedOperationException("remove() method is not supported");
        }
    }

    private final int fromIndex;
    private final Neuron[] neurons;
    private final int toIndex;

    public NeuronList(final Neuron[] neurons) {
        this(neurons, 0, neurons.length);
    }

    /**
     * @param fromIndex int; inklusive
     * @param toIndex int; exklusive
     */
    private NeuronList(final Neuron[] neurons, final int fromIndex, final int toIndex) {
        super();

        this.neurons = neurons;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public Neuron get(final int index) {
        return neurons[index + fromIndex];
    }

    @Override
    public Iterator<Neuron> iterator() {
        return new NeuronIterator();
    }

    public Stream<Neuron> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    public int size() {
        return toIndex - fromIndex;
    }

    @Override
    public Spliterator<Neuron> spliterator() {
        return Arrays.spliterator(neurons, fromIndex, toIndex);
    }

    public Stream<Neuron> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * @param fromIndex int; inklusive
     * @param toIndex int; exklusive
     */
    public NeuronList subList(final int fromIndex, final int toIndex) {
        return new NeuronList(neurons, fromIndex, toIndex);
    }
}
