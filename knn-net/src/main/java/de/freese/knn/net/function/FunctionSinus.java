package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Sinus Funktion.
 *
 * @author Thomas Freese
 * @since 16.04.2008
 */
public class FunctionSinus implements Function {
    @Override
    public double calculate(final double value) {
        return Math.sin(value);
    }
}
