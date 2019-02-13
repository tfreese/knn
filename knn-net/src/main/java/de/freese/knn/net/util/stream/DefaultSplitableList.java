// Created: 15.09.2016
package de.freese.knn.net.util.stream;

import java.util.List;
import java.util.Spliterator;
import de.freese.knn.net.util.stream.spliterator.SplitableListSpliterator;

/**
 * Liste mit fester Länge für die Streaming-API.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public class DefaultSplitableList<T> extends AbstractSplitableList<T>
{
    /**
    *
    */
    private final List<T> list;

    /**
     * Erzeugt eine neue Instanz von {@link DefaultSplitableList}.
     *
     * @param list {@link DefaultSplitableList}
     */
    public DefaultSplitableList(final List<T> list)
    {
        this(list, 0, list.size() - 1);
    }

    /**
     * Erzeugt eine neue Instanz von {@link DefaultSplitableList}.
     *
     * @param list {@link List}
     * @param indexStart int
     * @param indexEnd int
     */
    public DefaultSplitableList(final List<T> list, final int indexStart, final int indexEnd)
    {
        super(indexStart, indexEnd);

        this.list = list;
    }

    /**
     * @see de.freese.knn.net.util.stream.SplitableList#get(int)
     */
    @Override
    public T get(final int index)
    {
        return this.list.get(index + getIndexStart());
    }

    /**
     * @return {@link List}
     */
    protected List<T> getList()
    {
        return this.list;
    }

    /**
     * @see de.freese.knn.net.util.stream.SplitableList#set(int, java.lang.Object)
     */
    @Override
    public void set(final int index, final T object)
    {
        getList().set(index + getIndexStart(), object);
    }

    // /**
    // * @see de.freese.base.core.collection.stream.ISplitableList#sort(java.util.Comparator)
    // */
    // @Override
    // public void sort(final Comparator<? super T> comparator)
    // {
    // Collections.sort(getList(), comparator);
    // }

    /**
     * @see java.lang.Iterable#spliterator()
     */
    @Override
    public Spliterator<T> spliterator()
    {
        return new SplitableListSpliterator<>(getList(), getIndexStart(), getIndexEnd() + 1);
    }

    /**
     * @see de.freese.knn.net.util.stream.SplitableList#subList(int, int)
     */
    @Override
    public SplitableList<T> subList(final int fromIndex, final int toIndex)
    {
        return new DefaultSplitableList<>(getList(), fromIndex, toIndex);
    }
}
