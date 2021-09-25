// Created: 04.04.2012
package de.freese.knn.net.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.neuron.Neuron;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.visitor.BackwardVisitor;

/**
 * Basisklasse der Mathematik des {@link NeuralNet}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractKnnMath implements KnnMath
{
    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
    *
    */
    private final int parallelism;

    /**
     * Erstellt ein neues {@link AbstractKnnMath} Object.
     *
     * @param parallelism int
     */
    protected AbstractKnnMath(final int parallelism)
    {
        super();

        if (parallelism <= 0)
        {
            throw new IllegalArgumentException("parallelism must >= 1");
        }

        this.parallelism = parallelism;
    }

    /**
     * Mathematik für die Eingangsfehler eines Neurons.
     *
     * @param neuron {@link Neuron}
     * @param errors double[]
     * @param layerErrors double[]
     */
    protected void backward(final Neuron neuron, final double[] errors, final double[] layerErrors)
    {
        int layerIndex = neuron.getLayerIndex();
        double error = 0.0D;

        for (int i = 0; i < neuron.getOutputSize(); i++)
        {
            double weight = neuron.getOutputWeight(i);

            error += (weight * errors[i]);

            // Bias Neuron draufrechnen.
            error += (neuron.getInputBIAS() * errors[i]);
        }

        layerErrors[layerIndex] = error;
    }

    /**
     * Mathematik für die Ausgangswerte eines Neurons.
     *
     * @param neuron {@link Neuron}
     * @param inputs double[]
     * @param outputs double[]
     */
    protected void forward(final Neuron neuron, final double[] inputs, final double[] outputs)
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
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
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
     * Liefert den aktuellen Netzfehler für ein bestimmtes Ausgangs-Neuron.
     *
     * @param neuronIndex int
     * @param outputs double[]
     * @param outputTargets double[]
     *
     * @return double
     */
    protected double getNetError(final int neuronIndex, final double[] outputs, final double[] outputTargets)
    {
        double output = outputs[neuronIndex];
        double outputTarget = outputTargets[neuronIndex];

        return Math.pow(outputTarget - output, 2.0D);
    }

    /**
     * @return int
     */
    protected int getParallelism()
    {
        return this.parallelism;
    }

    /**
     * Aufsplitten der Neuronen für parallele Verarbeitung.<br>
     * Es wird pro Thread eine SubList verarbeitet.<br>
     * Keine parallele Verarbeitung für einzelne Elemente, dadurch zu hoher Verwaltungsaufwand für die Runtime.
     *
     * @param neurons {@link NeuronList}
     * @param parallelism int
     *
     * @return {@link List}<NeuronList>
     */
    protected List<NeuronList> getPartitions(final NeuronList neurons, final int parallelism)
    {
        int partitionCount = Math.min(neurons.size(), parallelism);
        int partitionLength = neurons.size() / partitionCount;

        int[] partitionSizes = new int[partitionCount];
        Arrays.fill(partitionSizes, partitionLength);

        int sum = partitionCount * partitionLength;

        // Länge der einzelnen Partitionen ist zu groß.
        // Von hinten Index für Index reduzieren bis es passt.
        int index = partitionCount - 1;

        while (sum > neurons.size())
        {
            partitionSizes[index]--;

            sum--;
            index--;
        }

        // Länge der einzelnen Partitionen ist zu klein.
        // Von vorne Index für Index erhöhen bis es passt.
        index = 0;

        while (sum < neurons.size())
        {
            partitionSizes[index]++;

            sum++;
            index++;
        }

        List<NeuronList> partitions = new ArrayList<>(partitionCount);
        int fromIndex = 0;

        for (int partitionSize : partitionSizes)
        {
            partitions.add(neurons.subList(fromIndex, fromIndex + partitionSize));

            fromIndex += partitionSize;
        }

        return partitions;
    }

    /**
     * Initialisiert die BIAS-Gewichte der Neuronen des Layers.
     *
     * @param layer {@link Layer}
     * @param valueInitializer {@link ValueInitializer}
     */
    protected void initialize(final Layer layer, final ValueInitializer valueInitializer)
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
     * Aktualisiert die Gewichte eines Neurons aus den Fehlern und Ausgangswerten des nachfolgenden Layers.
     *
     * @param neuron {@link Neuron}
     * @param teachFactor double
     * @param momentum double
     * @param leftOutputs double[]
     * @param deltaWeights double[][]
     * @param rightErrors double[]
     */
    protected void refreshLayerWeights(final Neuron neuron, final double teachFactor, final double momentum, final double[] leftOutputs,
                                       final double[][] deltaWeights, final double[] rightErrors)
    {
        int layerIndex = neuron.getLayerIndex();

        for (int i = 0; i < neuron.getOutputSize(); i++)
        {
            double weight = neuron.getOutputWeight(i);
            double deltaWeight = teachFactor * rightErrors[i] * leftOutputs[layerIndex];

            // Momentum-Term berücksichtigen (konjugierter Gradientenabstieg).
            // Der Momentum-Term erhöht die Schrittweite auf flachen Niveaus und reduziert
            // in Tälern.
            deltaWeight += (momentum * deltaWeights[layerIndex][i]);

            neuron.setOutputWeight(i, weight + deltaWeight);
            deltaWeights[layerIndex][i] = deltaWeight;

            // Bias verrechnen, feuert immer.
            // So wie es aussieht müssen die Biasgewichte nicht angepasst werden.
            //
            // double biasWeight = matrix.getBiasWeights()[i];
            // double biasDeltaWeight = this.teachFactor * rightErrors[i] * 1;
            // matrix.getBiasWeights()[i] = biasWeight + biasDeltaWeight;
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
    protected void setOutputError(final int neuronIndex, final double[] outputs, final double[] errors, final BackwardVisitor visitor)
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
     * @see de.freese.knn.net.math.KnnMath#setOutputError(de.freese.knn.net.layer.Layer, de.freese.knn.net.visitor.BackwardVisitor)
     */
    @Override
    public void setOutputError(final Layer layer, final BackwardVisitor visitor)
    {
        double[] outputs = visitor.getOutputs(layer);
        double[] errors = new double[outputs.length];

        for (int i = 0; i < outputs.length; i++)
        {
            setOutputError(i, outputs, errors, visitor);
        }

        visitor.setErrors(layer, errors);
    }
}
