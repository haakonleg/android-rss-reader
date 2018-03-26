package hakkon.android_rss_reader.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;

import hakkon.android_rss_reader.filesystem.BitmapCache;
import hakkon.android_rss_reader.network.DownloadBitmap;

/**
 * Created by hakkon on 18.03.18.
 */

public class GetBitmap extends BaseTask<Bitmap> {
    private Bitmap bitmap;
    private String url;

    public GetBitmap(Activity ca, String url, TaskCallback<Bitmap> cb) {
        super(ca, cb);
        this.url = url;
    }

    @Override
    protected void doTask() {
        // Try to load from cache
        this.bitmap = BitmapCache.loadImage(callingActivity, this.url);

        // Not in cache, download and save
        if (this.bitmap == null) {
            DownloadBitmap bitmapDownloader = new DownloadBitmap(this.url);
            try {
                this.bitmap = bitmapDownloader.getBitmap();
            } catch (IOException e) {
                Log.e("GetBitmap", Log.getStackTraceString(e));
                callbackToUI(-1, null);
                return;
            }
            BitmapCache.saveImage(callingActivity, this.url, this.bitmap);
        } else {
            Log.e("Loading image cache", this.url);
        }

        callbackToUI(0, this.bitmap);
    }
}
