/**
 * 06.06.2008
 */
package de.freese.knn.net.layer.hidden;

import de.freese.knn.net.function.FunctionLogarithmic;
import de.freese.knn.net.function.IFunction;
import de.freese.knn.net.layer.AbstractLayer;

/**
 * HiddenLayer für eine Logarithmus Aktivierungsfunktion.
 * 
 * @author Thomas Freese
 */
public class LogarithmicLayer extends AbstractLayer
{
	/**
	 *
	 */
	private IFunction function = new FunctionLogarithmic();

	/**
	 * Creates a new {@link LogarithmicLayer} object.
	 * 
	 * @param size int
	 */
	public LogarithmicLayer(final int size)
	{
		super(size);
	}

	/**
	 * @see de.freese.knn.net.layer.ILayer#getFunction()
	 */
	@Override
	public IFunction getFunction()
	{
		return this.function;
	}
}
