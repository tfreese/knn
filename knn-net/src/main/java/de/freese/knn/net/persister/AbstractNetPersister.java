/**
 * 11.06.2008
 */
package de.freese.knn.net.persister;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.matrix.Matrix;

/**
 * BasisPersister für das laden und speichen eines neuralen Netzes.
 * <p/>
 *
 * @author Thomas Freese
 */
public abstract class AbstractNetPersister implements INetPersister
{
    /**
     * Creates a new {@link AbstractNetPersister} object.
     */
    public AbstractNetPersister()
    {
        super();
    }

    /**
     * Liest einen Layer in den Stream.
     *
     * @param dis {@link DataInputStream}
     * @return {@link ILayer}
     * @throws Exception Falls was schief geht.
     */
    protected abstract ILayer loadLayer(DataInputStream dis) throws Exception;

    /**
     * Liest eine Matrix in den Stream.
     *
     * @param dis {@link DataInputStream}
     * @return {@link Matrix}
     * @throws Exception Falls was schief geht.
     */
    protected abstract Matrix loadMatrix(DataInputStream dis) throws Exception;

    /**
     * @see de.freese.knn.net.persister.INetPersister#save(java.io.DataOutputStream, de.freese.knn.net.NeuralNet)
     */
    @Override
    public void save(final DataOutputStream dos, final NeuralNet neuralNet) throws Exception
    {
        // Anzahl Layer
        dos.writeInt(neuralNet.getLayer().length);

        ILayer[] layers = neuralNet.getLayer();

        for (int i = 0; i < layers.length; i++)
        {
            ILayer layer = layers[i];
            saveLayer(dos, layer);

            if (i < (layers.length - 1))
            {
                saveMatrix(dos, layer.getOutputMatrix());
            }
        }

        dos.flush();
    }

    /**
     * Schreibt einen Layer in den Stream.
     *
     * @param dos {@link DataOutputStream}
     * @param layer {@link ILayer}
     * @throws Exception Falls was schief geht.
     */
    protected abstract void saveLayer(DataOutputStream dos, ILayer layer) throws Exception;

    /**
     * Schreibt eine Matrix in den Stream.
     *
     * @param dos {@link DataOutputStream}
     * @param matrix {@link Matrix}
     * @throws Exception Falls was schief geht.
     */
    protected abstract void saveMatrix(DataOutputStream dos, Matrix matrix) throws Exception;
}
