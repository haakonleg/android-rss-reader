package hakkon.android_rss_reader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import hakkon.android_rss_reader.database.Feed;
import hakkon.android_rss_reader.database.FeedItem;
import hakkon.android_rss_reader.tasks.BaseTask;
import hakkon.android_rss_reader.tasks.FeedParser;
import hakkon.android_rss_reader.tasks.GetFeeds;
import hakkon.android_rss_reader.tasks.GetItems;
import hakkon.android_rss_reader.tasks.GetRecentItems;
import hakkon.android_rss_reader.util.Messages;
import hakkon.android_rss_reader.util.NetworkState;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFeedFragment extends Fragment {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView feedList;
    private ProgressBar progressBar;
    private String feedTitle;
    private String feedUrl;
    private Boolean isHome;
    private FeedListAdapter adapter;

    public ViewFeedFragment() {
        // Required empty public constructor
    }

    public static ViewFeedFragment newInstanceFeed(String feedTitle, String feedUrl) {
        ViewFeedFragment fragment = new ViewFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_home", false);
        bundle.putString("feed_title", feedTitle);
        bundle.putString("feed_url", feedUrl);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ViewFeedFragment newInstanceHome() {
        ViewFeedFragment fragment = new ViewFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_home", true);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle;
        if (savedInstanceState != null)
            bundle = savedInstanceState;
        else
            bundle = getArguments();

        if (bundle != null) {
            this.feedTitle = bundle.getString("feed_title");
            this.feedUrl = bundle.getString("feed_url");
            this.isHome = bundle.getBoolean("is_home");
            this.adapter = new FeedListAdapter(getActivity(), new FeedListListener());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("feed_title", this.feedTitle);
        outState.putString("feed_url", this.feedUrl);
        outState.putBoolean("is_home", this.isHome);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_feed, container, false);

        // Find elements
        this.refreshLayout = view.findViewById(R.id.feed_refresh_layout);
        this.progressBar = view.findViewById(R.id.feed_progressbar);
        this.feedList = view.findViewById(R.id.feed_recycler_list);

        // Swipe to refresh
        this.refreshLayout.setOnRefreshListener(new ManualRefresh());

        // Set up recyclerview
        this.feedList.setAdapter(this.adapter);
        this.feedList.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get articles from cache
        BaseTask fetchTask;
        BaseTask.TaskCallback<List<FeedItem>> callback = (error, items) -> {
            this.adapter.setItems(items);
            this.progressBar.setVisibility(View.GONE);
        };

        if (this.isHome) {
            ((HomeActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
            fetchTask = new GetRecentItems(getActivity(), callback);
        } else {
            ((HomeActivity)getActivity()).getSupportActionBar().setTitle(this.feedTitle);
            fetchTask = new GetItems(getActivity(), this.feedUrl, callback);
        }
        ThreadPool.getInstance().execute(fetchTask);
    }

    // Listener for when an article is clicked
    private class FeedListListener implements FeedListAdapter.OnItemClicked {
        @Override
        public void onClick(FeedItem article) {
            ViewArticleFragment fragment =
                    ViewArticleFragment.newInstance(article);
            ((HomeActivity)getActivity()).displayContent(fragment, "ViewArticle");
        }
    }

    private class ManualRefresh implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            // Check connection first
            if (!NetworkState.hasNetwork(getActivity())) {
                Messages.showToast(getActivity(), "No network connection");
                refreshLayout.setRefreshing(false);
                return;
            }

            // If is home, refresh all feeds
            if (isHome) {
                List<Feed> feeds = ((HomeActivity)getActivity()).navAdapter.getFeeds();
                List<FeedItem> updatedItems = new ArrayList<>();

                for(ListIterator<Feed> iter = feeds.listIterator(); iter.hasNext();) {
                    int index = iter.nextIndex();
                    Feed feed = iter.next();

                    FeedParser updateTask = new FeedParser(getActivity(), feed.getOriginLink(), (error1, result) -> {
                        updatedItems.addAll(result.items);

                        // Run when all feeds are updated
                        if (index == feeds.size() - 1) {
                            if (updatedItems.size() > 0)
                                adapter.addItems(updatedItems);

                            Messages.showToast(getActivity(), "New items: " + Integer.toString(updatedItems.size()));
                            refreshLayout.setRefreshing(false);
                        }
                    });
                    ThreadPool.getInstance().execute(updateTask);
                }

                // Else refresh only this feed
            } else {
                FeedParser updateTask = new FeedParser(getActivity(), feedUrl, (error, result) -> {
                    if (result.items.size() > 0)
                        adapter.addItems(result.items);

                    Messages.showToast(getActivity(), "New items: " + Integer.toString(result.items.size()));
                    refreshLayout.setRefreshing(false);
                });
                ThreadPool.getInstance().execute(updateTask);
            }

            // Scroll to top
            feedList.scrollToPosition(0);
        }
    }
}
