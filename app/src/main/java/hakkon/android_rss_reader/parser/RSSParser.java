package hakkon.android_rss_reader.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import hakkon.android_rss_reader.database.FeedItem;
import hakkon.android_rss_reader.database.Feed;

/**
 * Created by hakkon on 17.03.18.
 */

public class RSSParser extends Parser {

    public RSSParser() throws XmlPullParserException {
        // RFC 822
        dateFormats = new SimpleDateFormat[] {
                new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)};
    }

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
            } else if (tag.equalsIgnoreCase("enclosure")) {
                readEnclosure(parser, item);
            } else if (tag.equalsIgnoreCase("media:content")) {
                readMedia(parser, item);
            } else if (tag.equalsIgnoreCase("media:thumbnail")) {
                if (item.getImage() == null)
                    item.setImage(parser.getAttributeValue(null, "url"));
                parser.nextTag();
            } else {
                skip(parser);
            }
        }

        // Image was not found in XML, so try to get image from the description or content
        if (item.getImage() == null) {
            findImage(item);
        }
        return item;
    }

    private void readEnclosure(XmlPullParser parser, FeedItem item) throws XmlPullParserException, IOException {
        String type = parser.getAttributeValue(null, "type").toLowerCase();
        String content = parser.getAttributeValue(null, "url");

        if (type.startsWith("image")) {
            item.setImage(content);
        }

        parser.nextTag();
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
