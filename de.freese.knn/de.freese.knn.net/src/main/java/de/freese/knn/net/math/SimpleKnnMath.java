/**
 * Created: 02.10.2011
 */

package de.freese.knn.net.math;

import java.util.List;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.matrix.IValueInitializer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} für die sequentielle Verarbeitung im aktuellen Thread.
 * 
 * @author Thomas Freese
 */
public class SimpleKnnMath extends AbstractKnnMath
{
	/**
	 * Erstellt ein neues {@link SimpleKnnMath} Object.
	 */
	public SimpleKnnMath()
	{
		super();
	}

	/**
	 * @see de.freese.knn.net.math.IKnnMath#backward(de.freese.knn.net.layer.ILayer,
	 *      de.freese.knn.net.visitor.BackwardVisitor)
	 */
	@Override
	public void backward(final ILayer layer, final BackwardVisitor visitor)
	{
		double[] errors = visitor.getLastErrors();
		double[] layerErrors = new double[layer.getSize()];

		backward(layer.getNeurons(), errors, layerErrors);

		visitor.setErrors(layer, layerErrors);
	}

	/**
	 * @see de.freese.knn.net.math.IKnnMath#forward(de.freese.knn.net.layer.ILayer,
	 *      de.freese.knn.net.visitor.ForwardVisitor)
	 */
	@Override
	public void forward(final ILayer layer, final ForwardVisitor visitor)
	{
		double[] inputs = visitor.getLastOutputs();
		double[] outputs = new double[layer.getSize()];

		forward(layer.getNeurons(), inputs, outputs);

		visitor.setOutputs(layer, outputs);
	}

	/**
	 * @see de.freese.knn.net.math.IKnnMath#initialize(de.freese.knn.net.matrix.IValueInitializer,
	 *      java.util.List)
	 */
	@Override
	public void initialize(final IValueInitializer valueInitializer, final List<ILayer> layers)
	{
		initialize(layers, valueInitializer);
	}

	/**
	 * @see de.freese.knn.net.math.IKnnMath#refreshLayerWeights(de.freese.knn.net.layer.ILayer,
	 *      de.freese.knn.net.layer.ILayer, double, double, de.freese.knn.net.visitor.BackwardVisitor)
	 */
	@Override
	public void refreshLayerWeights(final ILayer leftLayer, final ILayer rightLayer,
									final double teachFactor, final double momentum,
									final BackwardVisitor visitor)
	{
		double[] leftOutputs = visitor.getOutputs(leftLayer);
		double[][] deltaWeights = visitor.getDeltaWeights(leftLayer);
		double[] rightErrors = visitor.getErrors(rightLayer);

		refreshLayerWeights(leftLayer.getNeurons(), teachFactor, momentum, leftOutputs,
				deltaWeights, rightErrors);
	}
}
