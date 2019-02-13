package de.freese.knn.net.util.visitor;

/**
 * Interface eines besuchbaren Objektes des Visitor Patterns.
 */
public interface Visitable
{
    /**
     * @param visitor {@link Visitor}
     */
    public default void visit(final Visitor visitor)
    {
        visitor.visitObject(this);
    }
}