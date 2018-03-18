package hakkon.android_rss_reader;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import hakkon.android_rss_reader.feed.FeedItemModel;
import hakkon.android_rss_reader.feed.FeedListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFeedFragment extends Fragment {
    private String feedName;
    private RecyclerView feedList;
    private FeedListAdapter adapter;

    public ViewFeedFragment() {
        // Required empty public constructor
    }

    public static ViewFeedFragment newInstance(String feedName, ArrayList<FeedItemModel> items) {
        ViewFeedFragment fragment = new ViewFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString("feed_name", feedName);
        bundle.putParcelableArrayList("feed_items", items);
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
            this.feedName = bundle.getString("feed_name");
            this.adapter = new FeedListAdapter(bundle.getParcelableArrayList("feed_items"), new LoadImage());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_feed, container, false);

        // Set up recyclerview
        this.feedList = view.findViewById(R.id.feed_recycler_list);
        this.feedList.setAdapter(this.adapter);
        this.feedList.setHasFixedSize(true);

        // Set title on actionbar
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle(this.feedName);
        return view;
    }

    private class LoadImage implements FeedListAdapter.ImageLoader {
        @Override
        public void loadThis(ImageView view) {

        }
    }
}
