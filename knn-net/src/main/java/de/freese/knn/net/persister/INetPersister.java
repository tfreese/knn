/**
 * 11.06.2008
 */
package de.freese.knn.net.persister;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import de.freese.knn.net.NeuralNet;

/**
 * Interface um ein neuronales Netz zu speichern oder zu laden.
 *
 * @author Thomas Freese
 */
public interface INetPersister
{
    /**
     * Laden des Netzes aus dem Stream. Der Stream wird NICHT geschlossen !
     * 
     * @param dis {@link DataInputStream}
     * @param neuralNet {@link NeuralNet}
     * @throws Exception Falls was schief geht.
     */
    public void load(DataInputStream dis, NeuralNet neuralNet) throws Exception;

    /**
     * Speichern des Netzes in den Stream. Der Stream wird NICHT geschlossen !
     * 
     * @param dos {@link DataOutputStream}
     * @param neuralNet {@link NeuralNet}
     * @throws Exception Falls was schief geht.
     */
    public void save(DataOutputStream dos, NeuralNet neuralNet) throws Exception;
}
