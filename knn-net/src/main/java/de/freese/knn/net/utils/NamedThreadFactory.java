package de.freese.knn.net.utils;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.jspecify.annotations.NonNull;

/**
 * @author Thomas Freese
 * @since 10.09.2020
 */
public class NamedThreadFactory implements ThreadFactory {
    private final boolean daemon;
    private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
    private final String namePattern;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * <pre>
     * Defaults:
     * - daemon = true
     * </pre>
     *
     * @param namePattern String; Example: "thread-%d"
     */
    public NamedThreadFactory(final String namePattern) {
        this(namePattern, true);
    }

    /**
     * @param namePattern String; Example: "thread-%d"
     */
    public NamedThreadFactory(final String namePattern, final boolean daemon) {
        super();

        this.namePattern = Objects.requireNonNull(namePattern, "namePattern required");
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(@NonNull final Runnable r) {
        final Thread thread = defaultThreadFactory.newThread(r);

        final String threadName = String.format(namePattern, threadNumber.getAndIncrement());
        thread.setName(threadName);

        thread.setDaemon(daemon);

        return thread;
    }
}
