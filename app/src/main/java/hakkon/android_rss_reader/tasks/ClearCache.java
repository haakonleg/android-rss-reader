package hakkon.android_rss_reader.tasks;

import android.app.Activity;

import hakkon.android_rss_reader.database.Database;
import hakkon.android_rss_reader.database.FeedDatabase;
import hakkon.android_rss_reader.filesystem.BitmapCache;

/**
 * Created by hakkon on 25.03.18.
 */

public class ClearCache extends BaseTask<Void> {
    public ClearCache(Activity ca, TaskCallback<Void> cb) {
        super(ca, cb);
    }

    @Override
    protected void doTask() {
        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());
        db.feedItemDAO().deleteAll();
        BitmapCache.clear(callingActivity);
        callbackToUI(0, null);
    }
}
