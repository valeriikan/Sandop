package fi.oulu.mobisocial.sandop.helpers;

import android.app.Application;

import com.firebase.client.Firebase;

public class Sandop extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
