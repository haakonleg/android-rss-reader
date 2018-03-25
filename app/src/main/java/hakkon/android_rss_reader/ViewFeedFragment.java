package hakkon.android_rss_reader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hakkon.android_rss_reader.database.FeedItem;
import hakkon.android_rss_reader.tasks.GetItems;
import hakkon.android_rss_reader.tasks.GetRecentItems;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFeedFragment extends Fragment {
    private RecyclerView feedList;
    private String feedUrl;
    private Boolean isHome;
    private FeedListAdapter adapter;

    public ViewFeedFragment() {
        // Required empty public constructor
    }

    public static ViewFeedFragment newInstanceFeed(String feedUrl) {
        ViewFeedFragment fragment = new ViewFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("is_home", false);
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
            this.feedUrl = bundle.getString("feed_url");
            this.isHome = bundle.getBoolean("is_home");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("feed_url", this.feedUrl);
        outState.putBoolean("is_home", this.isHome);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_feed, container, false);

        // Set up recyclerview
        this.feedList = view.findViewById(R.id.feed_recycler_list);
        if (this.isHome) {
            GetRecentItems recentItems = new GetRecentItems(getActivity(), (error, items) -> {
                this.adapter = new FeedListAdapter(getActivity(), items, new FeedListListener());
                this.feedList.setAdapter(this.adapter);
            });
            ThreadPool.getInstance().execute(recentItems);
        } else {
            GetItems feedItems = new GetItems(getActivity(), this.feedUrl, (error, items) -> {
                this.adapter = new FeedListAdapter(getActivity(), items, new FeedListListener());
                this.feedList.setAdapter(this.adapter);

                // Set actionbar title
                ((HomeActivity)getActivity()).getSupportActionBar().setTitle(this.adapter.getItem(0).getParentTitle());
            });
            ThreadPool.getInstance().execute(feedItems);
        }
        this.feedList.setHasFixedSize(true);

        return view;
    }

    // Listener for when an article is clicked
    private class FeedListListener implements FeedListAdapter.OnItemClicked {
        @Override
        public void onClick(int position) {
            FeedItem article = adapter.getItem(position);
            ViewArticleFragment fragment =
                    ViewArticleFragment.newInstance(article);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right,
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right)
                    .replace(R.id.content_layout, fragment)
                    .addToBackStack("ViewArticle").commit();
        }
    }
}
