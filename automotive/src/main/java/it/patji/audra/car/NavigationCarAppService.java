package it.patji.audra.car;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.ApplicationInfo;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.car.app.CarAppService;
import androidx.car.app.Session;
import it.patji.audra.R;
import androidx.car.app.validation.HostValidator;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public final class NavigationCarAppService extends CarAppService {
    public static final String CHANNEL_ID = "NavigationSessionChannel";
    public static final String URI_SCHEME = "audra";
    public static final String URI_HOST = "navigation";

    private static final int NOTIFICATION_ID = 97654321;

    @NonNull
    public static Uri createDeepLinkUri(@NonNull String deepLinkAction) {
        return Uri.fromParts(URI_SCHEME, URI_HOST, deepLinkAction);
    }

    @Override
    @NonNull
    public Session onCreateSession() {
        createNotificationChannel();

        startForeground(NOTIFICATION_ID, getNotification());

        NavigationSession session = new NavigationSession();
        session.getLifecycle()
                .addObserver(
                        new DefaultLifecycleObserver() {
                            @Override
                            public void onDestroy(@NonNull LifecycleOwner owner) {
                                stopForeground(true);
                            }
                        });

        return session;
    }

    @NonNull
    @Override
    public HostValidator createHostValidator() {
        if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR;
        } else {
            return new HostValidator.Builder(getApplicationContext())
                    .addAllowedHosts(R.array.hosts_allowlist_sample)
                    .build();
        }
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(serviceChannel);
    }

    private Notification getNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.car_connection_established))
                .setSmallIcon(R.drawable.ic_launcher);

        builder.setChannelId(CHANNEL_ID);
        builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        return builder.build();
    }

}
