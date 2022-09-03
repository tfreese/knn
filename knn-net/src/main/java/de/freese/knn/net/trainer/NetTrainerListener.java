// Created: 06.06.2008
package de.freese.knn.net.trainer;

import java.util.EventListener;

/**
 * Listener für den Trainer des neuralen Netzes.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface NetTrainerListener extends EventListener
{
    /**
     * Nachdem ein Lernzyklus beendet ist, wird die Nummer und der Netzfehler des Zyklus übergeben.
     *
     * @param event {@link NetTrainerCycleEndedEvent}
     */
    void trainingCycleEnded(NetTrainerCycleEndedEvent event);
}
