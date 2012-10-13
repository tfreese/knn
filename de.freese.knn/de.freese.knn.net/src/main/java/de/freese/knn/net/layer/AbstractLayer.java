/**
 * 06.06.2008
 */
package de.freese.knn.net.layer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.freese.base.core.visitor.IVisitor;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.neuron.INeuron;
import de.freese.knn.net.neuron.Neuron;

/**
 * Basisklasse eines Layers.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractLayer implements ILayer
{
	/**
	 * 
	 */
	private Matrix inputMatrix = null;

	/**
	 * 
	 */
	private List<INeuron> neurons = new ArrayList<>();

	/**
	 * 
	 */
	private Matrix outputMatrix = null;

	/**
	 * 
	 */
	private final int size;

	/**
	 * Creates a new {@link AbstractLayer} object.
	 * 
	 * @param size int
	 */
	public AbstractLayer(final int size)
	{
		super();

		if (size <= 0)
		{
			throw new IllegalArgumentException("size");
		}

		this.size = size;
		this.neurons = createNeurons(this.neurons, size);
	}

	/**
	 * Erzeugt die Neuronen des Layers.
	 * 
	 * @param neurons {@link List}
	 * @param size int
	 * @return {@link List}
	 */
	protected List<INeuron> createNeurons(final List<INeuron> neurons, final int size)
	{
		for (int i = 0; i < size; i++)
		{
			INeuron neuron = new Neuron(this, i);
			neurons.add(neuron);
		}

		// Keine Nachtraegliche Aenderung mehr moeglich.
		return Collections.unmodifiableList(neurons);
	}

	/**
	 * @see de.freese.knn.net.layer.ILayer#getInputMatrix()
	 */
	@Override
	public Matrix getInputMatrix()
	{
		return this.inputMatrix;
	}

	/**
	 * @see de.freese.knn.net.layer.ILayer#getNeurons()
	 */
	@Override
	public List<INeuron> getNeurons()
	{
		return this.neurons;
	}

	/**
	 * @see de.freese.knn.net.layer.ILayer#getOutputMatrix()
	 */
	@Override
	public Matrix getOutputMatrix()
	{
		return this.outputMatrix;
	}

	/**
	 * @see de.freese.knn.net.layer.ILayer#getSize()
	 */
	@Override
	public int getSize()
	{
		return this.size;
	}

	/**
	 * @see de.freese.knn.net.layer.ILayer#setInputMatrix(de.freese.knn.net.matrix.Matrix)
	 */
	@Override
	public void setInputMatrix(final Matrix matrix)
	{
		this.inputMatrix = matrix;
	}

	/**
	 * @see de.freese.knn.net.layer.ILayer#setOutputMatrix(de.freese.knn.net.matrix.Matrix)
	 */
	@Override
	public void setOutputMatrix(final Matrix matrix)
	{
		this.outputMatrix = matrix;
	}

	/**
	 * @see de.freese.base.core.visitor.IVisitable#visit(de.freese.base.core.visitor.IVisitor)
	 */
	@Override
	public void visit(final IVisitor visitor)
	{
		visitor.visitObject(this);
	}
}
