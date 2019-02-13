/**
 * 06.06.2008
 */
package de.freese.knn.buttons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import de.freese.knn.net.trainer.TrainingInputSource;

/**
 * Erstellt TestTrainingsDaten für das {@link MatrixPanel}.
 *
 * @author Thomas Freese
 */
public class MatrixTrainingInputSource implements TrainingInputSource
{
    /**
     *
     */
    private final List<double[]> inputList = new ArrayList<>();

    /**
     *
     */
    private final List<double[]> outputList = new ArrayList<>();

    /**
     * Creates a new {@link MatrixTrainingInputSource} object.
     */
    public MatrixTrainingInputSource()
    {
        super();

        createTrainingSet();
    }

    /**
     * Erzeugt den Output fuer den Wert.
     *
     * @param value int
     * @return double[]
     */
    private double[] createOutput(final int value)
    {
        final double[] output = new double[10];
        Arrays.fill(output, 0.0D);

        output[value] = 1.0D;

        return output;
    }

    /**
     * Erzeugen der Trainingsdaten.
     */
    private void createTrainingSet()
    {
        // 0
        double[] matrix = new double[MatrixPanel.MAT_HEIGHT * MatrixPanel.MAT_WIDTH];
        Arrays.fill(matrix, 0.0D);
        matrix[7] = 1.0d;
        matrix[8] = 1.0d;
        matrix[9] = 1.0d;
        matrix[10] = 1.0d;
        matrix[13] = 1.0d;
        matrix[16] = 1.0d;
        matrix[19] = 1.0d;
        matrix[22] = 1.0d;
        matrix[25] = 1.0d;
        matrix[28] = 1.0d;
        matrix[31] = 1.0d;
        matrix[34] = 1.0d;
        matrix[37] = 1.0d;
        matrix[40] = 1.0d;
        matrix[43] = 1.0d;
        matrix[44] = 1.0d;
        matrix[45] = 1.0d;
        matrix[46] = 1.0d;
        this.inputList.add(matrix);
        this.outputList.add(createOutput(0));

        // 1
        matrix = new double[MatrixPanel.MAT_HEIGHT * MatrixPanel.MAT_WIDTH];
        Arrays.fill(matrix, 0.0D);
        matrix[9] = 1.0d;
        matrix[10] = 1.0d;
        matrix[14] = 1.0d;
        matrix[15] = 1.0d;
        matrix[16] = 1.0d;
        matrix[19] = 1.0d;
        matrix[20] = 1.0d;
        matrix[21] = 1.0d;
        matrix[22] = 1.0d;
        matrix[27] = 1.0d;
        matrix[28] = 1.0d;
        matrix[33] = 1.0d;
        matrix[34] = 1.0d;
        matrix[39] = 1.0d;
        matrix[40] = 1.0d;
        matrix[45] = 1.0d;
        matrix[46] = 1.0d;
        this.inputList.add(matrix);
        this.outputList.add(createOutput(1));

        // 2
        matrix = new double[MatrixPanel.MAT_HEIGHT * MatrixPanel.MAT_WIDTH];
        Arrays.fill(matrix, 0.0D);
        matrix[7] = 1.0D;
        matrix[8] = 1.0D;
        matrix[9] = 1.0D;
        matrix[10] = 1.0D;
        matrix[16] = 1.0D;
        matrix[22] = 1.0D;
        matrix[25] = 1.0D;
        matrix[26] = 1.0D;
        matrix[27] = 1.0D;
        matrix[28] = 1.0D;
        matrix[31] = 1.0D;
        matrix[37] = 1.0D;
        matrix[43] = 1.0D;
        matrix[44] = 1.0D;
        matrix[45] = 1.0D;
        matrix[46] = 1.0D;
        this.inputList.add(matrix);
        this.outputList.add(createOutput(2));

        // 3
        matrix = new double[MatrixPanel.MAT_HEIGHT * MatrixPanel.MAT_WIDTH];
        Arrays.fill(matrix, 0.0d);
        matrix[7] = 1.0d;
        matrix[8] = 1.0d;
        matrix[9] = 1.0d;
        matrix[10] = 1.0d;
        matrix[16] = 1.0d;
        matrix[22] = 1.0d;
        matrix[25] = 1.0d;
        matrix[26] = 1.0d;
        matrix[27] = 1.0d;
        matrix[28] = 1.0d;
        matrix[34] = 1.0d;
        matrix[40] = 1.0d;
        matrix[43] = 1.0d;
        matrix[44] = 1.0d;
        matrix[45] = 1.0d;
        matrix[46] = 1.0d;
        this.inputList.add(matrix);
        this.outputList.add(createOutput(3));

        // 4
        matrix = new double[MatrixPanel.MAT_HEIGHT * MatrixPanel.MAT_WIDTH];
        Arrays.fill(matrix, 0.0d);
        matrix[7] = 1.0D;
        matrix[10] = 1.0D;
        matrix[13] = 1.0D;
        matrix[16] = 1.0D;
        matrix[19] = 1.0D;
        matrix[22] = 1.0D;
        matrix[25] = 1.0D;
        matrix[26] = 1.0D;
        matrix[27] = 1.0D;
        matrix[28] = 1.0D;
        matrix[34] = 1.0D;
        matrix[40] = 1.0D;
        matrix[46] = 1.0D;
        this.inputList.add(matrix);
        this.outputList.add(createOutput(4));

        // 5
        matrix = new double[MatrixPanel.MAT_HEIGHT * MatrixPanel.MAT_WIDTH];
        Arrays.fill(matrix, 0.0d);
        matrix[7] = 1.0d;
        matrix[8] = 1.0d;
        matrix[9] = 1.0d;
        matrix[10] = 1.0d;
        matrix[13] = 1.0d;
        matrix[19] = 1.0d;
        matrix[25] = 1.0d;
        matrix[26] = 1.0d;
        matrix[27] = 1.0d;
        matrix[28] = 1.0d;
        matrix[34] = 1.0d;
        matrix[40] = 1.0d;
        matrix[43] = 1.0d;
        matrix[44] = 1.0d;
        matrix[45] = 1.0d;
        matrix[46] = 1.0d;
        this.inputList.add(matrix);
        this.outputList.add(createOutput(5));

        // 6
        matrix = new double[MatrixPanel.MAT_HEIGHT * MatrixPanel.MAT_WIDTH];
        Arrays.fill(matrix, 0.0d);
        matrix[7] = 1.0d;
        matrix[8] = 1.0d;
        matrix[9] = 1.0d;
        matrix[10] = 1.0d;
        matrix[13] = 1.0d;
        matrix[19] = 1.0d;
        matrix[25] = 1.0d;
        matrix[26] = 1.0d;
        matrix[27] = 1.0d;
        matrix[28] = 1.0d;
        matrix[31] = 1.0d;
        matrix[34] = 1.0d;
        matrix[37] = 1.0d;
        matrix[40] = 1.0d;
        matrix[43] = 1.0d;
        matrix[44] = 1.0d;
        matrix[45] = 1.0d;
        matrix[46] = 1.0d;
        this.inputList.add(matrix);
        this.outputList.add(createOutput(6));

        // 7
        matrix = new double[MatrixPanel.MAT_HEIGHT * MatrixPanel.MAT_WIDTH];
        Arrays.fill(matrix, 0.0d);
        matrix[7] = 1.0d;
        matrix[8] = 1.0d;
        matrix[9] = 1.0d;
        matrix[10] = 1.0d;
        matrix[16] = 1.0d;
        matrix[22] = 1.0d;
        matrix[28] = 1.0d;
        matrix[34] = 1.0d;
        matrix[40] = 1.0d;
        matrix[46] = 1.0d;
        this.inputList.add(matrix);
        this.outputList.add(createOutput(7));

        // 8
        matrix = new double[MatrixPanel.MAT_HEIGHT * MatrixPanel.MAT_WIDTH];
        Arrays.fill(matrix, 0.0d);
        matrix[7] = 1.0d;
        matrix[8] = 1.0d;
        matrix[9] = 1.0d;
        matrix[10] = 1.0d;
        matrix[13] = 1.0d;
        matrix[16] = 1.0d;
        matrix[19] = 1.0d;
        matrix[22] = 1.0d;
        matrix[25] = 1.0d;
        matrix[26] = 1.0d;
        matrix[27] = 1.0d;
        matrix[28] = 1.0d;
        matrix[31] = 1.0d;
        matrix[34] = 1.0d;
        matrix[37] = 1.0d;
        matrix[40] = 1.0d;
        matrix[43] = 1.0d;
        matrix[44] = 1.0d;
        matrix[45] = 1.0d;
        matrix[46] = 1.0d;
        this.inputList.add(matrix);
        this.outputList.add(createOutput(8));

        // 9
        matrix = new double[MatrixPanel.MAT_HEIGHT * MatrixPanel.MAT_WIDTH];
        Arrays.fill(matrix, 0.0d);
        matrix[7] = 1.0d;
        matrix[8] = 1.0d;
        matrix[9] = 1.0d;
        matrix[10] = 1.0d;
        matrix[13] = 1.0d;
        matrix[16] = 1.0d;
        matrix[19] = 1.0d;
        matrix[22] = 1.0d;
        matrix[25] = 1.0d;
        matrix[26] = 1.0d;
        matrix[27] = 1.0d;
        matrix[28] = 1.0d;
        matrix[34] = 1.0d;
        matrix[40] = 1.0d;
        matrix[43] = 1.0d;
        matrix[44] = 1.0d;
        matrix[45] = 1.0d;
        matrix[46] = 1.0d;
        this.inputList.add(matrix);
        this.outputList.add(createOutput(9));
    }

    /**
     * @see de.freese.knn.net.trainer.TrainingInputSource#getInputAt(int)
     */
    @Override
    public double[] getInputAt(final int index)
    {
        return this.inputList.get(index);
    }

    /**
     * @see de.freese.knn.net.trainer.TrainingInputSource#getOutputAt(int)
     */
    @Override
    public double[] getOutputAt(final int index)
    {
        return this.outputList.get(index);
    }

    /**
     * @see de.freese.knn.net.trainer.TrainingInputSource#getSize()
     */
    @Override
    public int getSize()
    {
        return this.inputList.size();
    }
}
