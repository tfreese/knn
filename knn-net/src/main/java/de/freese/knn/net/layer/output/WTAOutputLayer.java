/**
 * 06.06.2008
 */
package de.freese.knn.net.layer.output;

import java.util.Arrays;

import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Ausgangslayer. Dieser verwendet den "Winner-takes-it-all" Algorythmus.<br>
 * Das Neuron mit dem höchsten Ausgabewert erhält den Wert 1, alle anderen 0.
 * 
 * @author Thomas Freese
 */
public class WTAOutputLayer extends OutputLayer
{
	/**
	 * Creates a new {@link WTAOutputLayer} object.
	 * 
	 * @param size int
	 */
	public WTAOutputLayer(final int size)
	{
		super(size);
	}

	/**
	 * @see de.freese.knn.net.layer.output.OutputLayer#adjustOutput(de.freese.knn.net.visitor.ForwardVisitor)
	 */
	@Override
	public double[] adjustOutput(final ForwardVisitor visitor)
	{
		double[] outputs = visitor.getLastOutputs();

		double[] wtaOutputs = new double[outputs.length];
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
		Arrays.fill(wtaOutputs, 0);

		// 1 setzen
		wtaOutputs[maxOutputIndex] = 1.0D;

		return wtaOutputs;
	}
}
