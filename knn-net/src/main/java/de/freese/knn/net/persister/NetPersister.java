// Created: 11.06.2008
package de.freese.knn.net.persister;

import java.io.DataInput;
import java.io.DataOutput;

import de.freese.knn.net.NeuralNet;

/**
 * Interface um ein neuronales Netz zu Speichern oder zu Laden.
 *
 * @param <IN> Input-Type
 * @param <OUT> Output-Type
 *
 * @author Thomas Freese
 */
public interface NetPersister<IN, OUT>
{
    /**
     * Laden des Netzes.
     *
     * @param input {@link DataInput}
     *
     * @return {@link NeuralNet}
     *
     * @throws Exception Falls was schiefgeht.
     */
    NeuralNet load(IN input) throws Exception;

    /**
     * Speichern des Netzes.
     *
     * @param output {@link DataOutput}
     * @param knn {@link NeuralNet}
     *
     * @throws Exception Falls was schiefgeht.
     */
    void save(OUT output, NeuralNet knn) throws Exception;
}
