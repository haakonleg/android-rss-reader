package hakkon.android_rss_reader;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.MenuItem;

import hakkon.android_rss_reader.tasks.ClearCache;
import hakkon.android_rss_reader.util.Messages;

/**
 * Created by hakkon on 24.03.18.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setHasOptionsMenu(true);
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference clearCache = findPreference("clear_cache");
        clearCache.setOnPreferenceClickListener((preference) -> {
            ClearCache task = new ClearCache(getActivity(), (error, res) -> {
                Messages.showToast(getActivity(), "Cache cleared");
            });
            ThreadPool.getInstance().execute(task);
            return true;
        });

        ListPreference theme = (ListPreference) findPreference("selected_theme");
        theme.setOnPreferenceChangeListener((preference, newValue) -> {
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().recreate();
            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        HomeActivity homeActivity = ((HomeActivity)getActivity());
        homeActivity.getSupportActionBar().setTitle("Settings");
        homeActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
        homeActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            getFragmentManager().popBackStack();
        return super.onOptionsItemSelected(item);
    }
}
