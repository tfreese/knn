/**
 * 11.06.2008
 */
package de.freese.knn;

import de.freese.knn.buttons.MatrixTrainingInputSource;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.hidden.SigmoidLayer;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;
import de.freese.knn.net.math.forkjoin.ForkJoinKnnMath;
import de.freese.knn.net.trainer.ITrainingInputSource;
import de.freese.knn.net.trainer.LoggerNetTrainerListener;
import de.freese.knn.net.trainer.NetTrainer;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Klasse zum Test des BinaryPersisters.
 * 
 * @author Thomas Freese
 */
public class TestPersisterBinary
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		NeuralNet neuralNetwork = new NeuralNet(new ForkJoinKnnMath());
		neuralNetwork.addLayer(new InputLayer(54));
		neuralNetwork.addLayer(new SigmoidLayer(25));
		neuralNetwork.addLayer(new OutputLayer(10));
		neuralNetwork.connectLayer();
		double teachFactor = 0.5D;
		double momentum = 0.5D;
		double maximumError = 0.05D;
		int maximumIteration = 10000;

		NetTrainer trainer = new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
		// trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out));
		trainer.addNetTrainerListener(new LoggerNetTrainerListener());
		ITrainingInputSource trainingInputSource = new MatrixTrainingInputSource();
		trainer.train(neuralNetwork, trainingInputSource);

		// Speichern
		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream("NeuralNet.bin")))
		{
			neuralNetwork.save(outputStream);
		}

		neuralNetwork.release();

		// Laden
		neuralNetwork = new NeuralNet(new ForkJoinKnnMath());

		try (InputStream inputStream = new BufferedInputStream(new FileInputStream("NeuralNet.bin")))
		{
			neuralNetwork.load(inputStream);
		}

		// Netz testen
		double[] inputs = trainingInputSource.getInputAt(0);
		double[] outputs = neuralNetwork.getOutput(inputs);

		System.out.println(Arrays.toString(outputs));
	}
}
