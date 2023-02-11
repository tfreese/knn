// Created: 17.07.2011
package de.freese.knn.net.neuron;

import de.freese.knn.net.function.Function;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.matrix.Matrix;

/**
 * Default-Neuron des Netzes.
 *
 * @author Thomas Freese
 */
public class NeuronImpl implements Neuron {
    private final Layer layer;
    private final int layerIndex;
    private double inputBIAS;

    public NeuronImpl(final Layer layer, final int layerIndex) {
        super();

        this.layer = layer;
        this.layerIndex = layerIndex;
    }

    /**
     * @see de.freese.knn.net.neuron.Neuron#getFunction()
     */
    @Override
    public Function getFunction() {
        return this.layer.getFunction();
    }

    /**
     * @see de.freese.knn.net.neuron.Neuron#getInputBIAS()
     */
    @Override
    public double getInputBIAS() {
        return this.inputBIAS;
    }

    /**
     * @see de.freese.knn.net.neuron.Neuron#getInputSize()
     */
    @Override
    public int getInputSize() {
        Matrix matrix = getInputMatrix();

        if (matrix != null) {
            return matrix.getWeights().length;
        }

        return 0;
    }

    /**
     * @see de.freese.knn.net.neuron.Neuron#getInputWeight(int)
     */
    @Override
    public double getInputWeight(final int index) {
        Matrix matrix = getInputMatrix();

        if (matrix != null) {
            return matrix.getWeights()[index][getLayerIndex()];
        }

        return 0.0D;
    }

    /**
     * @see de.freese.knn.net.neuron.Neuron#getLayerIndex()
     */
    @Override
    public int getLayerIndex() {
        return this.layerIndex;
    }

    /**
     * @see de.freese.knn.net.neuron.Neuron#getOutputSize()
     */
    @Override
    public int getOutputSize() {
        Matrix matrix = getOutputMatrix();

        if (matrix != null) {
            return matrix.getWeights()[0].length;
        }

        return 0;
    }

    /**
     * @see de.freese.knn.net.neuron.Neuron#getOutputWeight(int)
     */
    @Override
    public double getOutputWeight(final int index) {
        Matrix matrix = getOutputMatrix();

        if (matrix != null) {
            return matrix.getWeights()[getLayerIndex()][index];
        }

        return 0.0D;
    }

    /**
     * @see de.freese.knn.net.neuron.Neuron#setInputBIAS(double)
     */
    @Override
    public void setInputBIAS(final double value) {
        this.inputBIAS = value;
    }

    /**
     * @see de.freese.knn.net.neuron.Neuron#setInputWeight(int, double)
     */
    @Override
    public void setInputWeight(final int index, final double weight) {
        Matrix matrix = getInputMatrix();

        if (matrix != null) {
            matrix.getWeights()[index][getLayerIndex()] = weight;
        }
    }

    /**
     * @see de.freese.knn.net.neuron.Neuron#setOutputWeight(int, double)
     */
    @Override
    public void setOutputWeight(final int index, final double weight) {
        Matrix matrix = getOutputMatrix();

        if (matrix != null) {
            matrix.getWeights()[getLayerIndex()][index] = weight;
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Neuron: ");
        sb.append("layerIndex=").append(this.layerIndex);
        sb.append("/").append(this.layer.getSize() - 1);

        return sb.toString();
    }

    /**
     * Liefert die Eingangsmatrix des Layers.<br>
     * Der {@link InputLayer} hat keine Eingangsmatrix !
     */
    protected Matrix getInputMatrix() {
        return this.layer.getInputMatrix();
    }

    /**
     * Liefert die Ausgangsmatrix des Layers.<br>
     * Der {@link OutputLayer} hat keine Ausgangsmatrix !
     */
    protected Matrix getOutputMatrix() {
        return this.layer.getOutputMatrix();
    }
}
