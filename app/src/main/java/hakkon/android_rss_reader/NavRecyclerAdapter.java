package hakkon.android_rss_reader;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hakkon.android_rss_reader.database.Feed;
import hakkon.android_rss_reader.tasks.GetBitmap;

public class NavRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FEED = 2;
    private static final int TYPE_FOOTER = 3;

    Activity activity;
    private List<Feed> items;
    private OnItemClicked listener;

    public NavRecyclerAdapter(Activity activity, OnItemClicked listener) {
        super();
        this.activity = activity;
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    public void addFeed(Feed item) {
        this.items.add(item);
        this.notifyItemInserted(getItemCount() - 1);
    }

    public List<Feed> getFeeds() {
        return this.items;
    }

    public Feed getFeed(int position) {
        return this.items.get(position);
    }

    public boolean hasFeed(String feedUrl) {
        for(Feed item : this.items) {
            if (feedUrl.equals(item.getOriginLink()))
                return true;
        }
        return false;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.activity);

        switch (viewType) {
            case TYPE_HEADER:
                View headerView = inflater.inflate(R.layout.nav_header_view, parent, false);
                return new ViewHolderHeader(headerView);
            case TYPE_FOOTER:
                View footerView = inflater.inflate(R.layout.nav_footer_view, parent, false);
                return new ViewHolderFooter(footerView);
            case TYPE_FEED:
                View feedView = inflater.inflate(R.layout.nav_item_view, parent, false);
                return new ViewHolderFeed(feedView);
            default:
                Log.e("NavRecyclerAdapter", "Got invalid viewType " + Integer.toString(viewType));
                return new ViewHolderFeed(null);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();

        switch (viewType) {
            case TYPE_FEED:
                bindFeed((ViewHolderFeed) holder, this.items.get(position - 1));
                break;
        }
    }

    private void bindFeed(ViewHolderFeed holder, Feed item) {
        String imageUrl = item.getImage();
        holder.feedImg.setImageDrawable(this.activity.getDrawable(R.drawable.ic_rss_feed_24dp));

        if (imageUrl != null) {
            GetBitmap bitmapTask = new GetBitmap(this.activity, imageUrl, (error, bitmap) -> {
                if (bitmap != null)
                    holder.feedImg.setImageBitmap(bitmap);
            });
            ThreadPool.getInstance().execute(bitmapTask);
        }

        holder.titleTxt.setText(item.getTitle());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        else if (position == getItemCount() - 1)
            return TYPE_FOOTER;
        else
            return TYPE_FEED;
    }

    // Items + header + footer
    @Override
    public int getItemCount() {
        return items.size() + 2;
    }

    class ViewHolderHeader extends RecyclerView.ViewHolder {
        View view;
        Button homeBtn;

        ViewHolderHeader(View view) {
            super(view);
            this.view = view;
            homeBtn = view.findViewById(R.id.nav_home_btn);

            homeBtn.setOnClickListener(v -> listener.onClickButton(homeBtn.getId()));
        }
    }

    class ViewHolderFooter extends RecyclerView.ViewHolder {
        View view;
        Button manageBtn;
        Button settingsBtn;

        ViewHolderFooter(View view) {
            super(view);
            this.view = view;
            manageBtn = view.findViewById(R.id.nav_manage_btn);
            settingsBtn = view.findViewById(R.id.nav_settings_btn);

            manageBtn.setOnClickListener(v -> listener.onClickButton(manageBtn.getId()));
            settingsBtn.setOnClickListener(v -> listener.onClickButton(settingsBtn.getId()));
        }
    }

    class ViewHolderFeed extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        View view;
        ImageView feedImg;
        TextView titleTxt;

        ViewHolderFeed(View view) {
            super(view);
            this.view = view;
            feedImg = view.findViewById(R.id.icon_img);
            titleTxt = view.findViewById(R.id.title_txt);

            this.view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(v -> listener.onClickFeed(items.get(getAdapterPosition() - 1)));
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.nav_feed_context_menu, menu);

            menu.findItem(R.id.feed_copy_url).setOnMenuItemClickListener(this);
            menu.findItem(R.id.feed_rename).setOnMenuItemClickListener(this);
            menu.findItem(R.id.feed_unsub).setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            listener.onContextItemSelected(items.get(getAdapterPosition() - 1), item);
            return true;
        }
    }

    public interface OnItemClicked {
        void onClickButton(int button);
        void onClickFeed(Feed feed);
        void onContextItemSelected(Feed feed, MenuItem item);
    }
}
