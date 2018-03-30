package hakkon.android_rss_reader.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import hakkon.android_rss_reader.R;
import hakkon.android_rss_reader.database.Database;
import hakkon.android_rss_reader.database.FeedDatabase;
import hakkon.android_rss_reader.database.FeedItem;

/**
 * Created by hakkon on 21.03.18.
 */

public class GetItems extends BaseTask<List<FeedItem>> {
    private ArrayList<String> feedUrls;

    public GetItems(Activity ca, ArrayList<String> feedUrls, TaskCallback<List<FeedItem>> cb) {
        super(ca, cb);
        this.feedUrls = feedUrls;
    }

    @Override
    protected void doTask() {
        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callingActivity);

        // Max items to show
        int limit = Integer.parseInt(prefs.getString("max_articles",
                callingActivity.getString(R.string.pref_max_articles_default)));

        List<FeedItem> items = db.feedItemDAO().getItems(this.feedUrls, limit);
        callbackToUI(0, items);
    }
}
