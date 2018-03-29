package hakkon.android_rss_reader.tasks;

import android.app.Activity;

import hakkon.android_rss_reader.database.Database;
import hakkon.android_rss_reader.database.Feed;
import hakkon.android_rss_reader.database.FeedDatabase;

/**
 * Created by hakkon on 28.03.18.
 */

public class DeleteFeed extends BaseTask<Void> {
    private Feed feed;

    public DeleteFeed(Activity ca, Feed feed, TaskCallback<Void> cb) {
        super(ca, cb);
        this.feed = feed;
    }

    @Override
    protected void doTask() {
        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());

        // Delete articles and feed
        db.feedItemDAO().deleteAll(this.feed.getOriginLink());
        db.feedDao().deleteFeed(this.feed);
        callbackToUI(0, null);
    }
}
