// Created: 15.09.2016
package de.freese.knn.net.util.stream;

import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import de.freese.knn.net.util.stream.spliterator.AbstractSplitableSpliterator;

/**
 * Interface für eine Datenstruktur mit fester Länge für die Streaming-API.<br>
 * Der Nutzen dieser Implementierung ist der performantere {@link AbstractSplitableSpliterator},<br>
 * der anstelle des langsamen {@link Spliterators}.IteratorSpliterator verwendet wird.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 */
public interface SplitableList<T> extends Iterable<T>
{
    /**
     * Liefert das Objekt am Index.
     *
     * @param index int
     * @return Object
     */
    public T get(int index);

    /**
     * Liefert einen parallelen Stream.
     *
     * @return {@link Stream}
     */
    public default Stream<T> parallelStream()
    {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * Setzt das Objekt am Index.
     *
     * @param index int
     * @param object Object
     */
    public void set(int index, T object);

    /**
     * Liefert die Größe der Collection.
     *
     * @return int
     */
    public int size();

    // /**
    // * Sortiert die Collection.
    // *
    // * @param comparator {@link Comparator}
    // */
    // public void sort(final Comparator<? super T> comparator);

    /**
     * Liefert einen seriellen Stream.
     *
     * @return {@link Stream}
     */
    public default Stream<T> stream()
    {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Liefert eine Sub-List.
     *
     * @param fromIndex int; inklusive
     * @param toIndex int; exklusive
     * @return {@link SplitableList}
     */
    public SplitableList<T> subList(int fromIndex, int toIndex);
}
