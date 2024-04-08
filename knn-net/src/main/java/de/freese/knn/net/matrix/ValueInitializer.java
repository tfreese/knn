// Created: 06.06.2008
package de.freese.knn.net.matrix;

/**
 * Interface eines Initializers für einen double-Wert.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ValueInitializer {
    double createNextValue();
}
