/**
 * 06.06.2008
 */
package de.freese.knn.net.layer.hidden;

import de.freese.knn.net.function.FunctionGauss;
import de.freese.knn.net.function.IFunction;
import de.freese.knn.net.layer.AbstractLayer;

/**
 * HiddenLayer für eine Gauss Aktivierungsfunktion.
 * 
 * @author Thomas Freese
 */
public class GaussLayer extends AbstractLayer
{
	/**
	 *
	 */
	private IFunction function = new FunctionGauss();

	/**
	 * Creates a new {@link GaussLayer} object.
	 * 
	 * @param size int
	 */
	public GaussLayer(final int size)
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
