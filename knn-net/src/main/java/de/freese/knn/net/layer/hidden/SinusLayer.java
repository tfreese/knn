/**
 * 06.06.2008
 */
package de.freese.knn.net.layer.hidden;

import de.freese.knn.net.function.FunctionSinus;
import de.freese.knn.net.function.IFunction;
import de.freese.knn.net.layer.AbstractLayer;

/**
 * HiddenLayer für eine Sinus Aktivierungsfunktion.
 * 
 * @author Thomas Freese
 */
public class SinusLayer extends AbstractLayer
{
	/**
	 * 
	 */
	private final IFunction function = new FunctionSinus();

	/**
	 * Creates a new {@link SinusLayer} object.
	 * 
	 * @param size int
	 */
	public SinusLayer(final int size)
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
