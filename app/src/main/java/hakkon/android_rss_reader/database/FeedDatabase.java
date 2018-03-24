package hakkon.android_rss_reader.database;

import android.arch.persistence.room.*;

/**
 * Created by hakkon on 21.03.18.
 */

@android.arch.persistence.room.Database(entities = {Feed.class, FeedItem.class}, version = 1)
public abstract class FeedDatabase extends RoomDatabase {
    public abstract FeedDAO feedDao();
    public abstract FeedItemDAO feedItemDAO();
}