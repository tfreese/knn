// Created: 06.06.2008
package de.freese.knn.net.matrix;

/**
 * Initialisiert die Werte mit Zufallszahlen.
 *
 * @author Thomas Freese
 */
public class ValueInitializerRandom implements ValueInitializer
{
    /**
     *
     */
    private final double oberGrenze;
    /**
     *
     */
    private final double unterGrenze;

    /**
     * Creates a new {@link ValueInitializerRandom} object.
     */
    public ValueInitializerRandom()
    {
        this(-0.5D, +0.5D);
    }

    /**
     * Creates a new {@link ValueInitializerRandom} object.
     *
     * @param grenzWert double, Von -GrenzWert bis +Grenzwert
     */
    public ValueInitializerRandom(final double grenzWert)
    {
        this(Math.abs(grenzWert) * -1, Math.abs(grenzWert));
    }

    /**
     * Creates a new {@link ValueInitializerRandom} object.
     *
     * @param unterGrenze double
     * @param oberGrenze double
     */
    public ValueInitializerRandom(final double unterGrenze, final double oberGrenze)
    {
        super();

        this.unterGrenze = unterGrenze;
        this.oberGrenze = oberGrenze;
    }

    /**
     * @see de.freese.knn.net.matrix.ValueInitializer#createNextValue()
     */
    @Override
    public double createNextValue()
    {
        // double weight = -0.05D + (Math.random() * 0.1D); // -0.05 bis +0.05
        // double weight = (2.0D * Math.random()) - 1.0D; // -1 bis +1
        // double weight = -0.5D + Math.random(); // -0.5 - +0.5
        return this.unterGrenze + (Math.random() * (this.oberGrenze - this.unterGrenze));
    }
}
