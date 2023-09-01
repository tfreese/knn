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

    @Override
    public Function getFunction() {
        return this.layer.getFunction();
    }

    @Override
    public double getInputBIAS() {
        return this.inputBIAS;
    }

    @Override
    public int getInputSize() {
        Matrix matrix = getInputMatrix();

        if (matrix != null) {
            return matrix.getWeights().length;
        }

        return 0;
    }

    @Override
    public double getInputWeight(final int index) {
        Matrix matrix = getInputMatrix();

        if (matrix != null) {
            return matrix.getWeights()[index][getLayerIndex()];
        }

        return 0.0D;
    }

    @Override
    public int getLayerIndex() {
        return this.layerIndex;
    }

    @Override
    public int getOutputSize() {
        Matrix matrix = getOutputMatrix();

        if (matrix != null) {
            return matrix.getWeights()[0].length;
        }

        return 0;
    }

    @Override
    public double getOutputWeight(final int index) {
        Matrix matrix = getOutputMatrix();

        if (matrix != null) {
            return matrix.getWeights()[getLayerIndex()][index];
        }

        return 0.0D;
    }

    @Override
    public void setInputBIAS(final double value) {
        this.inputBIAS = value;
    }

    @Override
    public void setInputWeight(final int index, final double weight) {
        Matrix matrix = getInputMatrix();

        if (matrix != null) {
            matrix.getWeights()[index][getLayerIndex()] = weight;
        }
    }

    @Override
    public void setOutputWeight(final int index, final double weight) {
        Matrix matrix = getOutputMatrix();

        if (matrix != null) {
            matrix.getWeights()[getLayerIndex()][index] = weight;
        }
    }

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
