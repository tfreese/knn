package de.freese.knn.net.visitor;

/**
 * Interface eines besuchbaren Objektes des Visitor Patterns.
 */
public interface Visitable {
    default void visit(final Visitor visitor) {
        visitor.visitObject(this);
    }
}
