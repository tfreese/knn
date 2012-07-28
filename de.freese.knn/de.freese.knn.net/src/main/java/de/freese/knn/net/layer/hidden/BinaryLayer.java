/**
 * 06.06.2008
 */
package de.freese.knn.net.layer.hidden;

import de.freese.knn.net.function.FunctionBinary;
import de.freese.knn.net.function.IFunction;
import de.freese.knn.net.layer.AbstractLayer;

/**
 * HiddenLayer für eine binäre Aktivierungsfunktion.
 * 
 * @author Thomas Freese
 */
public class BinaryLayer extends AbstractLayer
{
	/**
	 *
	 */
	private final IFunction function = new FunctionBinary();

	/**
	 * Creates a new {@link BinaryLayer} object.
	 * 
	 * @param size int
	 */
	public BinaryLayer(final int size)
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
