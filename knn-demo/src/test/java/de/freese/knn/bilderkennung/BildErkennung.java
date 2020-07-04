/**
 * 15.07.2008
 */
package de.freese.knn.bilderkennung;

import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.NeuralNetBuilder;
import de.freese.knn.net.function.FunctionSigmoide;
import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.persister.NetPersister;
import de.freese.knn.net.persister.NetPersisterBinary;
import de.freese.knn.net.trainer.NetTrainer;
import de.freese.knn.net.trainer.PrintStreamNetTrainerListener;
import de.freese.knn.net.trainer.TrainingInputSource;

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
        TrainingInputSource trainingInputSource = new ImagePixelTrainingInputSource();
        // final ITrainingInputSource trainingInputSource = new ImageInfoTrainingInputSource();
        File knnFile = new File("ImageNeuralNet.bin");

        knnFile.delete();

        if (!knnFile.exists())
        {
            // Speichern
            try ( // @formatter:off
              NeuralNet neuralNet = new NeuralNetBuilder()
                  //.knnMath(new KnnMathExecutor())
                  .layerInput(new InputLayer(36))
                  .layerHidden(new HiddenLayer(50, new FunctionSigmoide()))
                  .layerOutput(new OutputLayer(10))
                  .build();
             // @formatter:on
                 DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(knnFile))))
            {
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
                NetPersister<DataInput, DataOutput> persister = new NetPersisterBinary();
                persister.save(dos, neuralNet);
            }
        }
        else
        {
            // Laden
            try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(knnFile))))
            {
                NetPersister<DataInput, DataOutput> persister = new NetPersisterBinary();

                try (NeuralNet neuralNet = persister.load(dis))
                {
                    // Netz testen
                    for (int i = 0; i < trainingInputSource.getSize(); i++)
                    {
                        final double[] inputs = trainingInputSource.getInputAt(i);
                        final double[] outputs = neuralNet.getOutput(inputs);

                        for (final double output : outputs)
                        {
                            // System.out.print(output + ", ");
                            System.out.print(String.format(" %1.10f", Double.valueOf(output)));
                        }

                        System.out.println();
                    }
                }
            }
        }

        System.exit(0);
    }
}
