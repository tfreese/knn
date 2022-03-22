// Created: 06.06.2008
package de.freese.knn.net.matrix;

/**
 * Verknuepft 2 Layer miteinander und enthaelt die Daten der Gewichte.<br>
 * Die Rows sind die Neuronen des 1. Layers, die Columns die Neuronen des 2. Layers.
 *
 * @author Thomas Freese
 */
public class Matrix
{
    /**
     *
     */
    private final int inputSize;
    /**
     *
     */
    private final int outputSize;
    /**
     *
     */
    private final double[][] weights;

    /**
     * Creates a new {@link Matrix} object.
     *
     * @param inputSize int
     * @param outputSize int
     */
    public Matrix(final int inputSize, final int outputSize)
    {
        super();

        this.inputSize = inputSize;
        this.outputSize = outputSize;

        this.weights = new double[this.inputSize][this.outputSize];
    }

    /**
     * Liefert die Anzahl der Eingangsneuronen.
     *
     * @return int
     */
    public int getInputSize()
    {
        return this.inputSize;
    }

    /**
     * Liefert die Anzahl der Ausgangsneuronen.
     *
     * @return int
     */
    public int getOutputSize()
    {
        return this.outputSize;
    }

    /**
     * Gewichte. Die Rows sind die Neuronen des Layers, die Columns die Neuronen des vorherigen Layers.
     *
     * @return double[][]
     */
    public double[][] getWeights()
    {
        return this.weights;
    }
}
