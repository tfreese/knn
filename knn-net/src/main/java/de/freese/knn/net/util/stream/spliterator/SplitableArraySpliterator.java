// Created: 15.09.2016
package de.freese.knn.net.util.stream.spliterator;

import java.util.Spliterator;
import java.util.Spliterators;
import de.freese.knn.net.util.stream.DefaultSplitableArray;

/**
 * {@link Spliterator} für ein {@link DefaultSplitableArray}.<br>
 * Performantere Alternative zum {@link Spliterators}.IteratorSpliterator.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 * @see Spliterators (ArraySpliterator)
 */
public class SplitableArraySpliterator<T> extends AbstractSplitableSpliterator<T>
{
    /**
     *
     */
    private final T[] array;

    /**
     * Erzeugt eine neue Instanz von {@link SplitableArraySpliterator}
     *
     * @param array Object[]
     */
    public SplitableArraySpliterator(final T[] array)
    {
        this(array, 0, array.length, 0);
    }

    /**
     * Erzeugt eine neue Instanz von {@link SplitableArraySpliterator}
     *
     * @param array Object[]
     * @param characteristics int
     */
    public SplitableArraySpliterator(final T[] array, final int characteristics)
    {
        this(array, 0, array.length, characteristics);
    }

    /**
     * Erzeugt eine neue Instanz von {@link SplitableArraySpliterator}
     *
     * @param array Object[]
     * @param index int; untere Grenze
     * @param fence int; obere Grenze
     */
    public SplitableArraySpliterator(final T[] array, final int index, final int fence)
    {
        this(array, index, fence, 0);
    }

    /**
     * Erzeugt eine neue Instanz von {@link SplitableArraySpliterator}
     *
     * @param array Object[]
     * @param index int; untere Grenze
     * @param fence int; obere Grenze
     * @param characteristics int
     */
    public SplitableArraySpliterator(final T[] array, final int index, final int fence, final int characteristics)
    {
        super(index, fence, characteristics);

        this.array = array;
    }

    /**
     * @see de.freese.knn.net.util.stream.spliterator.AbstractSplitableSpliterator#createSplit(int, int, int)
     */
    @Override
    protected Spliterator<T> createSplit(final int index, final int fence, final int characteristics)
    {
        return new SplitableArraySpliterator<>(this.array, index, fence, characteristics);
    }

    /**
     * @see de.freese.knn.net.util.stream.spliterator.AbstractSplitableSpliterator#get(int)
     */
    @Override
    protected T get(final int index)
    {
        return this.array[index];
    }

    /**
     * @see de.freese.knn.net.util.stream.spliterator.AbstractSplitableSpliterator#getMaxSize()
     */
    @Override
    protected int getMaxSize()
    {
        return this.array.length;
    }
}
