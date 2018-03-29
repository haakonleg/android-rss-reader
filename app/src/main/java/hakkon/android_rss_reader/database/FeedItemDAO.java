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
public interface FeedItemDAO {
    @Query("SELECT * FROM FeedItem WHERE item_parent = :parent AND item_link = :link")
    FeedItem getItem(String parent, String link);

    @Query("SELECT * FROM FeedItem WHERE item_parent = :parent ORDER BY item_date DESC")
    List<FeedItem> getItems(String parent);

    @Query("SELECT * FROM FeedItem ORDER BY item_date DESC LIMIT :limit")
    List<FeedItem> getRecentItems(int limit);

    @Query("SELECT item_date FROM FeedItem WHERE item_parent = :parent ORDER BY item_date DESC LIMIT 1")
    long getNewestItem(String parent);

    @Query("SELECT COUNT(*) FROM FeedItem WHERE item_parent = :parent")
    int getCount(String parent);

    @Query("DELETE FROM FeedItem WHERE item_parent = :parent AND item_date IN" +
            "(SELECT item_date FROM FeedItem WHERE item_parent = :parent ORDER BY item_date ASC LIMIT 0,:count)")
    void deleteOldest(String parent, int count);

    @Query("DELETE FROM FeedItem")
    void deleteAll();

    @Insert
    void insertItems(List<FeedItem> items);

    @Update
    void updateItems(List<FeedItem> items);

    @Delete
    void deleteItem(FeedItem item);

}
