package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Tangens-Hyperbolic Funktion.
 *
 * @author Thomas Freese
 * @since 16.04.2008
 */
public class FunctionTanH implements Function {
    @Override
    public double calculate(final double value) {
        return -1.0D + (2.0D / (1.0D + Math.exp(-2.0D * value)));
    }
}
