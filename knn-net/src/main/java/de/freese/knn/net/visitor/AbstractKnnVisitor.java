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
    private KnnMath knnMath;

    private Map<Layer, double[]> values = new HashMap<>();

    public void clear()
    {
        this.values.clear();
        this.values = null;
    }

    /**
     * @see de.freese.knn.net.visitor.Visitor#visitObject(java.lang.Object)
     */
    @Override
    public void visitObject(final Object object)
    {
        if (object instanceof NeuralNet o)
        {
            visitKNN(o);
        }
        else if (object instanceof InputLayer o)
        {
            visitInputLayer(o);
        }
        else if (object instanceof OutputLayer o)
        {
            visitOutputLayer(o);
        }
        else if (object instanceof Layer o)
        {
            visitHiddenLayer(o);
        }
    }

    protected KnnMath getMath()
    {
        return this.knnMath;
    }

    protected Map<Layer, double[]> getValues()
    {
        return this.values;
    }

    protected abstract void visitHiddenLayer(Layer layer);

    protected abstract void visitInputLayer(InputLayer layer);

    protected void visitKNN(NeuralNet knn)
    {
        this.knnMath = knn.getMath();
    }

    protected abstract void visitOutputLayer(OutputLayer layer);
}
