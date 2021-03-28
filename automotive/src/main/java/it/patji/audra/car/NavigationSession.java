package it.patji.audra.car;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.CarToast;
import androidx.car.app.Screen;
import androidx.car.app.ScreenManager;
import androidx.car.app.Session;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public class NavigationSession extends Session {
    private final LocationListener locationListener =
            new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                }

                @Override
                @Deprecated
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                }

            };

    public NavigationSession() {
        Lifecycle lifecycle = getLifecycle();
        lifecycle.addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner lifecycleOwner) {
                LocationManager locationManager = (LocationManager) getCarContext().getSystemService(Context.LOCATION_SERVICE);
                locationManager.removeUpdates(locationListener);
            }
        });
    }

    @Override
    @NonNull
    public Screen onCreateScreen(@NonNull Intent intent) {
        NavigationScreen navigationScreen = new NavigationScreen(getCarContext());

        String action = intent.getAction();
        if (CarContext.ACTION_NAVIGATE.equals(action)) {
            CarToast.makeText(getCarContext(), "Navigation intent: " + intent.getDataString(), CarToast.LENGTH_LONG).show();
        }

        if (getCarContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocationUpdates();
        } else {
            // If we do not have the location permission, proceed to the navigation screen first, and
            // push the request permission screen. When the user grants the location permission, the
            // request permission screen will be popped and the navigation screen will be displayed.
            getCarContext().getCarService(ScreenManager.class).push(navigationScreen);
            return new RequestPermissionScreen(getCarContext(), this::requestLocationUpdates);
        }

        return navigationScreen;
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        LocationManager locationManager = (LocationManager) getCarContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
    }

}
