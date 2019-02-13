// Created: 15.09.2016
package de.freese.knn.net.util.stream;

import java.util.Iterator;

/**
 * Liste für eine Datenstruktur mit fester Länge für die Streaming-API.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public abstract class AbstractSplitableList<T> implements SplitableList<T>
{
    /**
     * @author Thomas Freese
     */
    private class Iter implements Iterator<T>
    {
        /**
         *
         */
        private int cursor = 0;

        /**
         * Erzeugt eine neue Instanz von {@link Iter}
         *
         * @param cursorStart int; Start-Position des Iterators
         */
        private Iter(final int cursorStart)
        {
            super();

            this.cursor = cursorStart;
        }

        /**
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext()
        {
            return this.cursor <= AbstractSplitableList.this.indexEnd;
        }

        /**
         * @see java.util.Iterator#next()
         */
        @Override
        public T next()
        {
            T next = get(this.cursor - getIndexStart());
            this.cursor++;

            return next;
        }

        /**
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove()
        {
            set(this.cursor - 1, null);
        }
    }

    /**
     * Index des letzten Elements.
     */
    private final int indexEnd;

    /**
     * Index des ersten Elements.
     */
    private final int indexStart;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractSplitableList}
     *
     * @param indexStart int
     * @param indexEnd int
     */
    public AbstractSplitableList(final int indexStart, final int indexEnd)
    {
        super();

        this.indexStart = indexStart;
        this.indexEnd = indexEnd;
    }

    /**
     * Liefert den Index des letzten Objektes.
     *
     * @return int
     */
    protected int getIndexEnd()
    {
        return this.indexEnd;
    }

    /**
     * Liefert den Index des ersten Objektes.
     *
     * @return int
     */
    protected int getIndexStart()
    {
        return this.indexStart;
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<T> iterator()
    {
        return new Iter(getIndexStart());
    }

    /**
     * @see de.freese.knn.net.util.stream.SplitableList#size()
     */
    @Override
    public int size()
    {
        return (this.indexEnd - this.indexStart) + 1;
    }
}
