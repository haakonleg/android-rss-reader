package hakkon.android_rss_reader.feed;

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

public class NavFeedListAdapter extends RecyclerView.Adapter<NavFeedListAdapter.ViewHolder> {
    private List<FeedModel> items;
    private OnItemClicked listener;

    public NavFeedListAdapter() {
        super();
        this.items = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClicked listener) {
        this.listener = listener;
    }

    public void addItem(FeedModel item) {
        this.items.add(item);
        this.notifyItemInserted(this.items.size() - 1);
    }

    public FeedModel getItem(int position) {
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
        FeedModel item = this.items.get(position);

        // TODO: Add image

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
