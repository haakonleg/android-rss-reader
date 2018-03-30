package hakkon.android_rss_reader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import hakkon.android_rss_reader.database.Feed;
import hakkon.android_rss_reader.database.FeedItem;
import hakkon.android_rss_reader.tasks.FeedParser;
import hakkon.android_rss_reader.tasks.GetItems;
import hakkon.android_rss_reader.util.Messages;
import hakkon.android_rss_reader.util.NetworkState;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFeedFragment extends Fragment {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView feedList;
    private ProgressBar progressBar;

    private String title;
    private ArrayList<String> feedUrls;

    private FeedListAdapter adapter;

    private ImageView refreshBtn;
    private Animation refreshAnim;

    public ViewFeedFragment() {
        // Required empty public constructor
    }

    // For displaying multiple feeds
    public static ViewFeedFragment newInstance(String title, ArrayList<String> feedUrls) {
        ViewFeedFragment fragment = new ViewFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putStringArrayList("feed_urls", feedUrls);
        fragment.setArguments(bundle);
        return fragment;
    }

    // For displaying single feed
    public static ViewFeedFragment newInstance(String title, String feedUrl) {
        ViewFeedFragment fragment = new ViewFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        ArrayList<String> feedUrls = new ArrayList<>();
        feedUrls.add(feedUrl);
        bundle.putStringArrayList("feed_urls", feedUrls);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle;
        if (savedInstanceState != null)
            bundle = savedInstanceState;
        else
            bundle = getArguments();

        if (bundle != null) {
            this.title = bundle.getString("title");
            this.feedUrls = bundle.getStringArrayList("feed_urls");
            this.adapter = new FeedListAdapter(getActivity(), new FeedListListener());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", this.title);
        outState.putStringArrayList("feed_urls", this.feedUrls);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_feed, container, false);

        // Find elements
        this.refreshLayout = view.findViewById(R.id.feed_refresh_layout);
        this.progressBar = view.findViewById(R.id.feed_progressbar);
        this.feedList = view.findViewById(R.id.feed_recycler_list);

        // Set up recyclerview
        this.feedList.setAdapter(this.adapter);
        this.feedList.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get articles from cache
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle(this.title);
        GetItems fetchTask = new GetItems(getActivity(), this.feedUrls, (error, items) -> {
            this.adapter.setItems(items);
            this.progressBar.setVisibility(View.GONE);
        });
        ThreadPool.getInstance().execute(fetchTask);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar, menu);

        // Set up refresh button
        this.refreshBtn = (ImageView) menu.findItem(R.id.action_bar_refresh).getActionView();
        this.refreshBtn.setImageResource(R.drawable.ic_refresh_24dp);

        this.refreshAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);
        this.refreshAnim.setRepeatCount(Animation.INFINITE);

        // Refresh button
        this.refreshBtn.setOnClickListener(v -> manualRefresh());
        // Swipe to refresh
        this.refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            manualRefresh();
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void manualRefresh() {
        this.refreshBtn.startAnimation(this.refreshAnim);

        // Check connection first
        if (!NetworkState.hasNetwork(getActivity())) {
            Messages.showToast(getActivity(), "No network connection");
            this.refreshAnim.cancel();
            return;
        }

        // Refresh all feeds
        List<FeedItem> updatedItems = new ArrayList<>();

        // Loop through feeds
        for(ListIterator<String> iter = this.feedUrls.listIterator(); iter.hasNext();) {
            int index = iter.nextIndex();
            String feedUrl = iter.next();

            FeedParser updateTask = new FeedParser(getActivity(), feedUrl, (error1, result) -> {
                updatedItems.addAll(result.items);

                // Run when all feeds are updated
                if (index == this.feedUrls.size() - 1) {
                    if (updatedItems.size() > 0)
                        adapter.addItems(updatedItems);

                    Messages.showToast(getActivity(), "Updated articles: " + Integer.toString(updatedItems.size()));
                    this.refreshAnim.cancel();
                    feedList.scrollToPosition(0);
                }
            });
            ThreadPool.getInstance().execute(updateTask);
        }
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
}
