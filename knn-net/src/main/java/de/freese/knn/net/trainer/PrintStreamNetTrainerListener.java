// Created: 11.06.2008
package de.freese.knn.net.trainer;

import java.io.PrintStream;
import java.util.Objects;

/**
 * {@link NetTrainerListener} fuer die Ausgabe auf einen {@link PrintStream}.
 *
 * @author Thomas Freese
 */
public class PrintStreamNetTrainerListener extends AbstractNetTrainerListener
{
    /**
     *
     */
    private PrintStream printStream;

    /**
     * Creates a new {@link PrintStreamNetTrainerListener} object.
     *
     * @param printStream {@link PrintStream}
     */
    public PrintStreamNetTrainerListener(final PrintStream printStream)
    {
        super();

        this.printStream = printStream;
    }

    /**
     * Creates a new {@link PrintStreamNetTrainerListener} object.
     *
     * @param printStream {@link PrintStream}
     * @param logModulo Welches wievielte Event soll geloggt werden ?
     */
    public PrintStreamNetTrainerListener(final PrintStream printStream, final int logModulo)
    {
        super(logModulo);

        this.printStream = Objects.requireNonNull(printStream, "printStream required");
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

        this.printStream.println(toString(event));
    }
}
