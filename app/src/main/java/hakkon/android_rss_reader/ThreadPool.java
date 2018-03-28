package hakkon.android_rss_reader;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import hakkon.android_rss_reader.tasks.BaseTask;

/**
 * Created by hakkon on 12.03.18.
 */

public class ThreadPool {
    private static ThreadPool singleInstance;
    private static final int NUMBER_OF_CORES;
    private static final int KEEP_ALIVE_TIME;

    private final BlockingQueue<Runnable> workQueue;
    private final ThreadPoolExecutor threadPool;

    private ThreadPool () {
        this.workQueue = new LinkedBlockingQueue<>();
        this.threadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                this.workQueue);
    }

    static {
        NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        KEEP_ALIVE_TIME = 5;
    }

    public static ThreadPool getInstance() {
        if (singleInstance == null) {
            singleInstance = new ThreadPool();
        }
        return singleInstance;
    }

    public void execute(BaseTask task) {
        this.threadPool.execute(task);
    }

    public void stopAll() {
        BaseTask[] tasks = new BaseTask[this.workQueue.size()];
        this.workQueue.toArray(tasks);

        int taskLen = tasks.length;

        synchronized (this) {
            for(int i = 0; i < taskLen; i++) {
                Thread thread = tasks[i].getThread();

                if (thread != null) {
                    thread.interrupt();
                }
            }
        }
    }
}
