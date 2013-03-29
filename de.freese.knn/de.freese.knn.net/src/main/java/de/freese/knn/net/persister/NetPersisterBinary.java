/**
 * 11.06.2008
 */
package de.freese.knn.net.persister;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.neuron.INeuron;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * NetPersister für das laden und speichen eines neuralen Netzes im Binärformat.
 * <p/>
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
     * @see de.freese.knn.net.persister.INetPersister#load(java.io.InputStream,
     * de.freese.knn.net.NeuralNet)
     */
    @Override
    public void load(final InputStream inputStream, final NeuralNet neuralNet) throws Exception
    {
        DataInputStream dis = null;

        if (inputStream instanceof DataInputStream)
        {
            dis = (DataInputStream) inputStream;
        }
        else
        {
            dis = new DataInputStream(inputStream);
        }

        // Anzahl Layer lesen
        int layerCount = dis.readInt();

        ILayer leftLayer = loadLayer(dis);
        neuralNet.addLayer(leftLayer);

        for (int i = 0; i < (layerCount - 1); i++)
        {
            Matrix matrix = loadMatrix(dis);

            ILayer rightLayer = loadLayer(dis);
            neuralNet.addLayer(rightLayer);

            // Layer verknüpfen
            leftLayer.setOutputMatrix(matrix);
            rightLayer.setInputMatrix(matrix);

            leftLayer = rightLayer;
        }
    }

    /**
     * @see
     * de.freese.knn.net.persister.AbstractNetPersister#loadLayer(java.io.InputStream)
     */
    @Override
    protected ILayer loadLayer(final InputStream inputStream) throws Exception
    {
        DataInputStream dis = (DataInputStream) inputStream;

        // Klassentyp
        String layerClazzName = dis.readUTF();

        // Neuronen
        int size = dis.readInt();

        Class<?> layerClazz = Class.forName(layerClazzName);

        Constructor<?> constructor = layerClazz.getConstructor(new Class<?>[]
        {
            int.class
        });

        ILayer layer = (ILayer) constructor.newInstance(new Object[]
        {
            new Integer(size)
        });

        // BIAS Gewichte der Neuronen
        for (INeuron neuron : layer.getNeurons())
        {
            neuron.setInputBIAS(dis.readDouble());
        }

        return layer;
    }

    /**
     * @see
     * de.freese.knn.net.persister.AbstractNetPersister#loadMatrix(java.io.InputStream)
     */
    @Override
    protected Matrix loadMatrix(final InputStream inputStream) throws Exception
    {
        DataInputStream dis = (DataInputStream) inputStream;

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
     * @see
     * de.freese.knn.net.persister.AbstractNetPersister#save(java.io.OutputStream,
     * de.freese.knn.net.NeuralNet)
     */
    @Override
    public void save(final OutputStream outputStream, final NeuralNet neuralNet) throws Exception
    {
        DataOutputStream dos = null;

        if (outputStream instanceof DataOutputStream)
        {
            dos = (DataOutputStream) outputStream;
        }
        else
        {
            dos = new DataOutputStream(outputStream);
        }

        // Anzahl Layer
        dos.writeInt(neuralNet.getLayer().size());

        super.save(dos, neuralNet);
    }

    /**
     * @see
     * de.freese.knn.net.persister.AbstractNetPersister#saveLayer(java.io.OutputStream,
     * de.freese.knn.net.layer.ILayer)
     */
    @Override
    protected void saveLayer(final OutputStream outputStream, final ILayer layer) throws Exception
    {
        DataOutputStream dos = (DataOutputStream) outputStream;

        // Klassentyp
        dos.writeUTF(layer.getClass().getName());

        // Neuronen
        List<INeuron> neurons = layer.getNeurons();
        dos.writeInt(neurons.size());

        // BIAS Gewichte
        for (INeuron neuron : neurons)
        {
            dos.writeDouble(neuron.getInputBIAS());
        }
    }

    /**
     * @see
     * de.freese.knn.net.persister.AbstractNetPersister#saveMatrix(java.io.OutputStream,
     * de.freese.knn.net.matrix.Matrix)
     */
    @Override
    protected void saveMatrix(final OutputStream outputStream, final Matrix matrix)
            throws Exception
    {
        DataOutputStream dos = (DataOutputStream) outputStream;

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
