package hakkon.android_rss_reader.tasks;

import android.app.Activity;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import hakkon.android_rss_reader.feed.FeedModel;
import hakkon.android_rss_reader.parser.DownloadFeed;
import hakkon.android_rss_reader.parser.RSSParser;

/**
 * Created by hakkon on 12.03.18.
 */

public class FeedParser extends BaseTask<FeedModel> {
    private FeedModel feed;
    private String url;

    public FeedParser(Activity ca, String url, TaskCallback<FeedModel> cb) {
        super(ca, cb);
        this.url = url;
        this.feed = new FeedModel();
    }

    @Override
    protected void doTask() {
        String data = null;
        try {
            DownloadFeed downloader = new DownloadFeed(this.url);
            data = downloader.getFeed();
        } catch (IOException e) {
            Log.e("FeedParser", Log.getStackTraceString(e));
            callbackToUI(-1, null);
            return;
        }

        try {
            RSSParser parser = new RSSParser();
            this.feed = parser.parse(data);
        } catch (IOException | XmlPullParserException e) {
            Log.e("FeedParser", Log.getStackTraceString(e));
            callbackToUI(-2, null);
            return;
        }

        callbackToUI(0, this.feed);
    }
}
