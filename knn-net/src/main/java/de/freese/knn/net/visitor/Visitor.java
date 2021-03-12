package de.freese.knn.net.visitor;

/**
 * Interface eines Visitors des Visitor Patterns.
 */
@FunctionalInterface
public interface Visitor
{
    /**
     * @param <T> Konkreter Typ
     * @param array Object[]
     */
    public default <T> void visitArray(final T[] array)
    {
        if (array == null)
        {
            return;
        }

        for (T object : array)
        {
            visitObject(object);
        }
    }

    /**
     * @param iterable {@link Iterable}
     */
    public default void visitIterable(final Iterable<?> iterable)
    {
        if (iterable == null)
        {
            return;
        }

        for (Object object : iterable)
        {
            visitObject(object);
        }
    }

    /**
     * @param object Object
     */
    public void visitObject(Object object);
}
