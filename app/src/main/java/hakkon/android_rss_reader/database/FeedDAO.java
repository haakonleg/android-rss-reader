package hakkon.android_rss_reader.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by hakkon on 21.03.18.
 */

@Dao
public interface FeedDAO {
    @Query("SELECT * FROM Feed")
    List<Feed> getFeeds();

    @Query("SELECT * FROM Feed WHERE feed_origin = :link")
    Feed getFeed(String link);

    @Insert
    void insertFeed(Feed feed);

    @Insert
    void insertFeeds(Feed... feeds);

    @Delete
    void deleteFeed(Feed feed);

    @Update
    void updateFeed(Feed feed);
}
