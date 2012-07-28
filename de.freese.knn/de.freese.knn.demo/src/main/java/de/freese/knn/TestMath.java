/**
 * 18.04.2008
 */
package de.freese.knn;

import org.junit.Test;

import de.freese.knn.net.function.FunctionSigmoide;
import de.freese.knn.net.function.IFunction;

/**
 * Testklasse der Mathematik.
 * 
 * @author Thomas Freese
 */
public class TestMath
{
	/**
	 * Erstellt ein neues {@link TestMath} Object.
	 */
	public TestMath()
	{
		super();
	}

	/**
	 * 
	 */
	@Test
	public void testSigmoide()
	{
		IFunction function = new FunctionSigmoide();

		for (float i = -2; i < 2; i += 0.1)
		{
			System.out.println(i + ", " + function.calculate(i));
		}
	}
}
