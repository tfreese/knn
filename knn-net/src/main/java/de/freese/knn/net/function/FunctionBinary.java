package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die binäre Funktion.
 *
 * @author Thomas Freese
 * @since 16.04.2008
 */
public record FunctionBinary(double threshold) implements Function {
    public FunctionBinary() {
        this(0.0D);
    }

    @Override
    public double calculate(final double value) {
        return (value > threshold()) ? 1.0D : 0.0D;
    }
}
