/**
 * 06.06.2008
 */
package de.freese.knn.net.matrix;

/**
 * Initialisiert die Werte mit einem festen Wert.
 * 
 * @author Thomas Freese
 */
public class ConstantValueInitializer implements IValueInitializer
{
	/**
	 *
	 */
	private final double weight;

	/**
	 * Creates a new {@link ConstantValueInitializer} object.
	 * 
	 * @param weight double
	 */
	public ConstantValueInitializer(final double weight)
	{
		super();

		this.weight = weight;
	}

	/**
	 * @see de.freese.knn.net.matrix.IValueInitializer#createNextValue()
	 */
	@Override
	public double createNextValue()
	{
		return this.weight;
	}
}
