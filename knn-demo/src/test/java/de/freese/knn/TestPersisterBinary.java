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
import de.freese.knn.net.NeuralNetBuilder;
import de.freese.knn.net.function.FunctionSigmoide;
import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.trainer.LoggerNetTrainerListener;
import de.freese.knn.net.trainer.NetTrainer;
import de.freese.knn.net.trainer.TrainingInputSource;

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
        TrainingInputSource trainingInputSource = new MatrixTrainingInputSource();
        File knnFile = new File("NeuralNet.bin");

        try ( // @formatter:off
              NeuralNet neuralNet = new NeuralNetBuilder()
                  .layer(new InputLayer(54))
                  .layer(new HiddenLayer(25, new FunctionSigmoide()))
                  .layer(new OutputLayer(10))
                  .build();
               // @formatter:on
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(knnFile))))
        {
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
