package org.ligi.fast.ui;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.ligi.fast.R;
import org.ligi.fast.TargetStore;
import org.ligi.fast.settings.FASTSettings;

public class FASTPreferenceFragment extends PreferenceFragmentCompat {

    private static Preference.OnPreferenceChangeListener themeChangedListener;
    private static Preference.OnPreferenceChangeListener resolutionChangedListener;
    private static Preference.OnPreferenceClickListener removeCacheListener;

    public static void setThemeChangedListener(Preference.OnPreferenceChangeListener themeChangedListener2) {
        FASTPreferenceFragment.themeChangedListener = themeChangedListener2;
    }

    public static void setRemoveCacheListener(Preference.OnPreferenceClickListener removeCacheListener2) {
        FASTPreferenceFragment.removeCacheListener = removeCacheListener2;

    }

    public static void setResolutionChangedListener(Preference.OnPreferenceChangeListener resolutionChangedListener2) {
        FASTPreferenceFragment.resolutionChangedListener = resolutionChangedListener2;
    }

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, String s) {
        addPreferencesFromResource(R.xml.settings);

        findPreference(FASTSettings.KEY_THEME).setOnPreferenceChangeListener(themeChangedListener);
        findPreference(FASTSettings.KEY_CLEAR_CACHE).setOnPreferenceClickListener(removeCacheListener);
        findPreference(FASTSettings.KEY_ICONRES).setOnPreferenceChangeListener(resolutionChangedListener);
        findPreference(FASTSettings.KEY_MARKETFORALL).setTitle(String.format(getResources().getString(R.string.open_in_for_all), TargetStore.STORE_NAME));
    }
}