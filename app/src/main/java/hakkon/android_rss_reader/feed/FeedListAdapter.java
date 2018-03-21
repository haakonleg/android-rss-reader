package hakkon.android_rss_reader.feed;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hakkon.android_rss_reader.R;

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

        // TODO: Load image

        holder.titleTxt.setText(item.getTitle());

        // Don't display the whole description
        String desc = item.getDescription();
        if (desc.length() > 80) {
            desc = desc.substring(0, 80) + "...";
        }

        holder.descTxt.setText(desc);
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
        TextView updatedTxt;

        ViewHolder(View view) {
            super(view);
            card = view.findViewById(R.id.feed_item_card);
            feedImg = view.findViewById(R.id.feed_item_img);
            titleTxt = view.findViewById(R.id.feed_item_title);
            descTxt = view.findViewById(R.id.feed_item_desc);
            updatedTxt = view.findViewById(R.id.feed_item_updated);
        }
    }

    public interface OnItemClicked {
        void onClick(int position);
    }
}
