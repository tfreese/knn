// Created: 13.02.2019
package de.freese.knn.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.math.KnnMath;
import de.freese.knn.net.math.KnnMathStream;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.matrix.ValueInitializerRandom;

/**
 * @author Thomas Freese
 */
public class NeuralNetBuilder
{
    private final List<HiddenLayer> hiddenLayers = new ArrayList<>();

    private InputLayer inputLayer;

    private KnnMath knnMath;

    private OutputLayer outputLayer;

    private ValueInitializer valueInitializer;

    public NeuralNet build()
    {
        return build(true);
    }

    public NeuralNet build(final boolean connectLayer)
    {
        NeuralNetImpl neuralNet = new NeuralNetImpl();

        // KnnMath
        neuralNet.setKnnMath(Objects.requireNonNullElseGet(this.knnMath, KnnMathStream::new));

        // ValueInitializer
        neuralNet.setValueInitializer(Objects.requireNonNullElseGet(this.valueInitializer, ValueInitializerRandom::new));

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
     * Default: {@link KnnMathStream}
     */
    public NeuralNetBuilder knnMath(final KnnMath knnMath)
    {
        this.knnMath = knnMath;

        return this;
    }

    public NeuralNetBuilder layerHidden(final HiddenLayer hiddenLayer)
    {
        this.hiddenLayers.add(hiddenLayer);

        return this;
    }

    public NeuralNetBuilder layerInput(final InputLayer inputLayer)
    {
        this.inputLayer = inputLayer;

        return this;
    }

    public NeuralNetBuilder layerOutput(final OutputLayer outputLayer)
    {
        this.outputLayer = outputLayer;

        return this;
    }

    /**
     * Default: {@link ValueInitializerRandom}
     */
    public NeuralNetBuilder valueInitializer(final ValueInitializer valueInitializer)
    {
        this.valueInitializer = valueInitializer;

        return this;
    }
}
