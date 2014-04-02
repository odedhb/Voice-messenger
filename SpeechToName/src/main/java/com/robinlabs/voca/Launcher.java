package com.robinlabs.voca;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by oded on 3/31/14.
 */
public class Launcher {

    public static final String NO_LAUNCHER_IS_SET_PACKAGE = "android";

    private final Activity activity;
    private final PackageManager pm;

    Launcher(Activity activity) {
        this.activity = activity;
        pm = activity.getPackageManager();
    }


    private String getCurrentLauncherPackage() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo == null) return null;

        return resolveInfo.activityInfo.packageName;
    }

    private void setAsLauncher() {
        String currentLauncher = getCurrentLauncherPackage();
        if (currentLauncher == null || currentLauncher.equals(NO_LAUNCHER_IS_SET_PACKAGE)) {
            activity.finish();
            Toast.makeText(activity, "Home button -> Voca -> always");
        } else if (activity.getPackageName().equals(currentLauncher)) {
            Toast.makeText(activity, "Voca will listen when tapping Home");
        } else {
            Toast.makeText(activity, "Tap \"Clear defaults\"");
            showManageAppSettings(currentLauncher);
        }
    }

    private void showManageAppSettings(String appPackage) {

        Uri packageUri = Uri.parse("package:" + appPackage);
        Intent uninstallIntent =
                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri);
        activity.startActivity(uninstallIntent);
    }

    public void toggleLaunch() {
        setAsLauncher();
    }

    public boolean considerGoingToNormalLauncher() {

//        if (ShrdPrfs.useVocaAsLauncher()) return false;

//        goToNormalLauncher();
        return true;
    }

/*

    public void goToNormalLauncher() {
        boolean decideNormalLauncherOnYourOwn = false;

        String secondLauncherSavedStringIntent = shrdPrfs.generalShrdPrfs().getString(ShrdPrfs.HOME_LABEL, null);
        if (secondLauncherSavedStringIntent != null) {
            try {
                startActivity(Intent.parseUri(secondLauncherSavedStringIntent, 0));
                activity.finish();
            } catch (URISyntaxException e) {
                decideNormalLauncherOnYourOwn = true;
            }
        } else {
            decideNormalLauncherOnYourOwn = true;
        }

        if (decideNormalLauncherOnYourOwn) {
            List<ResolveInfo> resolveInfos = pm.queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfos.size() > 2) {
                //show select screen to advanced users who have more than one launcher
                changeSecondaryLauncher();
                return;
            }

            for (final ResolveInfo resolveInfo : resolveInfos) {
                if (!ctx.getPackageName().equals(resolveInfo.activityInfo.packageName))  //if this activity is not in our activity (in other words, it's another default home screen)
                {
                    Intent goHomeIntent = new Intent().addCategory(Intent.CATEGORY_HOME).setAction(Intent.ACTION_MAIN).setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                    shrdPrfs.generalShrdPrfs().edit().putString(ShrdPrfs.HOME_LABEL, goHomeIntent.toUri(0)).commit();
                    break;
                }
            }
        }

    }
*/

}
