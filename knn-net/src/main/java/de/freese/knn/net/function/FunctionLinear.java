// Created: 16.04.2008
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch eine lineare Funktion: value * factor.
 *
 * @author Thomas Freese
 */
public class FunctionLinear implements Function
{
    /**
     *
     */
    private final double factor;

    /**
     * Creates a new {@link FunctionLinear} object.
     */
    public FunctionLinear()
    {
        this(1.0D);
    }

    /**
     * Creates a new {@link FunctionLinear} object.
     *
     * @param factor double
     */
    public FunctionLinear(final double factor)
    {
        super();

        this.factor = factor;
    }

    /**
     * @see de.freese.knn.net.function.Function#calculate(double)
     */
    @Override
    public double calculate(final double value)
    {
        return value * getFactor();
    }

    /**
     * @return double
     */
    public double getFactor()
    {
        return this.factor;
    }
}
