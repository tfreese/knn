// Created: 11.06.2008
package de.freese.knn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.knn.buttons.KnnButtonTrainingInputSource;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.NeuralNetBuilder;
import de.freese.knn.net.function.FunctionSigmoid;
import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.persister.NetPersister;
import de.freese.knn.net.persister.NetPersisterBinary;
import de.freese.knn.net.trainer.LoggerNetTrainerListener;
import de.freese.knn.net.trainer.NetTrainer;
import de.freese.knn.net.trainer.TrainingInputSource;

/**
 * Klasse zum Test des BinaryPersisters.
 *
 * @author Thomas Freese
 */
public final class TestPersisterBinary {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestPersisterBinary.class);

    public static void main(final String[] args) throws Exception {
        final TrainingInputSource trainingInputSource = new KnnButtonTrainingInputSource();
        final Path knnFile = Paths.get(System.getProperty("java.io.tmpdir"), "ButtonNeuralNet.bin");

        NeuralNet neuralNet = new NeuralNetBuilder()
                .layerInput(new InputLayer(54))
                .layerHidden(new HiddenLayer(25, new FunctionSigmoid()))
                .layerOutput(new OutputLayer(10))
                .build();

        try (DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(knnFile)))) {
            final double teachFactor = 0.5D;
            final double momentum = 0.5D;
            final double maximumError = 0.05D;
            final int maximumIteration = 10000;

            final NetTrainer trainer = new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
            // trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out));
            trainer.addNetTrainerListener(new LoggerNetTrainerListener());
            trainer.train(neuralNet, trainingInputSource);

            // Speichern
            final NetPersister<DataInput, DataOutput> persister = new NetPersisterBinary();
            persister.save(dos, neuralNet);

        }

        neuralNet.close();

        // Laden
        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(Files.newInputStream(knnFile)))) {
            final NetPersister<DataInput, DataOutput> persister = new NetPersisterBinary();

            neuralNet = persister.load(dis);

            // Netz testen
            final double[] inputs = trainingInputSource.getInputAt(0);
            final double[] outputs = neuralNet.getOutput(inputs);

            LOGGER.info(Arrays.toString(outputs));

            neuralNet.close();
        }
    }

    private TestPersisterBinary() {
        super();
    }
}
