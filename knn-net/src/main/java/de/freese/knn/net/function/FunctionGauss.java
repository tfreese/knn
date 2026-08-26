package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Gauss Funktion.
 *
 * @author Thomas Freese
 * @since 16.04.2008
 */
public class FunctionGauss implements Function {
    @Override
    public double calculate(final double value) {
        return Math.exp(-value * value);
    }
}
