package hakkon.android_rss_reader.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import hakkon.android_rss_reader.feed.FeedItemModel;
import hakkon.android_rss_reader.feed.FeedModel;

/**
 * Created by hakkon on 17.03.18.
 */

public class RSSParser {
    private XmlPullParser parser;

    public RSSParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        parser = factory.newPullParser();
    }

    public FeedModel parse(String xml) throws XmlPullParserException, IOException {
        FeedModel feed = new FeedModel();
        StringReader in = new StringReader(xml);

        parser.setInput(in);
        parser.nextTag();

        while(parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String tag = parser.getName();
            if (tag.equalsIgnoreCase("channel")) {
                readChannel(parser, feed);
            } else if (tag.equalsIgnoreCase("item")) {
                feed.addItem(readItem(parser));
            } else {
                skip(parser);
            }
        }

        in.close();
        return feed;
    }

    private FeedItemModel readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        FeedItemModel item = new FeedItemModel();

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
                item.setDate(readText(parser));
            } else {
                skip(parser);
            }
        }
        return item;
    }

    private void readChannel(XmlPullParser parser, FeedModel feed) throws XmlPullParserException, IOException {
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String tag = parser.getName();
            if (tag.equalsIgnoreCase("title")) {
                feed.setTitle(readText(parser));
            }
            else if (tag.equalsIgnoreCase("link")) {
                feed.setLink(readText(parser));
            }
            else if (tag.equalsIgnoreCase("description")) {
                feed.setDescription(readText(parser));
            }
        }
    }

    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String res = "";
        if (parser.next() == XmlPullParser.TEXT) {
            res = parser.getText();
            parser.nextTag();
        }
        return res;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
