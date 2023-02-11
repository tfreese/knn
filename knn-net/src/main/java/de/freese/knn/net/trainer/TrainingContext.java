// Created: 27.05.2011
package de.freese.knn.net.trainer;

import java.util.HashMap;
import java.util.Map;

import de.freese.knn.net.matrix.Matrix;

/**
 * Enthält Daten des Netzes für den kompletten Trainingslauf.
 *
 * @author Thomas Freese
 */
public class TrainingContext {
    private Map<Matrix, double[][]> deltaWeights = new HashMap<>();

    public void clear() {
        this.deltaWeights.clear();
        this.deltaWeights = null;
    }

    /**
     * Liefert die vorherigen Gewichtsänderungen der Matrix-Neuronen.
     */
    public double[][] getDeltaWeights(final Matrix matrix) {
        return this.deltaWeights.computeIfAbsent(matrix, key -> new double[matrix.getInputSize()][matrix.getOutputSize()]);
    }
}
