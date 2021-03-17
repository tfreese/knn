/**
 * Created: 12.06.2011
 */
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
public class ForwardVisitor extends AbstractKnnVisitor
{
    /**
     *
     */
    private double[] inputs;

    /**
     * false = nur die aktuellen Outputs werden gespeichert.
     */
    private final boolean trainingMode;

    /**
     * Erstellt ein neues {@link ForwardVisitor} Object.
     *
     * @param trainingMode boolean, false = nur die aktuellen Outputs werden gespeichert
     */
    public ForwardVisitor(final boolean trainingMode)
    {
        super();

        this.trainingMode = trainingMode;
    }

    /**
     * Liefert die Output-Daten des letzten Layers.
     *
     * @return double[]
     */
    public double[] getLastOutputs()
    {
        return getValues().get(null);
    }

    /**
     * Liefert die Output-Daten des Layers.
     *
     * @param layer {@link Layer}
     * @return double[]
     */
    double[] getOutputs(final Layer layer)
    {
        Layer key = this.trainingMode ? layer : null;

        return getValues().get(key);
    }

    /**
     * Setzt die Daten des Input-Layers.
     *
     * @param inputs double[]
     */
    public void setInputs(final double[] inputs)
    {
        this.inputs = inputs;
    }

    /**
     * Setzt die Output-Daten des Layers.
     *
     * @param layer {@link Layer}
     * @param outputs double[]
     */
    public void setOutputs(final Layer layer, final double[] outputs)
    {
        if (this.trainingMode)
        {
            getValues().put(layer, outputs);
        }

        // Aktuelle Outputs merken
        getValues().put(null, outputs);
    }

    /**
     * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitHiddenLayer(de.freese.knn.net.layer.Layer)
     */
    @Override
    protected void visitHiddenLayer(final Layer layer)
    {
        getMath().forward(layer, this);
    }

    /**
     * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitInputLayer(de.freese.knn.net.layer.InputLayer)
     */
    @Override
    protected void visitInputLayer(final InputLayer layer)
    {
        setOutputs(layer, this.inputs);
    }

    /**
     * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitKNN(de.freese.knn.net.NeuralNet)
     */
    @Override
    protected void visitKNN(final NeuralNet knn)
    {
        super.visitKNN(knn);

        Layer[] layers = knn.getLayer();

        // Vorwärts
        visitArray(layers);
    }

    /**
     * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitOutputLayer(de.freese.knn.net.layer.OutputLayer)
     */
    @Override
    protected void visitOutputLayer(final OutputLayer layer)
    {
        getMath().forward(layer, this);
    }
}
