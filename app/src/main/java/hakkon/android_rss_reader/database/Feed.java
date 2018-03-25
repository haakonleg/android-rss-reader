package hakkon.android_rss_reader.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by hakkon on 12.03.18.
 */

@Entity
public class Feed {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "feed_origin")
    private String originLink;

    @ColumnInfo(name = "feed_position")
    private int position;

    @ColumnInfo(name = "feed_title")
    private String title;

    @ColumnInfo(name = "feed_link")
    private String link;

    @ColumnInfo(name = "feed_desc")
    private String description;

    @ColumnInfo(name = "feed_img")
    private String image;

    @ColumnInfo(name = "feed_updated")
    private long updated;

    //private ArrayList<FeedItem> feedItems;

    public Feed() {
        this.originLink = "";
        this.title = "No title";
        this.link = "";
        this.description = "No description";
        this.image = "";
        this.updated = -1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    @NonNull
    public String getOriginLink() {
        return originLink;
    }

    public void setOriginLink(@NonNull String originLink) {
        this.originLink = originLink;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
