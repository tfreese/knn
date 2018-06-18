/**
 * 06.06.2008
 */
package de.freese.knn.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.base.core.visitor.Visitable;
import de.freese.knn.net.layer.ILayer;
import de.freese.knn.net.layer.input.InputLayer;
import de.freese.knn.net.layer.output.OutputLayer;
import de.freese.knn.net.math.IKnnMath;
import de.freese.knn.net.math.stream.StreamKnnMath;
import de.freese.knn.net.matrix.IValueInitializer;
import de.freese.knn.net.matrix.Matrix;
import de.freese.knn.net.matrix.RandomValueInitializer;
import de.freese.knn.net.persister.INetPersister;
import de.freese.knn.net.persister.NetPersisterBinary;
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
    private final IKnnMath knnMath;

    /**
     *
     */
    private boolean layerConnected = false;

    /**
     * Das Array wird in der #addLayer-Methode entsprechend vergrößert.
     */
    private ILayer[] layers = new ILayer[0];

    /**
     *
     */
    private final IValueInitializer valueInitializer;

    /**
     * Creates a new {@link NeuralNet} object.
     */
    public NeuralNet()
    {
        this(new StreamKnnMath(), new RandomValueInitializer());
    }

    /**
     * Creates a new {@link NeuralNet} object.
     *
     * @param knnMath {@link IKnnMath}
     */
    public NeuralNet(final IKnnMath knnMath)
    {
        this(knnMath, new RandomValueInitializer());
    }

    /**
     * Creates a new {@link NeuralNet} object.
     *
     * @param knnMath {@link IKnnMath}
     * @param valueInitializer {@link IValueInitializer}
     */
    public NeuralNet(final IKnnMath knnMath, final IValueInitializer valueInitializer)
    {
        super();

        this.knnMath = Objects.requireNonNull(knnMath, "knnMath required");
        this.valueInitializer = Objects.requireNonNull(valueInitializer, "valueInitializer required");
    }

    /**
     * Fügt einen Layer hinzu.<br>
     * Der erste muss ein {@link InputLayer} sein, der letzte ein {@link OutputLayer}.
     *
     * @param layer {@link ILayer}
     */
    public void addLayer(final ILayer layer)
    {
        if (this.layerConnected)
        {
            throw new IllegalStateException("Layer already connected");
        }

        // InputLayer ist immer der erste.
        if ((this.layers.length == 0) && !(layer instanceof InputLayer))
        {
            throw new IllegalArgumentException("InputLayer required");
        }

        // OutputLayer ist immer der letzte.
        if ((this.layers.length > 0) && (this.layers[this.layers.length - 1] instanceof OutputLayer))
        {
            throw new IllegalArgumentException("OutputLayer already set");
        }

        // Array vergrößern.
        ILayer[] array = Arrays.copyOf(this.layers, this.layers.length + 1);

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
    public void connectLayer()
    {
        if (this.layerConnected)
        {
            throw new IllegalStateException("Layer already connected");
        }

        for (int i = 0; i < (this.layers.length - 1); i++)
        {
            final ILayer leftLayer = this.layers[i];
            final ILayer rightLayer = this.layers[i + 1];

            final Matrix matrix = new Matrix(leftLayer.getNeurons().size(), rightLayer.getNeurons().size());

            leftLayer.setOutputMatrix(matrix);
            rightLayer.setInputMatrix(matrix);
        }

        // Gewichte initialisieren
        getMath().initialize(getValueInitializer(), this.layers);

        finishConnectLayer();
    }

    /**
     *
     */
    private void finishConnectLayer()
    {
        this.layerConnected = true;
    }

    /**
     * Liefert die Layer.
     *
     * @return {@link List}
     */
    public ILayer[] getLayer()
    {
        return this.layers;
    }

    /**
     * Liefert die Mathematik-Implementierung.
     *
     * @return {@link IKnnMath}
     */
    public IKnnMath getMath()
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
        return (OutputLayer) this.layers[this.layers.length - 1];
    }

    /**
     * @return {@link IValueInitializer}
     */
    private IValueInitializer getValueInitializer()
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
     * @param netPersister {@link INetPersister}
     * @throws Exception Falls was schief geht.
     */
    public void load(final DataInputStream dis, final INetPersister netPersister) throws Exception
    {
        netPersister.load(dis, this);

        finishConnectLayer();
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
     * @param netPersister {@link INetPersister}
     * @throws Exception Falls was schief geht.
     */
    public void save(final DataOutputStream dos, final INetPersister netPersister) throws Exception
    {
        netPersister.save(dos, this);
    }
}
