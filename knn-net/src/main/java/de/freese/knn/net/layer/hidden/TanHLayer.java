/**
 * 06.06.2008
 */
package de.freese.knn.net.layer.hidden;

import de.freese.knn.net.function.FunctionTanH;
import de.freese.knn.net.function.IFunction;
import de.freese.knn.net.layer.AbstractLayer;

/**
 * HiddenLayer für eine Tangendshyperbolikus Aktivierungsfunktion.
 * 
 * @author Thomas Freese
 */
public class TanHLayer extends AbstractLayer
{
	/**
	 * 
	 */
	private final IFunction function = new FunctionTanH();

	/**
	 * Creates a new {@link TanHLayer} object.
	 * 
	 * @param size int
	 */
	public TanHLayer(final int size)
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
