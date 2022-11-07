// Created: 06.06.2008
package de.freese.knn.net.layer;

import de.freese.knn.net.function.FunctionSigmoid;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Ausgangslayer.
 *
 * @author Thomas Freese
 */
public class OutputLayer extends AbstractLayer
{
    public OutputLayer(final int size)
    {
        super(size, new FunctionSigmoid());
    }

    /**
     * Anpassen der Outputs.
     */
    public double[] adjustOutput(final ForwardVisitor visitor)
    {
        return visitor.getLastOutputs();
    }
}
