// Created: 02.10.2011
package de.freese.knn.net.math;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} für die sequentielle Verarbeitung im aktuellen Thread.
 *
 * @author Thomas Freese
 */
public final class KnnMathSimple extends AbstractKnnMath {
    public KnnMathSimple() {
        super();
    }

    @Override
    public void backward(final Layer layer, final BackwardVisitor visitor) {
        double[] errors = visitor.getLastErrors();
        double[] layerErrors = new double[layer.getSize()];

        layer.getNeurons().forEach(neuron -> backward(neuron, errors, layerErrors));

        visitor.setErrors(layer, layerErrors);
    }

    @Override
    public void forward(final Layer layer, final ForwardVisitor visitor) {
        double[] inputs = visitor.getLastOutputs();
        double[] outputs = new double[layer.getSize()];

        layer.getNeurons().forEach(neuron -> forward(neuron, inputs, outputs));

        visitor.setOutputs(layer, outputs);
    }

    @Override
    public void initialize(final ValueInitializer valueInitializer, final Layer[] layers) {
        for (Layer layer : layers) {
            initialize(layer, valueInitializer);
        }
    }

    @Override
    public void refreshLayerWeights(final Layer leftLayer, final Layer rightLayer, final double teachFactor, final double momentum, final BackwardVisitor visitor) {
        double[] leftOutputs = visitor.getOutputs(leftLayer);
        double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
        double[] rightErrors = visitor.getErrors(rightLayer);

        leftLayer.getNeurons().forEach(neuron -> refreshLayerWeights(neuron, teachFactor, momentum, leftOutputs, deltaWeights, rightErrors));
    }
}
