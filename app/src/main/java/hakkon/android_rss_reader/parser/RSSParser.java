package hakkon.android_rss_reader.parser;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import hakkon.android_rss_reader.feed.FeedItem;
import hakkon.android_rss_reader.feed.Feed;

/**
 * Created by hakkon on 17.03.18.
 */

public class RSSParser extends Parser {
    // RFC 822
    private final SimpleDateFormat timeRSS =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.getDefault());

    public RSSParser() throws XmlPullParserException { }

    @Override
    public ParserResult parse(String xml) throws XmlPullParserException, IOException {
        ParserResult result = new ParserResult();
        result.feed = new Feed();
        result.items = new ArrayList<>();
        StringReader in = new StringReader(xml);

        parser.setInput(in);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "rss");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "channel");

        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String tag = parser.getName();
            if (tag.equalsIgnoreCase("title")) {
                result.feed.setTitle(readText(parser));
            } else if (tag.equalsIgnoreCase("link")) {
                result.feed.setLink(readText(parser));
            } else if (tag.equalsIgnoreCase("description")) {
                result.feed.setDescription(readText(parser));
            } else if (tag.equalsIgnoreCase("image")) {
                result.feed.setImage(readImage(parser));
            } else if (tag.equalsIgnoreCase("item")) {
                result.items.add(readItem(parser));
            } else {
                skip(parser);
            }
        }

        in.close();
        return result;
    }

    private FeedItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        FeedItem item = new FeedItem();

        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String tag = parser.getName();
            if (tag.equalsIgnoreCase("title")) {
                item.setTitle(readText(parser));
            } else if (tag.equalsIgnoreCase("link")) {
                item.setLink(readText(parser));
            } else if (tag.equalsIgnoreCase("description")) {
                item.setDescription(readText(parser));
            } else if (tag.equalsIgnoreCase("author")) {
                item.setAuthor(readText(parser));
            } else if (tag.equalsIgnoreCase("pubDate")) {
                item.setDate(readDate(parser));
            } else if (tag.equalsIgnoreCase("content:encoded")) {
                item.setEncodedContent(readText(parser));
            } else {
                skip(parser);
            }
        }
        return item;
    }

    private long readDate(XmlPullParser parser) throws XmlPullParserException, IOException {
        String unformatted = readText(parser);

        try {
            Date date = timeRSS.parse(unformatted);
            return date.getTime();
        } catch (ParseException e) {
            Log.e("RSSParser", Log.getStackTraceString(e));
        }

        return -1;
    }

    private String readImage(XmlPullParser parser) throws XmlPullParserException, IOException {
        String res = "";
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String tag = parser.getName();
            if (tag.equalsIgnoreCase("url")) {
                res = readText(parser);
            } else {
                skip(parser);
            }
        }
        return res;
    }
}
