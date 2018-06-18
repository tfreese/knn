package de.freese.knn.net.visitor;

import java.util.HashMap;
import java.util.Map;
import de.freese.base.core.visitor.Visitor;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;
import de.freese.knn.net.math.IKnnMath;

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
    private IKnnMath knnMath = null;

    /**
     *
     */
    private Map<ILayer, double[]> values = new HashMap<>();

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
     * @return {@link IKnnMath}
     */
    protected IKnnMath getMath()
    {
        return this.knnMath;
    }

    /**
     * @return {@link Map}<ILayer,double[]>
     */
    protected Map<ILayer, double[]> getValues()
    {
        return this.values;
    }

    /**
     * @param layer {@link ILayer}
     */
    protected abstract void visitHiddenLayer(final ILayer layer);

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
     * @see de.freese.base.core.visitor.Visitor#visitObject(java.lang.Object)
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
        else if (object instanceof ILayer)
        {
            visitHiddenLayer((ILayer) object);
        }
    }

    /**
     * @param layer {@link OutputLayer}
     */
    protected abstract void visitOutputLayer(final OutputLayer layer);
}
