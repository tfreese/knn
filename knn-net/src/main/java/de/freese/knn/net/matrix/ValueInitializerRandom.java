// Created: 06.06.2008
package de.freese.knn.net.matrix;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Initialisiert die Werte mit Zufallszahlen.
 *
 * @author Thomas Freese
 */
public class ValueInitializerRandom implements ValueInitializer {
    private final double lowerLimit;
    private final Random random = new SecureRandom();
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

    @Override
    public double createNextValue() {
        // return -0.5D + random.nextDouble(); // -0.5 - +0.5
        // return lowerLimit + (random.nextDouble() * (upperLimit - lowerLimit));

        return random.nextDouble(lowerLimit, upperLimit);
    }
}
