/**
 * 06.06.2008
 */
package de.freese.knn.net.matrix;

/**
 * Initialisiert die Werte mit einem festen Wert.
 *
 * @author Thomas Freese
 */
public class ValueInitializerConstant implements ValueInitializer
{
    /**
     *
     */
    private final double weight;

    /**
     * Creates a new {@link ValueInitializerConstant} object.
     * 
     * @param weight double
     */
    public ValueInitializerConstant(final double weight)
    {
        super();

        this.weight = weight;
    }

    /**
     * @see de.freese.knn.net.matrix.ValueInitializer#createNextValue()
     */
    @Override
    public double createNextValue()
    {
        return getWeight();
    }

    /**
     * @return double
     */
    public double getWeight()
    {
        return this.weight;
    }
}
