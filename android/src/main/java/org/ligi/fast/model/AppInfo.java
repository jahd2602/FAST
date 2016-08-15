package org.ligi.fast.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import org.ligi.axt.helpers.ResolveInfoHelper;
import org.ligi.fast.util.UmlautConverter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class to Retrieve / Store Application Information needed by this App
 */
public class AppInfo {
    private static final String SEPARATOR = ";;";
    private final AppIconCache iconCache;
    private String label;
    private String alternateLabel;
    private String packageName;
    private String alternatePackageName;
    private String activityName;
    private String hash;
    private long installTime;
    private int callCount;
    private boolean isValid = true;

    private AppInfo(Context ctx) {
        iconCache = new AppIconCache(ctx, this);
    }

    public AppInfo(Context ctx, String cache_str) {
        this(ctx);

        String[] app_info_str_split = cache_str.split(SEPARATOR);

        if (app_info_str_split.length > 4) {
            try {
                hash = app_info_str_split[0];
                label = app_info_str_split[1];
                packageName = app_info_str_split[2];
                activityName = app_info_str_split[3];
                callCount = Integer.parseInt(app_info_str_split[4]);

                if (app_info_str_split.length > 5) {
                    installTime = Long.parseLong(app_info_str_split[5]);
                }

                calculateAlternateLabelAndPackageName();
                return;
            } catch (Exception ignored) {
            }
        }
        isValid = false;
    }

    public AppInfo(Context _ctx, ResolveInfo ri) {
        this(_ctx);

        // init attributes
        label = new ResolveInfoHelper(ri).getLabelSafely(_ctx);
        label = label.replace("ά", "α")
                     .replaceAll("έ", "ε")
                     .replaceAll("ή", "η")
                     .replaceAll("ί", "ι")
                     .replaceAll("ό", "ο")
                     .replaceAll("ύ", "υ")
                     .replaceAll("ώ", "ω")
                     .replaceAll("Ά", "Α")
                     .replaceAll("Έ", "Ε")
                     .replaceAll("Ή", "Η")
                     .replaceAll("Ί", "Ι")
                     .replaceAll("Ό", "Ο")
                     .replaceAll("Ύ", "Υ")
                     .replaceAll("Ώ", "Ω");
        if (ri.activityInfo != null) {
            packageName = ri.activityInfo.packageName;
            activityName = ri.activityInfo.name;
        } else {
            packageName = "unknown";
            activityName = "unknown";
        }
        callCount = 0;

        final PackageManager pmManager = _ctx.getPackageManager();

        installTime = 0;

        try {
            final PackageInfo pi = pmManager.getPackageInfo(packageName, 0);
            if (Build.VERSION.SDK_INT >= 9) {
                installTime = pi.lastUpdateTime;
            }
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        hash = calculateTheHash();
        calculateAlternateLabelAndPackageName();
        iconCache.cacheIcon(ri);
    }

    private void calculateAlternateLabelAndPackageName() {
        alternateLabel = UmlautConverter.replaceAllUmlautsReturnNullIfEqual(label);
        alternatePackageName = UmlautConverter.replaceAllUmlautsReturnNullIfEqual(packageName);
    }

    public String toCacheString() {
        return hash + SEPARATOR + label + SEPARATOR + packageName +
               SEPARATOR + activityName + SEPARATOR + callCount + SEPARATOR + installTime;
    }

    private String calculateTheHash() {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(packageName.getBytes());
            md.update(activityName.getBytes());

            final byte[] messageDigest = md.digest();

            final StringBuilder hexString = new StringBuilder();
            for (byte digestByte : messageDigest) {
                hexString.append(Integer.toHexString(0xFF & digestByte));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.w("FAST", "MD5 not found - having a fallback - but really - no MD5 - where the f** am I?");
            return packageName; // fallback
        }
    }

    public Intent getIntent() {
        Intent intent = new Intent();
        intent.setClassName(packageName, activityName);
        return intent;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AppInfo)) {
            return false;
        }

        AppInfo other = (AppInfo) o;
        return toCacheString().equals(other.toCacheString());
    }

    public Drawable getIcon() {
        return iconCache.getIcon();
    }

    public String getPackageName() {
        return packageName;
    }

    public String getActivityName() {
        return packageName;
    }

    public String getLabel() {
        return label;
    }

    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int count) {
        callCount = count;
    }

    public long getInstallTime() {
        return installTime;
    }

    public void incrementCallCount() {
        callCount++;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getHash() {
        return hash;
    }

    public String getAlternateLabel() {
        return alternateLabel;
    }

    public String getAlternatePackageName() {
        return alternatePackageName;
    }

    public void mergeSafe(AppInfo appInfo) {
        final int localCallCount = getCallCount();
        final int remoteCallCount = appInfo.getCallCount();
        setCallCount(Math.max(localCallCount, remoteCallCount));

        label = appInfo.getLabel();
        calculateAlternateLabelAndPackageName();
    }
}
