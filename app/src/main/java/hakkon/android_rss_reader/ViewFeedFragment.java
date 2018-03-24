package hakkon.android_rss_reader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hakkon.android_rss_reader.database.FeedItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFeedFragment extends Fragment {
    private RecyclerView feedList;
    private String feedTitle;
    private ArrayList<FeedItem> items;
    private FeedListAdapter adapter;

    public ViewFeedFragment() {
        // Required empty public constructor
    }

    public static ViewFeedFragment newInstance(String title, List<FeedItem> items) {
        ViewFeedFragment fragment = new ViewFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString("feed_title",title);
        ArrayList<FeedItem> newList = new ArrayList<>(items.size());
        newList.addAll(items);
        bundle.putParcelableArrayList("feed_items", newList);
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
            this.items = bundle.getParcelableArrayList("feed_items");
            this.adapter = new FeedListAdapter(getActivity(), this.items, new FeedListListener());
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("feed_title", this.feedTitle);
        outState.putParcelableArrayList("feed_items", this.items);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_feed, container, false);

        // Set up recyclerview
        this.feedList = view.findViewById(R.id.feed_recycler_list);
        this.feedList.setAdapter(this.adapter);
        this.feedList.setHasFixedSize(true);

        // Set actionbar title
        ((HomeActivity)getActivity()).getSupportActionBar().setTitle(this.feedTitle);

        return view;
    }

    // Listener for when an article is clicked
    private class FeedListListener implements FeedListAdapter.OnItemClicked {
        @Override
        public void onClick(int position) {
            FeedItem article = adapter.getItem(position);
            ViewArticleFragment fragment =
                    ViewArticleFragment.newInstance(feedTitle, article);
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
