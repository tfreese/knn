/**
 * 06.06.2008
 */
package de.freese.knn.net.layer.hidden;

import de.freese.knn.net.function.FunctionLinear;
import de.freese.knn.net.function.IFunction;
import de.freese.knn.net.layer.AbstractLayer;
import de.freese.knn.net.layer.input.InputLayer;

/**
 * HiddenLayer für eine lineare Aktivierungsfunktion.
 * 
 * @author Thomas Freese
 */
public class LinearLayer extends AbstractLayer
{
	/**
	 * 
	 */
	private IFunction function = new FunctionLinear();

	/**
	 * Creates a new {@link InputLayer} object.
	 * 
	 * @param size int
	 */
	public LinearLayer(final int size)
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
