package org.ligi.fast.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.ligi.axt.helpers.FileHelper;
import org.ligi.fast.App;
import org.ligi.fast.R;

public class MyPreferenceActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        App.applyTheme(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.pref_with_actionbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FASTPreferenceFragment preferenceFragment = new FASTPreferenceFragment();

        FASTPreferenceFragment.setRemoveCacheListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new FileHelper(App.getBaseDir()).deleteRecursive();
                Intent intent = new Intent(MyPreferenceActivity.this, SearchActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return false;
            }
        });

        FASTPreferenceFragment.setThemeChangedListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                finish();
                startActivity(getIntent());
                return true;
            }
        });

        FASTPreferenceFragment.setResolutionChangedListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                new FileHelper(App.getBaseDir()).deleteRecursive();
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, preferenceFragment).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, SearchActivity.class));
    }
}
