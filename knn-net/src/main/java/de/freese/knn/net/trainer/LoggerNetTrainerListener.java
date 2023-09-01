// Created: 17.07.2011
package de.freese.knn.net.trainer;

import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link NetTrainerListener} für die Ausgabe auf einen {@link PrintStream}.
 *
 * @author Thomas Freese
 */
public class LoggerNetTrainerListener extends AbstractNetTrainerListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerNetTrainerListener.class);

    public LoggerNetTrainerListener() {
        super();
    }

    public LoggerNetTrainerListener(final int logModulo) {
        super(logModulo);
    }

    @Override
    public void trainingCycleEnded(final NetTrainerCycleEndedEvent event) {
        if ((event.getIteration() % getLogModulo()) != 0) {
            return;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(toString(event));
        }
    }
}
