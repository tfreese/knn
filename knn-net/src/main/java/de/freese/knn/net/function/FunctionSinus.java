/**
 * 16.04.2008
 */
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Sinus Funktion.
 * 
 * @author Thomas Freese
 */
public class FunctionSinus implements Function
{
	/**
	 * Creates a new {@link FunctionSinus} object.
	 */
	public FunctionSinus()
	{
		super();
	}

	/**
	 * @see de.freese.knn.net.function.Function#calculate(double)
	 */
	@Override
	public double calculate(final double value)
	{
		return Math.sin(value);
	}
}
