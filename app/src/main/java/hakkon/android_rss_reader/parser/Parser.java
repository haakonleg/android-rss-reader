package hakkon.android_rss_reader.parser;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import hakkon.android_rss_reader.database.Feed;
import hakkon.android_rss_reader.database.FeedItem;

/**
 * Created by hakkon on 19.03.18.
 */

public abstract class Parser {
    // Feed types
    public static final int TYPE_RSS = 0;
    public static final int TYPE_ATOM = 1;
    public static final int TYPE_RDF = 2;
    public static final int TYPE_UNKNOWN = -1;

    // Date formats
    protected SimpleDateFormat[] dateFormats;

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

    /**
     * Finds an image url in the HTML source
     * @param item The FeedItem to find image for
     */
    void findImage(FeedItem item) {
        String htmlString = "";
        if (item.getEncodedContent() != null)
            htmlString = item.getEncodedContent();
        else if (item.getDescription() != null)
            htmlString = item.getDescription();

        Document html = Jsoup.parse(htmlString);
        Element image = html.selectFirst("img");
        if (image != null)
            item.setImage(image.attr("src"));
    }

    void readMedia(XmlPullParser parser, FeedItem item) throws XmlPullParserException, IOException {
        String content = parser.getAttributeValue(null, "url");
        String mime = parser.getAttributeValue(null, "type");

        String format = null;
        if (content != null) {
            int lastDot = content.lastIndexOf(".") + 1;
            if (lastDot != -1)
                format = content.substring(lastDot, content.length()).toLowerCase();
        }

        if (mime != null) {
            mime = mime.toLowerCase();
            if (mime.startsWith("image"))
                item.setImage(content);
        } else if (format != null) {
            switch (format) {
                case "jpg":
                    item.setImage(content);
                    break;
                case "jpeg":
                    item.setImage(content);
                    break;
                case "gif":
                    item.setImage(content);
                    break;
                case "png":
                    item.setImage(content);
                    break;
            }
        }

        while(parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;
            skip(parser);
        }
    }

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

    long readDate(XmlPullParser parser) throws XmlPullParserException, IOException {
        String unformatted = readText(parser);

        for (int i = 0; i < dateFormats.length; i++) {
            SimpleDateFormat format = dateFormats[i];
            try {
                Date date = format.parse(unformatted);
                return date.getTime();
            } catch (ParseException e) {
                if (i == dateFormats.length - 1)
                    Log.e("AtomParser", Log.getStackTraceString(e));
            }
        }

        return -1;
    }

    public class ParserResult {
        public Feed feed;
        public List<FeedItem> items;
    }
}
