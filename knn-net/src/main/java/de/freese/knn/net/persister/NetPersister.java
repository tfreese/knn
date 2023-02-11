// Created: 11.06.2008
package de.freese.knn.net.persister;

import de.freese.knn.net.NeuralNet;

/**
 * Interface um ein neuronales Netz zu Speichern oder zu Laden.
 *
 * @param <IN> Input-Type
 * @param <OUT> Output-Type
 *
 * @author Thomas Freese
 */
public interface NetPersister<IN, OUT> {
    NeuralNet load(IN input) throws Exception;

    void save(OUT output, NeuralNet knn) throws Exception;
}
