package hakkon.android_rss_reader;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.List;

import hakkon.android_rss_reader.database.FeedItem;
import hakkon.android_rss_reader.tasks.GetBitmap;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.ViewHolder> {
    private Activity activity;
    private List<FeedItem> items;
    private OnItemClicked listener;

    public FeedListAdapter(Activity activity, List<FeedItem> items, OnItemClicked listener) {
        super();
        this.activity = activity;
        this.items = items;
        this.listener = listener;
    }

    public FeedItem getItem(int position) {
        return items.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.feed_item_view, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FeedItem item = this.items.get(position);

        // Set image
        String imgUrl = item.getImage();
        if (imgUrl != null) {
            GetBitmap bitmapTask = new GetBitmap(this.activity, imgUrl, (error, bitmap) -> {
                if (bitmap != null) {
                    holder.feedImg.setVisibility(View.VISIBLE);
                    holder.feedImg.setImageBitmap(bitmap);
                } else {
                    holder.feedImg.setVisibility(View.INVISIBLE);
                }
            });
            ThreadPool.getInstance().execute(bitmapTask);
        } else {
            holder.feedImg.setVisibility(View.INVISIBLE);
        }

        holder.titleTxt.setText(item.getTitle());


        // Determine what to display as description
        String desc = "No description";
        if (item.getDescription() != null) {
            desc = item.getDescription();
        } else if (item.getEncodedContent() != null) {
            desc = item.getEncodedContent();
        }

        // Don't display the whole description
        if (desc.length() > 80) {
            desc = desc.substring(0, 80) + "...";
        }

        holder.descTxt.setText(Jsoup.clean(desc, Whitelist.none()));
        holder.parentTxt.setText(item.getParentTitle());
        holder.updatedTxt.setText(item.getAge());
        holder.card.setOnClickListener((v) -> this.listener.onClick(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        ImageView feedImg;
        TextView titleTxt;
        TextView descTxt;
        TextView parentTxt;
        TextView updatedTxt;

        ViewHolder(View view) {
            super(view);
            card = view.findViewById(R.id.feed_item_card);
            feedImg = view.findViewById(R.id.feed_item_img);
            titleTxt = view.findViewById(R.id.feed_item_title);
            descTxt = view.findViewById(R.id.feed_item_desc);
            parentTxt = view.findViewById(R.id.feed_item_parent);
            updatedTxt = view.findViewById(R.id.feed_item_updated);
        }
    }

    public interface OnItemClicked {
        void onClick(int position);
    }
}
