// Created: 15.04.2008
package de.freese.knn.buttons;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.NeuralNetBuilder;
import de.freese.knn.net.function.FunctionSigmoid;
import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.math.KnnMathStream;
import de.freese.knn.net.trainer.LoggerNetTrainerListener;
import de.freese.knn.net.trainer.NetTrainer;

/**
 * GUI.
 *
 * @author Thomas Freese
 */
public class KnnButtonMain extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(KnnButtonMain.class);
    @Serial
    private static final long serialVersionUID = -2245301418603208848L;

    public static void main(final String[] args) {
        // Training
        // int parallelism = Runtime.getRuntime().availableProcessors();

        final NeuralNet neuralNet = new NeuralNetBuilder()
                // .knnMath(new KnnMathSimple())
                .knnMath(new KnnMathStream()) // Ist Default im NeuralNetBuilder
                // .knnMath(new KnnMathForkJoin(ForkJoinPool.commonPool()))
                // .knnMath(new KnnMathExecutor(parallelism, Executors.newFixedThreadPool(parallelism)))
                // .knnMath(new KnnMathQueueWorker(parallelism))
                // .knnMath(new KnnMathReactor(parallelism))
                // .knnMath(new KnnMathPublishSubscribe(parallelism, Executors.newFixedThreadPool(parallelism)))
                // .knnMath(new KnnMathCompletionService(parallelism, Executors.newFixedThreadPool(parallelism) ))
                // .knnMath(new KnnMathExecutorHalfWork(Executors.newSingleThreadExecutor()))
                // .knnMath(new KnnMathDisruptorPerPartition(parallelism))
                // .knnMath(new KnnMathDisruptorPerNeuron(parallelism))
                // .knnMath(new KnnMathVirtualThread())
                .layerInput(new InputLayer(54))
                .layerHidden(new HiddenLayer(100, new FunctionSigmoid()))
                .layerOutput(new OutputLayer(10))
                .build();

        final double teachFactor = 0.5D;
        final double momentum = 0.5D;
        final double maximumError = 0.05D; // 5 %
        final int maximumIteration = 2000;

        final NetTrainer trainer = new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
        // trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out));
        trainer.addNetTrainerListener(new LoggerNetTrainerListener());
        trainer.train(neuralNet, new KnnButtonTrainingInputSource());

        new KnnButtonMain().showGui(neuralNet);
    }

    private void showGui(final NeuralNet neuralNet) {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                try {
                    neuralNet.close();
                    System.exit(0);
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    System.exit(-1);
                }
            }
        });

        setResizable(true);
        setLayout(new BorderLayout());

        final KnnButtonPanel buttonPanel = new KnnButtonPanel(neuralNet).initGui();

        getContentPane().add(buttonPanel, BorderLayout.CENTER);
        pack();

        final Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        final int w = getSize().width;
        final int h = getSize().height;
        final int x = (dimension.width - w) / 2;
        final int y = (dimension.height - h) / 2;

        setLocation(x, y);

        setVisible(true);
    }
}
