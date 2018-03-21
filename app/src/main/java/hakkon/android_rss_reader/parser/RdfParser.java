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

/**
 * Created by hakkon on 19.03.18.
 */

public class RdfParser extends Parser {
    public RdfParser () throws XmlPullParserException {}

    @Override
    public ParserResult parse(String xml) throws XmlPullParserException, IOException {
        ParserResult result = new ParserResult();
        result.feed = new Feed();
        result.items = new ArrayList<>();
        StringReader in = new StringReader(xml);

        parser.setInput(in);
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "rdf:RDF");

        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String tag = parser.getName();
            if (tag.equalsIgnoreCase("channel")) {
                readChannel(parser, result.feed);
            } else if (tag.equalsIgnoreCase("item")) {
                result.items.add(readItem(parser));
            } else {
                skip(parser);
            }
        }

        in.close();
        return result;
    }

    private void readChannel(XmlPullParser parser, Feed in) throws XmlPullParserException, IOException {
        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String tag = parser.getName();
            if (tag.equalsIgnoreCase("title")) {
                in.setTitle(readText(parser));
            } else if (tag.equalsIgnoreCase("link")) {
                in.setLink(readText(parser));
            } else if (tag.equalsIgnoreCase("description")) {
                in.setDescription(readText(parser));
            } else if (tag.equalsIgnoreCase("image")) {
                String image = parser.getAttributeValue(null, "rdf:resource");
                if (image != null)
                    in.setImage(parser.getAttributeValue(null, "rdf:resource"));
                parser.nextTag();
            } else if (tag.equalsIgnoreCase("dc:date")) {
                in.setUpdated(readDate(parser));
            } else {
                skip(parser);
            }
        }
    }

    private long readDate(XmlPullParser parser) throws XmlPullParserException, IOException {
        String unformatted = readText(parser);

        try {
            Date date = Parser.timeRdf.parse(unformatted);
            return date.getTime();
        } catch (ParseException e) {
            Log.e("RdfParser", Log.getStackTraceString(e));
        }

        return -1;
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
            } else if (tag.equalsIgnoreCase("dc:date")) {
                item.setDate(readDate(parser));
            } else {
                skip(parser);
            }
        }
        return item;
    }
}
