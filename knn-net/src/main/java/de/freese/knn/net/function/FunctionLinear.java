/**
 * 16.04.2008
 */
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die lineare Funktion.
 * 
 * @author Thomas Freese
 */
public class FunctionLinear implements IFunction
{
	/**
	 * Creates a new {@link FunctionLinear} object.
	 */
	public FunctionLinear()
	{
		super();
	}

	/**
	 * @see de.freese.knn.net.function.IFunction#calculate(double)
	 */
	@Override
	public double calculate(final double value)
	{
		return value;
	}
}
