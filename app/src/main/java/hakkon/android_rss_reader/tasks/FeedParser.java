package hakkon.android_rss_reader.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hakkon.android_rss_reader.database.Database;
import hakkon.android_rss_reader.database.FeedDatabase;
import hakkon.android_rss_reader.database.Feed;
import hakkon.android_rss_reader.database.FeedItem;
import hakkon.android_rss_reader.network.DownloadFeed;
import hakkon.android_rss_reader.parser.AtomParser;
import hakkon.android_rss_reader.parser.Parser;
import hakkon.android_rss_reader.parser.RSSParser;
import hakkon.android_rss_reader.parser.RdfParser;

/**
 * Created by hakkon on 12.03.18.
 */

public class FeedParser extends BaseTask<Feed> {
    private String url;

    public FeedParser(Activity ca, String url, TaskCallback<Feed> cb) {
        super(ca, cb);
        this.url = url;
    }

    @Override
    protected void doTask() {
        Parser.ParserResult result = null;
        String data = null;

        // Download the feed xml
        try {
            DownloadFeed downloader = new DownloadFeed(this.url);
            data = downloader.getFeed();
        } catch (IOException e) {
            Log.e("FeedParser", Log.getStackTraceString(e));
            callbackToUI(-1, null);
            return;
        }

        // Determine parser type and get the parsed result
        try {
            int type = Parser.detectType(data);
            switch (type) {
                case Parser.TYPE_RSS:
                    result = new RSSParser().parse(data);
                    break;
                case Parser.TYPE_ATOM:
                    result = new AtomParser().parse(data);
                    break;
                case Parser.TYPE_RDF:
                    result = new RdfParser().parse(data);
                    break;
                case Parser.TYPE_UNKNOWN:
                    callbackToUI(-2, null);
                    return;
            }
        } catch (IOException | XmlPullParserException e) {
            Log.e("FeedParser", Log.getStackTraceString(e));
            callbackToUI(-3, null);
            return;
        }

        result.feed.setOriginLink(this.url);
        saveToDb(result.feed);
        saveItemsToDb(result.items);

        callbackToUI(0, result.feed);
    }

    private void saveToDb(Feed feed) {
        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());

        // Check if it exists already, if not insert
        if (db.feedDao().getFeed(feed.getOriginLink()) == null)
            db.feedDao().insertFeed(feed);
    }

    private void saveItemsToDb(List<FeedItem> items) {
        FeedDatabase db = Database.getInstance(callingActivity.getApplicationContext());

        // Determine newly updated articles to add
        try {
            ArrayList<FeedItem> toInsert = new ArrayList<>();
            long lastDate = db.feedItemDAO().getNewestItem(this.url);
            for(FeedItem item : items) {
                if (item.getDate() > lastDate) {
                    item.setParentFeed(this.url);
                    toInsert.add(item);
                }
            }
            // Insert the items
            db.feedItemDAO().insertItems(toInsert);

            // Determine if we need to delete old articles (max feeds reached)
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callingActivity);
            int max = Integer.parseInt(prefs.getString("max_articles", null));
            int feedCount = db.feedItemDAO().getCount(this.url);
            Log.e("ARTICLECOUNT", "ArticleCount: " + Integer.toString(feedCount) + " " + this.url);

            if (feedCount > max) {
                db.feedItemDAO().deleteOldest(this.url, feedCount - max);
            }

        } catch (SQLiteException e) {
            Log.e("FeedParser", Log.getStackTraceString(e));
        }
    }
}
