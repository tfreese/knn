// Created: 16.04.2008
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die binäre Funktion.
 *
 * @author Thomas Freese
 */
public class FunctionBinary implements Function {
    private final double threshold;

    public FunctionBinary() {
        this(0.0D);
    }

    public FunctionBinary(final double threshold) {
        super();

        this.threshold = threshold;
    }

    @Override
    public double calculate(final double value) {
        return (value > getThreshold()) ? 1.0D : 0.0D;
    }

    public double getThreshold() {
        return this.threshold;
    }
}
