package de.freese.knn.net.math.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SPIELEREI !!! Statt pool-x steht ThreadQueue-x im ThreadNamen, aber schoen schauts aus :-)
 *
 * @author Thomas Freese
 */
class KnnThreadQueueThreadFactory implements ThreadFactory
{
    /**
     * 
     */
    private static final AtomicInteger queueNumber = new AtomicInteger(1);

    /**
     * 
     */
    private final ThreadGroup group;

    /**
     * 
     */
    private final String namePrefix;

    /**
     * 
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * Creates a new {@link KnnThreadQueueThreadFactory} object.
     */
    KnnThreadQueueThreadFactory()
    {
        SecurityManager sm = System.getSecurityManager();

        this.group = sm != null ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = "KnnQueue-" + queueNumber.getAndIncrement() + "-thread-";
    }

    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(final Runnable r)
    {
        Thread thread = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0);

        if (thread.isDaemon())
        {
            thread.setDaemon(false);
        }

        if (thread.getPriority() != Thread.NORM_PRIORITY)
        {
            thread.setPriority(Thread.NORM_PRIORITY);
        }

        return thread;
    }
}