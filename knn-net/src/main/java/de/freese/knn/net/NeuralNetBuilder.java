/**
 * Created: 13.02.2019
 */

package de.freese.knn.net;

import java.util.ArrayList;
import java.util.List;
import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.math.KnnMath;
import de.freese.knn.net.math.stream.KnnMathStream;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.matrix.ValueInitializerRandom;

/**
 * @author Thomas Freese
 */
public class NeuralNetBuilder
{
    /**
     *
     */
    private KnnMath knnMath = null;

    /**
     *
     */
    private final List<Layer> layer = new ArrayList<>();

    /**
     *
     */
    private ValueInitializer valueInitializer = null;

    /**
     * Erstellt ein neues {@link NeuralNetBuilder} Object.
     */
    public NeuralNetBuilder()
    {
        super();
    }

    /**
     * @return {@link NeuralNet}
     */
    @SuppressWarnings("resource")
    public NeuralNet build()
    {
        NeuralNet neuralNet = new NeuralNet();

        if (this.knnMath != null)
        {
            neuralNet.setKnnMath(this.knnMath);
        }
        else
        {
            neuralNet.setKnnMath(new KnnMathStream());
        }

        if (this.valueInitializer != null)
        {
            neuralNet.setValueInitializer(this.valueInitializer);
        }
        else
        {
            neuralNet.setValueInitializer(new ValueInitializerRandom());
        }

        if (this.layer.size() < 3)
        {
            throw new IllegalStateException("neuralNetwork need min. 3 Layer: InputLayer, HiddenLayer, OutputLayer");
        }

        if (!(this.layer.get(0) instanceof InputLayer))
        {
            throw new IllegalStateException("first layer must be an InputLayer");
        }

        for (int i = 1; i < (this.layer.size() - 1); i++)
        {
            if (!(this.layer.get(i) instanceof HiddenLayer))
            {
                throw new IllegalStateException("HiddenLayer expected, found " + this.layer.get(i).getClass().getSimpleName());
            }
        }

        if (!(this.layer.get(this.layer.size() - 1) instanceof OutputLayer))
        {
            throw new IllegalStateException("last layer must be an OutputLayer");
        }

        for (Layer l : this.layer)
        {
            neuralNet.addLayer(l);
        }

        neuralNet.connectLayer();

        return neuralNet;
    }

    /**
     * @param knnMath {@link KnnMath}
     * @return {@link NeuralNetBuilder}
     */
    public NeuralNetBuilder knnMath(final KnnMath knnMath)
    {
        this.knnMath = knnMath;

        return this;
    }

    /**
     * @param layer {@link Layer}
     * @return {@link NeuralNetBuilder}
     */
    public NeuralNetBuilder layer(final Layer layer)
    {
        this.layer.add(layer);

        return this;
    }

    /**
     * @param valueInitializer {@link ValueInitializer}
     * @return {@link NeuralNetBuilder}
     */
    public NeuralNetBuilder valueInitializer(final ValueInitializer valueInitializer)
    {
        this.valueInitializer = valueInitializer;

        return this;
    }
}
