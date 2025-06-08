// Created: 06.06.2008
package de.freese.knn.net.matrix;

/**
 * Verknüpft 2 Layer miteinander und enthält die Daten der Gewichte.<br>
 * Die Rows sind die Neuronen des 1. Layers, die Columns die Neuronen des 2. Layers.
 *
 * @author Thomas Freese
 */
public class Matrix {
    private final int inputSize;
    private final int outputSize;
    private final double[][] weights;

    public Matrix(final int inputSize, final int outputSize) {
        super();

        this.inputSize = inputSize;
        this.outputSize = outputSize;

        weights = new double[inputSize][outputSize];
    }

    /**
     * Liefert die Anzahl der Eingangsneuronen.
     */
    public int getInputSize() {
        return inputSize;
    }

    /**
     * Liefert die Anzahl der Ausgangsneuronen.
     */
    public int getOutputSize() {
        return outputSize;
    }

    /**
     * Gewichte. Die Rows sind die Neuronen des Layers, die Columns die Neuronen des vorherigen Layers.
     */
    public double[][] getWeights() {
        return weights;
    }
}
