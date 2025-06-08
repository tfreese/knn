// Created: 06.06.2008
package de.freese.knn.net.trainer;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.visitor.BackwardVisitor;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Trainer für das neurale Netz.
 *
 * @author Thomas Freese
 */
public class NetTrainer {
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
    // private double momentumInitial = .momentum;
    /**
     * Anteil der vorherigen Gewichtsveränderung
     */
    private double momentum = 0.5D;
    // /**
    // * Lernfaktor
    // */
    // private double teachFactorInitial = .teachFactor;
    /**
     * Lernfaktor
     */
    private double teachFactor = 0.5D;

    /**
     * @param maximumError double, max. Netzfehler 0-1
     * @param maxIterations int, max. Anzahl an Lernzyklen
     */
    public NetTrainer(final double teachFactor, final double momentum, final double maximumError, final int maxIterations) {
        super();

        this.teachFactor = teachFactor;
        // this.teachFactorInitial = teachFactor;
        this.momentum = momentum;
        // this.momentumInitial = momentum;
        this.maximumError = maximumError;
        this.maxIterations = maxIterations;
    }

    public void addNetTrainerListener(final NetTrainerListener listener) {
        listenerList.add(NetTrainerListener.class, listener);
    }

    public void removeNetTrainerListener(final NetTrainerListener listener) {
        listenerList.remove(NetTrainerListener.class, listener);
    }

    /**
     * Trainiert das neurale Netz mit Daten aus dem {@link TrainingInputSource}.
     */
    public void train(final NeuralNet neuralNet, final TrainingInputSource inputSource) {
        final long start = System.currentTimeMillis();
        final TrainingContext trainingContext = new TrainingContext();

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            double error = 0.0D;

            for (int index = 0; index < inputSource.getSize(); index++) {
                final double[] input = inputSource.getInputAt(index);
                final double[] output = inputSource.getOutputAt(index);

                error += train(trainingContext, neuralNet, input, output);
            }

            fireCycleEnded(new NetTrainerCycleEndedEvent(this, iteration, error, teachFactor, momentum));

            // Dynamische Anpassung der Lernrate und Momentum.
            // Da passt was noch nicht !
            //
            // final double stepLR = (teachFactorInitial - teachFactor) / maxIterations;
            // final double stepMom = (momentumInitial - momentum) / maxIterations;
            // final int currCircle = maxIterations - iteration;
            // teachFactor = teachFactorInitial - (stepLR * currCircle);
            // momentum = momentumInitial - (stepMom * currCircle);
            if (error <= maximumError) {
                // Letzter Stand loggen.
                final NetTrainerCycleEndedEvent event = new NetTrainerCycleEndedEvent(this, iteration, error, teachFactor, momentum);

                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info(event.toString());
                }

                final long ms = System.currentTimeMillis() - start;

                LOGGER.info("Required Time: {} ms", ms);

                break;
            }
        }

        trainingContext.clear();
    }

    private void fireCycleEnded(final NetTrainerCycleEndedEvent event) {
        final Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == NetTrainerListener.class) {
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
    private double train(final TrainingContext trainingContext, final NeuralNet neuralNet, final double[] inputs, final double[] outputs) {
        final ForwardVisitor forwardVisitor = new ForwardVisitor(true);
        forwardVisitor.setInputs(inputs);
        neuralNet.visit(forwardVisitor);

        // Fehler durch die Hidden- bis zum InputLayer propagieren, Gradientenabstiegsverfahren
        final BackwardVisitor backwardVisitor = new BackwardVisitor(trainingContext, forwardVisitor);
        backwardVisitor.setOutputTargets(outputs);
        neuralNet.visit(backwardVisitor);

        final Layer[] layer = neuralNet.getLayer();

        // Gewichte durch die Hidden- bis zum InputLayer aktualisieren.
        for (int i = layer.length - 1; i > 0; i--) {
            final Layer rightLayer = layer[i];
            final Layer leftLayer = layer[i - 1];

            neuralNet.getMath().refreshLayerWeights(leftLayer, rightLayer, teachFactor, momentum, backwardVisitor);
        }

        final double netzFehler = backwardVisitor.getNetError();

        backwardVisitor.clear();

        return netzFehler;
    }
}
