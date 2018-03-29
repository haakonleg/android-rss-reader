package hakkon.android_rss_reader;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import hakkon.android_rss_reader.tasks.ClearCache;
import hakkon.android_rss_reader.util.Messages;

/**
 * Created by hakkon on 24.03.18.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        ((HomeActivity)getActivity()).getSupportActionBar().setTitle("Settings");

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


}
