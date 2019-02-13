/**
 * 11.06.2008
 */
package de.freese.knn.net.persister;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;
import de.freese.knn.net.layer.Layer;

/**
 * Interface um ein neuronales Netz zu speichern oder zu laden.
 *
 * @author Thomas Freese
 */
public interface NetPersister
{
    /**
     * Laden des Netzes aus dem Stream. Der Stream wird NICHT geschlossen !
     *
     * @param dis {@link DataInputStream}
     * @param layerConsumer {@link Consumer}
     * @throws Exception Falls was schief geht.
     */
    public void load(DataInputStream dis, Consumer<Layer> layerConsumer) throws Exception;

    /**
     * Speichern des Netzes in den Stream. Der Stream wird NICHT geschlossen !
     *
     * @param dos {@link DataOutputStream}
     * @param layerSupplier {@link Supplier}
     * @throws Exception Falls was schief geht.
     */
    public void save(DataOutputStream dos, Supplier<Layer[]> layerSupplier) throws Exception;
}
