package it.patji.audra.nav;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.patji.audra.R;

public class NavigationService extends Service {
    public static final String CHANNEL_ID = "NavigationServiceChannel";

    private final IBinder binder;

    public class LocalBinder extends Binder {
        @NonNull
        public NavigationService getService() {
            return NavigationService.this;
        }
    }

    public NavigationService() {
        binder = new LocalBinder();
    }

    @Override
    public void onCreate() {
        NotificationManager mNotificationManager = getSystemService(NotificationManager.class);
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        mNotificationManager.createNotificationChannel(serviceChannel);
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(@NonNull Intent intent) {
        return true; // Ensures onRebind() is called when a client re-binds.
    }

}
