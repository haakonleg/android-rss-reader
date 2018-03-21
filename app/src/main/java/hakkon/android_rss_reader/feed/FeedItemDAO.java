package hakkon.android_rss_reader.feed;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by hakkon on 21.03.18.
 */

@Dao
public interface FeedItemDAO {
    @Query("SELECT * FROM FeedItem WHERE item_parent = :parent ORDER BY item_date DESC")
    List<FeedItem> getItems(String parent);

    @Query("SELECT * FROM FeedItem ORDER BY item_date DESC LIMIT :limit")
    List<FeedItem> getRecentItems(int limit);

    @Query("SELECT item_date FROM FeedItem WHERE item_parent = :parent ORDER BY item_date DESC LIMIT 1")
    long getNewestItem(String parent);

    @Insert
    void insertItems(List<FeedItem> items);

    @Delete
    void deleteItem(FeedItem item);
}
