package de.freese.knn.net.layer;

import de.freese.knn.net.function.Function;

/**
 * EingangsLayer.
 *
 * @author Thomas Freese
 * @since 06.06.2008
 */
public class HiddenLayer extends AbstractLayer {
    public HiddenLayer(final int size, final Function function) {
        super(size, function);
    }
}
