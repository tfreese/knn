/**
 * 16.04.2008
 */
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Gauss Funktion.
 * 
 * @author Thomas Freese
 */
public class FunctionGauss implements IFunction
{
	/**
	 * Creates a new {@link FunctionGauss} object.
	 */
	public FunctionGauss()
	{
		super();
	}

	/**
	 * @see de.freese.knn.net.function.IFunction#calculate(double)
	 */
	@Override
	public double calculate(final double value)
	{
		return Math.exp(-value * value);
	}
}
