/**
 * Created: 02.10.2011
 */

package de.freese.knn.net.math.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.math.AbstractKnnMath;
import de.freese.knn.net.matrix.IValueInitializer;
import de.freese.knn.net.neuron.INeuron;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Mathematik des {@link NeuralNet} mit dem {@link ExecutorService}-Framework.
 * 
 * @author Thomas Freese
 */
public class ExecutorKnnMath extends AbstractKnnMath
{
	/**
	 *
	 */
	private boolean createdExecutor = false;

	/**
	 * 
	 */
	private final Executor executor;

	/**
	 * 
	 */
	private final int processors;

	/**
	 * Erstellt ein neues {@link ExecutorKnnMath} Object.
	 */
	public ExecutorKnnMath()
	{
		this(Executors.newCachedThreadPool(new KnnThreadQueueThreadFactory()));

		this.createdExecutor = true;
	}

	/**
	 * Erstellt ein neues {@link ExecutorKnnMath} Object.
	 * 
	 * @param executor {@link Executor}
	 */
	public ExecutorKnnMath(final Executor executor)
	{
		super();

		if (executor == null)
		{
			throw new NullPointerException("Executor");
		}

		this.executor = executor;
		this.processors = Runtime.getRuntime().availableProcessors();
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

		List<List<INeuron>> pieces = getPieces(layer.getNeurons());
		CountDownLatch latch = new CountDownLatch(pieces.size());

		for (List<INeuron> piece : pieces)
		{
			BackwardTask task = new BackwardTask(latch, piece, errors, layerErrors);
			this.executor.execute(task);
		}

		doLatchAwait(latch);

		visitor.setErrors(layer, layerErrors);
	}

	/**
	 * Blockiert den aktuellen Thread bis der Latch auf 0 ist.
	 * 
	 * @param latch {@link CountDownLatch}
	 */
	private void doLatchAwait(final CountDownLatch latch)
	{
		try
		{
			latch.await();
		}
		catch (Throwable th)
		{
			if (th instanceof RuntimeException)
			{
				throw (RuntimeException) th;
			}

			throw new RuntimeException(th);
		}
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

		List<List<INeuron>> pieces = getPieces(layer.getNeurons());
		CountDownLatch latch = new CountDownLatch(pieces.size());

		for (List<INeuron> piece : pieces)
		{
			ForwardTask task = new ForwardTask(latch, piece, inputs, outputs);
			this.executor.execute(task);
		}

		doLatchAwait(latch);

		visitor.setOutputs(layer, outputs);
	}

	/**
	 * Aufsplitten der Neuronen für parallele Verarbeitung.
	 * 
	 * @param neurons List
	 * @return List<List<INeuron>>
	 */
	private List<List<INeuron>> getPieces(final List<INeuron> neurons)
	{
		List<List<INeuron>> pieces = new ArrayList<>();

		if ((this.processors == 1) || (neurons.size() == 1))
		{
			pieces.add(neurons);

			return pieces;
		}

		if (neurons.size() < this.processors)
		{
			pieces.add(neurons);

			return pieces;
		}

		int size = neurons.size() / this.processors;
		int start = 0;

		for (int p = 0; p < (this.processors - 1); p++)
		{
			List<INeuron> part = neurons.subList(start, size);
			pieces.add(part);

			start = size;
		}

		List<INeuron> part = neurons.subList(start, neurons.size());
		pieces.add(part);

		return pieces;
	}

	/**
	 * @see de.freese.knn.net.math.IKnnMath#initialize(de.freese.knn.net.matrix.IValueInitializer,
	 *      java.util.List)
	 */
	@Override
	public void initialize(final IValueInitializer valueInitializer, final List<ILayer> layers)
	{
		CountDownLatch latch = new CountDownLatch(layers.size());

		for (ILayer layer : layers)
		{
			InitializeTask task = new InitializeTask(latch, valueInitializer, layer);
			this.executor.execute(task);
		}

		doLatchAwait(latch);
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

		List<List<INeuron>> pieces = getPieces(leftLayer.getNeurons());
		CountDownLatch latch = new CountDownLatch(pieces.size());

		for (List<INeuron> piece : pieces)
		{
			RefreshWeightsTask task =
					new RefreshWeightsTask(latch, piece, teachFactor, momentum, leftOutputs,
							deltaWeights, rightErrors);
			this.executor.execute(task);
		}

		doLatchAwait(latch);
	}

	/**
	 * @see de.freese.base.core.release.IReleaseable#release()
	 */
	@Override
	public void release()
	{
		super.release();

		if (this.createdExecutor && (this.executor instanceof ExecutorService))
		{
			getLogger().info("Shutdown ExecutorService");

			ExecutorService executorService = (ExecutorService) this.executor;
			executorService.shutdown();

			while (!executorService.isTerminated())
			{
				try
				{
					executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
				}
				catch (InterruptedException ex)
				{
					getLogger().error(null, ex);
				}
			}
		}
	}
}
