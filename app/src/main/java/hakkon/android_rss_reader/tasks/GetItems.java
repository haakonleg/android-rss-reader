package hakkon.android_rss_reader.tasks;

import android.app.Activity;

import java.util.List;

import hakkon.android_rss_reader.database.Database;
import hakkon.android_rss_reader.database.FeedDatabase;
import hakkon.android_rss_reader.database.FeedItem;

/**
 * Created by hakkon on 21.03.18.
 */

public class GetItems extends BaseTask<List<FeedItem>> {
    private String originLink;

    public GetItems(Activity ca, String originLink, TaskCallback<List<FeedItem>> cb) {
        super(ca, cb);
        this.originLink = originLink;
    }

    @Override
    protected void doTask() {
        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());
        List<FeedItem> items = db.feedItemDAO().getItems(this.originLink);
        callbackToUI(0, items);
    }
}
