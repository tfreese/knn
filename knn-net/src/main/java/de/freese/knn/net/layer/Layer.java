// Created: 06.06.2008
package de.freese.knn.net.layer;

import de.freese.knn.net.function.Function;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.Visitable;

/**
 * Interface eines Layers.
 *
 * @author Thomas Freese
 */
public interface Layer extends Visitable
{
    /**
     * Liefert die Funktion des Layers.
     *
     * @return {@link Function}
     */
    Function getFunction();

    /**
     * Liefert die Eingangsmatrix des Layers.
     *
     * @return {@link Matrix}
     */
    Matrix getInputMatrix();

    /**
     * Liefert die Neuronen des Layers.
     *
     * @return {@link NeuronList}
     */
    NeuronList getNeurons();

    /**
     * Liefert die Ausgangsmatrix des Layers.
     *
     * @return {@link Matrix}
     */
    Matrix getOutputMatrix();

    /**
     * Liefert die Anzahl der Neuronen.
     *
     * @return int
     */
    int getSize();

    /**
     * Setzt die Eingangsmatrix des Layers.
     *
     * @param matrix {@link Matrix}
     */
    void setInputMatrix(Matrix matrix);

    /**
     * Setzt die Ausgangsmatrix des Layers.
     *
     * @param matrix {@link Matrix}
     */
    void setOutputMatrix(Matrix matrix);
}
