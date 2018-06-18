/**
 * 16.04.2008
 */
package de.freese.knn.net.function;

/**
 * Interface einer Funktion zum Verrechnen der Eingangswerte von Neuronen.
 * 
 * @author Thomas Freese
 */
public interface IFunction
{
	/**
	 * Liefert den verrechneten Eingangswert.
	 * 
	 * @param value double
	 * @return double
	 */
	public double calculate(double value);
}
