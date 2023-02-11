// Created: 11.06.2008
package de.freese.knn.net.persister;

import java.io.DataInput;
import java.io.DataOutput;
import java.lang.reflect.Constructor;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.NeuralNetBuilder;
import de.freese.knn.net.function.Function;
import de.freese.knn.net.function.FunctionBinary;
import de.freese.knn.net.function.FunctionGauss;
import de.freese.knn.net.function.FunctionLinear;
import de.freese.knn.net.function.FunctionLogarithmic;
import de.freese.knn.net.function.FunctionSigmoid;
import de.freese.knn.net.function.FunctionSinus;
import de.freese.knn.net.function.FunctionTanH;
import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.neuron.Neuron;
import de.freese.knn.net.neuron.NeuronList;

/**
 * NetPersister für das laden und speichen eines neuralen Netzes im Binärformat.
 *
 * @author Thomas Freese
 */
public class NetPersisterBinary implements NetPersister<DataInput, DataOutput> {
    /**
     * @see de.freese.knn.net.persister.NetPersister#load(java.lang.Object)
     */
    @Override
    public NeuralNet load(final DataInput input) throws Exception {
        NeuralNetBuilder builder = new NeuralNetBuilder();

        // Anzahl Layer lesen
        int layerCount = input.readInt();

        Layer leftLayer = loadLayer(input);
        builder.layerInput((InputLayer) leftLayer);

        for (int i = 0; i < (layerCount - 1); i++) {
            Matrix matrix = loadMatrix(input);

            Layer rightLayer = loadLayer(input);

            if (i < (layerCount - 2)) {
                builder.layerHidden((HiddenLayer) rightLayer);
            }
            else {
                builder.layerOutput((OutputLayer) rightLayer);
            }

            // Layer verknüpfen
            leftLayer.setOutputMatrix(matrix);
            rightLayer.setInputMatrix(matrix);

            leftLayer = rightLayer;
        }

        return builder.build(false);
    }

    /**
     * @see de.freese.knn.net.persister.NetPersister#save(java.lang.Object, de.freese.knn.net.NeuralNet)
     */
    @Override
    public void save(final DataOutput output, final NeuralNet knn) throws Exception {
        // Anzahl Layer
        Layer[] layers = knn.getLayer();

        output.writeInt(layers.length);

        for (int i = 0; i < layers.length; i++) {
            Layer layer = layers[i];
            saveLayer(output, layer);

            if (i < (layers.length - 1)) {
                saveMatrix(output, layer.getOutputMatrix());
            }
        }
    }

    protected Function loadFunction(final DataInput input) throws Exception {
        // Klassentyp
        String clazzName = input.readUTF();

        Class<?> clazz = Class.forName(clazzName);
        Function function = null;

        // Funktions-Parameter
        if (FunctionBinary.class.equals(clazz)) {
            double threshold = input.readDouble();

            function = new FunctionBinary(threshold);
        }
        else if (FunctionGauss.class.equals(clazz)) {
            function = new FunctionGauss();
        }
        else if (FunctionLinear.class.equals(clazz)) {
            double factor = input.readDouble();

            function = new FunctionLinear(factor);
        }
        else if (FunctionLogarithmic.class.equals(clazz)) {
            function = new FunctionLogarithmic();
        }
        else if (FunctionSigmoid.class.equals(clazz)) {
            double durchgang = input.readDouble();
            double steigung = input.readDouble();

            function = new FunctionSigmoid(durchgang, steigung);
        }
        else if (FunctionSinus.class.equals(clazz)) {
            function = new FunctionSinus();
        }
        else if (FunctionTanH.class.equals(clazz)) {
            function = new FunctionTanH();
        }
        else {
            throw new UnsupportedOperationException("unknown function type: " + clazz.getName());
        }

        return function;
    }

    protected Layer loadLayer(final DataInput input) throws Exception {
        // Klassentyp
        String clazzName = input.readUTF();

        // Neuronen
        int size = input.readInt();

        Function function = loadFunction(input);

        Class<?> clazz = Class.forName(clazzName);
        Layer layer = null;

        if (HiddenLayer.class.equals(clazz)) {
            Constructor<?> constructor = clazz.getConstructor(int.class, Function.class);
            layer = (Layer) constructor.newInstance(size, function);
        }
        else {
            Constructor<?> constructor = clazz.getConstructor(int.class);
            layer = (Layer) constructor.newInstance(size);
        }

        // BIAS Gewichte der Neuronen
        for (Neuron neuron : layer.getNeurons()) {
            neuron.setInputBIAS(input.readDouble());
        }

        return layer;
    }

    protected Matrix loadMatrix(final DataInput input) throws Exception {
        int inputSize = input.readInt();
        int outputSize = input.readInt();

        Matrix matrix = new Matrix(inputSize, outputSize);

        // Gewichte
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                matrix.getWeights()[i][j] = input.readDouble();
            }
        }

        return matrix;
    }

    protected void saveFunction(final DataOutput output, final Function function) throws Exception {
        // Klassentyp
        output.writeUTF(function.getClass().getName());

        // Funktions-Parameter
        if (function instanceof FunctionBinary f) {
            output.writeDouble(f.getThreshold());
        }
        else if (function instanceof FunctionLinear f) {
            output.writeDouble(f.getFactor());
        }
        else if (function instanceof FunctionSigmoid f) {
            output.writeDouble(f.getDurchgang());
            output.writeDouble(f.getSteigung());
        }
    }

    protected void saveLayer(final DataOutput output, final Layer layer) throws Exception {
        // Klassentyp
        output.writeUTF(layer.getClass().getName());

        // Neuronen
        NeuronList neurons = layer.getNeurons();
        output.writeInt(neurons.size());

        // Funktion
        saveFunction(output, layer.getFunction());

        // BIAS Gewichte
        for (Neuron neuron : neurons) {
            output.writeDouble(neuron.getInputBIAS());
        }
    }

    protected void saveMatrix(final DataOutput output, final Matrix matrix) throws Exception {
        output.writeInt(matrix.getInputSize());
        output.writeInt(matrix.getOutputSize());

        // Gewichte
        for (int i = 0; i < matrix.getInputSize(); i++) {
            for (int j = 0; j < matrix.getOutputSize(); j++) {
                output.writeDouble(matrix.getWeights()[i][j]);
            }
        }
    }
}
