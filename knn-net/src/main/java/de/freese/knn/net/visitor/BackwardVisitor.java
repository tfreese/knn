/**
 * Created: 12.06.2011
 */

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
public class BackwardVisitor extends AbstractKnnVisitor
{
    /**
     *
     */
    private final ForwardVisitor forwardVisitor;

    /**
     *
     */
    private double[] outputTargets;

    /**
     *
     */
    private final TrainingContext trainingContext;

    /**
     * Erstellt ein neues {@link BackwardVisitor} Object.
     *
     * @param trainingContext {@link TrainingContext} 
     * @param forwardVisitor {@link ForwardVisitor}
     */
    public BackwardVisitor(final TrainingContext trainingContext, final ForwardVisitor forwardVisitor)
    {
        super();

        this.trainingContext = trainingContext;
        this.forwardVisitor = forwardVisitor;
    }

    /**
     * @see de.freese.knn.net.visitor.AbstractKnnVisitor#clear()
     */
    @Override
    public void clear()
    {
        super.clear();

        this.forwardVisitor.clear();
    }

    /**
     * Liefert die vorherige Gewichtsänderungen der Neuronen.
     *
     * @param layer {@link Layer}
     * @return double[][]
     */
    public double[][] getDeltaWeights(final Layer layer)
    {
        Matrix matrix = layer.getOutputMatrix();

        return this.trainingContext.getDeltaWeights(matrix);
    }

    /**
     * Setzt die Fehler-Daten des Layers.
     *
     * @param layer {@link Layer}
     * @return double[]
     */
    public double[] getErrors(final Layer layer)
    {
        return getValues().get(layer);
    }

    /**
     * Liefert die Fehler-Daten des letzten Layers.
     *
     * @return double[]
     */
    public double[] getLastErrors()
    {
        return getValues().get(null);
    }

    /**
     * Liefert die Output-Daten des letzten Layers.
     *
     * @return double[]
     */
    private double[] getLastOutputs()
    {
        return this.forwardVisitor.getLastOutputs();
    }

    /**
     * Liefert den aktuellen Netzfehler.
     *
     * @return double
     */
    public double getNetError()
    {
        double[] outputs = getLastOutputs();
        double[] targets = getOutputTargets();

        double error = getMath().getNetError(outputs, targets);

        return error;
    }

    /**
     * Setzt die Output-Daten des Layers.
     *
     * @param layer {@link Layer}
     * @return double[]
     */
    public double[] getOutputs(final Layer layer)
    {
        return this.forwardVisitor.getOutputs(layer);
    }

    /**
     * Liefert die Ausgabeziele der Neuronen, wird im {@link NetTrainer} benötigt.
     *
     * @return double[]
     */
    public double[] getOutputTargets()
    {
        return this.outputTargets;
    }

    /**
     * Setzt die Fehler-Daten des Layers.
     *
     * @param layer {@link Layer}
     * @param errors double[]
     */
    public void setErrors(final Layer layer, final double[] errors)
    {
        getValues().put(layer, errors);

        // Aktuelle Fehler merken
        getValues().put(null, errors);
    }

    /**
     * Setzt die Ausgabeziele der Neuronen, wird im {@link NetTrainer} benötigt.
     *
     * @param outputTargets double[]
     */
    public void setOutputTargets(final double[] outputTargets)
    {
        this.outputTargets = outputTargets;
    }

    /**
     * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitHiddenLayer(de.freese.knn.net.layer.Layer)
     */
    @Override
    protected void visitHiddenLayer(final Layer layer)
    {
        getMath().backward(layer, this);
    }

    /**
     * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitInputLayer(de.freese.knn.net.layer.InputLayer)
     */
    @Override
    protected void visitInputLayer(final InputLayer layer)
    {
        getMath().backward(layer, this);
    }

    /**
     * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitKNN(de.freese.knn.net.NeuralNet)
     */
    @Override
    protected void visitKNN(final NeuralNet knn)
    {
        super.visitKNN(knn);

        Layer[] layers = knn.getLayer();

        // Rückwärts
        for (int i = layers.length - 1; i >= 0; i--)
        {
            visitObject(layers[i]);
        }
    }

    /**
     * @see de.freese.knn.net.visitor.AbstractKnnVisitor#visitOutputLayer(de.freese.knn.net.layer.OutputLayer)
     */
    @Override
    protected void visitOutputLayer(final OutputLayer layer)
    {
        getMath().setOutputError(layer, this);
    }
}
