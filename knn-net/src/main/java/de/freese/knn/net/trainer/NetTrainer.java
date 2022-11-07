// Created: 06.06.2008
package de.freese.knn.net.trainer;

import javax.swing.event.EventListenerList;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trainer für das neurale Netz.
 *
 * @author Thomas Freese
 */
public class NetTrainer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NetTrainer.class);

    private final EventListenerList listenerList = new EventListenerList();

    private int maxIterations = 2000;
    /**
     * Max. Netzfehler 5 %
     */
    private double maximumError = 0.05D;
    // /**
    // * Anfänglicher Anteil der vorherigen Gewichtsveränderung.
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
     * @param maximumError double, max. Netzfehler 0-1
     * @param maxIterations int, max. Anzahl an Lernzyklen
     */
    public NetTrainer(final double teachFactor, final double momentum, final double maximumError, final int maxIterations)
    {
        super();

        this.teachFactor = teachFactor;
        // this.teachFactorInitial = teachFactor;
        this.momentum = momentum;
        // this.momentumInitial = momentum;
        this.maximumError = maximumError;
        this.maxIterations = maxIterations;
    }

    public void addNetTrainerListener(final NetTrainerListener listener)
    {
        this.listenerList.add(NetTrainerListener.class, listener);
    }

    public void removeNetTrainerListener(final NetTrainerListener listener)
    {
        this.listenerList.remove(NetTrainerListener.class, listener);
    }

    /**
     * Trainiert das neurale Netz mit Daten aus dem {@link TrainingInputSource}.
     */
    public void train(final NeuralNet neuralNet, final TrainingInputSource inputSource)
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

            fireCycleEnded(new NetTrainerCycleEndedEvent(this, iteration, error, this.teachFactor, this.momentum));

            // Dynamische Anpassung der Lernrate und Momentum.
            // TODO da passt was noch nicht
            // double stepLR = (this.teachFactorInitial - this.teachFactor) / this.maxIterations;
            // double stepMom = (this.momentumInitial - this.momentum) / this.maxIterations;
            // int currCircle = this.maxIterations - iteration;
            // this.teachFactor = this.teachFactorInitial - (stepLR * currCircle);
            // this.momentum = this.momentumInitial - (stepMom * currCircle);
            if (error <= this.maximumError)
            {
                // Letzter Stand loggen.
                NetTrainerCycleEndedEvent event = new NetTrainerCycleEndedEvent(this, iteration, error, this.teachFactor, this.momentum);

                if (LOGGER.isInfoEnabled())
                {
                    LOGGER.info(event.toString());
                }

                long ms = System.currentTimeMillis() - start;

                LOGGER.info("Required Time: {} ms", ms);

                break;
            }
        }

        trainingContext.clear();
    }

    private void fireCycleEnded(final NetTrainerCycleEndedEvent event)
    {
        Object[] listeners = this.listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == NetTrainerListener.class)
            {
                ((NetTrainerListener) listeners[i + 1]).trainingCycleEnded(event);
            }
        }
    }

    /**
     * Trainiert das neurale Netz mit den Eingangs- und Ausgangsdaten und liefert den Netzfehler.<br>
     * Backpropagation Methode.
     *
     * @return double, Netzfehler
     */
    private double train(final TrainingContext trainingContext, final NeuralNet neuralNet, final double[] inputs, final double[] outputs)
    {
        ForwardVisitor forwardVisitor = new ForwardVisitor(true);
        forwardVisitor.setInputs(inputs);
        neuralNet.visit(forwardVisitor);

        // Fehler durch die Hidden- bis zum InputLayer propagieren, Gradientenabstiegsverfahren
        BackwardVisitor backwardVisitor = new BackwardVisitor(trainingContext, forwardVisitor);
        backwardVisitor.setOutputTargets(outputs);
        neuralNet.visit(backwardVisitor);

        Layer[] layer = neuralNet.getLayer();

        // Gewichte durch die Hidden- bis zum InputLayer aktualisieren.
        for (int i = layer.length - 1; i > 0; i--)
        {
            Layer rightLayer = layer[i];
            Layer leftLayer = layer[i - 1];

            neuralNet.getMath().refreshLayerWeights(leftLayer, rightLayer, this.teachFactor, this.momentum, backwardVisitor);
        }

        double netzFehler = backwardVisitor.getNetError();

        backwardVisitor.clear();

        return netzFehler;
    }
}
