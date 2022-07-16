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
    default <T> void visitArray(final T[] array)
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
    default void visitIterable(final Iterable<?> iterable)
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
    void visitObject(Object object);
}
