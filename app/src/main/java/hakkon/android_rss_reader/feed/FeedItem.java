package hakkon.android_rss_reader.feed;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by hakkon on 12.03.18.
 */

@Entity(primaryKeys = {"item_parent", "item_link"})
public class FeedItem implements Parcelable {
    @NonNull
    @ColumnInfo(name = "item_parent")
    @ForeignKey(entity = Feed.class, parentColumns = "feed_origin", childColumns = "item_parent")
    private String parentFeed;

    @ColumnInfo(name = "item_title")
    private String title;

    @NonNull
    @ColumnInfo(name = "item_link")
    private String link;

    @ColumnInfo(name = "item_desc")
    private String description;

    @ColumnInfo(name = "item_author")
    private String author;

    @ColumnInfo(name = "item_date")
    private long date;

    @ColumnInfo(name = "item_encoded")
    private String encodedContent;

    public FeedItem() {
        this.title = "No title";
        this.link = "";
        this.description = "No description";
        this.author = "No author";
        this.date = -1;
        this.encodedContent = "";
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) { this.date = date; }

    public String getEncodedContent() {
        return encodedContent;
    }

    public void setEncodedContent(String encodedContent) {
        this.encodedContent = encodedContent;
    }

    public String getParentFeed() {
        return parentFeed;
    }

    public void setParentFeed(String parentFeed) {
        this.parentFeed = parentFeed;
    }

    private FeedItem(Parcel in) {
        this.title = in.readString();
        this.link = in.readString();
        this.description = in.readString();
        this.author = in.readString();
        this.date = in.readLong();
        this.encodedContent = in.readString();
    }

    public static final Creator<FeedItem> CREATOR = new Creator<FeedItem>() {
        @Override
        public FeedItem createFromParcel(Parcel source) {
            return new FeedItem(source);
        }

        @Override
        public FeedItem[] newArray(int size) {
            return new FeedItem[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.link);
        dest.writeString(this.description);
        dest.writeString(this.author);
        dest.writeLong(this.date);
        dest.writeString(this.encodedContent);
    }
}
