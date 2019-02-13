/**
 * 11.06.2008
 */
package de.freese.knn.net.persister;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.neuron.Neuron;
import de.freese.knn.net.neuron.NeuronList;

/**
 * NetPersister für das laden und speichen eines neuralen Netzes im Binärformat.
 * <p/>
 *
 * @author Thomas Freese
 */
public class NetPersisterBinary extends AbstractNetPersister
{
    /**
     * Creates a new {@link NetPersisterBinary} object.
     */
    public NetPersisterBinary()
    {
        super();
    }

    /**
     * @see de.freese.knn.net.persister.NetPersister#load(java.io.DataInputStream, de.freese.knn.net.NeuralNet)
     */
    @Override
    public void load(final DataInputStream dis, final NeuralNet neuralNet) throws Exception
    {
        // Anzahl Layer lesen
        int layerCount = dis.readInt();

        Layer leftLayer = loadLayer(dis);
        neuralNet.addLayer(leftLayer);

        for (int i = 0; i < (layerCount - 1); i++)
        {
            Matrix matrix = loadMatrix(dis);

            Layer rightLayer = loadLayer(dis);
            neuralNet.addLayer(rightLayer);

            // Layer verknüpfen
            leftLayer.setOutputMatrix(matrix);
            rightLayer.setInputMatrix(matrix);

            leftLayer = rightLayer;
        }
    }

    /**
     * @see de.freese.knn.net.persister.AbstractNetPersister#loadLayer(java.io.DataInputStream)
     */
    @Override
    protected Layer loadLayer(final DataInputStream dis) throws Exception
    {
        // Klassentyp
        String layerClazzName = dis.readUTF();

        // Neuronen
        int size = dis.readInt();

        Class<?> layerClazz = Class.forName(layerClazzName);

        Constructor<?> constructor = layerClazz.getConstructor(new Class<?>[]
        {
                int.class
        });

        Layer layer = (Layer) constructor.newInstance(size);

        // BIAS Gewichte der Neuronen
        for (Neuron neuron : layer.getNeurons())
        {
            neuron.setInputBIAS(dis.readDouble());
        }

        return layer;
    }

    /**
     * @see de.freese.knn.net.persister.AbstractNetPersister#loadMatrix(java.io.DataInputStream)
     */
    @Override
    protected Matrix loadMatrix(final DataInputStream dis) throws Exception
    {
        int inputSize = dis.readInt();
        int outputSize = dis.readInt();

        Matrix matrix = new Matrix(inputSize, outputSize);

        // Gewichte
        for (int i = 0; i < inputSize; i++)
        {
            for (int o = 0; o < outputSize; o++)
            {
                matrix.getWeights()[i][o] = dis.readDouble();
            }
        }

        return matrix;
    }

    /**
     * @see de.freese.knn.net.persister.AbstractNetPersister#saveLayer(java.io.DataOutputStream, de.freese.knn.net.layer.Layer)
     */
    @Override
    protected void saveLayer(final DataOutputStream dos, final Layer layer) throws Exception
    {
        // Klassentyp
        dos.writeUTF(layer.getClass().getName());

        // Neuronen
        NeuronList neurons = layer.getNeurons();
        dos.writeInt(neurons.size());

        // BIAS Gewichte
        for (Neuron neuron : neurons)
        {
            dos.writeDouble(neuron.getInputBIAS());
        }
    }

    /**
     * @see de.freese.knn.net.persister.AbstractNetPersister#saveMatrix(java.io.DataOutputStream, de.freese.knn.net.matrix.Matrix)
     */
    @Override
    protected void saveMatrix(final DataOutputStream dos, final Matrix matrix) throws Exception
    {
        dos.writeInt(matrix.getInputSize());
        dos.writeInt(matrix.getOutputSize());

        // Gewichte
        for (int i = 0; i < matrix.getInputSize(); i++)
        {
            for (int o = 0; o < matrix.getOutputSize(); o++)
            {
                dos.writeDouble(matrix.getWeights()[i][o]);
            }
        }
    }
}
