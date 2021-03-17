/**
 * 15.04.2008
 */
package de.freese.knn.buttons;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
     * @author Thomas Freese
     */
    private class ToggleButtonListener implements ActionListener
    {
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(final ActionEvent event)
        {
            double[] outputVector = KnnButtonDemo.this.neuralNetwork.getOutput(KnnButtonDemo.this.matrixPanel.getInputVector());

            double output = Double.MIN_VALUE;
            double value = Double.NaN;

            for (int i = 0; i < outputVector.length; i++)
            {
                double rndValue = roundDouble(outputVector[i] * 100, 3);

                KnnButtonDemo.this.labelsOutput[i].setText(i + ": " + rndValue + " %");

                if (rndValue > 80)
                {
                    KnnButtonDemo.this.labelsOutput[i].setForeground(Color.RED);
                }
                else if (rndValue > 50)
                {
                    KnnButtonDemo.this.labelsOutput[i].setForeground(Color.BLUE);
                }
                else
                {
                    KnnButtonDemo.this.labelsOutput[i].setForeground(Color.BLACK);
                }

                if (outputVector[i] > output)
                {
                    output = outputVector[i];
                    value = i;
                }
            }

            KnnButtonDemo.this.labelRecognized.setText("Erkannt als: " + value);
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -2245301418603208848L;

    /**
     * @param args String[]
     */
    public static void main(final String[] args)
    {
        new KnnButtonDemo().showGui();
    }

    /**
     *
     */
    private JLabel labelRecognized;

    /**
     *
     */
    private JLabel[] labelsOutput = new JLabel[10];

    /**
     *
     */
    private KnnButtonPanel matrixPanel;

    /**
     *
     */
    private NeuralNet neuralNetwork;

    /**
     * Rundet ein Double Werte mit Angabe der Anzahl an Nachkommastellen.
     *
     * @param wert double
     * @param nachkommaStellen the number of digits after the decimal point
     * @return double
     */
    private double roundDouble(final double wert, final int nachkommaStellen)
    {
        // double mask = Math.pow(10.0, nachkommaStellen);
        //
        // return (Math.round(wert * mask)) / mask;
        if (Double.isNaN(wert) || Double.isInfinite(wert) || (wert == 0.0D))
        {
            return 0.0D;
        }

        BigDecimal bigDecimal = BigDecimal.valueOf(wert);
        bigDecimal = bigDecimal.setScale(nachkommaStellen, RoundingMode.HALF_UP);

        return bigDecimal.doubleValue();
    }

    /**
     *
     */
    private void showGui()
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
                    KnnButtonDemo.this.neuralNetwork.close();
                    System.exit(0);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        setResizable(true);
        setLayout(new BorderLayout());

        this.matrixPanel = new KnnButtonPanel().initGui(new ToggleButtonListener());
        this.labelRecognized = new JLabel("Erkannt als: ");

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new GridLayout(12, 1));
        outputPanel.add(new JLabel("Output-Neuronen"));

        for (int i = 0; i < this.labelsOutput.length; i++)
        {
            this.labelsOutput[i] = new JLabel(String.valueOf(i));
            outputPanel.add(this.labelsOutput[i]);
        }

        // Training
        int parallelism = Runtime.getRuntime().availableProcessors();

        // @formatter:off
        this.neuralNetwork = new NeuralNetBuilder()
//                .knnMath(new KnnMathSimple())
                .knnMath(new KnnMathStream()) // Ist Default im NeuralNetBuilder
//                .knnMath(new KnnMathForkJoin(ForkJoinPool.commonPool()))
//                .knnMath(new KnnMathExecutor(parallelism, Executors.newFixedThreadPool(parallelism)))
//                .knnMath(new KnnMathQueueWorker(parallelism))
//                .knnMath(new KnnMathReactor(parallelism))
//                .knnMath(new KnnMathPublishSubscribe(parallelism, Executors.newFixedThreadPool(parallelism)))
//                .knnMath(new KnnMathCompletionService(parallelism, Executors.newFixedThreadPool(parallelism) ))
//                .knnMath(new KnnMathExecutorHalfWork(Executors.newSingleThreadExecutor()))
//                .knnMath(new KnnMathDisruptor(parallelism))
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
        trainer.train(this.neuralNetwork, new KnnButtonTrainingInputSource());

        getContentPane().add(this.matrixPanel, BorderLayout.CENTER);
        getContentPane().add(this.labelRecognized, BorderLayout.SOUTH);
        getContentPane().add(outputPanel, BorderLayout.EAST);
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
