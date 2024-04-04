// Created: 16.04.2008
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Sigmoid/Sprung/Treppen Funktion.
 *
 * @author Thomas Freese
 */
public class FunctionSigmoid implements Function {
    /**
     * X-Durchgang für y = 0.5
     */
    private final double durchgang;
    /**
     * Steigung -> 0 Treppenfunktion
     */
    private final double steigung;

    public FunctionSigmoid() {
        this(0.0D, 1.0D);
    }

    public FunctionSigmoid(final double durchgang, final double steigung) {
        super();

        this.durchgang = durchgang;
        this.steigung = steigung;
    }

    @Override
    public double calculate(final double value) {
        return 1.0D / (1.0D + Math.exp(-((value - getDurchgang()) / getSteigung())));
    }

    /**
     * X-Durchgang für y = 0.5.
     */
    public double getDurchgang() {
        return this.durchgang;
    }

    /**
     * Steigung -> 0 Treppenfunktion.
     */
    public double getSteigung() {
        return this.steigung;
    }
}
