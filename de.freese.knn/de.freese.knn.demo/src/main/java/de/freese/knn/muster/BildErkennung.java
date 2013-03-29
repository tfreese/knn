/**
 * 15.07.2008
 */
package de.freese.knn.muster;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.hidden.SigmoidLayer;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;
import de.freese.knn.net.math.executor.ExecutorKnnMath;
import de.freese.knn.net.trainer.ITrainingInputSource;
import de.freese.knn.net.trainer.NetTrainer;
import de.freese.knn.net.trainer.PrintStreamNetTrainerListener;
import java.awt.Toolkit;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Testklasse.
 * <p/>
 * @author Thomas Freese
 */
public class BildErkennung
{
    /**
     * @param args String[]
     * <p/>
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        NeuralNet neuralNet = null;
        // ITrainingInputSource trainingInputSource = new ImagePixelTrainingInputSource();
        ITrainingInputSource trainingInputSource = new ImageInfoTrainingInputSource();
        File nnFile = new File("ImageNeuralNet.bin");
        // if (!nnFile.exists())
        // if (true)
        {
            // neuralNet = new NeuralNet();
            neuralNet = new NeuralNet(new ExecutorKnnMath());
//			neuralNet = new NeuralNet(new ForkJoinKnnMath());
            neuralNet.addLayer(new InputLayer(36));
            neuralNet.addLayer(new SigmoidLayer(50));
            neuralNet.addLayer(new OutputLayer(10));
            neuralNet.connectLayer();

            double teachFactor = 0.5D;
            double momentum = 0.5D;
            double maximumError = 0.01D;
            int maximumIteration = Integer.MAX_VALUE;

            long start = System.currentTimeMillis();

            NetTrainer trainer =
                    new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
            trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out, 500));
            // trainer.addNetTrainerListener(new LoggerNetTrainerListener(500));
            trainer.train(neuralNet, trainingInputSource);

            System.out.println("Lernzeit: " + ((System.currentTimeMillis() - start) / 1000) + " s");

            Toolkit.getDefaultToolkit().beep();

            // Speichern
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(nnFile)))
            {
                neuralNet.save(outputStream);
            }
        }
        // else
        // {
        // InputStream inputStream = new BufferedInputStream(new FileInputStream(nnFile));
        // neuralNet.load(inputStream);
        // inputStream.close();
        // }

        // Netz testen
        for (int i = 0; i < trainingInputSource.getSize(); i++)
        {
            double[] inputs = trainingInputSource.getInputAt(i);
            double[] outputs = neuralNet.getOutput(inputs);

            for (double output : outputs)
            {
                // System.out.print(output + ", ");
                System.out.print(String.format("   %1.10f", Double.valueOf(output)));
            }

            System.out.println();
        }

        try
        {
            neuralNet.release();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        System.exit(0);
    }
}
