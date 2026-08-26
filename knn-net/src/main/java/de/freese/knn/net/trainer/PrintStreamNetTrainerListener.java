package de.freese.knn.net.trainer;

import java.io.PrintStream;
import java.util.Objects;

/**
 * {@link NetTrainerListener} für die Ausgabe auf einen {@link PrintStream}.
 *
 * @author Thomas Freese
 * @since 11.06.2008
 */
public class PrintStreamNetTrainerListener extends AbstractNetTrainerListener {
    private final PrintStream printStream;

    public PrintStreamNetTrainerListener(final PrintStream printStream) {
        super();

        this.printStream = printStream;
    }

    /**
     * @param logModulo Welches wievielte Event soll geloggt werden ?
     */
    public PrintStreamNetTrainerListener(final PrintStream printStream, final int logModulo) {
        super(logModulo);

        this.printStream = Objects.requireNonNull(printStream, "printStream required");
    }

    @Override
    public void trainingCycleEnded(final NetTrainerCycleEndedEvent event) {
        if ((event.getIteration() % getLogModulo()) != 0) {
            return;
        }

        printStream.println(toString(event));
    }
}
