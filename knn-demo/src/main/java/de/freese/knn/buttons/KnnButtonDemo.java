// Created: 15.04.2008
package de.freese.knn.buttons;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.NeuralNetBuilder;
import de.freese.knn.net.function.FunctionSigmoide;
import de.freese.knn.net.layer.HiddenLayer;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.math.stream.KnnMathStream;
import de.freese.knn.net.trainer.NetTrainer;
import de.freese.knn.net.trainer.PrintStreamNetTrainerListener;

/**
 * GUI.
 *
 * @author Thomas Freese
 */
public class KnnButtonDemo extends JFrame
{
    /**
     *
     */
    private static final long serialVersionUID = -2245301418603208848L;

    /**
     * @param args String[]
     */
    @SuppressWarnings("unused")
    public static void main(final String[] args)
    {
        // Training
        int parallelism = Runtime.getRuntime().availableProcessors();

        // @formatter:off
        NeuralNet neuralNet = new NeuralNetBuilder()
//                .knnMath(new KnnMathSimple())
                .knnMath(new KnnMathStream()) // Ist Default im NeuralNetBuilder
//                .knnMath(new KnnMathForkJoin(ForkJoinPool.commonPool()))
//                .knnMath(new KnnMathExecutor(parallelism, Executors.newFixedThreadPool(parallelism)))
//                .knnMath(new KnnMathQueueWorker(parallelism))
//                .knnMath(new KnnMathReactor(parallelism))
//                .knnMath(new KnnMathPublishSubscribe(parallelism, Executors.newFixedThreadPool(parallelism)))
//                .knnMath(new KnnMathCompletionService(parallelism, Executors.newFixedThreadPool(parallelism) ))
//                .knnMath(new KnnMathExecutorHalfWork(Executors.newSingleThreadExecutor()))
//                .knnMath(new KnnMathDisruptorPerPartition(parallelism))
//                .knnMath(new KnnMathDisruptorPerNeuron(parallelism))
                .layerInput(new InputLayer(54))
                .layerHidden(new HiddenLayer(100, new FunctionSigmoide()))
                .layerOutput(new OutputLayer(10))
                .build()
                ;
        // @formatter:on

        double teachFactor = 0.5D;
        double momentum = 0.5D;
        double maximumError = 0.05D; // 5 %
        int maximumIteration = 2000;

        NetTrainer trainer = new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
        trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out));
        // trainer.addNetTrainerListener(new LoggerNetTrainerListener());
        trainer.train(neuralNet, new KnnButtonTrainingInputSource());

        new KnnButtonDemo().showGui(neuralNet);
    }

    /**
     * @param neuralNet {@link NeuralNet}
     */
    private void showGui(final NeuralNet neuralNet)
    {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent event)
            {
                try
                {
                    neuralNet.close();
                    System.exit(0);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    System.exit(-1);
                }
            }
        });

        setResizable(true);
        setLayout(new BorderLayout());

        KnnButtonPanel buttonPanel = new KnnButtonPanel(neuralNet).initGui();

        getContentPane().add(buttonPanel, BorderLayout.CENTER);
        pack();

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        int w = getSize().width;
        int h = getSize().height;
        int x = (dimension.width - w) / 2;
        int y = (dimension.height - h) / 2;

        setLocation(x, y);

        setVisible(true);
    }
}
