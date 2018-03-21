package hakkon.android_rss_reader.parser;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import hakkon.android_rss_reader.feed.FeedItem;
import hakkon.android_rss_reader.feed.Feed;

public class AtomParser extends Parser {
    public AtomParser() throws XmlPullParserException { }

    public ParserResult parse(String xml) throws XmlPullParserException, IOException {
        ParserResult result = new ParserResult();
        result.feed = new Feed();
        result.items = new ArrayList<>();
        StringReader in = new StringReader(xml);

        parser.setInput(in);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "feed");

        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String tag = parser.getName();
            if (tag.equalsIgnoreCase("title")) {
                result.feed.setTitle(readText(parser));
            } else if (tag.equalsIgnoreCase("updated")) {
                result.feed.setUpdated(readDate(parser));
            } else if (tag.equalsIgnoreCase("link")) {
                result.feed.setLink(readText(parser));
            } else if (tag.equalsIgnoreCase("icon")) {
                result.feed.setImage(readText(parser));
            } else if (tag.equalsIgnoreCase("entry")) {
                result.items.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }

        return result;
    }

    private FeedItem readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        FeedItem item = new FeedItem();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String tag = parser.getName();
            if (tag.equalsIgnoreCase("title")) {
                item.setTitle(readText(parser));
            } else if (tag.equalsIgnoreCase("updated")) {
                item.setDate(readDate(parser));
            } else if (tag.equalsIgnoreCase("author")) {
                item.setAuthor(readAuthor(parser));
            } else if (tag.equalsIgnoreCase("content")) {
                item.setEncodedContent(readText(parser));
            } else if (tag.equalsIgnoreCase("link")) {
                item.setLink(parser.getAttributeValue(null, "href"));
                parser.nextTag();
            } else if (tag.equalsIgnoreCase("summary")) {
                item.setDescription(readText(parser));
            } else {
                skip(parser);
            }
        }

        return item;
    }

    private String readAuthor(XmlPullParser parser) throws XmlPullParserException, IOException {
        StringBuilder author = new StringBuilder();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            if (parser.getName().equalsIgnoreCase("name")) {
                author.append(readText(parser)).append(", ");
            } else {
                skip(parser);
            }
        }
        return author.toString();
    }

    private long readDate(XmlPullParser parser) throws XmlPullParserException, IOException {
        String unformatted = readText(parser);

        try {
            Date date = Parser.timeAtom.parse(unformatted);
            return date.getTime();
        } catch (ParseException e) {
            Log.e("RSSParser", Log.getStackTraceString(e));
        }

        return -1;
    }
}
