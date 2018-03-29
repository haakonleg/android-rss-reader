package hakkon.android_rss_reader;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import hakkon.android_rss_reader.database.Feed;
import hakkon.android_rss_reader.tasks.DeleteFeed;
import hakkon.android_rss_reader.tasks.FeedParser;
import hakkon.android_rss_reader.tasks.GetFeeds;
import hakkon.android_rss_reader.tasks.UpdateFeed;
import hakkon.android_rss_reader.util.Messages;
import hakkon.android_rss_reader.util.NetworkState;

public class HomeActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    private DrawerLayout drawerLayout;
    private RecyclerView feedsList;
    public NavRecyclerAdapter navAdapter;
    private FloatingActionButton addFeedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

        // Set up actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);

        // Find elements
        this.drawerLayout = findViewById(R.id.home_drawer_layout);
        this.feedsList = findViewById(R.id.nav_feeds_list);
        this.addFeedBtn = findViewById(R.id.nav_add_feed_btn);

        // Set up nav feed list
        this.navAdapter = new NavRecyclerAdapter(this, new NavListener());
        this.feedsList.setAdapter(this.navAdapter);

        // Add feed click listener
        this.addFeedBtn.setOnClickListener((v) -> this.showAddFeedPopup());

        // Set fragment manager listener
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        // Do initializations
        initHome();
    }

    private void showAddFeedPopup() {
        AddFeedFragment fragment = new AddFeedFragment();

        // Callback when feed added by user
        fragment.setFeedAddedCallback((url) -> {
            if (this.navAdapter.hasFeed(url))
                Messages.showError(this, "You have already added this feed", null);
            else
                updateFeed(url, true);
        });
        fragment.show(getSupportFragmentManager(), "AddFeedDialog");
    }

    private void initHome() {
        // Add stored feeds to drawer list
        GetFeeds getFeeds = new GetFeeds(this, (error, feeds) -> {

            boolean hasNetwork = NetworkState.hasNetwork(this);
            for (Feed feed : feeds) {
                this.navAdapter.addFeed(feed);
                // Update feed if we have an internet connection
                if (hasNetwork)
                    updateFeed(feed.getOriginLink(), false);
            }

            // Display new feed entries (home screen)
            if (getSupportFragmentManager().findFragmentByTag("HomeFragment") == null) {
                ViewFeedFragment fragment = ViewFeedFragment.newInstanceHome();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, fragment, "HomeFragment").commit();
            }
        });
        ThreadPool.getInstance().execute(getFeeds);
    }

    private void updateFeed(String url, boolean addToDrawer) {
        FeedParser parser = new FeedParser(this, url, (error, result) -> {
            if (error == FeedParser.PARSER_ERROR_DOWNLOAD) {
                Messages.showError(this, "There was an error downloading this feed: " + url, null);
            } else if (error == FeedParser.PARSER_ERROR_INVALID_FEED) {
                Messages.showError(this, "This does not seem to be a valid feed: " + url, null);
            } else if (error == FeedParser.PARSER_ERROR_PARSE_ERROR) {
                Messages.showError(this, "There was an error parsing this feed: " + url, null);
            } else if (addToDrawer) {
                this.navAdapter.addFeed(result.feed);
            }
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

            new Handler().postDelayed(() -> {
                drawerLayout.closeDrawers();
            }, 100);
        }

        @Override
        public void onClickFeed(Feed feed) {
            getSupportFragmentManager().popBackStack();
            ViewFeedFragment fragment = ViewFeedFragment.newInstanceFeed(feed.getTitle(), feed.getOriginLink());
            displayContent(fragment, "ViewFeed");

            new Handler().postDelayed(() -> {
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

    @Override
    public void onBackStackChanged() {
        // Get current fragment name
        FragmentManager fm = getSupportFragmentManager();
        int cnt = fm.getBackStackEntryCount();

        // Show back button
        if (cnt > 0 && (fm.getBackStackEntryAt(cnt-1).getName().equals("ViewArticle") ||
                fm.getBackStackEntryAt(cnt-1).getName().equals("Settings"))) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FragmentManager fm = getSupportFragmentManager();
            int cnt = fm.getBackStackEntryCount();

            // Back button
            if (cnt > 0 && (fm.getBackStackEntryAt(cnt - 1).getName().equals("ViewArticle") ||
                    fm.getBackStackEntryAt(cnt - 1).getName().equals("Settings")))
                fm.popBackStack();
            // Drawer button
            else
                this.drawerLayout.openDrawer(GravityCompat.START);
        }
        return false;
    }
}
