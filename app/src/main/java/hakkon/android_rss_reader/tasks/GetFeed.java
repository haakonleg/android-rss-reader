package hakkon.android_rss_reader.tasks;

import android.app.Activity;
import android.util.Log;

import java.util.concurrent.Callable;

import hakkon.android_rss_reader.database.Database;
import hakkon.android_rss_reader.database.FeedDatabase;
import hakkon.android_rss_reader.feed.Feed;

/**
 * Created by hakkon on 21.03.18.
 */

public class GetFeed extends BaseTask<Feed> {
    private String originLink;

    public GetFeed(Activity ca, String originLink, TaskCallback<Feed> cb) {
        super(ca, cb);
        this.originLink = originLink;
    }

    @Override
    protected void doTask() {
        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());
        Feed feed = db.feedDao().getFeed(this.originLink);
        callbackToUI(0, feed);
    }

}
