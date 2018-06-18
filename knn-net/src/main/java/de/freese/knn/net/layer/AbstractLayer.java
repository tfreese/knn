/**
 * 06.06.2008
 */
package de.freese.knn.net.layer;

import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.neuron.INeuron;
import de.freese.knn.net.neuron.Neuron;
import de.freese.knn.net.neuron.NeuronList;

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
    private final NeuronList neurons;

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
        this.neurons = new NeuronList(new INeuron[size]);
        createNeurons(this.neurons);
    }

    /**
     * Erzeugt die Neuronen des Layers.
     *
     * @param neurons {@link NeuronList}
     */
    protected void createNeurons(final NeuronList neurons)
    {
        for (int i = 0; i < neurons.size(); i++)
        {
            neurons.set(i, new Neuron(this, i));
        }
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
    public NeuronList getNeurons()
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
}
