package it.patji.audra.nav;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.patji.audra.car.NavigationCarAppService;

public class DeepLinkNotificationReceiver extends BroadcastReceiver {
    public static final String INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP = "INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP";

    private static final Set<String> SUPPORTED_ACTIONS = new HashSet<>(Collections.singletonList(INTENT_ACTION_NAV_NOTIFICATION_OPEN_APP));

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        String intentAction = intent.getAction();
        if (SUPPORTED_ACTIONS.contains(intentAction)) {
            CarContext.startCarApp(intent,
                    new Intent(Intent.ACTION_VIEW)
                            .setComponent(new ComponentName(context, NavigationCarAppService.class))
                            .setData(NavigationCarAppService.createDeepLinkUri(intentAction))
            );
        }
    }
}
