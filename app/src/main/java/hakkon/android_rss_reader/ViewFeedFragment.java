package hakkon.android_rss_reader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import hakkon.android_rss_reader.feed.FeedItem;
import hakkon.android_rss_reader.feed.FeedListAdapter;
import hakkon.android_rss_reader.feed.Feed;
import hakkon.android_rss_reader.tasks.GetItems;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFeedFragment extends Fragment {
    private RecyclerView feedList;
    private Feed feed;
    private FeedListAdapter adapter;

    public ViewFeedFragment() {
        // Required empty public constructor
    }

    public static ViewFeedFragment newInstance(Feed feed) {
        ViewFeedFragment fragment = new ViewFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("feed", feed);
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
            this.feed = bundle.getParcelable("feed");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("feed", this.feed);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_feed, container, false);

        // Set up recyclerview
        this.feedList = view.findViewById(R.id.feed_recycler_list);
        GetItems task = new GetItems(getActivity(), this.feed.getOriginLink(), (error, items) -> {
            this.adapter = new FeedListAdapter(items, new LoadImage(), new FeedListListener());
            this.feedList.setAdapter(this.adapter);
            this.feedList.setHasFixedSize(true);
        });
        ThreadPool.getInstance().execute(task);

        // Set actionbar title
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle(feed.getTitle());

        return view;
    }

    // Listener for when image needs to be loaded
    private class LoadImage implements FeedListAdapter.ImageLoader {
        @Override
        public void loadThis(ImageView view) {

        }
    }

    // Listener for when an article is clicked
    private class FeedListListener implements FeedListAdapter.OnItemClicked {
        @Override
        public void onClick(int position) {
            FeedItem article = adapter.getItem(position);
            ViewArticleFragment fragment =
                    ViewArticleFragment.newInstance(feed.getTitle(), article);
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
