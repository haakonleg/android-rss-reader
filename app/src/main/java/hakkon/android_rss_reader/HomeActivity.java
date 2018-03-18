package hakkon.android_rss_reader;

import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import hakkon.android_rss_reader.feed.FeedModel;
import hakkon.android_rss_reader.feed.NavFeedListAdapter;
import hakkon.android_rss_reader.tasks.FeedParser;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private RecyclerView feedsList;
    private NavFeedListAdapter feedsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        // Find elements
        this.drawerLayout = findViewById(R.id.home_drawer_layout);
        this.feedsList = findViewById(R.id.nav_feeds_list);

        // Set up nav feed list
        this.feedsListAdapter = new NavFeedListAdapter();
        this.feedsListAdapter.setOnItemClickListener(new FeedsListListener());
        this.feedsList.setAdapter(this.feedsListAdapter);

        FeedParser parser = new FeedParser(this, "https://resett.no/feed/", (error, feed) -> {
            this.feedsListAdapter.addItem(feed);
        });
        ThreadPool.getInstance().execute(parser);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FeedsListListener implements NavFeedListAdapter.OnItemClicked {
        @Override
        public void onClick(int position) {
            Log.e("LISTITEM", Integer.toString(position));
            FeedModel feed = feedsListAdapter.getItem(position);

            ViewFeedFragment fragment = ViewFeedFragment.newInstance(feed.getTitle(), feed.getItems());
            getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, fragment).commit();
        }
    }
}
