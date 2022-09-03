// Created: 16.04.2008
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Sigmoid/Sprung/Treppen Funktion.
 *
 * @author Thomas Freese
 */
public class FunctionSigmoid implements Function
{
    /**
     * X-Durchgang für y = 0.5
     */
    private final double durchgang;
    /**
     * Steigung -> 0 Treppenfunktion
     */
    private final double steigung;

    /**
     * Creates a new {@link FunctionSigmoid} object.
     */
    public FunctionSigmoid()
    {
        this(0.0D, 1.0D);
    }

    /**
     * Creates a new {@link FunctionSigmoid} object.
     *
     * @param durchgang double
     * @param steigung double
     */
    public FunctionSigmoid(final double durchgang, final double steigung)
    {
        super();

        this.durchgang = durchgang;
        this.steigung = steigung;
    }

    /**
     * @see de.freese.knn.net.function.Function#calculate(double)
     */
    @Override
    public double calculate(final double value)
    {
        return (1.0D / (1.0D + Math.exp(-((value - getDurchgang()) / getSteigung()))));
    }

    /**
     * X-Durchgang für y = 0.5.
     *
     * @return double
     */
    public double getDurchgang()
    {
        return this.durchgang;
    }

    /**
     * Steigung -> 0 Treppenfunktion.
     *
     * @return double
     */
    public double getSteigung()
    {
        return this.steigung;
    }
}
