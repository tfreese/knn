/**
 * 06.06.2008
 */
package de.freese.knn.net.layer;

import de.freese.knn.net.function.FunctionSigmoide;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Ausgangslayer.
 *
 * @author Thomas Freese
 */
public class OutputLayer extends AbstractLayer
{
    /**
     * Creates a new {@link OutputLayer} object.
     *
     * @param size int
     */
    public OutputLayer(final int size)
    {
        super(size, new FunctionSigmoide());
    }

    /**
     * Anpassen der Outputs.
     *
     * @param visitor {@link ForwardVisitor}
     * @return double[]
     */
    public double[] adjustOutput(final ForwardVisitor visitor)
    {
        return visitor.getLastOutputs();
    }
}
