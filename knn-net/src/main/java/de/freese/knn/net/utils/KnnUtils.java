// Created: 03.07.2020
package de.freese.knn.net.utils;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

/**
 * @author Thomas Freese
 */
public final class KnnUtils {
    public static final int DEFAULT_POOL_SIZE = Optional.ofNullable(Integer.getInteger("knn.defaultPoolSize")).orElseGet(() -> Runtime.getRuntime().availableProcessors());

    public static void shutdown(final ExecutorService executorService, final Logger logger) {
        logger.info("shutdown ExecutorService");

        if (executorService == null) {
            logger.warn("ExecutorService is null");

            return;
        }

        executorService.shutdown();

        try {
            // Wait a while for existing tasks to terminate.
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warn("Timed out while waiting for ExecutorService");

                // Cancel currently executing tasks.
                executorService.shutdownNow().stream()
                        .filter(Future.class::isInstance)
                        .map(Future.class::cast)
                        .forEach(future -> future.cancel(true))
                ;

                // Wait a while for tasks to respond to being cancelled.
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    logger.error("ExecutorService did not terminate");
                }
                else {
                    logger.info("ExecutorService terminated");
                }
            }
            else {
                logger.info("ExecutorService terminated");
            }
        }
        catch (InterruptedException iex) {
            logger.warn("Interrupted while waiting for ExecutorService");

            // (Re-)Cancel if current thread also interrupted.
            executorService.shutdownNow();

            // Preserve interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    private KnnUtils() {
        super();
    }
}
