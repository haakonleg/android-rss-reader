package hakkon.android_rss_reader;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.ListIterator;

import hakkon.android_rss_reader.database.Feed;
import hakkon.android_rss_reader.tasks.DeleteFeed;
import hakkon.android_rss_reader.tasks.FeedParser;
import hakkon.android_rss_reader.tasks.GetFeeds;
import hakkon.android_rss_reader.tasks.UpdateFeed;
import hakkon.android_rss_reader.util.Messages;
import hakkon.android_rss_reader.util.NetworkState;

public class HomeActivity extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    private RecyclerView feedsList;
    private NavRecyclerAdapter navAdapter;
    private FloatingActionButton addFeedBtn;
    private ProgressBar syncProgress;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set default prefs and theme
        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
        String theme = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("selected_theme", getString(R.string.pref_theme_default));
        if (theme.equals("dark"))
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find elements
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.drawerLayout = findViewById(R.id.home_drawer_layout);
        this.feedsList = findViewById(R.id.nav_feeds_list);
        this.addFeedBtn = findViewById(R.id.nav_add_feed_btn);
        this.syncProgress = findViewById(R.id.sync_progressbar);

        // Set up actionbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        // Set up nav feed list
        this.navAdapter = new NavRecyclerAdapter(this, new NavListener());
        this.feedsList.setAdapter(this.navAdapter);

        // Add feed click listener
        this.addFeedBtn.setOnClickListener((v) -> this.showAddFeedPopup());

        // Do initializations
        initHome();

        this.mHandler = new Handler();
    }

    private void showAddFeedPopup() {
        AddFeedFragment fragment = new AddFeedFragment();

        // Callback when feed added by user
        fragment.setFeedAddedCallback((url) -> {
            if (this.navAdapter.hasFeed(url))
                Messages.showError(this, "You have already added this feed", null);
            else
                addFeed(url);
        });
        fragment.show(getSupportFragmentManager(), "AddFeedDialog");
    }

    private void initHome() {
        // Get stored feeds
        GetFeeds getFeeds = new GetFeeds(this, (error, feeds) -> {
            this.navAdapter.setFeeds(feeds);

            ArrayList<String> feedUrls = new ArrayList<>();
            for (Feed feed : feeds)
                feedUrls.add(feed.getOriginLink());

            // Display new feed entries (home screen), only if it doesn't already exist
            if (getSupportFragmentManager().findFragmentByTag("HomeFragment") == null) {
                ViewFeedFragment fragment = ViewFeedFragment.newInstance("Recent Articles", feedUrls);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, fragment, "HomeFragment");

                // Update all feeds if we have an internet connection
                if (NetworkState.hasNetwork(this)) {
                    this.syncProgress.setVisibility(View.VISIBLE);
                    syncFeeds(feedUrls, () -> {
                        ft.commit();
                        this.syncProgress.setVisibility(View.GONE);
                    });
                } else {
                    ft.commit();
                }
            }
        });
        ThreadPool.getInstance().execute(getFeeds);
    }

    private void syncFeeds(ArrayList<String> feedUrls, Runnable runAfter) {
        for(ListIterator<String> iter = feedUrls.listIterator(); iter.hasNext();) {
            int index = iter.nextIndex();
            String feedUrl = iter.next();

            FeedParser parser = new FeedParser(this, feedUrl, (error, result) -> {
                if (index == feedUrls.size() - 1)
                    runAfter.run();
            });
            ThreadPool.getInstance().execute(parser);
        }
    }

    private void addFeed(String url) {
        this.syncProgress.setVisibility(View.VISIBLE);
        FeedParser parser = new FeedParser(this, url, (error, result) -> {
            if (error == FeedParser.PARSER_ERROR_DOWNLOAD) {
                Messages.showError(this, "There was an error downloading this feed: " + url, null);
            } else if (error == FeedParser.PARSER_ERROR_INVALID_FEED) {
                Messages.showError(this, "This does not seem to be a valid feed: " + url, null);
            } else if (error == FeedParser.PARSER_ERROR_PARSE_ERROR) {
                Messages.showError(this, "There was an error parsing this feed: " + url, null);
            }
            this.navAdapter.addFeed(result.feed);
        });
        ThreadPool.getInstance().execute(parser);
    }

    public void displayContent(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right)
                .replace(R.id.content_layout, fragment)
                .addToBackStack(tag).commit();
    }

    private class NavListener implements NavRecyclerAdapter.OnItemClicked {
        @Override
        public void onClickButton(int button) {
            if (button == R.id.nav_home_btn)
                getSupportFragmentManager().popBackStack();
            else if (button == R.id.nav_settings_btn)
                displayContent(new SettingsFragment(), "Settings");

            mHandler.postDelayed(() -> {
                drawerLayout.closeDrawers();
            }, 100);
        }

        @Override
        public void onClickFeed(Feed feed) {
            getSupportFragmentManager().popBackStack();
            ViewFeedFragment fragment = ViewFeedFragment.newInstance(feed.getTitle(), feed.getOriginLink());
            displayContent(fragment, "ViewFeed");

            mHandler.postDelayed(() -> {
                drawerLayout.closeDrawers();
            }, 100);
        }

        @Override
        public void onContextItemSelected(Feed feed, MenuItem item) {
            if (item.getItemId() == R.id.feed_copy_url) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newRawUri("FeedURL", Uri.parse(feed.getOriginLink()));
                clipboard.setPrimaryClip(clip);
                Messages.showToast(HomeActivity.this, "Copied to clipboard");

            } else if (item.getItemId() == R.id.feed_rename) {

                Messages.showInputDialog(HomeActivity.this, feed.getTitle(), getString(R.string.dialog_rename), (text) -> {
                    feed.setTitle(text);
                    UpdateFeed updateTask = new UpdateFeed(HomeActivity.this, feed, (error, res) -> {
                        navAdapter.updateFeed(feed);
                    });
                    ThreadPool.getInstance().execute(updateTask);
                });

            } else if (item.getItemId() == R.id.feed_unsub) {

                Messages.showAreYouSure(HomeActivity.this, feed.getTitle(),
                        HomeActivity.this.getString(R.string.dialog_unsubscribe), (dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        DeleteFeed deleteTask = new DeleteFeed(HomeActivity.this, feed, (error, res) -> {
                            navAdapter.removeFeed(feed);
                        });
                        ThreadPool.getInstance().execute(deleteTask);
                    }
                });

            }
        }
    }
}
