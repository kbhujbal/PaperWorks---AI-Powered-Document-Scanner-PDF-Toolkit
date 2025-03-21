package com.templatemela.camscanner.main_utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrixColorFilter;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Date;

public class Constant {
    public static Bitmap IDCardBitmap = null;
    //to identify the activity
    public static String IdentifyActivity = "IdentifyActivity";
    public static int[][] adjustProgressArray = {new int[]{0, 128}, new int[]{1, 78}, new int[]{2, 66}, new int[]{3, 0}};
    public static String ascending_date = "Ascending date";
    public static String ascending_name = "Ascending name";
    public static Bitmap bookBitmap = null;
    public static String card_type = "Single";
    public static ColorMatrixColorFilter[] coloreffect = {new ColorMatrixColorFilter(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}), new ColorMatrixColorFilter(new float[]{1.0f, 0.0f, 0.0f, 0.0f, -60.0f, 0.0f, 1.0f, 0.0f, 0.0f, -60.0f, 0.0f, 0.0f, 1.0f, 0.0f, -90.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}), new ColorMatrixColorFilter(new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f}), new ColorMatrixColorFilter(new float[]{1.1953125f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.671875f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.3984375f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.7265625f, 0.0f}), new ColorMatrixColorFilter(new float[]{1.1953125f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.671875f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.3984375f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.8671875f, 0.0f})};
    //current camera view on scaneer  activity(document/ id card/ book/ photo)
    public static String current_camera_view = "Document";
    //current tag on MainActivity(All Docs /bussiness card /id card / academic doc/ personal doc)
    public static String current_tag = "All Docs";
    public static String descending_date = "Descending date";
    public static String descending_name = "Descending name";

    //selected filter
    public static int filterPosition = 0;
    //
    public static String inputType = "Group";
    public static Bitmap original;
    public static int selectedFont;
    public static int selectedWatermarkFont;
    public static Bitmap singleSideBitmap;
    public static String foldname = "PaperWorks";


    //preferd theme
    public static String PREFS_NAME = "theme_prefs";
    public static String KEY_THEME = "prefs.theme";
    public static  int THEME_UNDEFINED = -1;
    public static  int THEME_LIGHT = 0;
    public static  int THEME_DARK = 1;


    //multiple scan.
    public static ArrayList<Bitmap> multipleScan = new ArrayList<>();

    public static String getDateTime(String str) {
        return new SimpleDateFormat(str).format(new Date());
    }

    public static int getProgressPercentage(int i, int i2) {
        return (i * 100) / i2;
    }

    public static void shareApp(Activity activity) {
//        try {
//            Intent sendIntent = new Intent();
//            sendIntent.setAction(Intent.ACTION_SEND);
//            sendIntent.putExtra(Intent.EXTRA_TEXT,
//                    "Hey check out my app at: https://play.google.com/store/apps/details?id=" + activity.getPackageName());
//            sendIntent.setType("text/plain");
//            activity.startActivity(sendIntent);
//         } catch (Exception e) {
//            e.toString();
//        }
    }

    public static void rateApp(Activity activity) {
//        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
//        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
//        try {
//            activity.startActivity(myAppLinkToMarket);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(activity, " unable to find market app", Toast.LENGTH_LONG).show();
//        }
    }
}
