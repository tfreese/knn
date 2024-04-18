// Created: 15.07.2008
package de.freese.knn.bilderkennung;

import java.awt.Toolkit;
import java.util.Arrays;
import java.util.stream.Collectors;

import de.freese.knn.bilderkennung.utils.ImageData;
import de.freese.knn.bilderkennung.utils.image.info.ImageInfo;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.NeuralNetBuilder;
import de.freese.knn.net.function.FunctionSigmoid;
import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.math.KnnMathStream;
import de.freese.knn.net.trainer.NetTrainer;
import de.freese.knn.net.trainer.PrintStreamNetTrainerListener;
import de.freese.knn.net.trainer.TrainingInputSource;

/**
 * Testklasse.
 *
 * @author Thomas Freese
 */
public final class BildErkennungMain {
    public static void main(final String[] args) throws Exception {
        // final TrainingInputSource trainingInputSource = new ImageInfoTrainingInputSource();
        final TrainingInputSource trainingInputSource = new ImagePixelTrainingInputSource();

        //        final int parallelism = Runtime.getRuntime().availableProcessors();

        final NeuralNetBuilder builder = new NeuralNetBuilder()
                // .knnMath(new KnnMathSimple())
                .knnMath(new KnnMathStream()) // Ist Default im NeuralNetBuilder
                // .knnMath(new KnnMathForkJoin(ForkJoinPool.commonPool()))
                // .knnMath(new KnnMathExecutor(Executors.newFixedThreadPool(parallelism), parallelism))
                // .knnMath(new KnnMathQueueWorker(parallelism))
                // .knnMath(new KnnMathReactor(parallelism))
                // .knnMath(new KnnMathPublishSubscribe(Executors.newFixedThreadPool(parallelism), parallelism))
                // .knnMath(new KnnMathCompletionService(Executors.newFixedThreadPool(parallelism), parallelism))
                // .knnMath(new KnnMathExecutorHalfWork(Executors.newFixedThreadPool(1)))
                // .knnMath(new KnnMathDisruptorPerPartition(parallelism))
                // .knnMath(new KnnMathDisruptorPerNeuron(parallelism))
                // .knnMath(new KnnMathVirtualThread())
                ;

        if (trainingInputSource instanceof ImageInfoTrainingInputSource) {
            builder.layerInput(new InputLayer(trainingInputSource.getInputAt(0).length))
                    .layerHidden(new HiddenLayer(100, new FunctionSigmoid()))
            ;
        }
        else if (trainingInputSource instanceof ImagePixelTrainingInputSource) {
            builder.layerInput(new InputLayer(trainingInputSource.getInputAt(0).length))
                    .layerHidden(new HiddenLayer(1000, new FunctionSigmoid()))
                    .layerHidden(new HiddenLayer(100, new FunctionSigmoid()))
            ;
        }

        builder.layerOutput(new OutputLayer(trainingInputSource.getSize()));
        // builder.layerOutput(new OutputWtaLayer(trainingInputSource.getSize()));

        final NeuralNet neuralNet = builder.build();

        final double teachFactor = 0.5D;
        final double momentum = 0.5D;
        final double maximumError = 0.05D; // 5 %
        final int maximumIteration = 100_000;

        final NetTrainer trainer = new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
        trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out, 10));
        // trainer.addNetTrainerListener(new LoggerNetTrainerListener(100));
        trainer.train(neuralNet, trainingInputSource);

        Toolkit.getDefaultToolkit().beep();

        // Ausgabe testen, Index 5 erwartet.
        System.out.println();

        double[] outputs = null;

        if (trainingInputSource instanceof ImageInfoTrainingInputSource) {
            final ImageInfo testImageInfo = new ImageInfo("Seaside.jpg");

            outputs = neuralNet.getOutput(testImageInfo.getInfoVectorReScaled());
        }
        else if (trainingInputSource instanceof ImagePixelTrainingInputSource) {
            final ImageData imageData = new ImageData("Seaside.jpg");

            outputs = neuralNet.getOutput(imageData.getPixels());
        }

        System.out.println("TestImage: Expected Index 5");
        System.out.println(Arrays.stream(outputs).mapToObj(v -> String.format("%7.3f %%", v * 100)).collect(Collectors.joining(",", "[", "]")));
        System.out.println();

        neuralNet.close();

        System.exit(0);
    }

    private BildErkennungMain() {
        super();
    }
}
