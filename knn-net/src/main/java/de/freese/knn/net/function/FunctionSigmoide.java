/**
 * 16.04.2008
 */
package de.freese.knn.net.function;

/**
 * Verrechnen der Eingangswerte durch die Sigmoid Funktion.
 * 
 * @author Thomas Freese
 */
public class FunctionSigmoide implements IFunction
{
	/**
	 * X-Durchgang fuer y = 0.5
	 */
	private double durchgang = 0.0D;

	/**
	 * Steigung -> 0 Treppenfunktion
	 */
	private double steigung = 1.0D;

	/**
	 * Creates a new {@link FunctionSigmoide} object.
	 */
	public FunctionSigmoide()
	{
		super();
	}

	/**
	 * X-Durchgang fuer y = 0.5.
	 * 
	 * @param durchgang double
	 */
	public void setDurchgang(final double durchgang)
	{
		this.durchgang = durchgang;
	}

	/**
	 * X-Durchgang fuer y = 0.5.
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
	 * @param steigung double
	 */
	public void setSteigung(final double steigung)
	{
		this.steigung = steigung;
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

	/**
	 * @see de.freese.knn.net.function.IFunction#calculate(double)
	 */
	@Override
	public double calculate(final double value)
	{
		return (1.0D / (1.0D + Math.exp(-((value - this.durchgang) / this.steigung))));
	}
}
