package hakkon.android_rss_reader.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by hakkon on 17.03.18.
 */

public class DownloadFeed {
    private String url;

    public DownloadFeed(String url) {
        this.url = url;
    }

    public String getFeed() throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Read data
        BufferedReader in;
        if (conn.getResponseCode() == 200) {
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            throw new IOException("Error response code " + Integer.toString(conn.getResponseCode()));
        }

        StringBuilder buffer = new StringBuilder();
        int data;
        while((data = in.read()) != -1) {
            buffer.append((char) data);
        }

        in.close();
        conn.disconnect();
        return buffer.toString();
    }

}