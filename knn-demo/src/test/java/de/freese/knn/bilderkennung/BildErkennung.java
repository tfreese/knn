/**
 * 15.07.2008
 */
package de.freese.knn.bilderkennung;

import java.awt.Toolkit;
import java.util.Arrays;
import java.util.stream.Collectors;
import de.freese.knn.bilderkennung.utils.ImageData;
import de.freese.knn.bilderkennung.utils.image.info.ImageInfo;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.NeuralNetBuilder;
import de.freese.knn.net.function.FunctionSigmoide;
import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.math.stream.KnnMathStream;
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
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // TrainingInputSource trainingInputSource = new ImageInfoTrainingInputSource();
        TrainingInputSource trainingInputSource = new ImagePixelTrainingInputSource();

        int parallelism = 7;

        // @formatter:off
        NeuralNetBuilder builder = new NeuralNetBuilder()
                //.knnMath(new KnnMathSimple())
                .knnMath(new KnnMathStream())
                //.knnMath(new KnnMathForkJoin(ForkJoinPool.commonPool()))
                //.knnMath(new KnnMathExecutor(Executors.newFixedThreadPool(parallelism), parallelism))
                //.knnMath(new KnnMathQueueWorker(parallelism))
                //.knnMath(new KnnMathReactor(Schedulers.newBoundedElastic(parallelism, Integer.MAX_VALUE, "knn-scheduler-"), parallelism))
                //.knnMath(new KnnMathPublishSubscribe(Executors.newFixedThreadPool(parallelism), parallelism))
                //.knnMath(new KnnMathCompletionService(Executors.newFixedThreadPool(parallelism), parallelism))
                //.knnMath(new KnnMathExecutorHalfWork(Executors.newFixedThreadPool(1)))
                //.knnMath(new KnnMathDisruptor(parallelism))
                ;
        // @formatter:on

        if (trainingInputSource instanceof ImageInfoTrainingInputSource)
        {
            // @formatter:off
            builder.layerInput(new InputLayer(trainingInputSource.getInputAt(0).length))
                .layerHidden(new HiddenLayer(100, new FunctionSigmoide()))
                ;
            // @formatter:on
        }
        else if (trainingInputSource instanceof ImagePixelTrainingInputSource)
        {
            // @formatter:off
            builder.layerInput(new InputLayer(trainingInputSource.getInputAt(0).length))
                .layerHidden(new HiddenLayer(1000, new FunctionSigmoide()))
                .layerHidden(new HiddenLayer(100, new FunctionSigmoide()))
                ;
            // @formatter:on
        }

        builder.layerOutput(new OutputLayer(trainingInputSource.getSize()));
        // builder.layerOutput(new OutputWTALayer(trainingInputSource.getSize()));

        try (NeuralNet neuralNet = builder.build())
        {
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

            if (trainingInputSource instanceof ImageInfoTrainingInputSource)
            {
                ImageInfo testImageInfo = new ImageInfo("Seaside.jpg");

                outputs = neuralNet.getOutput(testImageInfo.getInfoVectorReScaled());
            }
            else if (trainingInputSource instanceof ImagePixelTrainingInputSource)
            {
                ImageData imageData = new ImageData("Seaside.jpg");

                outputs = neuralNet.getOutput(imageData.getPixels());
            }

            System.out.println("TestImage: Expected Index 5");
            System.out.println(Arrays.stream(outputs).mapToObj(v -> String.format("%7.3f %%", v * 100)).collect(Collectors.joining(",", "[", "]")));
            System.out.println();
        }

        System.exit(0);
    }
}
