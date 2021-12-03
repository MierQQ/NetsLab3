package Searchers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static ThreadPool instance = null;
    private int numberOfThreads = 2;

    public ExecutorService getPool() {
        return pool;
    }

    private ExecutorService pool;
    private ThreadPool() {
        pool = Executors.newFixedThreadPool(numberOfThreads);
    }

    public static ThreadPool getInstance() {
        if (instance == null) {
            synchronized (ThreadPool.class) {
                if (instance == null) {
                    instance = new ThreadPool();
                }
            }
        }
        return instance;
    }
}
