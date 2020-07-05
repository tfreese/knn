/**
 * Created: 27.05.2011
 */

package de.freese.knn.net.trainer;

import java.util.HashMap;
import java.util.Map;
import de.freese.knn.net.matrix.Matrix;

/**
 * Enthaelt Daten des Netzes fuer den kompletten Trainingslauf.
 *
 * @author Thomas Freese
 */
public class TrainingContext
{
    /**
     *
     */
    private Map<Matrix, double[][]> deltaWeights = new HashMap<>();

    /**
     * Erstellt ein neues {@link TrainingContext} Object.
     */
    public TrainingContext()
    {
        super();
    }

    /**
     * Aufraeumen.
     */
    public void clear()
    {
        this.deltaWeights.clear();
        this.deltaWeights = null;
    }

    /**
     * Liefert die vorherige Gewichtsaenderungen der Matrix-Neuronen.
     *
     * @param matrix {@link Matrix}
     * @return double[][]
     */
    public double[][] getDeltaWeights(final Matrix matrix)
    {
        double[][] dWeights = this.deltaWeights.computeIfAbsent(matrix, key -> new double[matrix.getInputSize()][matrix.getOutputSize()]);

        return dWeights;
    }
}
