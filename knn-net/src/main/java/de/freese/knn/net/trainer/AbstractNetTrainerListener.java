// Created: 11.06.2008
package de.freese.knn.net.trainer;

/**
 * Basisklasse des {@link NetTrainerListener}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractNetTrainerListener implements NetTrainerListener {
    /**
     * Welches wievielte Event soll geloggt werden ?
     */
    private final int logModulo;

    protected AbstractNetTrainerListener() {
        this(1);
    }

    /**
     * @param logModulo int Welches wievielte Event soll geloggt werden ?
     */
    protected AbstractNetTrainerListener(final int logModulo) {
        super();

        this.logModulo = logModulo;
    }

    /**
     * Welches wievielte Event soll geloggt werden ?
     */
    protected int getLogModulo() {
        return this.logModulo;
    }

    protected String toString(final NetTrainerCycleEndedEvent event) {
        return event.toString();
    }
}
