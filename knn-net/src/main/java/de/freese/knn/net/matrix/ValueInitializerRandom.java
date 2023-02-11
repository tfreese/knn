// Created: 06.06.2008
package de.freese.knn.net.matrix;

/**
 * Initialisiert die Werte mit Zufallszahlen.
 *
 * @author Thomas Freese
 */
public class ValueInitializerRandom implements ValueInitializer {
    private final double lowerLimit;
    private final double upperLimit;

    public ValueInitializerRandom() {
        this(-0.5D, +0.5D);
    }

    /**
     * @param limit double, -limit to +limit
     */
    public ValueInitializerRandom(final double limit) {
        this(Math.abs(limit) * -1, Math.abs(limit));
    }

    public ValueInitializerRandom(final double lowerLimit, final double upperLimit) {
        super();

        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    /**
     * @see de.freese.knn.net.matrix.ValueInitializer#createNextValue()
     */
    @Override
    public double createNextValue() {
        // double weight = -0.05D + (Math.random() * 0.1D); // -0.05 bis +0.05
        // double weight = (2.0D * Math.random()) - 1.0D; // -1 bis +1
        // double weight = -0.5D + Math.random(); // -0.5 - +0.5
        return this.lowerLimit + (Math.random() * (this.upperLimit - this.lowerLimit));
    }
}
