// Created: 16.04.2008
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Logarithmus Funktion.
 *
 * @author Thomas Freese
 */
public class FunctionLogarithmic implements Function {
    @Override
    public double calculate(final double value) {
        if (value >= 0.0D) {
            return Math.log(1.0D + value);
        }

        return -Math.log(1.0D - value);
    }
}
