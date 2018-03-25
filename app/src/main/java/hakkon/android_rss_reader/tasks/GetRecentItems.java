package hakkon.android_rss_reader.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;

import hakkon.android_rss_reader.database.Database;
import hakkon.android_rss_reader.database.FeedDatabase;
import hakkon.android_rss_reader.database.FeedItem;

/**
 * Created by hakkon on 21.03.18.
 */

public class GetRecentItems extends BaseTask<List<FeedItem>> {
    public GetRecentItems(Activity ca, TaskCallback<List<FeedItem>> cb) {
        super(ca, cb);
    }

    @Override
    protected void doTask() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callingActivity);
        int limit = Integer.parseInt(prefs.getString("max_recent_articles", null));

        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());
        List<FeedItem> items = db.feedItemDAO().getRecentItems(limit);
        callbackToUI(0, items);
    }
}
