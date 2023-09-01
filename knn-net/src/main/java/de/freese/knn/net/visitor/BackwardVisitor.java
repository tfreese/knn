// Created: 12.06.2011
package de.freese.knn.net.visitor;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.trainer.NetTrainer;
import de.freese.knn.net.trainer.TrainingContext;

/**
 * Durchläuft rückwärts das Netz und berechnet die Fehler der Layer.
 *
 * @author Thomas Freese
 */
public class BackwardVisitor extends AbstractKnnVisitor {
    private final ForwardVisitor forwardVisitor;

    private final TrainingContext trainingContext;

    private double[] outputTargets;

    public BackwardVisitor(final TrainingContext trainingContext, final ForwardVisitor forwardVisitor) {
        super();

        this.trainingContext = trainingContext;
        this.forwardVisitor = forwardVisitor;
    }

    @Override
    public void clear() {
        super.clear();

        this.forwardVisitor.clear();
    }

    /**
     * Liefert die vorherigen Gewichtsänderungen der Neuronen.
     */
    public double[][] getDeltaWeights(final Layer layer) {
        Matrix matrix = layer.getOutputMatrix();

        return this.trainingContext.getDeltaWeights(matrix);
    }

    /**
     * Setzt die Fehler-Daten des Layers.
     */
    public double[] getErrors(final Layer layer) {
        return getValues().get(layer);
    }

    /**
     * Liefert die Fehler-Daten des letzten Layers.
     */
    public double[] getLastErrors() {
        return getValues().get(null);
    }

    /**
     * Liefert den aktuellen Netzfehler.
     */
    public double getNetError() {
        double[] outputs = getLastOutputs();
        double[] targets = getOutputTargets();

        return getMath().getNetError(outputs, targets);
    }

    /**
     * Liefert die Ausgabeziele der Neuronen, wird im {@link NetTrainer} benötigt.
     */
    public double[] getOutputTargets() {
        return this.outputTargets;
    }

    /**
     * Setzt die Output-Daten des Layers.
     */
    public double[] getOutputs(final Layer layer) {
        return this.forwardVisitor.getOutputs(layer);
    }

    /**
     * Setzt die Fehler-Daten des Layers.
     */
    public void setErrors(final Layer layer, final double[] errors) {
        getValues().put(layer, errors);

        // Aktuelle Fehler merken
        getValues().put(null, errors);
    }

    /**
     * Setzt die Ausgabeziele der Neuronen, wird im {@link NetTrainer} benötigt.
     */
    public void setOutputTargets(final double[] outputTargets) {
        this.outputTargets = outputTargets;
    }

    @Override
    protected void visitHiddenLayer(final Layer layer) {
        getMath().backward(layer, this);
    }

    @Override
    protected void visitInputLayer(final InputLayer layer) {
        getMath().backward(layer, this);
    }

    @Override
    protected void visitKNN(final NeuralNet knn) {
        super.visitKNN(knn);

        Layer[] layers = knn.getLayer();

        // Rückwärts
        for (int i = layers.length - 1; i >= 0; i--) {
            visitObject(layers[i]);
        }
    }

    @Override
    protected void visitOutputLayer(final OutputLayer layer) {
        getMath().setOutputError(layer, this);
    }

    /**
     * Liefert die Output-Daten des letzten Layers.
     */
    private double[] getLastOutputs() {
        return this.forwardVisitor.getLastOutputs();
    }
}
