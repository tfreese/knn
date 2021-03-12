// Created: 05.11.2020
package de.freese.knn.net.math.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * @author Thomas Freese
 */
class MathEventHandler implements EventHandler<MathEvent>
{
    /**
     *
     */
    private final int ordinal;

    /**
     * Erstellt ein neues {@link MathEventHandler} Object.
     *
     * @param ordinal int
     */
    MathEventHandler(final int ordinal)
    {
        super();

        this.ordinal = ordinal;
    }

    /**
     * @see com.lmax.disruptor.EventHandler#onEvent(java.lang.Object, long, boolean)
     */
    @Override
    public void onEvent(final MathEvent event, final long sequence, final boolean endOfBatch) throws Exception
    {
        event.runnables[this.ordinal].run();

        event.runnables[this.ordinal] = null;
    }
}
