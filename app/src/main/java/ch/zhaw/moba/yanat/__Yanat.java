package ch.zhaw.moba.yanat;

import android.app.Application;
import android.content.Context;

/**
 * Created by michael on 08.04.16.
 * NOT USED
 */
public class __Yanat extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        __Yanat.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return __Yanat.context;
    }
}
