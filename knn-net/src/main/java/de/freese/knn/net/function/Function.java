package de.freese.knn.net.function;

import java.util.function.UnaryOperator;

/**
 * Interface einer Funktion zum Verrechnen der Eingangswerte von Neuronen.
 *
 * @author Thomas Freese
 * @since 16.04.2008
 */
@FunctionalInterface
public interface Function extends UnaryOperator<Double> {
    @Override
    default Double apply(final Double t) {
        return calculate(t);
    }

    /**
     * Liefert den verrechneten Eingangswert.
     */
    double calculate(double value);
}
