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
 * Basisklasses des neuralen Netzes.
 *
 * @author Thomas Freese
 */
class NeuralNetImpl implements NeuralNet
{
    /**
     *
     */
    private KnnMath knnMath;
    /**
     * Das Array wird in der #addLayer-Methode entsprechend vergrößert.
     */
    private Layer[] layers = {};
    /**
     *
     */
    private ValueInitializer valueInitializer;

    /**
     * Creates a new {@link NeuralNetImpl} object.
     */
    NeuralNetImpl()
    {
        super();
    }

    /**
     * Fügt einen Layer hinzu.<br>
     * Der erste muss ein {@link InputLayer} sein, der letzte ein {@link OutputLayer}.
     *
     * @param layer {@link Layer}
     */
    void addLayer(final Layer layer)
    {
        // Array vergrößern.
        Layer[] array = Arrays.copyOf(this.layers, this.layers.length + 1);

        array[this.layers.length] = layer;
        this.layers = array;
    }

    /**
     * @see de.freese.knn.net.NeuralNet#close()
     */
    @Override
    public void close()
    {
        LOGGER.info("close");

        getMath().close();

        this.layers = null;
    }

    /**
     * Verbindet die Layer mit den Matrixobjekten.
     */
    void connectLayer()
    {
        for (int i = 0; i < (getLayer().length - 1); i++)
        {
            final Layer leftLayer = getLayer()[i];
            final Layer rightLayer = getLayer()[i + 1];

            final Matrix matrix = new Matrix(leftLayer.getNeurons().size(), rightLayer.getNeurons().size());

            leftLayer.setOutputMatrix(matrix);
            rightLayer.setInputMatrix(matrix);
        }

        // Gewichte initialisieren
        getMath().initialize(getValueInitializer(), getLayer());
    }

    /**
     * @see de.freese.knn.net.NeuralNet#getLayer()
     */
    @Override
    public Layer[] getLayer()
    {
        return this.layers;
    }

    /**
     * @see de.freese.knn.net.NeuralNet#getMath()
     */
    @Override
    public KnnMath getMath()
    {
        return this.knnMath;
    }

    /**
     * @see de.freese.knn.net.NeuralNet#getOutput(double[])
     */
    @Override
    public double[] getOutput(final double[] inputs)
    {
        ForwardVisitor visitor = new ForwardVisitor(false);
        visitor.setInputs(inputs);

        visit(visitor);

        final double[] outputs = getOutputLayer().adjustOutput(visitor);

        visitor.clear();

        return outputs;
    }

    /**
     * Liefert den {@link OutputLayer}.
     *
     * @return {@link OutputLayer}
     */
    private OutputLayer getOutputLayer()
    {
        return (OutputLayer) getLayer()[getLayer().length - 1];
    }

    /**
     * @return {@link ValueInitializer}
     */
    private ValueInitializer getValueInitializer()
    {
        return this.valueInitializer;
    }

    /**
     * @param knnMath {@link KnnMath}
     */
    void setKnnMath(final KnnMath knnMath)
    {
        this.knnMath = Objects.requireNonNull(knnMath, "knnMath required");
    }

    /**
     * @param valueInitializer {@link ValueInitializer}
     */
    void setValueInitializer(final ValueInitializer valueInitializer)
    {
        this.valueInitializer = Objects.requireNonNull(valueInitializer, "valueInitializer required");
    }
}
