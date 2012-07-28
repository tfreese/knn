/**
 * 06.06.2008
 */
package de.freese.knn.net;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.model.release.IReleaseable;
import de.freese.base.model.visitor.IVisitable;
import de.freese.base.model.visitor.IVisitor;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;
import de.freese.knn.net.math.IKnnMath;
import de.freese.knn.net.math.SimpleKnnMath;
import de.freese.knn.net.matrix.IValueInitializer;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.matrix.RandomValueInitializer;
import de.freese.knn.net.persister.INetPersister;
import de.freese.knn.net.persister.NetPersisterBinary;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Basisklasses des neuralen Netzes.
 * 
 * @author Thomas Freese
 */
public class NeuralNet implements IVisitable, IReleaseable
{
	/**
	 *
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NeuralNet.class);

	/**
	 *
	 */
	private final IKnnMath knnMath;

	/**
	 * 
	 */
	private boolean layerConnected = false;

	/**
	 * 
	 */
	private List<ILayer> layers = new ArrayList<>();

	/**
	 * 
	 */
	private final IValueInitializer valueInitializer;

	/**
	 * Creates a new {@link NeuralNet} object.
	 */
	public NeuralNet()
	{
		this(new SimpleKnnMath(), new RandomValueInitializer());
	}

	/**
	 * Creates a new {@link NeuralNet} object.
	 * 
	 * @param knnMath {@link IKnnMath}
	 */
	public NeuralNet(final IKnnMath knnMath)
	{
		this(knnMath, new RandomValueInitializer());
	}

	/**
	 * Creates a new {@link NeuralNet} object.
	 * 
	 * @param knnMath {@link IKnnMath}
	 * @param valueInitializer {@link IValueInitializer}
	 */
	public NeuralNet(final IKnnMath knnMath, final IValueInitializer valueInitializer)
	{
		super();

		if (knnMath == null)
		{
			throw new NullPointerException("IKnnMath");
		}

		if (valueInitializer == null)
		{
			throw new NullPointerException("IValueInitializer");
		}

		this.knnMath = knnMath;
		this.valueInitializer = valueInitializer;
	}

	/**
	 * Fügt einen Layer hinzu.<br>
	 * Der erste muss ein {@link InputLayer} sein, der letzte ein {@link OutputLayer}.
	 * 
	 * @param layer {@link ILayer}
	 */
	public void addLayer(final ILayer layer)
	{
		if (this.layerConnected)
		{
			throw new IllegalStateException("Layer already connected");
		}

		// InputLayer ist immer der erste
		if ((this.layers.isEmpty()) && !(layer instanceof InputLayer))
		{
			throw new IllegalArgumentException("InputLayer required");
		}

		// OutputLayer ist immer der letzte
		if ((this.layers.size() > 0)
				&& (this.layers.get(this.layers.size() - 1) instanceof OutputLayer))
		{
			throw new IllegalArgumentException("OutputLayer already set");
		}

		this.layers.add(layer);
	}

	/**
	 * Verbindet die Layer mit den Matrixobjekten.
	 */
	public void connectLayer()
	{
		if (this.layerConnected)
		{
			throw new IllegalStateException("Layer already connected");
		}

		for (int i = 0; i < (this.layers.size() - 1); i++)
		{
			ILayer leftLayer = this.layers.get(i);
			ILayer rightLayer = this.layers.get(i + 1);

			Matrix matrix =
					new Matrix(leftLayer.getNeurons().size(), rightLayer.getNeurons().size());

			leftLayer.setOutputMatrix(matrix);
			rightLayer.setInputMatrix(matrix);
		}

		// Gewichte initialisieren
		getMath().initialize(getValueInitializer(), this.layers);

		finishConnectLayer();
	}

	/**
	 * 
	 */
	private void finishConnectLayer()
	{
		// Ab hier keine Aenderung der Layers mehr moeglich.
		this.layers = Collections.unmodifiableList(this.layers);

		this.layerConnected = true;
	}

	/**
	 * Liefert die Layer.
	 * 
	 * @return {@link List}
	 */
	public List<ILayer> getLayer()
	{
		return this.layers;
	}

	/**
	 * Liefert die Mathematik-Implementierung.
	 * 
	 * @return {@link IKnnMath}
	 */
	public IKnnMath getMath()
	{
		return this.knnMath;
	}

	/**
	 * Berechnet und liefert die Ausgabewerte anhand der Eingabewerte.
	 * 
	 * @param inputs double[]
	 * @return double[]
	 */
	public double[] getOutput(final double[] inputs)
	{
		ForwardVisitor visitor = new ForwardVisitor(false);
		visitor.setInputs(inputs);

		visit(visitor);

		double[] outputs = getOutputLayer().adjustOutput(visitor);

		visitor.clear();
		visitor = null;

		return outputs;
	}

	/**
	 * Liefert den {@link OutputLayer}.
	 * 
	 * @return {@link OutputLayer}
	 */
	private OutputLayer getOutputLayer()
	{
		return (OutputLayer) this.layers.get(this.layers.size() - 1);
	}

	/**
	 * @return {@link IValueInitializer}
	 */
	private IValueInitializer getValueInitializer()
	{
		return this.valueInitializer;
	}

	/**
	 * Laden des Netzes aus dem Stream. Der Stream wird NICHT geschlossen !
	 * 
	 * @param inputStream {@link InputStream}
	 * @throws Exception Falls was schief geht.
	 */
	public void load(final InputStream inputStream) throws Exception
	{
		load(inputStream, new NetPersisterBinary());
	}

	/**
	 * Laden des Netzes aus dem Stream. Der Stream wird NICHT geschlossen !
	 * 
	 * @param inputStream {@link InputStream}
	 * @param netPersister {@link INetPersister}
	 * @throws Exception Falls was schief geht.
	 */
	public void load(final InputStream inputStream, final INetPersister netPersister)
		throws Exception
	{
		netPersister.load(inputStream, this);

		finishConnectLayer();
	}

	/**
	 * @see de.freese.base.model.release.IReleaseable#release()
	 */
	@Override
	public void release()
	{
		LOGGER.info("");

		// this.layers.clear();
		this.layers = null;

		getMath().release();
	}

	/**
	 * Speichern des Netzes in den Stream. Der Stream wird NICHT geschlossen !
	 * 
	 * @param outputStream {@link OutputStream}
	 * @throws Exception Falls was schief geht.
	 */
	public void save(final OutputStream outputStream) throws Exception
	{
		save(outputStream, new NetPersisterBinary());
	}

	/**
	 * Speichern des Netzes in den Stream. Der Stream wird NICHT geschlossen !
	 * 
	 * @param outputStream {@link OutputStream}
	 * @param netPersister {@link INetPersister}
	 * @throws Exception Falls was schief geht.
	 */
	public void save(final OutputStream outputStream, final INetPersister netPersister)
		throws Exception
	{
		netPersister.save(outputStream, this);
	}

	/**
	 * @see de.freese.base.model.visitor.IVisitable#visit(de.freese.base.model.visitor.IVisitor)
	 */
	@Override
	public void visit(final IVisitor visitor)
	{
		visitor.visitObject(this);
	}
}
