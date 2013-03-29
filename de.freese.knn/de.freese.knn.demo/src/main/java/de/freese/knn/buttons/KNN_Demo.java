/**
 * 15.04.2008
 */
package de.freese.knn.buttons;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.hidden.SigmoidLayer;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;
import de.freese.knn.net.math.forkjoin.ForkJoinKnnMath;
import de.freese.knn.net.trainer.NetTrainer;
import de.freese.knn.net.trainer.PrintStreamNetTrainerListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
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

/**
 * GUI.
 * 
 * @author Thomas Freese
 */
public class KNN_Demo extends JFrame
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
		public void actionPerformed(final ActionEvent e)
		{
			double[] outputVector = KNN_Demo.this.neuralNetwork.getOutput(KNN_Demo.this.matrixPanel.getInputVector());

			double output = Double.MIN_VALUE;
			double value = Double.NaN;

			for (int i = 0; i < outputVector.length; i++)
			{
				double rndValue = roundDouble(outputVector[i] * 100, 3);

				KNN_Demo.this.labelsOutput[i].setText(String.valueOf(i) + ": " + rndValue + " %");

				if (rndValue > 80)
				{
					KNN_Demo.this.labelsOutput[i].setForeground(Color.RED);
				}
				else if (rndValue > 50)
				{
					KNN_Demo.this.labelsOutput[i].setForeground(Color.BLUE);
				}
				else
				{
					KNN_Demo.this.labelsOutput[i].setForeground(Color.BLACK);
				}

				if (outputVector[i] > output)
				{
					output = outputVector[i];
					value = i;
				}
			}

			KNN_Demo.this.labelRecognized.setText("Erkannt als: " + value);
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
		new KNN_Demo();
	}

	/**
	 * 
	 */
	private JLabel labelRecognized = null;

	/**
	 * 
	 */
	private JLabel[] labelsOutput = new JLabel[10];

	/**
	 * 
	 */
	private MatrixPanel matrixPanel = null;

	/**
	 *
	 */
	private NeuralNet neuralNetwork = null;

	/**
	 * Creates a new {@link KNN_Demo} object.
	 * 
	 * @throws HeadlessException Falls was schief geht.
	 */
	public KNN_Demo() throws HeadlessException
	{
		super();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			/**
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(final WindowEvent e)
			{
				try
				{
					KNN_Demo.this.neuralNetwork.release();
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

		// this.neuralNetwork = new NeuralNet();
		// this.neuralNetwork = new NeuralNet(new ExecutorKnnMath());
		this.neuralNetwork = new NeuralNet(new ForkJoinKnnMath());
		// this.neuralNetwork = new NeuralNet(new ForkJoinKnnMath(), new
		// ConstantValueInitializer(0.5D));
		this.neuralNetwork.addLayer(new InputLayer(54));
		this.neuralNetwork.addLayer(new SigmoidLayer(25));
		this.neuralNetwork.addLayer(new OutputLayer(10));
		this.neuralNetwork.connectLayer();

		this.matrixPanel = new MatrixPanel(new ToggleButtonListener());
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
		double teachFactor = 0.5D;
		double momentum = 0.5D;
		double maximumError = 0.05D;
		int maximumIteration = 10000;

		NetTrainer trainer = new NetTrainer(teachFactor, momentum, maximumError, maximumIteration);
		trainer.addNetTrainerListener(new PrintStreamNetTrainerListener(System.out));
		// trainer.addNetTrainerListener(new LoggerNetTrainerListener());
		trainer.train(this.neuralNetwork, new TestTrainingInputSource());

		getContentPane().add(this.matrixPanel, BorderLayout.CENTER);
		getContentPane().add(this.labelRecognized, BorderLayout.SOUTH);
		getContentPane().add(outputPanel, BorderLayout.EAST);
		pack();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;

		setLocation(x, y);

		setVisible(true);
	}

	/**
	 * Rundet ein Double Werte mit Angabe der Anzahl an Nachkommastellen.
	 * 
	 * @param wert double
	 * @param nachkommaStellen the number of digits after the decimal point
	 * @return double
	 */
	public double roundDouble(final double wert, final int nachkommaStellen)
	{
		// double mask = Math.pow(10.0, nachkommaStellen);
		//
		// return (Math.round(wert * mask)) / mask;
		if (Double.isNaN(wert) || Double.isInfinite(wert) || (wert == 0.0D))
		{
			return 0.0D;
		}

		BigDecimal bigDecimal = new BigDecimal(wert);
		bigDecimal = bigDecimal.setScale(nachkommaStellen, RoundingMode.HALF_UP);

		return bigDecimal.doubleValue();
	}
}
