package hakkon.android_rss_reader.tasks;

import android.app.Activity;
import android.os.Process;

/**
 * Created by hakkon on 12.03.18.
 */

public abstract class BaseTask<T> implements Runnable {
    private Thread thread;
    private TaskCallback<T> cb;
    protected Activity callingActivity;

    protected BaseTask (Activity ca, TaskCallback<T> cb) {
        this.callingActivity = ca;
        this.cb = cb;
    }

    public Thread getThread() {
        return thread;
    }

    protected void callbackToUI (int error, T obj) {
        callingActivity.runOnUiThread(() -> {
            cb.onComplete(error, obj);
        });
    }

    @Override
    public void run() {
        // Set background priority
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        this.thread = Thread.currentThread();
        this.doTask();
    }

    protected abstract void doTask();

    public interface TaskCallback<T> {
        public void onComplete(int error, T res);
    }
}