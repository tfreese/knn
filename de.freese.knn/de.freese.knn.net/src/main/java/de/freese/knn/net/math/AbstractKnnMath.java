/**
 * Created: 04.04.2012
 */

package de.freese.knn.net.math;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.matrix.IValueInitializer;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.neuron.INeuron;
import de.freese.knn.net.visitor.BackwardVisitor;

/**
 * Basisklasse der Mathematik des {@link NeuralNet}.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractKnnMath implements IKnnMath
{
	/**
	 * Mathematik für die Eingangsfehler eines Layers.
	 * 
	 * @param neurons {@link List}
	 * @param errors double[]
	 * @param layerErrors double[]
	 */
	public static void backward(final List<INeuron> neurons, final double[] errors,
								final double[] layerErrors)
	{
		for (INeuron neuron : neurons)
		{
			int layerIndex = neuron.getLayerIndex();
			double error = 0.0D;

			for (int o = 0; o < neuron.getOutputSize(); o++)
			{
				double weight = neuron.getOutputWeight(o);

				error += (weight * errors[o]);

				// Bias Neuron draufrechnen
				error += (neuron.getInputBIAS() * errors[o]);
			}

			layerErrors[layerIndex] = error;
		}
	}

	/**
	 * Mathematik für die Ausgangswerte eines Layers.
	 * 
	 * @param neurons {@link List}
	 * @param inputs double[]
	 * @param outputs double[]
	 */
	public static void forward(final List<INeuron> neurons, final double[] inputs,
								final double[] outputs)
	{
		for (INeuron neuron : neurons)
		{
			int layerIndex = neuron.getLayerIndex();
			double eingangsSumme = 0.0D;

			// Bias Neuron draufrechnen
			eingangsSumme += neuron.getInputBIAS();

			for (int i = 0; i < neuron.getInputSize(); i++)
			{
				double weight = neuron.getInputWeight(i);
				eingangsSumme += (weight * inputs[i]);
			}

			// Aktivierungsfunktion
			outputs[layerIndex] = neuron.getFunction().calculate(eingangsSumme);
		}
	}

	/**
	 * Initialisiert die BIAS-Gewichte der Neuronen eines Layers.
	 * 
	 * @param layers {@link List}
	 * @param valueInitializer {@link IValueInitializer}
	 */
	public static void initialize(final List<ILayer> layers,
									final IValueInitializer valueInitializer)
	{
		for (ILayer layer : layers)
		{
			// BIAS Gewichte
			for (INeuron neuron : layer.getNeurons())
			{
				neuron.setInputBIAS(valueInitializer.createNextValue());
			}

			Matrix matrix = layer.getOutputMatrix();

			if (matrix != null)
			{
				// Gewichte
				for (int column = 0; column < matrix.getOutputSize(); column++)
				{
					for (int row = 0; row < matrix.getInputSize(); row++)
					{
						double weight = valueInitializer.createNextValue();
						matrix.getWeights()[row][column] = weight;
					}
				}
			}
		}
	}

	/**
	 * @param neurons {@link List}
	 * @param teachFactor double
	 * @param momentum double
	 * @param leftOutputs double[]
	 * @param deltaWeights double[][]
	 * @param rightErrors double[]
	 */
	public static void refreshLayerWeights(final List<INeuron> neurons, final double teachFactor,
											final double momentum, final double[] leftOutputs,
											final double[][] deltaWeights,
											final double[] rightErrors)
	{
		for (INeuron neuron : neurons)
		{
			int layerIndex = neuron.getLayerIndex();

			for (int o = 0; o < neuron.getOutputSize(); o++)
			{
				double weight = neuron.getOutputWeight(o);
				double deltaWeight = teachFactor * rightErrors[o] * leftOutputs[layerIndex];

				// Momentum-Term beruecksichtigen (konjugierter Gradientenabstieg).
				// Der Momentum-Term erhoeht die Schrittweite auf flachen Niveaus und reduziert
				// in Taelern.
				deltaWeight += (momentum * deltaWeights[layerIndex][o]);

				neuron.setOutputWeight(o, weight + deltaWeight);
				deltaWeights[layerIndex][o] = deltaWeight;

				// Bias verrechnen, feuert immer.
				// TODO So wie es aussieht muessen die Biasgewichte nicht angepasst werden.
				// double biasWeight = matrix.getBiasWeights()[column];
				// double biasDeltaWeight = this.teachFactor * rightErrors[column] * 1;
				// matrix.getBiasWeights()[column] = biasWeight + biasDeltaWeight;
			}
		}
	}

	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Erstellt ein neues {@link AbstractKnnMath} Object.
	 */
	public AbstractKnnMath()
	{
		super();
	}

	/**
	 * @return {@link Logger}
	 */
	protected Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @see de.freese.knn.net.math.IKnnMath#getNetError(double[], double[])
	 */
	@Override
	public double getNetError(final double[] outputs, final double[] outputTargets)
	{
		double error = 0.0D;

		for (int i = 0; i < outputs.length; i++)
		{
			double output = outputs[i];
			double outputTarget = outputTargets[i];

			error += Math.pow(outputTarget - output, 2.0D);
		}

		error /= 2.0D;

		return error;
	}

	/**
	 * @see de.freese.base.model.release.IReleaseable#release()
	 */
	@Override
	public void release()
	{
		getLogger().info("");
	}

	/**
	 * @see de.freese.knn.net.math.IKnnMath#setOutputError(de.freese.knn.net.layer.ILayer,
	 *      de.freese.knn.net.visitor.BackwardVisitor)
	 */
	@Override
	public void setOutputError(final ILayer layer, final BackwardVisitor visitor)
	{
		double[] outputs = visitor.getOutputs(layer);
		double[] errors = new double[outputs.length];

		// Ausgabefehler berechnen, Gradientenabstiegsverfahren
		for (int o = 0; o < outputs.length; o++)
		{
			double output = outputs[o];
			double outputTarget = visitor.getOutputTargets()[o];

			// Berechnung des Mittleren quadratischen Fehlers
			double error = (outputTarget - output) * output * (1.0D - output);

			// FlatspotProblem korrigieren
			// error += 0.01D;
			errors[o] = error;
		}

		visitor.setErrors(layer, errors);
	}
}
