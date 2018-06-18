/**
 * 06.06.2008
 */
package de.freese.knn.net.trainer;

import java.util.EventListener;

/**
 * Listener fuer den Trainer des neuralen Netzes.
 * 
 * @author Thomas Freese
 */
public interface INetTrainerListener extends EventListener
{
	/**
	 * Nachdem ein Lernzyklus beendet ist, wird die Nummer und der Netzfehler des Zykluses
	 * uebergeben.
	 * 
	 * @param event {@link NetTrainerCycleEndedEvent}
	 */
	public void trainingCycleEnded(NetTrainerCycleEndedEvent event);
}
