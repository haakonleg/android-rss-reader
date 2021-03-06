package hakkon.android_rss_reader.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by hakkon on 12.03.18.
 */

@Entity
public class FeedItem implements Parcelable {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.getDefault());

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "item_parent")
    @ForeignKey(entity = Feed.class, parentColumns = "feed_origin", childColumns = "item_parent")
    private String parentFeed;

    @ColumnInfo(name = "item_parent_title")
    @ForeignKey(entity = Feed.class, parentColumns = "feed_title", childColumns = "item_parent_title")
    private String parentTitle;

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

    @ColumnInfo(name = "item_img")
    private String image;

    public FeedItem() { }

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

    public String getParentTitle() {
        return parentTitle;
    }

    public void setParentTitle(String parentTitle) {
        this.parentTitle = parentTitle;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAge() {
        long curr = System.currentTimeMillis();
        int min = (int)((curr - this.date) / (1000*60));

        if (min < 60) {
            return Integer.toString(min) + " minutes ago";
        }
        else if (min < 24*60)
            return Integer.toString(min / 60) + " hours ago";
        else
            return Integer.toString(min / (60*24)) + " days ago";
    }

    public String getFormattedDate() {
        return sdf.format(this.date);
    }

    private FeedItem(Parcel in) {
        this.parentFeed = in.readString();
        this.parentTitle = in.readString();
        this.title = in.readString();
        this.link = in.readString();
        this.description = in.readString();
        this.author = in.readString();
        this.date = in.readLong();
        this.encodedContent = in.readString();
        this.image = in.readString();
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
        dest.writeString(this.parentFeed);
        dest.writeString(this.parentTitle);
        dest.writeString(this.title);
        dest.writeString(this.link);
        dest.writeString(this.description);
        dest.writeString(this.author);
        dest.writeLong(this.date);
        dest.writeString(this.encodedContent);
        dest.writeString(this.image);
    }
}
