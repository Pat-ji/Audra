package it.patji.audra.car;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.Screen;
import androidx.car.app.model.Action;
import androidx.car.app.model.CarColor;
import androidx.car.app.model.MessageTemplate;
import androidx.car.app.model.ParkedOnlyOnClickListener;
import androidx.car.app.model.Template;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import it.patji.audra.app.MainActivity;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RequestPermissionScreen extends Screen implements DefaultLifecycleObserver {
    public interface LocationPermissionCheckCallback {

        void onPermissionGranted();

    }

    private static final int MSG_SEND_POLL_PERMISSION = 1;
    private static final long POLL_PERMISSION_FREQUENCY_MILLIS = SECONDS.toMillis(1);

    private final Handler handler;
    private final LocationPermissionCheckCallback locationPermissionCheckCallback;

    public RequestPermissionScreen(@NonNull CarContext carContext, @NonNull LocationPermissionCheckCallback callback) {
        super(carContext);

        handler = new Handler(Looper.getMainLooper(), new HandlerCallback());
        locationPermissionCheckCallback = callback;

        getLifecycle().addObserver(this);
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        handler.sendMessageDelayed(handler.obtainMessage(MSG_SEND_POLL_PERMISSION), POLL_PERMISSION_FREQUENCY_MILLIS);
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        handler.removeMessages(MSG_SEND_POLL_PERMISSION);
    }

    @NonNull
    @Override
    public Template onGetTemplate() {
        ParkedOnlyOnClickListener listener = ParkedOnlyOnClickListener.create(
                () -> getCarContext()
                        .startActivity(
                                new Intent(
                                        getCarContext(),
                                        MainActivity.class)
                                        .setFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK)));

        return new MessageTemplate.Builder(
                "Please allow location access via the phone.")
                .setHeaderAction(Action.APP_ICON)
                .addAction(
                        new Action.Builder()
                                .setBackgroundColor(CarColor.BLUE)
                                .setOnClickListener(listener)
                                .setTitle("Go to Phone")
                                .build())
                .build();
    }

    public class HandlerCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_SEND_POLL_PERMISSION) {
                if (getCarContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionCheckCallback.onPermissionGranted();
                    RequestPermissionScreen.this.finish();
                    return true;
                }

                handler.sendMessageDelayed(handler.obtainMessage(MSG_SEND_POLL_PERMISSION), POLL_PERMISSION_FREQUENCY_MILLIS);
                return true;
            }

            return false;
        }
    }
}
