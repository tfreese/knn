// Created: 06.06.2008
package de.freese.knn.net.layer;

import java.util.Arrays;

import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Ausgangslayer.<br>
 * Dieser verwendet den "Winner-takes-it-all" Algorythmus.<br>
 * Das Neuron mit dem höchsten Ausgabewert erhält den Wert 1, alle anderen 0.
 *
 * @author Thomas Freese
 */
public class OutputWTALayer extends OutputLayer
{
    /**
     * Creates a new {@link OutputWTALayer} object.
     *
     * @param size int
     */
    public OutputWTALayer(final int size)
    {
        super(size);
    }

    /**
     * @see de.freese.knn.net.layer.OutputLayer#adjustOutput(de.freese.knn.net.visitor.ForwardVisitor)
     */
    @Override
    public double[] adjustOutput(final ForwardVisitor visitor)
    {
        double[] outputs = visitor.getLastOutputs();

        double maxOutput = Double.MIN_VALUE;
        int maxOutputIndex = -1;

        for (int i = 0; i < outputs.length; i++)
        {
            if (outputs[i] > maxOutput)
            {
                maxOutput = outputs[i];
                maxOutputIndex = i;
            }
        }

        // Alles auf 0 setzen
        double[] wtaOutputs = new double[outputs.length];
        Arrays.fill(wtaOutputs, 0);

        // 1 setzen
        wtaOutputs[maxOutputIndex] = 1.0D;

        return wtaOutputs;
    }
}
