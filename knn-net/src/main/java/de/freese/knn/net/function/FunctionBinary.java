/**
 * 16.04.2008
 */
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die binaere Funktion.
 * 
 * @author Thomas Freese
 */
public class FunctionBinary implements IFunction
{
	/**
	 * Creates a new {@link FunctionBinary} object.
	 */
	public FunctionBinary()
	{
		super();
	}

	/**
	 * @see de.freese.knn.net.function.IFunction#calculate(double)
	 */
	@Override
	public double calculate(final double value)
	{
		return (value > 0.0D) ? 1.0D : 0.0D;
	}
}
