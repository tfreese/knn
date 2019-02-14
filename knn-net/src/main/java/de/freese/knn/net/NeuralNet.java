/**
 * 06.06.2008
 */
package de.freese.knn.net;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.math.KnnMath;
import de.freese.knn.net.util.visitor.Visitable;

/**
 * Interface des neuralen Netzes.
 *
 * @author Thomas Freese
 */
public interface NeuralNet extends Visitable, AutoCloseable
{
    /**
     *
     */
    static final Logger LOGGER = LoggerFactory.getLogger(NeuralNet.class);

    /**
     * Liefert die Layer.
     *
     * @return {@link List}
     */
    public Layer[] getLayer();

    /**
     * @return {@link Logger}
     */
    public default Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * Liefert die Mathematik-Implementierung.
     *
     * @return {@link KnnMath}
     */
    public KnnMath getMath();

    /**
     * Berechnet und liefert die Ausgabewerte anhand der Eingabewerte.
     *
     * @param inputs double[]
     * @return double[]
     */
    public double[] getOutput(final double[] inputs);
}
