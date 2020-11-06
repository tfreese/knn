// Created: 05.11.2020
package de.freese.knn.net.math.disruptor;

/**
 * @author Thomas Freese
 */
class MathEvent
{
    /**
     *
     */
    final Runnable[] runnables;

    /**
     * Erstellt ein neues {@link MathEvent} Object.
     *
     * @param parallelism int
     */
    MathEvent(final int parallelism)
    {
        super();

        this.runnables = new Runnable[parallelism];
    }

    /**
     * @return {@link Runnable}[]
     */
    Runnable[] getRunnables()
    {
        return this.runnables;
    }
}