package engine.utils.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ThreadPoolManager
 * -----------------
 *
 * Singleton thread pool manager that recycles a fixed pool of threads
 * for body physics computation tasks.
 */
public final class ThreadPoolManager {

    // region Singleton instance
    private static ThreadPoolManager instance;
    private static int configuredPoolSize = -1;
    private final ExecutorService executor;

    private static final int DEFAULT_POOL_SIZE = 250;
    // endregion

    /**
     * Private constructor - ensures singleton pattern via lazy initialization
     */
    private ThreadPoolManager(int poolSize) {
        this.executor = Executors.newFixedThreadPool(poolSize,
                r -> {
                    Thread t = new Thread(r);
                    t.setName("BodyThread-" + System.nanoTime());
                    t.setPriority(Thread.NORM_PRIORITY - 1);
                    return t;
                });
    }

    private static synchronized ThreadPoolManager getInstance() {
        if (instance == null) {
            int poolSize = configuredPoolSize > 0 ? configuredPoolSize : DEFAULT_POOL_SIZE;
            instance = new ThreadPoolManager(poolSize);
        }
        return instance;
    }

    /**
     * Configure pool size BEFORE first submit.
     * If the pool is already created, this call is ignored.
     *
     * @param poolSize desired pool size (typically maxBodies)
     */
    public static synchronized void configure(int poolSize) {
        if (poolSize <= 0) {
            throw new IllegalArgumentException("poolSize must be > 0");
        }
        if (instance != null) {
            return;
        }
        configuredPoolSize = poolSize;
    }

    /**
     * Submit a body's Runnable task to the thread pool.
     *
     * @param task the Runnable body to execute
     */
    public static void submit(Runnable task) {
        getInstance().executor.submit(task);
    }

    /**
     * Prestart all core threads to avoid startup jitter.
     */
    public static void prestartAllCoreThreads() {
        ExecutorService executor = getInstance().executor;
        if (executor instanceof ThreadPoolExecutor) {
            ((ThreadPoolExecutor) executor).prestartAllCoreThreads();
        }
    }

    /**
     * Shutdown the thread pool (for testing or application shutdown).
     */
    public static void shutdown() {
        if (instance != null) {
            instance.executor.shutdown();
        }
    }

    /**
     * Get current queue size (for debugging/monitoring).
     *
     * @return approximate number of pending tasks in the thread pool queue
     */
    public static int getQueueSize() {
        if (instance == null) {
            return 0;
        }
        if (instance.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) instance.executor).getQueue().size();
        }
        return -1;
    }
}
