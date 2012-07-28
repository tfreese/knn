/**
 * 11.06.2008
 */
package de.freese.knn.net.persister;

import java.io.InputStream;
import java.io.OutputStream;

import de.freese.knn.net.NeuralNet;

/**
 * Interface um ein neuronales Netz zu speichern oder zu laden.
 * 
 * @author Thomas Freese
 */
public interface INetPersister
{
	/**
	 * Laden des Netzes aus dem Stream. Der Stream wird NICHT geschlossen !
	 * 
	 * @param inputStream {@link InputStream}
	 * @param neuralNet {@link NeuralNet}
	 * @throws Exception Falls was schief geht.
	 */
	public void load(InputStream inputStream, NeuralNet neuralNet) throws Exception;

	/**
	 * Speichern des Netzes in den Stream. Der Stream wird NICHT geschlossen !
	 * 
	 * @param outputStream {@link OutputStream}
	 * @param neuralNet {@link NeuralNet}
	 * @throws Exception Falls was schief geht.
	 */
	public void save(OutputStream outputStream, NeuralNet neuralNet) throws Exception;
}
