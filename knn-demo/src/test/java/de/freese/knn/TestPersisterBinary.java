/**
 * 11.06.2008
 */
package de.freese.knn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import de.freese.knn.buttons.MatrixTrainingInputSource;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.hidden.SigmoidLayer;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;
import de.freese.knn.net.trainer.ITrainingInputSource;
import de.freese.knn.net.trainer.LoggerNetTrainerListener;
import de.freese.knn.net.trainer.NetTrainer;

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
        ITrainingInputSource trainingInputSource = new MatrixTrainingInputSource();
        File knnFile = new File("NeuralNet.bin");

        try (NeuralNet neuralNet = new NeuralNet();
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(knnFile))))
        {
            neuralNet.addLayer(new InputLayer(54));
            neuralNet.addLayer(new SigmoidLayer(25));
            neuralNet.addLayer(new OutputLayer(10));
            neuralNet.connectLayer();
            double teachFactor = 0.5D;
            double momentum = 0.5D;
            double maximumError = 0.05D;
            int maximumIteration = 10000;

            NetTrainer trainer = new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
            // trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out));
            trainer.addNetTrainerListener(new LoggerNetTrainerListener());
            trainer.train(neuralNet, trainingInputSource);

            // Speichern
            neuralNet.save(dos);
        }

        // Laden
        try (NeuralNet neuralNet = new NeuralNet();
             DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(knnFile))))
        {
            neuralNet.load(dis);

            // Netz testen
            double[] inputs = trainingInputSource.getInputAt(0);
            double[] outputs = neuralNet.getOutput(inputs);

            System.out.println(Arrays.toString(outputs));
        }
    }
}
