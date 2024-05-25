package app.android.contadordecalorias.utils;

import android.app.Activity;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatDelegate;

public class WindowUtil {

    public static void configWindoDefault(Activity a) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }
}
