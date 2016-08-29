package co.gobd.tracker.ui.service;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import javax.inject.Inject;

import co.gobd.tracker.R;
import co.gobd.tracker.application.GoAssetApplication;
import co.gobd.tracker.service.tracker.TrackerCallback;
import co.gobd.tracker.service.tracker.TrackerService;
import co.gobd.tracker.ui.activity.MainActivity;
import co.gobd.tracker.utility.SessionManager;

public class LocationService extends Service
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public static final float SMALLEST_DISPLACEMENT = 0;

    private static final String LOG_TAG = LocationService.class.getSimpleName();
    public static Location mCurrentLocation;
    @Inject
    TrackerService trackerService;
    @Inject
    Context context;
    @Inject
    SessionManager sessionManager;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    public void onCreate() {
        // Injects the dependency
        ((GoAssetApplication) getApplication()).getComponent().inject(this);

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        String message = "Location service stopped";
        Log.i(LOG_TAG, message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(LOG_TAG, "Building GoogleApiClient");
        mGoogleApiClient =
                new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
    }

    protected void startLocationUpdates() {

        startServiceAsForeground();

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                this);
    }

    private void startServiceAsForeground() {

        // Requires a notification to run a service as foreground
        int NOTIFICATION_ID = 1;

        // Create an Intent that will open the main Activity
        // if the notification is clicked.
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_directions_run_white_24dp)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_go_logo))
                .setContentTitle("GO! Asset")
                .setContentText("Tracking is running")
                .setColor(getResources().getColor(R.color.colorPrimary));

        Notification notification = builder.build();

        // Set the Notification as ongoing
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

        // Move the Service to the Foreground
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.i(LOG_TAG, "GoogleApi connected");

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;

        String assetId = sessionManager.getAssetId();
        String name = sessionManager.getUsername();
        trackerService.sendLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(),
                assetId, name, new TrackerCallback() {

                    @Override
                    public void onLocationSendSuccess() {
                        Toast.makeText(context, R.string.location_sent_successful, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLocationSendFailure() {
                        Toast.makeText(context, R.string.location_sent_failure, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onConnectionError() {
                        Toast.makeText(context, R.string.message_connection_error, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
