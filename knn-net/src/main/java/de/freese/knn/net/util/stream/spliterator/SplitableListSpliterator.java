// Created: 15.09.2016
package de.freese.knn.net.util.stream.spliterator;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import de.freese.knn.net.util.stream.DefaultSplitableList;

/**
 * {@link Spliterator} für eine {@link DefaultSplitableList}.<br>
 * Performantere Alternative zum {@link Spliterators}.IteratorSpliterator.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 * @see Spliterators (ArraySpliterator)
 */
public class SplitableListSpliterator<T> extends AbstractSplitableSpliterator<T>
{
    /**
     *
     */
    private final List<T> list;

    /**
     * Erzeugt eine neue Instanz von {@link SplitableListSpliterator}
     *
     * @param list {@link List}
     */
    public SplitableListSpliterator(final List<T> list)
    {
        this(list, 0, list.size(), 0);
    }

    /**
     * Erzeugt eine neue Instanz von {@link SplitableListSpliterator}
     *
     * @param list {@link List}
     * @param characteristics int
     */
    public SplitableListSpliterator(final List<T> list, final int characteristics)
    {
        this(list, 0, list.size(), characteristics);
    }

    /**
     * Erzeugt eine neue Instanz von {@link SplitableListSpliterator}
     *
     * @param list {@link List}
     * @param index int; untere Grenze
     * @param fence int; obere Grenze
     */
    public SplitableListSpliterator(final List<T> list, final int index, final int fence)
    {
        this(list, index, fence, 0);
    }

    /**
     * Erzeugt eine neue Instanz von {@link SplitableListSpliterator}
     *
     * @param list {@link List}
     * @param index int; untere Grenze
     * @param fence int; obere Grenze
     * @param characteristics int
     */
    public SplitableListSpliterator(final List<T> list, final int index, final int fence, final int characteristics)
    {
        super(index, fence, characteristics);

        this.list = list;
    }

    /**
     * @see de.freese.knn.net.util.stream.spliterator.AbstractSplitableSpliterator#createSplit(int, int, int)
     */
    @Override
    protected Spliterator<T> createSplit(final int index, final int fence, final int characteristics)
    {
        return new SplitableListSpliterator<>(this.list, index, fence, characteristics);
    }

    /**
     * @see de.freese.knn.net.util.stream.spliterator.AbstractSplitableSpliterator#get(int)
     */
    @Override
    protected T get(final int index)
    {
        return this.list.get(index);
    }

    /**
     * @see de.freese.knn.net.util.stream.spliterator.AbstractSplitableSpliterator#getMaxSize()
     */
    @Override
    protected int getMaxSize()
    {
        return this.list.size();
    }
}
