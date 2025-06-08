// Created: 12.06.2011
package de.freese.knn.net.visitor;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.layer.OutputLayer;

/**
 * Durchläuft vorwärts das Netz und sammelt die Outputs der Layer ein.
 *
 * @author Thomas Freese
 */
public class ForwardVisitor extends AbstractKnnVisitor {
    /**
     * false = nur die aktuellen Outputs werden gespeichert.
     */
    private final boolean trainingMode;
    private double[] inputs;

    /**
     * @param trainingMode boolean, false = nur die aktuellen Outputs werden gespeichert
     */
    public ForwardVisitor(final boolean trainingMode) {
        super();

        this.trainingMode = trainingMode;
    }

    /**
     * Liefert die Output-Daten des letzten Layers.
     */
    public double[] getLastOutputs() {
        return getValues().get(null);
    }

    /**
     * Setzt die Daten des Input-Layers.
     */
    public void setInputs(final double[] inputs) {
        this.inputs = inputs;
    }

    /**
     * Setzt die Output-Daten des Layers.
     */
    public void setOutputs(final Layer layer, final double[] outputs) {
        if (trainingMode) {
            getValues().put(layer, outputs);
        }

        // Aktuelle Outputs merken
        getValues().put(null, outputs);
    }

    /**
     * Liefert die Output-Daten des Layers.
     */
    double[] getOutputs(final Layer layer) {
        final Layer key = trainingMode ? layer : null;

        return getValues().get(key);
    }

    @Override
    protected void visitHiddenLayer(final Layer layer) {
        getMath().forward(layer, this);
    }

    @Override
    protected void visitInputLayer(final InputLayer layer) {
        setOutputs(layer, inputs);
    }

    @Override
    protected void visitKnn(final NeuralNet knn) {
        super.visitKnn(knn);

        final Layer[] layers = knn.getLayer();

        // Vorwärts
        visitArray(layers);
    }

    @Override
    protected void visitOutputLayer(final OutputLayer layer) {
        getMath().forward(layer, this);
    }
}
