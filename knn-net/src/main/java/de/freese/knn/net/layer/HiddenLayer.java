/**
 * 06.06.2008
 */
package de.freese.knn.net.layer;

import de.freese.knn.net.function.Function;

/**
 * EingangsLayer.
 *
 * @author Thomas Freese
 */
public class HiddenLayer extends AbstractLayer
{
    /**
     * Creates a new {@link HiddenLayer} object.
     *
     * @param size int
     * @param function {@link Function}
     */
    public HiddenLayer(final int size, final Function function)
    {
        super(size, function);
    }
}
