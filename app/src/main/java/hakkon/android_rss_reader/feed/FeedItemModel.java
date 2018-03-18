package hakkon.android_rss_reader.feed;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hakkon on 12.03.18.
 */

public class FeedItemModel implements Parcelable {
    // RFC 822
    private static final SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.getDefault());

    private String title;
    private String link;
    private String description;
    private String author;
    private long date;

    public FeedItemModel() {
        this.title = "No title";
        this.link = "";
        this.description = "No description";
        this.author = "No author";
        this.date = -1;
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

    public void setDate(String unformatted) {
        Date date = null;
        try {
            date = sdf.parse(unformatted);
        } catch (ParseException e) {
            Log.e("FeedItemModel", Log.getStackTraceString(e));
        }
        if (date != null) {
            this.date = date.getTime();
        }
    }

    private FeedItemModel(Parcel in) {
        this.title = in.readString();
        this.link = in.readString();
        this.description = in.readString();
        this.author = in.readString();
        this.date = in.readLong();
    }

    public static final Creator<FeedItemModel> CREATOR = new Creator<FeedItemModel>() {
        @Override
        public FeedItemModel createFromParcel(Parcel source) {
            return new FeedItemModel(source);
        }

        @Override
        public FeedItemModel[] newArray(int size) {
            return new FeedItemModel[0];
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
    }
}
