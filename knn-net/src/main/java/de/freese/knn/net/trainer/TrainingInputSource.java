// Created: 06.06.2008
package de.freese.knn.net.trainer;

/**
 * Interface um Trainingsdaten fuer den {@link NetTrainer} zu liefern.
 *
 * @author Thomas Freese
 */
public interface TrainingInputSource
{
    /**
     * Liefert die Eingangswerte am Index.
     *
     * @param index int
     *
     * @return double[]
     */
    double[] getInputAt(int index);

    /**
     * Liefert die Ausgangswerte am Index.
     *
     * @param index int
     *
     * @return double[]
     */
    double[] getOutputAt(int index);

    /**
     * Liefert die Anzahl der Trainingsdaten.
     *
     * @return int
     */
    int getSize();
}
