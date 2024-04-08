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
public interface Layer extends Visitable {
    /**
     * Liefert die Funktion des Layers.
     */
    Function getFunction();

    /**
     * Liefert die Eingangsmatrix des Layers.
     */
    Matrix getInputMatrix();

    /**
     * Liefert die Neuronen des Layers.
     */
    NeuronList getNeurons();

    /**
     * Liefert die Ausgangsmatrix des Layers.
     */
    Matrix getOutputMatrix();

    /**
     * Liefert die Anzahl der Neuronen.
     */
    int getSize();

    /**
     * Setzt die Eingangsmatrix des Layers.
     */
    void setInputMatrix(Matrix matrix);

    /**
     * Setzt die Ausgangsmatrix des Layers.
     */
    void setOutputMatrix(Matrix matrix);
}
