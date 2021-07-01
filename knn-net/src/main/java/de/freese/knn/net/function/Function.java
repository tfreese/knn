/**
 * 16.04.2008
 */
package de.freese.knn.net.function;

import java.util.function.UnaryOperator;

/**
 * Interface einer Funktion zum Verrechnen der Eingangswerte von Neuronen.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface Function extends UnaryOperator<Double>
{
    /**
     * @see java.util.function.Function#apply(java.lang.Object)
     */
    @Override
    default Double apply(final Double t)
    {
        return calculate(t);
    }

    /**
     * Liefert den verrechneten Eingangswert.
     *
     * @param value double
     * @return double
     */
    double calculate(double value);
}
