/**
 * 06.06.2008
 */
package de.freese.knn.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.knn.net.layer.InputLayer;
import de.freese.knn.net.layer.Layer;
import de.freese.knn.net.layer.OutputLayer;
import de.freese.knn.net.math.KnnMath;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.matrix.ValueInitializer;
import de.freese.knn.net.persister.NetPersister;
import de.freese.knn.net.persister.NetPersisterBinary;
import de.freese.knn.net.util.visitor.Visitable;
import de.freese.knn.net.visitor.ForwardVisitor;

/**
 * Basisklasses des neuralen Netzes.
 *
 * @author Thomas Freese
 */
public class NeuralNet implements Visitable, AutoCloseable
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NeuralNet.class);

    /**
     *
     */
    private KnnMath knnMath = null;

    /**
     * Das Array wird in der #addLayer-Methode entsprechend vergrößert.
     */
    private Layer[] layers = new Layer[0];

    /**
     *
     */
    private ValueInitializer valueInitializer = null;

    /**
     * Creates a new {@link NeuralNet} object.
     */
    public NeuralNet()
    {
        super();
    }

    /**
     * Fügt einen Layer hinzu.<br>
     * Der erste muss ein {@link InputLayer} sein, der letzte ein {@link OutputLayer}.
     *
     * @param layer {@link Layer}
     */
    public void addLayer(final Layer layer)
    {
        // Array vergrößern.
        Layer[] array = Arrays.copyOf(this.layers, this.layers.length + 1);

        array[this.layers.length] = layer;
        this.layers = array;
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception
    {
        LOGGER.info("");

        if (getMath() instanceof AutoCloseable)
        {
            ((AutoCloseable) getMath()).close();
        }

        this.layers = null;
    }

    /**
     * Verbindet die Layer mit den Matrixobjekten.
     */
    void connectLayer()
    {
        for (int i = 0; i < (getLayer().length - 1); i++)
        {
            final Layer leftLayer = getLayer()[i];
            final Layer rightLayer = getLayer()[i + 1];

            final Matrix matrix = new Matrix(leftLayer.getNeurons().size(), rightLayer.getNeurons().size());

            leftLayer.setOutputMatrix(matrix);
            rightLayer.setInputMatrix(matrix);
        }

        // Gewichte initialisieren
        getMath().initialize(getValueInitializer(), getLayer());
    }

    /**
     * Liefert die Layer.
     *
     * @return {@link List}
     */
    public Layer[] getLayer()
    {
        return this.layers;
    }

    /**
     * Liefert die Mathematik-Implementierung.
     *
     * @return {@link KnnMath}
     */
    public KnnMath getMath()
    {
        return this.knnMath;
    }

    /**
     * Berechnet und liefert die Ausgabewerte anhand der Eingabewerte.
     *
     * @param inputs double[]
     * @return double[]
     */
    public double[] getOutput(final double[] inputs)
    {
        ForwardVisitor visitor = new ForwardVisitor(false);
        visitor.setInputs(inputs);

        visit(visitor);

        final double[] outputs = getOutputLayer().adjustOutput(visitor);

        visitor.clear();
        visitor = null;

        return outputs;
    }

    /**
     * Liefert den {@link OutputLayer}.
     *
     * @return {@link OutputLayer}
     */
    private OutputLayer getOutputLayer()
    {
        return (OutputLayer) getLayer()[getLayer().length - 1];
    }

    /**
     * @return {@link ValueInitializer}
     */
    private ValueInitializer getValueInitializer()
    {
        return this.valueInitializer;
    }

    /**
     * Laden des Netzes aus dem Stream. Der Stream wird NICHT geschlossen !
     *
     * @param dis {@link DataInputStream}
     * @throws Exception Falls was schief geht.
     */
    public void load(final DataInputStream dis) throws Exception
    {
        load(dis, new NetPersisterBinary());
    }

    /**
     * Laden des Netzes aus dem Stream. Der Stream wird NICHT geschlossen !
     *
     * @param dis {@link DataInputStream}
     * @param netPersister {@link NetPersister}
     * @throws Exception Falls was schief geht.
     */
    public void load(final DataInputStream dis, final NetPersister netPersister) throws Exception
    {
        netPersister.load(dis, this);
    }

    /**
     * Speichern des Netzes in den Stream. Der Stream wird NICHT geschlossen !
     *
     * @param dos {@link DataOutputStream}
     * @throws Exception Falls was schief geht.
     */
    public void save(final DataOutputStream dos) throws Exception
    {
        save(dos, new NetPersisterBinary());
    }

    /**
     * Speichern des Netzes in den Stream. Der Stream wird NICHT geschlossen !
     *
     * @param dos {@link DataOutputStream}
     * @param netPersister {@link NetPersister}
     * @throws Exception Falls was schief geht.
     */
    public void save(final DataOutputStream dos, final NetPersister netPersister) throws Exception
    {
        netPersister.save(dos, this);
    }

    /**
     * @param knnMath {@link KnnMath}
     */
    void setKnnMath(final KnnMath knnMath)
    {
        this.knnMath = knnMath;
    }

    /**
     * @param valueInitializer {@link ValueInitializer}
     */
    void setValueInitializer(final ValueInitializer valueInitializer)
    {
        this.valueInitializer = valueInitializer;
    }
}
