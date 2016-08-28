package co.gobd.tracker.ui.activity;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import co.gobd.tracker.R;
import co.gobd.tracker.adapter.JobAdapter;
import co.gobd.tracker.adapter.OnTaskUpdateClickListener;
import co.gobd.tracker.application.GoAssetApplication;
import co.gobd.tracker.model.job.JobModel;
import co.gobd.tracker.model.job.order.OrderCart;
import co.gobd.tracker.model.job.task.JobTaskTypes;
import co.gobd.tracker.presenter.MainPresenter;
import co.gobd.tracker.service.job.JobService;
import co.gobd.tracker.ui.service.LocationService;
import co.gobd.tracker.ui.view.MainView;
import co.gobd.tracker.ui.view.OnJobItemClickListener;
import co.gobd.tracker.utility.Constant;
import co.gobd.tracker.utility.ListParser.JobParser;
import co.gobd.tracker.utility.ServiceUtility;
import co.gobd.tracker.utility.SessionManager;

public class MainActivity extends AppCompatActivity
        implements MainView, OnJobItemClickListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, OnTaskUpdateClickListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Inject
    SessionManager sessionManager;

    @Inject
    JobService jobService;

    @Inject
    Context context;

    @Inject
    MainPresenter mainPresenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    @BindView(R.id.rv_joblist)
    RecyclerView rvJobList;

    @BindView(R.id.fab_toggle_tracking)
    FloatingActionButton fabToggleTracking;

    List<JobModel> jobModelList;
    JobAdapter jobAdapter;

    ProgressDialog progressDialog;
    MaterialDialog taskUpdateDialog;

    CheckBox cbPickup;
    CheckBox cbDelivery;

    private ActionBarDrawerToggle drawerToggle;

    /**
     * Keeps a reference of ButterKnife object so that it can be cleared from memory later
     * Unbinder#unbind() is called in Activity#onDestroy()
     */
    private Unbinder butterKnifeUnbinder;

    private boolean isPickupInProgress;
    private boolean isDeliveryInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        butterKnifeUnbinder = ButterKnife.bind(this);

        // Dagger 2 Injection
        ((GoAssetApplication) getApplication()).getComponent().inject(this);

        ServiceUtility.checkGooglePlayServices(context, this);

        setupToolbar();

        setupNavigationDrawer();

        //setupTaskUpdateDialog();

        jobAdapter = new JobAdapter(context);
        mainPresenter.initialise(this);
        mainPresenter.loadAdapterData();

        jobAdapter.setOnJobItemClickListener(this);
        jobAdapter.setOnTaskUpdateClickListener(this);

        setupRecyclerView(jobAdapter);

        setupNavigationHeaderView(sessionManager.getUsername());

        fabToggleTracking.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(this);
    }

    public MaterialDialog createTaskUpdateDialog(final String jobId, final String pickUpTaskId, final String deliveryTaskId) {
        boolean wrapInScrollView = true;
        return new MaterialDialog.Builder(this)
                .title(R.string.title_task_update)
                .customView(R.layout.layout_task_update, wrapInScrollView)
                .positiveText(R.string.dialog_button_update)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (cbPickup.isChecked() && isPickupInProgress) {
                            mainPresenter.updateTaskStateToCompleted(jobId, pickUpTaskId);
                        }
                        if (cbDelivery.isChecked() && isDeliveryInProgress) {
                            mainPresenter.updateTaskStateToCompleted(jobId, deliveryTaskId);
                        }
                        mainPresenter.loadAdapterData();
                    }
                })
                .build();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupNavigationHeaderView(String assetName) {
        View headerLayout = navigationView.getHeaderView(0);
        TextView tvAssetName = ButterKnife.findById(headerLayout, R.id.nav_header_asset_name);
        tvAssetName.setText(assetName);
    }

    private void setupRecyclerView(JobAdapter jobAdapter) {
        rvJobList.setLayoutManager(new LinearLayoutManager(context));
        rvJobList.setItemAnimator(new DefaultItemAnimator());
        rvJobList.setAdapter(jobAdapter);
    }

    private void setupNavigationDrawer() {
        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        drawerLayout.addDrawerListener(drawerToggle);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocationStatus();
        if (isLocationServiceRunning(LocationService.class)) {
            fabToggleTracking.setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            fabToggleTracking.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainPresenter.onDestroy();
        butterKnifeUnbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkLocationStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            //Toast.makeText(this, "Location is enabled", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }
    }

    public void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(
                "Location is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //dialog.cancel();
                MainActivity.this.finish();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private boolean isLocationServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    private void stopLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        stopService(intent);
    }

    private void logout() {
        stopLocationService();
        sessionManager.clearAll();
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(View view, int position, JobModel jobModel) {

        Intent intent = new Intent(this, JobDetailsActivity.class);

        JobParser jobParser = new JobParser(jobModel);

        String noteToDeliveryMan = jobParser.getNoteToDeliveryMan();
        String pickupAddress = jobParser.getPickupLocation().getAddress();
        String deliveryAddress = jobParser.getDeliveryLocation().getAddress();
        //String pickupLat = jobParser.getPickupLocation().getPoint().getLatitude();
        //String pickupLon = jobParser.getPickupLocation().getPoint().getLongitude();
        //String deliveryLat = jobParser.getDeliveryLocation().getPoint().getLatitude();
        //String deliveryLon = jobParser.getDeliveryLocation().getPoint().getLongitude();
        OrderCart orderCart = jobModel.getOrder().getOrderCart();
        String packageDescription = jobModel.getOrder().getDescription();

        intent.putExtra(Constant.Job.ORDER_CART, orderCart);
        intent.putExtra(Constant.Job.PACKAGE_DESCRIPTION, packageDescription);
        intent.putExtra(Constant.Job.PICKUP_ADDRESS, pickupAddress);
        intent.putExtra(Constant.Job.DELIVERY_ADDRESS, deliveryAddress);
        intent.putExtra(Constant.Job.JOB_ID, jobModel.getId());
        intent.putExtra(Constant.Job.JOB_HRID, jobModel.getHRID());
        intent.putExtra(Constant.Job.NOTE_TO_DELIVERY_MAN, noteToDeliveryMan);
        intent.putExtra(Constant.Job.TASK_ID_PICKUP, jobParser.getTaskId(JobTaskTypes.PACKAGE_PICKUP));
        intent.putExtra(Constant.Job.TASK_ID_PICKUP, jobParser.getTaskId(JobTaskTypes.DELIVERY));

        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_toggle_tracking:
                if (isLocationServiceRunning(LocationService.class)) {
                    stopLocationService();
                    fabToggleTracking.setImageResource(R.drawable.ic_play_arrow_white_24dp);
                } else {
                    startLocationService();
                    fabToggleTracking.setImageResource(R.drawable.ic_pause_white_24dp);
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }

        setTitle(item.getTitle());
        drawerLayout.closeDrawers();

        switch (item.getItemId()) {
            case R.id.nav_settings:
                //TODO: Implement Settings activity/fragment
                break;
            case R.id.nav_logout:
                logout();
                break;
            case R.id.nav_about:
                //TODO: Use About Libraries for hassle free about page
                // https://github.com/mikepenz/AboutLibraries
                break;
        }


        return true;
    }


    @Override
    public void setJobModelList(List<JobModel> jobModelList) {
        jobAdapter.setAdapterData(jobModelList);
    }

    @Override
    public void startProgress() {
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Downloading Jobs... ");
        progressDialog.show();
    }

    @Override
    public void stopProgress() {
        progressDialog.dismiss();
    }

    @Override
    public void showTaskUpdateSuccessfulMsg() {
        Toast.makeText(context, R.string.msg_task_update_successful, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showTaskUpdateError() {
        Toast.makeText(context, R.string.msg_task_update_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showConnectionError() {
        Toast.makeText(context, R.string.message_connection_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskUpdateClick(String jobId, String pickupTaskId, String deliveryTaskId,
                                  String pickUpTaskState, String deliveryTaskState) {
        taskUpdateDialog = createTaskUpdateDialog(jobId, pickupTaskId, deliveryTaskId);
        taskUpdateDialog.show();

        View view = taskUpdateDialog.getCustomView();
        cbPickup = (CheckBox) view.findViewById(R.id.cb_pickup);
        cbDelivery = (CheckBox) view.findViewById(R.id.cb_delivery);

        isPickupInProgress = true;
        isDeliveryInProgress = true;

        updateCheckBox(pickUpTaskState, deliveryTaskState);

    }

    public void updateCheckBox(String pickUpTaskState, String deliveryTaskState) {
        if (pickUpTaskState.equals(Constant.JobTaskState.COMPLETED)) {
            disableCheckBox(cbPickup);
            isPickupInProgress = false;
        }

        if (deliveryTaskState.equals(Constant.JobTaskState.COMPLETED)) {
            disableCheckBox(cbDelivery);
            isDeliveryInProgress = false;
        }
    }

    public void disableCheckBox(CheckBox checkBox) {
        checkBox.setChecked(true);
        checkBox.setEnabled(false);

    }

}
