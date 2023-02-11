// Created: 06.06.2008
package de.freese.knn.net.trainer;

/**
 * Interface um Trainingsdaten für den {@link NetTrainer} zu liefern.
 *
 * @author Thomas Freese
 */
public interface TrainingInputSource {
    /**
     * Liefert die Eingangswerte am Index.
     */
    double[] getInputAt(int index);

    /**
     * Liefert die Ausgangswerte am Index.
     */
    double[] getOutputAt(int index);

    /**
     * Liefert die Anzahl der Trainingsdaten.
     */
    int getSize();
}
