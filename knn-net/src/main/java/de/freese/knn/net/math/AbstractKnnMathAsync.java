/**
 * Created: 06.07.2020
 */

package de.freese.knn.net.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import de.freese.knn.net.NeuralNet;
import de.freese.knn.net.neuron.NeuronList;
import de.freese.knn.net.utils.KnnThreadQueueThreadFactory;
import de.freese.knn.net.utils.KnnUtils;

/**
 * Asynchrone Mathematik des {@link NeuralNet}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractKnnMathAsync extends AbstractKnnMath implements AutoCloseable
{
    /**
    *
    */
    private boolean createdExecutor = false;

    /**
    *
    */
    private ExecutorService executorService = null;

    /**
     * Erstellt ein neues {@link AbstractKnnMathAsync} Object.
     */
    public AbstractKnnMathAsync()
    {
        super();

        // this.executorService = Executors.newCachedThreadPool(new KnnThreadQueueThreadFactory());
        // this.executorService = Executors.newWorkStealingPool(KnnUtils.DEFAULT_POOL_SIZE);
        this.executorService = Executors.newFixedThreadPool(getPoolSize(), new KnnThreadQueueThreadFactory());

        this.createdExecutor = true;
    }

    /**
     * Erstellt ein neues {@link AbstractKnnMathAsync} Object.
     *
     * @param executorService {@link ExecutorService}
     */
    public AbstractKnnMathAsync(final ExecutorService executorService)
    {
        super();

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
    }

    /**
     * @see java.lang.AutoCloseable#close()
     */
    @Override
    public void close() throws Exception
    {
        if (isCreatedExecutor())
        {
            KnnUtils.shutdown(getExecutorService(), getLogger());
        }
    }

    /**
     * @return {@link ExecutorService}
     */
    protected ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    /**
     * Aufsplitten der Neuronen für parallele Verarbeitung.<br>
     * Es wird pro Thread eine SubList verarbeitet.<br>
     * Keine parallele Verarbeitung für einzelne Elemente, dadurch zu hoher Verwaltungsaufwand für die Runtime.
     *
     * @param neurons {@link NeuronList}
     * @return {@link List}<NeuronList>
     */
    protected List<NeuronList> getPartitions(final NeuronList neurons)
    {
        List<NeuronList> partitions = new ArrayList<>(getPoolSize() + 1);

        int size = neurons.size() / getPoolSize();

        if (size <= 1)
        {
            // Keine Partitionen mit nur einem oder mit zu wenigen Elementen.
            size = 2;
        }

        for (int i = 0; i < neurons.size(); i += size)
        {
            partitions.add(neurons.subList(i, Math.min(i + size, neurons.size())));
        }

        return partitions;
    }

    /**
     * Liefert die Größe des ThreadPools.
     *
     * @return int
     */
    protected int getPoolSize()
    {
        return KnnUtils.DEFAULT_POOL_SIZE;
    }

    /**
     * @return boolean
     */
    protected boolean isCreatedExecutor()
    {
        return this.createdExecutor;
    }

    /**
     * Warten bis alle Tasks fertig sind.
     *
     * @param completionService {@link CompletionService}
     * @param count int; Anzahl der Tasks
     */
    protected void waitForCompletionService(final CompletionService<?> completionService, final int count)
    {
        for (int i = 0; i < count; i++)
        {
            try
            {
                completionService.take();
            }
            catch (InterruptedException ex)
            {
                getLogger().error(null, ex);
            }
        }
    }

    /**
     * Warten bis alle Tasks fertig sind.
     *
     * @param future {@link Future}
     */
    protected void waitForFuture(final Future<?> future)
    {
        try
        {
            future.get();
        }
        catch (InterruptedException | ExecutionException ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * Warten bis alle Tasks fertig sind.
     *
     * @param futures {@link List}
     */
    protected void waitForFutures(final List<? extends Future<Void>> futures)
    {
        for (Future<Void> future : futures)
        {
            waitForFuture(future);
        }
    }

    /**
     * Blockiert den aktuellen Thread, bis der Latch auf 0 ist.
     *
     * @param latch {@link CountDownLatch}
     */
    protected void waitForLatch(final CountDownLatch latch)
    {
        try
        {
            latch.await();
        }
        catch (RuntimeException rex)
        {
            throw rex;
        }
        catch (Throwable th)
        {
            throw new RuntimeException(th);
        }
    }
}
