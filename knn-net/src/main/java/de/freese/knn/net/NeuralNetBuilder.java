// Created: 13.02.2019
package de.freese.knn.net;

import java.util.ArrayList;
import java.util.List;

import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
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
    private final List<HiddenLayer> hiddenLayers = new ArrayList<>();
    /**
     *
     */
    private InputLayer inputLayer;
    /**
     *
     */
    private KnnMath knnMath;
    /**
     *
     */
    private OutputLayer outputLayer;
    /**
     *
     */
    private ValueInitializer valueInitializer;

    /**
     * @return {@link NeuralNet}
     */
    public NeuralNet build()
    {
        return build(true);
    }

    /**
     * @param connectLayer boolean
     *
     * @return {@link NeuralNet}
     */
    public NeuralNet build(final boolean connectLayer)
    {
        NeuralNetImpl neuralNet = new NeuralNetImpl();

        // KnnMath
        if (this.knnMath != null)
        {
            neuralNet.setKnnMath(this.knnMath);
        }
        else
        {
            neuralNet.setKnnMath(new KnnMathStream());
        }

        // ValueInitializer
        if (this.valueInitializer != null)
        {
            neuralNet.setValueInitializer(this.valueInitializer);
        }
        else
        {
            neuralNet.setValueInitializer(new ValueInitializerRandom());
        }

        // InputLayer
        if (this.inputLayer == null)
        {
            throw new IllegalArgumentException("InputLayer required");
        }

        neuralNet.addLayer(this.inputLayer);

        // HiddenLayer
        if (this.hiddenLayers.isEmpty())
        {
            throw new IllegalArgumentException("HiddenLayer required");
        }

        for (HiddenLayer l : this.hiddenLayers)
        {
            neuralNet.addLayer(l);
        }

        // OutputLayer
        if (this.outputLayer == null)
        {
            throw new IllegalArgumentException("OutputLayer required");
        }

        neuralNet.addLayer(this.outputLayer);

        if (connectLayer)
        {
            neuralNet.connectLayer();
        }

        return neuralNet;
    }

    /**
     * @param knnMath {@link KnnMath}
     *
     * @return {@link NeuralNetBuilder}
     */
    public NeuralNetBuilder knnMath(final KnnMath knnMath)
    {
        this.knnMath = knnMath;

        return this;
    }

    /**
     * @param hiddenLayer {@link HiddenLayer}
     *
     * @return {@link NeuralNetBuilder}
     */
    public NeuralNetBuilder layerHidden(final HiddenLayer hiddenLayer)
    {
        this.hiddenLayers.add(hiddenLayer);

        return this;
    }

    /**
     * @param inputLayer {@link InputLayer}
     *
     * @return {@link NeuralNetBuilder}
     */
    public NeuralNetBuilder layerInput(final InputLayer inputLayer)
    {
        this.inputLayer = inputLayer;

        return this;
    }

    /**
     * @param outputLayer {@link OutputLayer}
     *
     * @return {@link NeuralNetBuilder}
     */
    public NeuralNetBuilder layerOutput(final OutputLayer outputLayer)
    {
        this.outputLayer = outputLayer;

        return this;
    }

    /**
     * @param valueInitializer {@link ValueInitializer}
     *
     * @return {@link NeuralNetBuilder}
     */
    public NeuralNetBuilder valueInitializer(final ValueInitializer valueInitializer)
    {
        this.valueInitializer = valueInitializer;

        return this;
    }
}
