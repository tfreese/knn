/**
 * 11.06.2008
 */
package de.freese.knn.net.trainer;

/**
 * Basisklasse des {@link NetTrainerListener}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractNetTrainerListener implements NetTrainerListener
{
    /**
     * Welches wievielte Event soll geloggt werden ?
     */
    private final int logModulo;

    /**
     * Creates a new {@link AbstractNetTrainerListener} object.
     */
    protected AbstractNetTrainerListener()
    {
        this(1);
    }

    /**
     * Creates a new {@link AbstractNetTrainerListener} object.
     *
     * @param logModulo int Welches wievielte Event soll geloggt werden ?
     */
    protected AbstractNetTrainerListener(final int logModulo)
    {
        super();

        this.logModulo = logModulo;
    }

    /**
     * Welches wievielte Event soll geloggt werden ?
     *
     * @return int
     */
    protected int getLogModulo()
    {
        return this.logModulo;
    }

    /**
     * @param event {@link NetTrainerCycleEndedEvent}
     * @return String
     */
    protected String toString(final NetTrainerCycleEndedEvent event)
    {
        return event.toString();
    }
}
