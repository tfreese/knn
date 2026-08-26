package de.freese.knn.net.matrix;

/**
 * Interface eines Initializers für einen double-Wert.
 *
 * @author Thomas Freese
 * @since 06.06.2008
 */
@FunctionalInterface
public interface ValueInitializer {
    double createNextValue();
}
