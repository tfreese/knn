/**
 * 06.06.2008
 */
package de.freese.knn.net.layer;

import java.util.Objects;
import de.freese.knn.net.function.Function;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.neuron.Neuron;
import de.freese.knn.net.neuron.NeuronImpl;
import de.freese.knn.net.neuron.NeuronList;

/**
 * Basisklasse eines Layers.
 *
 * @author Thomas Freese
 */
public abstract class AbstractLayer implements Layer
{
    /**
     *
     */
    private final Function function;

    /**
     *
     */
    private Matrix inputMatrix;

    /**
     *
     */
    private final NeuronList neurons;

    /**
     *
     */
    private Matrix outputMatrix;

    /**
     *
     */
    private final int size;

    /**
     * Creates a new {@link AbstractLayer} object.
     *
     * @param size int
     * @param function {@link Function}
     */
    public AbstractLayer(final int size, final Function function)
    {
        super();

        if (size <= 0)
        {
            throw new IllegalArgumentException("size");
        }

        this.size = size;
        this.function = Objects.requireNonNull(function, "function required");

        this.neurons = new NeuronList(new Neuron[size]);

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
            neurons.set(i, new NeuronImpl(this, i));
        }
    }

    /**
     * @see de.freese.knn.net.layer.Layer#getFunction()
     */
    @Override
    public Function getFunction()
    {
        return this.function;
    }

    /**
     * @see de.freese.knn.net.layer.Layer#getInputMatrix()
     */
    @Override
    public Matrix getInputMatrix()
    {
        return this.inputMatrix;
    }

    /**
     * @see de.freese.knn.net.layer.Layer#getNeurons()
     */
    @Override
    public NeuronList getNeurons()
    {
        return this.neurons;
    }

    /**
     * @see de.freese.knn.net.layer.Layer#getOutputMatrix()
     */
    @Override
    public Matrix getOutputMatrix()
    {
        return this.outputMatrix;
    }

    /**
     * @see de.freese.knn.net.layer.Layer#getSize()
     */
    @Override
    public int getSize()
    {
        return this.size;
    }

    /**
     * @see de.freese.knn.net.layer.Layer#setInputMatrix(de.freese.knn.net.matrix.Matrix)
     */
    @Override
    public void setInputMatrix(final Matrix matrix)
    {
        this.inputMatrix = matrix;
    }

    /**
     * @see de.freese.knn.net.layer.Layer#setOutputMatrix(de.freese.knn.net.matrix.Matrix)
     */
    @Override
    public void setOutputMatrix(final Matrix matrix)
    {
        this.outputMatrix = matrix;
    }
}
