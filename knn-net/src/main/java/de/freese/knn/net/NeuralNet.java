// Created: 06.06.2008
package de.freese.knn.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.KnnMath;
import de.freese.knn.net.visitor.Visitable;

/**
 * Interface des neuralen Netzes.
 *
 * @author Thomas Freese
 */
public interface NeuralNet extends Visitable {
    Logger LOGGER = LoggerFactory.getLogger(NeuralNet.class);

    void close();

    Layer[] getLayer();

    default Logger getLogger() {
        return LOGGER;
    }

    KnnMath getMath();

    /**
     * Berechnet und liefert die Ausgabewerte anhand der Eingabewerte.
     */
    double[] getOutput(double[] inputs);
}
