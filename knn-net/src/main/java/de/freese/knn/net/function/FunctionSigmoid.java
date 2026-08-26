package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Sigmoid/Sprung/Treppen Funktion.
 *
 * @param durchgang X-Durchgang für y = 0.5
 * @param steigung Steigung -> 0 Treppenfunktion
 *
 * @author Thomas Freese
 * @since 16.04.2008
 */
public record FunctionSigmoid(double durchgang, double steigung) implements Function {
    public FunctionSigmoid() {
        this(0.0D, 1.0D);
    }

    @Override
    public double calculate(final double value) {
        return 1.0D / (1.0D + Math.exp(-((value - durchgang()) / steigung())));
    }

    /**
     * X-Durchgang für y = 0.5.
     */
    @Override
    public double durchgang() {
        return durchgang;
    }

    /**
     * Steigung -> 0 Treppenfunktion.
     */
    @Override
    public double steigung() {
        return steigung;
    }
}
