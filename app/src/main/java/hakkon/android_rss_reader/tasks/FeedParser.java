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

public class FeedParser extends BaseTask<Parser.ParserResult> {
    public static final int PARSER_ERROR_DOWNLOAD = -1;
    public static final int PARSER_ERROR_INVALID_FEED = -2;
    public static final int PARSER_ERROR_PARSE_ERROR = -3;
    public static final int PARSER_OK = 0;

    private FeedDatabase db;
    private String url;

    public FeedParser(Activity ca, String url, TaskCallback<Parser.ParserResult> cb) {
        super(ca, cb);
        this.url = url;
        this.db = Database.getInstance(callingActivity.getApplicationContext());
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
            callbackToUI(PARSER_ERROR_DOWNLOAD, null);
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
                    callbackToUI(PARSER_ERROR_INVALID_FEED, null);
                    return;
            }
        } catch (IOException | XmlPullParserException e) {
            Log.e("FeedParser", Log.getStackTraceString(e));
            callbackToUI(PARSER_ERROR_PARSE_ERROR, null);
            return;
        }

        // Save feed to database if it doesn't exist
        Feed feed = db.feedDao().getFeed(this.url);
        if (feed == null) {
            result.feed.setOriginLink(this.url);
            // Set feed image to favicon
            String favicon = result.feed.getLink() + "/favicon.ico";
            result.feed.setImage(favicon);

            db.feedDao().insertFeed(result.feed);
            feed = db.feedDao().getFeed(this.url);
        }

        // Check new articles
        List<FeedItem> affectedItems = checkNewItems(result.items, feed.getTitle());

        callbackToUI(PARSER_OK, new Parser.ParserResult(feed, affectedItems));
    }

    private List<FeedItem> checkNewItems(List<FeedItem> items, String feedTitle) {
        // Determine newly updated articles to add
        ArrayList<FeedItem> toInsert = new ArrayList<>();
        ArrayList<FeedItem> toUpdate = new ArrayList<>();
        try {
            long lastDate = db.feedItemDAO().getNewestItem(this.url);
            for(FeedItem item : items) {
                if (item.getDate() > lastDate) {
                    item.setParentFeed(this.url);
                    item.setParentTitle(feedTitle);

                    // Check if the article is just updated and not a new one
                    FeedItem old = db.feedItemDAO().getItem(this.url, item.getLink());
                    if (old != null) {
                        item.setId(old.getId());
                        toUpdate.add(item);
                    } else {
                        toInsert.add(item);
                    }
                }
            }
            // Insert and update
            db.feedItemDAO().insertItems(toInsert);
            db.feedItemDAO().updateItems(toUpdate);

            // Determine if we need to delete old articles (max feeds reached)
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(callingActivity);
            int max = Integer.parseInt(prefs.getString("max_articles", null));
            int feedCount = db.feedItemDAO().getCount(this.url);

            if (feedCount > max) {
                db.feedItemDAO().deleteOldest(this.url, feedCount - max);
            }
        } catch (SQLiteException e) {
            Log.e("FeedParser", Log.getStackTraceString(e));
        }

        ArrayList<FeedItem> affectedItems = new ArrayList<>();
        affectedItems.addAll(toInsert);
        affectedItems.addAll(toUpdate);
        return affectedItems;
    }
}
