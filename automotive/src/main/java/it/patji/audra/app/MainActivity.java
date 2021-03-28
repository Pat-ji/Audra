package it.patji.audra.app;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import it.patji.audra.databinding.ActivityMainBinding;
import it.patji.audra.nav.NavigationService;

public class MainActivity extends AppCompatActivity {
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            navigationService = ((NavigationService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            navigationService = null;
        }
    };

    private NavigationService navigationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindService(new Intent(this, NavigationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    protected void onStop() {
        if (navigationService != null) {
            unbindService(serviceConnection);
            navigationService = null;
        }

        super.onStop();
    }

}