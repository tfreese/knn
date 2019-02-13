/**
 * 11.06.2008
 */
package de.freese.knn.net.persister;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.function.Supplier;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.Matrix;

/**
 * BasisPersister für das laden und speichen eines neuralen Netzes.
 * <p/>
 *
 * @author Thomas Freese
 */
public abstract class AbstractNetPersister implements NetPersister
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
     * @return {@link Layer}
     * @throws Exception Falls was schief geht.
     */
    protected abstract Layer loadLayer(DataInputStream dis) throws Exception;

    /**
     * Liest eine Matrix in den Stream.
     *
     * @param dis {@link DataInputStream}
     * @return {@link Matrix}
     * @throws Exception Falls was schief geht.
     */
    protected abstract Matrix loadMatrix(DataInputStream dis) throws Exception;

    /**
     * @see de.freese.knn.net.persister.NetPersister#save(java.io.DataOutputStream, java.util.function.Supplier)
     */
    @Override
    public void save(final DataOutputStream dos, final Supplier<Layer[]> layerSupplier) throws Exception
    {
        // Anzahl Layer
        Layer[] layers = layerSupplier.get();

        dos.writeInt(layers.length);

        for (int i = 0; i < layers.length; i++)
        {
            Layer layer = layers[i];
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
     * @param layer {@link Layer}
     * @throws Exception Falls was schief geht.
     */
    protected abstract void saveLayer(DataOutputStream dos, Layer layer) throws Exception;

    /**
     * Schreibt eine Matrix in den Stream.
     *
     * @param dos {@link DataOutputStream}
     * @param matrix {@link Matrix}
     * @throws Exception Falls was schief geht.
     */
    protected abstract void saveMatrix(DataOutputStream dos, Matrix matrix) throws Exception;
}
