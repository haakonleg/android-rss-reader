package hakkon.android_rss_reader.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;

import hakkon.android_rss_reader.filesystem.BitmapCache;
import hakkon.android_rss_reader.network.DownloadBitmap;
import hakkon.android_rss_reader.util.NetworkState;

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
        if (this.bitmap == null && NetworkState.hasNetwork(callingActivity)) {
            DownloadBitmap bitmapDownloader = new DownloadBitmap(this.url);
            try {
                Bitmap rawBitmap = bitmapDownloader.getBitmap();
                if (rawBitmap != null) {
                    BitmapCache.saveImage(callingActivity, this.url, rawBitmap);
                    this.bitmap = BitmapCache.loadImage(callingActivity, this.url);
                }
            } catch (IOException e) {
                Log.e("GetBitmap", Log.getStackTraceString(e));
                callbackToUI(-1, null);
            }
        }
        callbackToUI(0, this.bitmap);
    }
}
