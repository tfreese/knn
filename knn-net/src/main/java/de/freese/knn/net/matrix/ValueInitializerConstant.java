// Created: 06.06.2008
package de.freese.knn.net.matrix;

/**
 * Initialisiert die Werte mit einem festen Wert.
 *
 * @author Thomas Freese
 */
public class ValueInitializerConstant implements ValueInitializer {
    private final double weight;

    public ValueInitializerConstant(final double weight) {
        super();

        this.weight = weight;
    }

    @Override
    public double createNextValue() {
        return weight;
    }
}
