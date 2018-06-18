/**
 * 15.07.2008
 */
package de.freese.knn.muster;

import java.awt.Toolkit;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.hidden.SigmoidLayer;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;
import de.freese.knn.net.trainer.ITrainingInputSource;
import de.freese.knn.net.trainer.NetTrainer;
import de.freese.knn.net.trainer.PrintStreamNetTrainerListener;

/**
 * Testklasse.
 *
 * @author Thomas Freese
 */
public class BildErkennung
{
    /**
     * @param args String[]
     *            <p/>
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        ITrainingInputSource trainingInputSource = new ImagePixelTrainingInputSource();
        // final ITrainingInputSource trainingInputSource = new ImageInfoTrainingInputSource();
        File knnFile = new File("ImageNeuralNet.bin");

        // neuralNet = new NeuralNet(new ExecutorKnnMath());
        // neuralNet = new NeuralNet(new ForkJoinKnnMath());

        // if (!nnFile.exists())

        // Speichern
        try (NeuralNet neuralNet = new NeuralNet();
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(knnFile))))
        {
            neuralNet.addLayer(new InputLayer(36));
            neuralNet.addLayer(new SigmoidLayer(50));
            neuralNet.addLayer(new OutputLayer(10));
            neuralNet.connectLayer();

            final double teachFactor = 0.5D;
            final double momentum = 0.5D;
            final double maximumError = 0.01D;
            final int maximumIteration = Integer.MAX_VALUE;

            final long start = System.currentTimeMillis();

            final NetTrainer trainer = new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
            trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out, 500));
            // trainer.addNetTrainerListener(new LoggerNetTrainerListener(500));
            trainer.train(neuralNet, trainingInputSource);

            System.out.println("Lernzeit: " + ((System.currentTimeMillis() - start) / 1000) + " s");

            Toolkit.getDefaultToolkit().beep();

            // Speichern
            neuralNet.save(dos);
        }

        // Laden
        // try (NeuralNet neuralNet = new NeuralNet())
        // {
        // // Netz testen
        // for (int i = 0; i < trainingInputSource.getSize(); i++)
        // {
        // final double[] inputs = trainingInputSource.getInputAt(i);
        // final double[] outputs = neuralNet.getOutput(inputs);
        //
        // for (final double output : outputs)
        // {
        // // System.out.print(output + ", ");
        // System.out.print(String.format(" %1.10f", Double.valueOf(output)));
        // }
        //
        // System.out.println();
        // }
        // }

        System.exit(0);
    }
}
