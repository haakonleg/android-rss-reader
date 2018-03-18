package hakkon.android_rss_reader.feed;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by hakkon on 12.03.18.
 */

public class FeedModel implements Parcelable {
    private String title;
    private String link;
    private String description;
    private String image;

    private ArrayList<FeedItemModel> feedItemModels;

    public FeedModel() {
        this.title = "No title";
        this.link = "";
        this.description = "No description";
        this.image = "";
        this.feedItemModels = new ArrayList<>();
    }

    public void addItem(FeedItemModel item) {
        this.feedItemModels.add(item);
    }

    public ArrayList<FeedItemModel> getItems() {
        return this.feedItemModels;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.link);
        dest.writeString(this.description);
        dest.writeString(this.image);
        dest.writeList(this.feedItemModels);
    }

    private FeedModel(Parcel in) {
        this.title = in.readString();
        this.link = in.readString();
        this.description = in.readString();
        this.image = in.readString();
        this.feedItemModels = in.readArrayList(FeedItemModel.class.getClassLoader());
    }

    private static final Creator<FeedModel> CREATOR = new Creator<FeedModel>() {
        @Override
        public FeedModel createFromParcel(Parcel source) {
            return new FeedModel(source);
        }

        @Override
        public FeedModel[] newArray(int size) {
            return new FeedModel[0];
        }
    };
}
