package hakkon.android_rss_reader.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hakkon on 18.03.18.
 */

public class DownloadBitmap {
    private String url;

    public DownloadBitmap(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() throws IOException {
        URL url = new URL(this.url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(true);

        // Data reader
        BufferedInputStream in;

        // Handle response code
        int response = conn.getResponseCode();
        if (response == 301) {
            this.url = this.url.replace("http://", "https://");
            return getBitmap();
        } else if (response == 302) {
            this.url = conn.getHeaderField("Location");
            return getBitmap();
        } else if (conn.getResponseCode() == 200) {
            in = new BufferedInputStream(conn.getInputStream());
        } else {
            Log.e("URL", this.url);
            throw new IOException("Error response code " + Integer.toString(conn.getResponseCode()));
        }
        return BitmapFactory.decodeStream(in);
    }
}
