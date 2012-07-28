/**
 * 06.06.2008
 */
package de.freese.knn.net.layer.hidden;

import de.freese.knn.net.function.FunctionSigmoide;
import de.freese.knn.net.function.IFunction;
import de.freese.knn.net.layer.AbstractLayer;

/**
 * HiddenLayer für eine sigmoide Aktivierungsfunktion.
 * 
 * @author Thomas Freese
 */
public class SigmoidLayer extends AbstractLayer
{
	/**
	 *
	 */
	private final IFunction function = new FunctionSigmoide();

	/**
	 * Creates a new {@link SigmoidLayer} object.
	 * 
	 * @param size int
	 */
	public SigmoidLayer(final int size)
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
