package org.ligi.fast;

import android.app.Activity;
import android.app.Application;

import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.settings.AndroidFASTSettings;
import org.ligi.fast.settings.FASTSettings;
import org.ligi.tracedroid.TraceDroid;

import java.io.File;

public class App extends Application {

    private static FASTSettings settings;
    private static App appInstance;

    public static final String LOG_TAG = "FAST App Search";

    public interface PackageChangedListener {
        void onPackageChange(AppInfoList appInfoList);
    }

    public static PackageChangedListener packageChangedListener;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        TraceDroid.init(this);
        settings = new AndroidFASTSettings(App.this);
    }

    public static FASTSettings getSettings() {
        return settings;
    }


    private static int getThemeByString(String theme) {

        switch (theme) {
            case "dark":
                return R.style.AppTheme_Dark;

            case "light":
            default:
                return R.style.AppTheme_Light;

        }
    }

    public static void injectSettingsForTesting(FASTSettings newSettings) {
        settings = newSettings;
    }

    public static void applyTheme(Activity activity) {
        applyTheme(activity, getSettings().getTheme());
    }

    public static void applyTheme(Activity activity, final String theme) {
        activity.setTheme(getThemeByString(theme));
    }

    public static String getStoreURL4PackageName(String pname) {
        return TargetStore.STORE_URL + pname;
    }

    public static File getBaseDir() {
        return appInstance.getFilesDir();
    }
}
