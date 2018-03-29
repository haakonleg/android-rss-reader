package hakkon.android_rss_reader.tasks;

import android.app.Activity;

import java.util.List;

import hakkon.android_rss_reader.database.Database;
import hakkon.android_rss_reader.database.Feed;
import hakkon.android_rss_reader.database.FeedDatabase;
import hakkon.android_rss_reader.database.FeedItem;

/**
 * Created by hakkon on 29.03.18.
 */

public class UpdateFeed extends BaseTask<Void> {
    private Feed feed;

    public UpdateFeed(Activity ca, Feed feed, TaskCallback<Void> cb) {
        super(ca, cb);
        this.feed = feed;
    }

    @Override
    protected void doTask() {
        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());

        Feed old = db.feedDao().getFeed(this.feed.getOriginLink());
        db.feedDao().updateFeed(this.feed);

        // Check if name was changed and need to update all child items
        if (!old.getTitle().equals(this.feed.getTitle())) {
            List<FeedItem> items = db.feedItemDAO().getItems(this.feed.getOriginLink());
            for (FeedItem item : items) {
                item.setParentTitle(this.feed.getTitle());
            }
            db.feedItemDAO().updateItems(items);
        }

        callbackToUI(0, null);
    }
}
