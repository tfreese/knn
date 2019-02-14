/**
 * 11.06.2008
 */
package de.freese.knn.net.persister;

import java.io.DataInput;
import java.io.DataOutput;
import de.freese.knn.net.NeuralNet;

/**
 * Interface um ein neuronales Netz zu Speichern oder zu Laden.
 *
 * @author Thomas Freese
 */
public interface NetPersister
{
    /**
     * Laden des Netzes.
     *
     * @param input {@link DataInput}
     * @return {@link NeuralNet}
     * @throws Exception Falls was schief geht.
     */
    public NeuralNet load(DataInput input) throws Exception;

    /**
     * Speichern des Netzes.
     *
     * @param output {@link DataOutput}
     * @param knn {@link NeuralNet}
     * @throws Exception Falls was schief geht.
     */
    public void save(DataOutput output, NeuralNet knn) throws Exception;
}
