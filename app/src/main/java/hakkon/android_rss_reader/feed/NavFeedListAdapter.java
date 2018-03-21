package hakkon.android_rss_reader.feed;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hakkon.android_rss_reader.R;
import hakkon.android_rss_reader.ThreadPool;
import hakkon.android_rss_reader.tasks.GetBitmap;

public class NavFeedListAdapter extends RecyclerView.Adapter<NavFeedListAdapter.ViewHolder> {
    Activity activity;
    private List<Feed> items;
    private OnItemClicked listener;

    public NavFeedListAdapter(Activity activity, OnItemClicked listener) {
        super();
        this.activity = activity;
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    public void addItem(Feed item) {
        this.items.add(item);
        this.notifyItemInserted(this.items.size() - 1);
    }

    public Feed getItem(int position) {
        return this.items.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.nav_item_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Feed item = this.items.get(position);

        String imageUrl = item.getImage();
        if (!imageUrl.isEmpty()) {
            GetBitmap bitmapTask = new GetBitmap(this.activity, imageUrl, (error, bitmap) -> {
                if (bitmap != null)
                    holder.feedImg.setImageBitmap(bitmap);
                else
                    holder.feedImg.setImageDrawable(this.activity.getDrawable(R.drawable.ic_rss_feed_24dp));
            });
            ThreadPool.getInstance().execute(bitmapTask);
        }

        holder.titleTxt.setText(item.getTitle());
        holder.view.setOnClickListener((v) -> this.listener.onClick(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView feedImg;
        TextView titleTxt;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            feedImg = view.findViewById(R.id.icon_img);
            titleTxt = view.findViewById(R.id.title_txt);
        }
    }

    public interface OnItemClicked {
        void onClick(int position);
    }
}
