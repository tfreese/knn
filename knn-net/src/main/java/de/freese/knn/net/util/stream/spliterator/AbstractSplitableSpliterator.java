// Created: 15.09.2016
package de.freese.knn.net.util.stream.spliterator;

import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import de.freese.knn.net.util.stream.SplitableList;

/**
 * {@link Spliterator} für eine {@link SplitableList}.<br>
 * Performantere Alternative zum {@link Spliterators}.IteratorSpliterator.
 *
 * @author Thomas Freese
 * @param <T> Konkreter Typ
 * @see Spliterators (ArraySpliterator)
 */
public abstract class AbstractSplitableSpliterator<T> implements Spliterator<T>
{
    /**
     * Element-Größe ab der nicht mehr gesplitted wird.
     */
    private static final int DEFAULT_SPLIT_THRESHOLD = Optional.ofNullable(System.getProperty("split_threshold")).map(Integer::parseInt).orElse(5);

    /**
     *
     */
    private final int characteristics;

    /**
     *
     */
    private final int fence;

    /**
     *
     */
    private int index = 0;

    /**
     * Element-Größe ab der nicht mehr gesplitted wird.
     */
    private int splitThreshold = DEFAULT_SPLIT_THRESHOLD;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractSplitableSpliterator}
     *
     * @param index int; untere Grenze
     * @param fence int; obere Grenze
     */
    public AbstractSplitableSpliterator(final int index, final int fence)
    {
        this(index, fence, 0);
    }

    /**
     * Erzeugt eine neue Instanz von {@link AbstractSplitableSpliterator}
     *
     * @param index int; untere Grenze
     * @param fence int; obere Grenze
     * @param characteristics int
     */
    public AbstractSplitableSpliterator(final int index, final int fence, final int characteristics)
    {
        super();

        this.index = index;
        this.fence = fence;
        this.characteristics = characteristics | Spliterator.SIZED | Spliterator.SUBSIZED;
    }

    /**
     * @see java.util.Spliterator#characteristics()
     */
    @Override
    public int characteristics()
    {
        return this.characteristics;
    }

    /**
     * Liefert den {@link Spliterator} für den List-Bereich.
     *
     * @param index int
     * @param fence int
     * @param characteristics int
     * @return {@link Spliterator}
     */
    protected abstract Spliterator<T> createSplit(final int index, int fence, int characteristics);

    /**
     * @see java.util.Spliterator#estimateSize()
     */
    @Override
    public long estimateSize()
    {
        return (long) this.fence - this.index;
    }

    /**
     * Zur Performance-Optimierung wird hier nicht die Default-Implementierung verwendet.
     *
     * @see java.util.Spliterator#forEachRemaining(java.util.function.Consumer)
     */
    @Override
    public void forEachRemaining(final Consumer<? super T> action)
    {
        // Spliterator.super.forEachRemaining(action);

        Objects.requireNonNull(action, "action required");

        int i = 0;

        if ((getMaxSize() >= this.fence) && ((i = this.index) >= 0) && (i < (this.index = this.fence)))
        {
            do
            {
                action.accept(get(i));
            }
            while (++i < this.fence);
        }
    }

    /**
     * Liefert das Objekt am Index.
     *
     * @param index int
     * @return Object
     */
    protected abstract T get(int index);

    /**
     * Liefert die Gesamte-/Größte Länge der Liste.
     *
     * @return int
     */
    protected abstract int getMaxSize();

    /**
     * Element-Größe ab der nicht mehr gesplitted wird.
     *
     * @return int
     */
    public int getSplitThreshold()
    {
        return this.splitThreshold;
    }

    /**
     * Element-Größe ab der nicht mehr gesplitted wird.
     *
     * @param splitThreshold int
     */
    public void setSplitThreshold(final int splitThreshold)
    {
        this.splitThreshold = splitThreshold;
    }

    /**
     * @see java.util.Spliterator#tryAdvance(java.util.function.Consumer)
     */
    @Override
    public boolean tryAdvance(final Consumer<? super T> action)
    {
        Objects.requireNonNull(action, "action required");

        if ((this.index >= 0) && (this.index < this.fence))
        {
            T element = get(this.index++);
            action.accept(element);

            return true;
        }

        return false;
    }

    /**
     * @see java.util.Spliterator#trySplit()
     */
    @Override
    public Spliterator<T> trySplit()
    {
        // Wenn Rest kleiner als n Elemente -> nicht mehr Splitten.
        if ((this.fence - this.index) < getSplitThreshold())
        {
            return null;
        }

        int lo = this.index;
        int mid = (lo + this.fence) >>> 1; // (index + fence) / 2)

        return (lo >= mid) ? null : createSplit(lo, this.index = mid, this.characteristics);
    }
}
