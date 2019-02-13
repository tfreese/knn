/**
 * 16.04.2008
 */
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die binaere Funktion.
 *
 * @author Thomas Freese
 */
public class FunctionBinary implements Function
{
    /**
     *
     */
    private final double threshold;

    /**
     * Creates a new {@link FunctionBinary} object.
     */
    public FunctionBinary()
    {
        this(0.0D);
    }

    /**
     * Creates a new {@link FunctionBinary} object.
     * 
     * @param threshold double
     */
    public FunctionBinary(final double threshold)
    {
        super();

        this.threshold = threshold;
    }

    /**
     * @see de.freese.knn.net.function.Function#calculate(double)
     */
    @Override
    public double calculate(final double value)
    {
        return (value > getThreshold()) ? 1.0D : 0.0D;
    }

    /**
     * @return double
     */
    public double getThreshold()
    {
        return this.threshold;
    }
}
