package hakkon.android_rss_reader;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.util.LinkedList;
import java.util.List;

import hakkon.android_rss_reader.database.FeedItem;
import hakkon.android_rss_reader.tasks.GetBitmap;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.ViewHolder> {
    private Activity activity;
    private LinkedList<FeedItem> items;
    private OnItemClicked listener;

    public FeedListAdapter(Activity activity, OnItemClicked listener) {
        super();
        this.activity = activity;
        this.listener = listener;
    }

    public void setItems(List<FeedItem> items) {
        this.items = new LinkedList<>();
        this.items.addAll(items);
        this.notifyDataSetChanged();
    }

    public void addItems(List<FeedItem> items) {
        this.items.addAll(0, items);
        this.notifyDataSetChanged();
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
        holder.feedImg.setVisibility(View.INVISIBLE);

        if (imgUrl != null) {
            GetBitmap bitmapTask = new GetBitmap(this.activity, imgUrl, (error, bitmap) -> {
                if (bitmap != null) {
                    holder.feedImg.setVisibility(View.VISIBLE);
                    holder.feedImg.setImageBitmap(bitmap);
                }
            });
            ThreadPool.getInstance().execute(bitmapTask);
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
    }

    @Override
    public int getItemCount() {
        if (this.items == null)
            return 0;
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

            this.card.setOnClickListener(v -> listener.onClick(items.get(getAdapterPosition())));
        }
    }

    public interface OnItemClicked {
        void onClick(FeedItem item);
    }
}
