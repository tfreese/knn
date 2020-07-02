package de.freese.knn.net.visitor;

import java.util.HashMap;
import java.util.Map;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.math.KnnMath;

/**
 * Basisklasse eines Netz-Visitors.
 *
 * @author Thomas Freese
 */
public abstract class AbstractKnnVisitor implements Visitor
{
    /**
     *
     */
    private KnnMath knnMath = null;

    /**
     *
     */
    private Map<Layer, double[]> values = new HashMap<>();

    /**
     * Erstellt ein neues {@link AbstractKnnVisitor} Object.
     */
    public AbstractKnnVisitor()
    {
        super();
    }

    /**
     * Aufräumen.
     */
    public void clear()
    {
        this.values.clear();
        this.values = null;
    }

    /**
     * @return {@link KnnMath}
     */
    protected KnnMath getMath()
    {
        return this.knnMath;
    }

    /**
     * @return {@link Map}<ILayer,double[]>
     */
    protected Map<Layer, double[]> getValues()
    {
        return this.values;
    }

    /**
     * @param layer {@link Layer}
     */
    protected abstract void visitHiddenLayer(final Layer layer);

    /**
     * @param layer {@link InputLayer}
     */
    protected abstract void visitInputLayer(final InputLayer layer);

    /**
     * @param knn {@link NeuralNet}
     */
    protected void visitKNN(final NeuralNet knn)
    {
        this.knnMath = knn.getMath();
    }

    /**
     * @see de.freese.knn.net.visitor.Visitor#visitObject(java.lang.Object)
     */
    @Override
    public void visitObject(final Object object)
    {
        if (object instanceof NeuralNet)
        {
            visitKNN((NeuralNet) object);
        }
        else if (object instanceof InputLayer)
        {
            visitInputLayer((InputLayer) object);
        }
        else if (object instanceof OutputLayer)
        {
            visitOutputLayer((OutputLayer) object);
        }
        else if (object instanceof Layer)
        {
            visitHiddenLayer((Layer) object);
        }
    }

    /**
     * @param layer {@link OutputLayer}
     */
    protected abstract void visitOutputLayer(final OutputLayer layer);
}
