package de.freese.knn.net.visitor;

/**
 * Interface eines Visitors des Visitor Patterns.
 */
@FunctionalInterface
public interface Visitor {
    default <T> void visitArray(final T[] array) {
        if (array == null) {
            return;
        }

        for (T object : array) {
            visitObject(object);
        }
    }

    default void visitIterable(final Iterable<?> iterable) {
        if (iterable == null) {
            return;
        }

        for (Object object : iterable) {
            visitObject(object);
        }
    }

    void visitObject(Object object);
}
