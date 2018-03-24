package hakkon.android_rss_reader.tasks;

import android.app.Activity;

import java.util.List;

import hakkon.android_rss_reader.database.Database;
import hakkon.android_rss_reader.database.FeedDatabase;
import hakkon.android_rss_reader.database.Feed;

/**
 * Created by hakkon on 21.03.18.
 */

public class GetFeeds extends BaseTask<List<Feed>> {
    public GetFeeds(Activity ca, TaskCallback<List<Feed>> cb) {
        super(ca, cb);
    }

    @Override
    protected void doTask() {
        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());
        List<Feed> feeds = db.feedDao().getFeeds();
        callbackToUI(0, feeds);
    }

}
