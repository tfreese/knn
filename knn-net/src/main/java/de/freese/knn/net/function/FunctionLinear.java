package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch eine lineare Funktion: value * factor.
 *
 * @author Thomas Freese
 * @since 16.04.2008
 */
public record FunctionLinear(double factor) implements Function {
    public FunctionLinear() {
        this(1.0D);
    }

    @Override
    public double calculate(final double value) {
        return value * factor();
    }
}
