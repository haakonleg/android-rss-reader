package hakkon.android_rss_reader.tasks;

import android.app.Activity;

import java.util.List;

import hakkon.android_rss_reader.database.Database;
import hakkon.android_rss_reader.database.FeedDatabase;
import hakkon.android_rss_reader.feed.FeedItem;

/**
 * Created by hakkon on 21.03.18.
 */

public class GetRecentItems extends BaseTask<List<FeedItem>> {
    private int limit;

    public GetRecentItems(Activity ca, int limit, TaskCallback<List<FeedItem>> cb) {
        super(ca, cb);
        this.limit = limit;
    }

    @Override
    protected void doTask() {
        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());
        List<FeedItem> items = db.feedItemDAO().getRecentItems(this.limit);
        callbackToUI(0, items);
    }
}
