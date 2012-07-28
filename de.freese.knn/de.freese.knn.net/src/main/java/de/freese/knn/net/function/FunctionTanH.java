/**
 * 16.04.2008
 */
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Tangendshyperbolikus Funktion.
 * 
 * @author Thomas Freese
 */
public class FunctionTanH implements IFunction
{
	/**
	 * Creates a new {@link FunctionTanH} object.
	 */
	public FunctionTanH()
	{
		super();
	}

	/**
	 * @see de.freese.knn.net.function.IFunction#calculate(double)
	 */
	@Override
	public double calculate(final double value)
	{
		return -1.0D + (2.0D / (1.0D + Math.exp(-2.0D * value)));
	}
}
