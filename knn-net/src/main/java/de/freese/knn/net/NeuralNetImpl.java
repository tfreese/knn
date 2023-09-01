// Created: 06.06.2008
package de.freese.knn.net;

import java.util.Arrays;
import java.util.Objects;

import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.math.KnnMath;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Basisklasse des neuralen Netzes.
 *
 * @author Thomas Freese
 */
class NeuralNetImpl implements NeuralNet {
    private KnnMath knnMath;
    /**
     * Das Array wird in der #addLayer-Methode entsprechend vergrößert.
     */
    private Layer[] layers = {};

    private ValueInitializer valueInitializer;

    NeuralNetImpl() {
        super();
    }

    @Override
    public void close() {
        LOGGER.info("close");

        getMath().close();

        this.layers = null;
    }

    @Override
    public Layer[] getLayer() {
        return this.layers;
    }

    @Override
    public KnnMath getMath() {
        return this.knnMath;
    }

    @Override
    public double[] getOutput(final double[] inputs) {
        ForwardVisitor visitor = new ForwardVisitor(false);
        visitor.setInputs(inputs);

        visit(visitor);

        final double[] outputs = getOutputLayer().adjustOutput(visitor);

        visitor.clear();

        return outputs;
    }

    /**
     * Der erste muss ein {@link InputLayer} sein, der letzte ein {@link OutputLayer}.
     */
    void addLayer(final Layer layer) {
        // Array vergrößern.
        Layer[] array = Arrays.copyOf(this.layers, this.layers.length + 1);

        array[this.layers.length] = layer;
        this.layers = array;
    }

    /**
     * Verbindet die Layer mit den Matrixobjekten.
     */
    void connectLayer() {
        for (int i = 0; i < (getLayer().length - 1); i++) {
            final Layer leftLayer = getLayer()[i];
            final Layer rightLayer = getLayer()[i + 1];

            final Matrix matrix = new Matrix(leftLayer.getNeurons().size(), rightLayer.getNeurons().size());

            leftLayer.setOutputMatrix(matrix);
            rightLayer.setInputMatrix(matrix);
        }

        // Gewichte initialisieren
        getMath().initialize(getValueInitializer(), getLayer());
    }

    void setKnnMath(final KnnMath knnMath) {
        this.knnMath = Objects.requireNonNull(knnMath, "knnMath required");
    }

    void setValueInitializer(final ValueInitializer valueInitializer) {
        this.valueInitializer = Objects.requireNonNull(valueInitializer, "valueInitializer required");
    }

    private OutputLayer getOutputLayer() {
        return (OutputLayer) getLayer()[getLayer().length - 1];
    }

    private ValueInitializer getValueInitializer() {
        return this.valueInitializer;
    }
}
