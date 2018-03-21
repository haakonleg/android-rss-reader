package hakkon.android_rss_reader.database;

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * Created by hakkon on 21.03.18.
 */

public class Database {
    private static FeedDatabase singleInstance;

    public static FeedDatabase getInstance(Context context) {
        if (singleInstance == null) {
            singleInstance = Room.databaseBuilder(context, FeedDatabase.class, "FeedDatabase").build();
        }
        return singleInstance;
    }
}
