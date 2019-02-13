/**
 * Created: 04.04.2012
 */
package de.freese.knn.net.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.neuron.Neuron;
import de.freese.knn.net.visitor.BackwardVisitor;

/**
 * Basisklasse der Mathematik des {@link NeuralNet}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractKnnMath implements KnnMath
{
    /**
     * Mathematik für die Eingangsfehler eines Layers.
     *
     * @param neuron {@link Neuron}
     * @param errors double[]
     * @param layerErrors double[]
     */
    public static void backward(final Neuron neuron, final double[] errors, final double[] layerErrors)
    {
        int layerIndex = neuron.getLayerIndex();
        double error = 0.0D;

        for (int o = 0; o < neuron.getOutputSize(); o++)
        {
            double weight = neuron.getOutputWeight(o);

            error += (weight * errors[o]);

            // Bias Neuron draufrechnen.
            error += (neuron.getInputBIAS() * errors[o]);
        }

        layerErrors[layerIndex] = error;
    }

    /**
     * Mathematik für die Ausgangswerte eines Layers.
     *
     * @param neuron {@link Neuron}
     * @param inputs double[]
     * @param outputs double[]
     */
    public static void forward(final Neuron neuron, final double[] inputs, final double[] outputs)
    {
        int layerIndex = neuron.getLayerIndex();
        double eingangsSumme = 0.0D;

        // Bias Neuron draufrechnen.
        eingangsSumme += neuron.getInputBIAS();

        for (int i = 0; i < neuron.getInputSize(); i++)
        {
            double weight = neuron.getInputWeight(i);
            eingangsSumme += (weight * inputs[i]);
        }

        // Aktivierungsfunktion.
        outputs[layerIndex] = neuron.getFunction().calculate(eingangsSumme);
    }

    /**
     * Liefert den aktuellen Netzfehler für ein bestimmtes Ausgangs-Neuron.
     *
     * @param neuronIndex int
     * @param outputs double[]
     * @param outputTargets double[]
     * @return double
     */
    public static double getNetError(final int neuronIndex, final double[] outputs, final double[] outputTargets)
    {
        double output = outputs[neuronIndex];
        double outputTarget = outputTargets[neuronIndex];

        double error = Math.pow(outputTarget - output, 2.0D);

        return error;
    }

    /**
     * Initialisiert die BIAS-Gewichte der Neuronen eines Layers.
     *
     * @param layer {@link Layer}
     * @param valueInitializer {@link ValueInitializer}
     */
    public static void initialize(final Layer layer, final ValueInitializer valueInitializer)
    {
        // BIAS Gewichte.
        layer.getNeurons().forEach(neuron -> neuron.setInputBIAS(valueInitializer.createNextValue()));

        // Gewichte.
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

    /**
     * Aktualisiert die Gewichte der Neuronen eines Layers passend zum Ausgangsfehler.
     *
     * @param neuron {@link Neuron}
     * @param teachFactor double
     * @param momentum double
     * @param leftOutputs double[]
     * @param deltaWeights double[][]
     * @param rightErrors double[]
     */
    public static void refreshLayerWeights(final Neuron neuron, final double teachFactor, final double momentum,
            final double[] leftOutputs, final double[][] deltaWeights, final double[] rightErrors)
    {
        int layerIndex = neuron.getLayerIndex();

        for (int o = 0; o < neuron.getOutputSize(); o++)
        {
            double weight = neuron.getOutputWeight(o);
            double deltaWeight = teachFactor * rightErrors[o] * leftOutputs[layerIndex];

            // Momentum-Term berücksichtigen (konjugierter Gradientenabstieg).
            // Der Momentum-Term erhöht die Schrittweite auf flachen Niveaus und reduziert
            // in Tälern.
            deltaWeight += (momentum * deltaWeights[layerIndex][o]);

            neuron.setOutputWeight(o, weight + deltaWeight);
            deltaWeights[layerIndex][o] = deltaWeight;

            // Bias verrechnen, feuert immer.
            // TODO So wie es aussieht müssen die Biasgewichte nicht angepasst werden.
            // double biasWeight = matrix.getBiasWeights()[column];
            // double biasDeltaWeight = this.teachFactor * rightErrors[column] * 1;
            // matrix.getBiasWeights()[column] = biasWeight + biasDeltaWeight;
        }
    }

    /**
     * Liefert den Ausgabefehler nach dem Gradientenabstiegsverfahren für ein bestimmtes Ausgangs-Neuron.
     *
     * @param neuronIndex int
     * @param outputs double[]
     * @param errors double[]
     * @param visitor {@link BackwardVisitor}
     */
    public static void setOutputError(final int neuronIndex, final double[] outputs, final double[] errors, final BackwardVisitor visitor)
    {
        double output = outputs[neuronIndex];
        double outputTarget = visitor.getOutputTargets()[neuronIndex];

        // Berechnung des Mittleren quadratischen Fehlers.
        double error = (outputTarget - output) * output * (1.0D - output);

        // Flatspot-Problem korrigieren.
        // error += 0.01D;

        errors[neuronIndex] = error;
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
     * @see de.freese.knn.net.math.KnnMath#getNetError(double[], double[])
     */
    @Override
    public double getNetError(final double[] outputs, final double[] outputTargets)
    {
        double error = 0.0D;

        for (int i = 0; i < outputs.length; i++)
        {
            error += getNetError(i, outputs, outputTargets);
        }

        error /= 2.0D;

        return error;
    }

    /**
     * @see de.freese.knn.net.math.KnnMath#setOutputError(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void setOutputError(final Layer layer, final BackwardVisitor visitor)
    {
        double[] outputs = visitor.getOutputs(layer);
        double[] errors = new double[outputs.length];

        for (int o = 0; o < outputs.length; o++)
        {
            setOutputError(o, outputs, errors, visitor);
        }

        visitor.setErrors(layer, errors);
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}