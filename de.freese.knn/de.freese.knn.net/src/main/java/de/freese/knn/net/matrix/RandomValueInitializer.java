/**
 * 06.06.2008
 */
package de.freese.knn.net.matrix;

/**
 * Initialisiert die Werte mit Zufallszahlen.
 * 
 * @author Thomas Freese
 */
public class RandomValueInitializer implements IValueInitializer
{
	/**
	 * 
	 */
	private double oberGrenze = 0;

	/**
	 *
	 */
	private double unterGrenze = 0;

	/**
	 * Creates a new {@link RandomValueInitializer} object.
	 */
	public RandomValueInitializer()
	{
		this(-0.5D, +0.5D);
	}

	/**
	 * Creates a new {@link RandomValueInitializer} object.
	 * 
	 * @param grenzWert double, Von -GrenzWert bis +Grenzwert
	 */
	public RandomValueInitializer(final double grenzWert)
	{
		this(Math.abs(grenzWert) * -1, Math.abs(grenzWert));
	}

	/**
	 * Creates a new {@link RandomValueInitializer} object.
	 * 
	 * @param unterGrenze double
	 * @param oberGrenze double
	 */
	public RandomValueInitializer(final double unterGrenze, final double oberGrenze)
	{
		super();

		this.unterGrenze = unterGrenze;
		this.oberGrenze = oberGrenze;
	}

	/**
	 * @see de.freese.knn.net.matrix.IValueInitializer#createNextValue()
	 */
	@Override
	public double createNextValue()
	{
		// double weight = -0.05D + (Math.random() * 0.1D); // -0.05 bis +0.05
		// double weight = (2.0D * Math.random()) - 1.0D; // -1 bis +1
		// double weight = -0.5D + Math.random(); // -0.5 - +0.5
		return this.unterGrenze + (Math.random() * (this.oberGrenze - this.unterGrenze));
	}
}
