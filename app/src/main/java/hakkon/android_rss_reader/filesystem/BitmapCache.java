package hakkon.android_rss_reader.filesystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hakkon on 24.03.18.
 */

public class BitmapCache {
    // 10MB cache
    private static final long CACHE_SIZE = 10485760;
    private static final String CACHE_DIR = "images/";

    /**
     * Save image to internal storage cache
     * @param context
     * @param url The url to the image
     * @param bitmap The bitmap to save
     */
    public static void saveImage(Context context, String url, Bitmap bitmap) {
        File cacheDir = new File(context.getFilesDir(), CACHE_DIR);

        // Create cache directory if it doesn't exist
        if (!cacheDir.exists())
            cacheDir.mkdirs();

        // Write image to file and compress
        try (FileOutputStream fos = new FileOutputStream(new File(cacheDir, Integer.toHexString(url.hashCode())), true)) {
            // Resize bitmap if necessary and compress
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > 400 || height > 400)
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)(width * 0.3), (int)(height * 0.3), true);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (IOException e) {
            Log.e("BitmapCache", Log.getStackTraceString(e));
        }

        // Check if cache is full
        while(sizeOfDir(cacheDir) > CACHE_SIZE)
            deleteOld(cacheDir);
    }

    /**
     * Load image from internal storage cache
     * @param context
     * @param url The url to the image
     * @return If image was found in cache, the bitmap is returned or null if not found
     */
    public static Bitmap loadImage(Context context, String url) {
        File cacheDir = new File(context.getFilesDir(), CACHE_DIR);

        File bitmapFile = new File(cacheDir, Integer.toHexString(url.hashCode()));

        if (!bitmapFile.exists())
            return null;
        else
            return BitmapFactory.decodeFile(bitmapFile.toString());
    }

    private static long sizeOfDir(File dir) {
        if (dir.listFiles() == null)
            return 0;
        
        long len = 0;
        for (File file : dir.listFiles()) {
            len += file.length();
        }
        return len;
    }

    public static void clear(Context context) {
        File cacheDir = new File(context.getFilesDir(), CACHE_DIR);
        for (File file : cacheDir.listFiles()) {
            if (file.isFile())
                file.delete();
        }
    }

    private static void deleteOld(File cacheDir) {
        File[] files = cacheDir.listFiles();

        File oldest = files[0];
        for (File file : files) {
            if (file.lastModified() < oldest.lastModified())
                oldest = file;
        }
        oldest.delete();
    }
}
