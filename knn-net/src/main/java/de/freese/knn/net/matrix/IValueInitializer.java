/**
 * 06.06.2008
 */
package de.freese.knn.net.matrix;

/**
 * Interface eines Initialisieres fuer einen double-Wert.
 * 
 * @author Thomas Freese
 */
public interface IValueInitializer
{
	/**
	 * Erzeugt einen neuen Wert.
	 * 
	 * @return double
	 */
	public abstract double createNextValue();
}
