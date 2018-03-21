package hakkon.android_rss_reader.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import hakkon.android_rss_reader.feed.Feed;
import hakkon.android_rss_reader.feed.FeedItem;

/**
 * Created by hakkon on 19.03.18.
 */

public abstract class Parser {
    // Feed types
    public static final int TYPE_RSS = 0;
    public static final int TYPE_ATOM = 1;
    public static final int TYPE_RDF = 2;
    public static final int TYPE_UNKNOWN = -1;

    public static int detectType(String xml) throws XmlPullParserException, IOException {
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        StringReader in = new StringReader(xml);

        parser.setInput(in);
        parser.nextTag();
        if (parser.getName().equalsIgnoreCase("rss"))
            return TYPE_RSS;
        else if (parser.getName().equalsIgnoreCase("feed"))
            return TYPE_ATOM;
        else if (parser.getName().equalsIgnoreCase("rdf:RDF"))
            return TYPE_RDF;
        else
            return TYPE_UNKNOWN;
    }

    XmlPullParser parser;

    Parser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        parser = factory.newPullParser();
    }

    // Must be implemented
    public abstract ParserResult parse(String xml) throws XmlPullParserException, IOException;

    String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String res = "";
        if (parser.next() == XmlPullParser.TEXT) {
            res = parser.getText();
            parser.nextTag();
        }
        return res;
    }

    void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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

    public class ParserResult {
        public Feed feed;
        public List<FeedItem> items;
    }
}
