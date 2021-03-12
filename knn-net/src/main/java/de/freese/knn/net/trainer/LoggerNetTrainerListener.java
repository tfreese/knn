/**
 * Created: 17.07.2011
 */

package de.freese.knn.net.trainer;

import java.io.PrintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link NetTrainerListener} fuer die Ausgabe auf einen {@link PrintStream}.
 *
 * @author Thomas Freese
 */
public class LoggerNetTrainerListener extends AbstractNetTrainerListener
{
    /**
     * 
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerNetTrainerListener.class);

    /**
     * Erstellt ein neues {@link LoggerNetTrainerListener} Object.
     */
    public LoggerNetTrainerListener()
    {
        super();
    }

    /**
     * Creates a new {@link LoggerNetTrainerListener} object.
     * 
     * @param logModulo int Welches wievielte Event soll geloggt werden ?
     */
    public LoggerNetTrainerListener(final int logModulo)
    {
        super(logModulo);
    }

    /**
     * @see de.freese.knn.net.trainer.NetTrainerListener#trainingCycleEnded(de.freese.knn.net.trainer.NetTrainerCycleEndedEvent)
     */
    @Override
    public void trainingCycleEnded(final NetTrainerCycleEndedEvent event)
    {
        if ((event.getIteration() % getLogModulo()) != 0)
        {
            return;
        }

        LOGGER.info(toString(event));
    }
}
