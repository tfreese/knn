// Created: 06.06.2008
package de.freese.knn.net.layer;

import java.util.Objects;
import java.util.function.BiFunction;

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
public abstract class AbstractLayer implements Layer {
    private final Function function;
    private final NeuronList neurons;
    private final int size;

    private Matrix inputMatrix;
    private Matrix outputMatrix;

    protected AbstractLayer(final int size, final Function function) {
        this(size, function, NeuronImpl::new);
    }

    protected AbstractLayer(final int size, final Function function, final BiFunction<Layer, Integer, Neuron> neuronSupplier) {
        super();

        if (size <= 0) {
            throw new IllegalArgumentException("size <= 0: " + size);
        }

        this.size = size;
        this.function = Objects.requireNonNull(function, "function required");

        final Neuron[] neuronArray = new Neuron[size];

        for (int i = 0; i < neuronArray.length; i++) {
            neuronArray[i] = neuronSupplier.apply(this, i);
        }

        this.neurons = new NeuronList(neuronArray);
    }

    @Override
    public Function getFunction() {
        return this.function;
    }

    @Override
    public Matrix getInputMatrix() {
        return this.inputMatrix;
    }

    @Override
    public NeuronList getNeurons() {
        return this.neurons;
    }

    @Override
    public Matrix getOutputMatrix() {
        return this.outputMatrix;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public void setInputMatrix(final Matrix matrix) {
        this.inputMatrix = matrix;
    }

    @Override
    public void setOutputMatrix(final Matrix matrix) {
        this.outputMatrix = matrix;
    }
}
