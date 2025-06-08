// Created: 16.04.2008
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch eine lineare Funktion: value * factor.
 *
 * @author Thomas Freese
 */
public class FunctionLinear implements Function {
    private final double factor;

    public FunctionLinear() {
        this(1.0D);
    }

    public FunctionLinear(final double factor) {
        super();

        this.factor = factor;
    }

    @Override
    public double calculate(final double value) {
        return value * getFactor();
    }

    public double getFactor() {
        return factor;
    }
}
