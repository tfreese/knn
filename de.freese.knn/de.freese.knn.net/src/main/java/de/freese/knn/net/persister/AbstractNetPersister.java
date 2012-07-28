/**
 * 11.06.2008
 */
package de.freese.knn.net.persister;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.matrix.Matrix;

/**
 * BasisPersister für das laden und speichen eines neuralen Netzes.
 * 
 * @author Thomas Freese
 */
public abstract class AbstractNetPersister implements INetPersister
{
	/**
	 * Creates a new {@link AbstractNetPersister} object.
	 */
	public AbstractNetPersister()
	{
		super();
	}

	/**
	 * @see de.freese.knn.net.persister.INetPersister#save(java.io.OutputStream,
	 *      de.freese.knn.net.NeuralNet)
	 */
	@Override
	public void save(final OutputStream outputStream, final NeuralNet neuralNet) throws Exception
	{
		List<ILayer> layers = neuralNet.getLayer();

		for (int i = 0; i < layers.size(); i++)
		{
			ILayer layer = layers.get(i);
			saveLayer(outputStream, layer);

			if (i < layers.size() - 1)
			{
				saveMatrix(outputStream, layer.getOutputMatrix());
			}
		}

		outputStream.flush();
	}

	/**
	 * Liest einen Layer in den Stream.
	 * 
	 * @param inputStream {@link InputStream}
	 * @return {@link ILayer}
	 * @throws Exception Falls was schief geht.
	 */
	protected abstract ILayer loadLayer(InputStream inputStream) throws Exception;

	/**
	 * Liest eine Matrix in den Stream.
	 * 
	 * @param inputStream {@link InputStream}
	 * @return {@link Matrix}
	 * @throws Exception Falls was schief geht.
	 */
	protected abstract Matrix loadMatrix(InputStream inputStream) throws Exception;

	/**
	 * Schreibt einen Layer in den Stream.
	 * 
	 * @param outputStream {@link OutputStream}
	 * @param layer {@link ILayer}
	 * @throws Exception Falls was schief geht.
	 */
	protected abstract void saveLayer(OutputStream outputStream, ILayer layer) throws Exception;

	/**
	 * Schreibt eine Matrix in den Stream.
	 * 
	 * @param outputStream {@link OutputStream}
	 * @param matrix {@link Matrix}
	 * @throws Exception Falls was schief geht.
	 */
	protected abstract void saveMatrix(OutputStream outputStream, Matrix matrix) throws Exception;
}
