/**
 * 06.06.2008
 */
package de.freese.knn.net.trainer;

import java.util.List;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Trainer für das neurale Netz.
 * 
 * @author Thomas Freese
 */
public class NetTrainer
{
	/**
	 *
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NetTrainer.class);

	/**
	 *
	 */
	private transient EventListenerList listenerList = new EventListenerList();

	/**
	 * Max. Netzfehler 5 %
	 */
	private double maximumError = 0.05D;

	/**
	 *
	 */
	private int maxIterations = 2000;

	// /**
	// * Anfänglicher Anteil der vorherigen Gewichtsveränderung
	// */
	// private double momentumInitial = this.momentum;

	/**
	 * Anteil der vorherigen Gewichtsveränderung
	 */
	private double momentum = 0.5D;

	// /**
	// * Lernfaktor
	// */
	// private double teachFactorInitial = this.teachFactor;

	/**
	 * Lernfaktor
	 */
	private double teachFactor = 0.5D;

	/**
	 * Creates a new {@link NetTrainer} object.
	 * 
	 * @param teachFactor double
	 * @param momentum double
	 * @param maximumError double, max. Netzfehler 0-1
	 * @param maxIterations int, max. Anzahl an Lernzyklen
	 */
	public NetTrainer(final double teachFactor, final double momentum, final double maximumError,
			final int maxIterations)
	{
		super();

		this.teachFactor = teachFactor;
		// this.teachFactorInitial = teachFactor;
		this.momentum = momentum;
		// this.momentumInitial = momentum;
		this.maximumError = maximumError;
		this.maxIterations = maxIterations;
	}

	/**
	 * Hinzufügen eines {@link INetTrainerListener}.
	 * 
	 * @param listener {@link INetTrainerListener}
	 */
	public void addNetTrainerListener(final INetTrainerListener listener)
	{
		this.listenerList.add(INetTrainerListener.class, listener);
	}

	/**
	 * Feuert ein Event, wenn ein Lernzyklus beendet ist.
	 * 
	 * @param event {@link NetTrainerCycleEndedEvent}
	 */
	private void fireCycleEnded(final NetTrainerCycleEndedEvent event)
	{
		Object[] listeners = this.listenerList.getListenerList();

		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == INetTrainerListener.class)
			{
				((INetTrainerListener) listeners[i + 1]).trainingCycleEnded(event);
			}
		}
	}

	/**
	 * Entfernen eines {@link INetTrainerListener}.
	 * 
	 * @param listener {@link INetTrainerListener}
	 */
	public void removeNetTrainerListener(final INetTrainerListener listener)
	{
		this.listenerList.remove(INetTrainerListener.class, listener);
	}

	/**
	 * Trainiert das neurale Netz mit Daten aus dem {@link ITrainingInputSource}.
	 * 
	 * @param neuralNet {@link NeuralNet}
	 * @param inputSource {@link ITrainingInputSource}
	 */
	public void train(final NeuralNet neuralNet, final ITrainingInputSource inputSource)
	{
		long start = System.currentTimeMillis();
		TrainingContext trainingContext = new TrainingContext();

		for (int iteration = 0; iteration < this.maxIterations; iteration++)
		{
			double error = 0.0D;

			for (int index = 0; index < inputSource.getSize(); index++)
			{
				double[] input = inputSource.getInputAt(index);
				double[] output = inputSource.getOutputAt(index);

				error += train(trainingContext, neuralNet, input, output);
			}

			fireCycleEnded(new NetTrainerCycleEndedEvent(this, iteration, error, this.teachFactor,
					this.momentum));

			// Dynamische Anpassung der Lernrate und Momentum
			// TODO da passt was noch nich
			// double stepLR = (this.teachFactorInitial - this.teachFactor) / this.maxIterations;
			// double stepMom = (this.momentumInitial - this.momentum) / this.maxIterations;
			// int currCicle = this.maxIterations - iteration;
			// this.teachFactor = this.teachFactorInitial - (stepLR * currCicle);
			// this.momentum = this.momentumInitial - (stepMom * currCicle);
			if (error <= this.maximumError)
			{
				// Letzter Stand loggen
				NetTrainerCycleEndedEvent event =
						new NetTrainerCycleEndedEvent(this, iteration, error, this.teachFactor,
								this.momentum);
				LOGGER.info(event.toString());

				long ms = System.currentTimeMillis() - start;

				LOGGER.info("Required Time: {} ms", Long.valueOf(ms));

				break;
			}
		}

		trainingContext.clear();
		trainingContext = null;
	}

	/**
	 * Trainiert das neurale Netz mit den Eingangs- und Ausgangsdaten und liefert den Netzfehler.<br>
	 * Backpropagation Methode.
	 * 
	 * @param trainingContext {@link TrainingContext}
	 * @param neuralNet {@link NeuralNet}
	 * @param inputs double[]
	 * @param outputs double[]
	 * @return double, Netzfehler
	 */
	private double train(final TrainingContext trainingContext, final NeuralNet neuralNet,
							final double[] inputs, final double[] outputs)
	{
		ForwardVisitor forwardVisitor = new ForwardVisitor(true);
		forwardVisitor.setInputs(inputs);
		neuralNet.visit(forwardVisitor);

		// Fehler durch die Hidden- bis zum Inputlayer propagieren, Gradientenabstiegsverfahren
		BackwardVisitor backwardVisitor = new BackwardVisitor(trainingContext, forwardVisitor);
		backwardVisitor.setOutputTargets(outputs);
		neuralNet.visit(backwardVisitor);

		List<ILayer> layer = neuralNet.getLayer();

		// Gewichte durch die Hidden- bis zum Inputlayer aktualisieren
		for (int i = layer.size() - 1; i > 0; i--)
		{
			ILayer rightLayer = layer.get(i);
			ILayer leftLayer = layer.get(i - 1);

			neuralNet.getMath().refreshLayerWeights(leftLayer, rightLayer, this.teachFactor,
					this.momentum, backwardVisitor);
		}

		double netzFehler = backwardVisitor.getNetError();

		backwardVisitor.clear();
		backwardVisitor = null;

		return netzFehler;
	}
}
