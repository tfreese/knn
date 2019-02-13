// Created: 15.09.2016
package de.freese.knn.net.util.stream;

import java.util.Spliterator;
import de.freese.knn.net.util.stream.spliterator.SplitableArraySpliterator;

/**
 * Liste für ein Array mit fester Länge für die Streaming-API.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class DefaultSplitableArray<T> extends AbstractSplitableList<T>
{
    /**
    *
    */
    private final T[] array;

    /**
     * Erzeugt eine neue Instanz von {@link DefaultSplitableArray}.
     *
     * @param array Object[]
     */
    public DefaultSplitableArray(final T[] array)
    {
        this(array, 0, array.length - 1);
    }

    /**
     * Erzeugt eine neue Instanz von {@link DefaultSplitableArray}.
     *
     * @param array Object[]
     * @param indexStart int
     * @param indexEnd int
     */
    public DefaultSplitableArray(final T[] array, final int indexStart, final int indexEnd)
    {
        super(indexStart, Math.min(array.length - 1, indexEnd));

        this.array = array;
    }

    /**
     * @see de.freese.knn.net.util.stream.SplitableList#get(int)
     */
    @Override
    public T get(final int index)
    {
        return getArray()[index + getIndexStart()];
    }

    /**
     * @return Object[]
     */
    protected T[] getArray()
    {
        return this.array;
    }

    /**
     * @see de.freese.knn.net.util.stream.SplitableList#set(int, java.lang.Object)
     */
    @Override
    public void set(final int index, final T object)
    {
        getArray()[index + getIndexStart()] = object;
    }

    // /**
    // * @see de.freese.base.core.collection.stream.ISplitableList#sort(java.util.Comparator)
    // */
    // @Override
    // public void sort(final Comparator<? super T> comparator)
    // {
    // Arrays.sort(getArray(), getIndexStart(), getIndexEnd(), comparator);
    // }

    /**
     * @see java.lang.Iterable#spliterator()
     */
    @Override
    public Spliterator<T> spliterator()
    {
        return new SplitableArraySpliterator<>(getArray(), getIndexStart(), getIndexEnd() + 1);
    }

    /**
     * @see de.freese.knn.net.util.stream.SplitableList#subList(int, int)
     */
    @Override
    public SplitableList<T> subList(final int fromIndex, final int toIndex)
    {
        return new DefaultSplitableArray<>(getArray(), fromIndex, Math.min(this.array.length - 1, toIndex));
    }
}
