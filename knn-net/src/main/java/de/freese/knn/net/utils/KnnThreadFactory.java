package de.freese.knn.net.utils;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Thomas Freese
 */
public class KnnThreadFactory implements ThreadFactory {
    private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

    private final String namePrefix;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public KnnThreadFactory(final String namePrefix) {

        this.namePrefix = Objects.requireNonNull(namePrefix, "namePrefix required");
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable r) {
        Thread thread = this.defaultThreadFactory.newThread(r);

        thread.setName(this.namePrefix + this.threadNumber.getAndIncrement());
        thread.setDaemon(true);

        return thread;
    }
}
