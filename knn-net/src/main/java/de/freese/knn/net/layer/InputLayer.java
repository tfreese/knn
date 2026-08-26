package de.freese.knn.net.layer;

import de.freese.knn.net.function.FunctionLinear;
import de.freese.knn.net.neuron.NeuronInput;

/**
 * EingangsLayer.
 *
 * @author Thomas Freese
 * @since 06.06.2008
 */
public class InputLayer extends AbstractLayer {
    public InputLayer(final int size) {
        super(size, new FunctionLinear(), NeuronInput::new);
    }
}
