// Created: 06.06.2008
package de.freese.knn.net.matrix;

/**
 * Interface eines Initialisieres fuer einen double-Wert.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ValueInitializer
{
    /**
     * Erzeugt einen neuen Wert.
     *
     * @return double
     */
    double createNextValue();
}
