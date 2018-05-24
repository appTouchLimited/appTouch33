package uk.co.apptouch.apptouch30;

import android.app.Application;

public class AppGlobals extends Application {

    public static final String SERVER_IP = "http://138.68.170.85:9000";
    public static final String BASE_URL = String.format("%s/api/", SERVER_IP);

}
