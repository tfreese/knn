/**
 * 16.04.2008
 */
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Gauss Funktion.
 *
 * @author Thomas Freese
 */
public class FunctionGauss implements Function
{
    /**
     * @see de.freese.knn.net.function.Function#calculate(double)
     */
    @Override
    public double calculate(final double value)
    {
        return Math.exp(-value * value);
    }
}
