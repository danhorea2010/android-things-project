package han.dorea.mediarecorder;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import han.dorea.mediarecorder.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private MediaRecorder recorder;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private static final int POLL_INTERVAL = 200;
    private SoundMeter mSensor;

    private final Handler mHandler = new Handler();
    private long time = 0;


    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private Runnable mSleepTask = () -> start();

    // Runnable thread to monitor audio level
    private Runnable mPollTask = new Runnable() {
        @Override
        public void run() {
            double decibels = mSensor.getDecibels();

            // update display?


            Log.i("noise", "Current decibels: " + decibels);
            if(decibels < 80)
            {
                evaluateState(120);
            }
            else if (decibels < 90)
            {
                evaluateState(50);//Put time in minutes
            }
            else if (decibels < 100)
            {
                evaluateState(15);
            }
            else if(decibels < 110)
            {
                evaluateState(2);
            }
            else
            {
                evaluateState(0);
            }

            // For testing...
            if(decibels < 0){
                evaluateState(120);
            }


            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
    };



    private void start() {
        time = System.nanoTime();
        mSensor.start(getExternalCacheDir());
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    private void stop() {

        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        checkAndRequestPermissions();
        mSensor = new SoundMeter();

    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void evaluateState(long t)
    {
        //We evaluate the state given, and compare it to the current state and time
        //The logic is as follows: each state has a corresponding time that it has to be larger than
        long endTime = System.nanoTime();
        if(endTime - time >= t*60000000)
        {
            //We push a notification Danger
            addNotification("Danger! Sound Levels Have Exceeded a Safe Threshold");
            Toast.makeText(getApplicationContext(), "Danger! Sound Levels Have Exceeded a Safe Threshold", Toast.LENGTH_SHORT).show();

        }
        else
        {
            //We push a notification warning
            addNotification("Warning! Sound Levels May Approach a Dangerous Threshold Soon");
            Toast.makeText(getApplicationContext(), "Warning! Sound Levels May Approach a Dangerous Threshold Soon", Toast.LENGTH_SHORT).show();
        }
    }

    //Creating a notification
    private void addNotification(String s){
        NotificationCompat.Builder b = new NotificationCompat.Builder(this)
                .setSmallIcon(1)
                .setContentTitle("ALERT")
                .setContentText(s);
        Intent notificationIntent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        b.setContentIntent(contentIntent);
        NotificationManager m = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(0,b.build());
    }


}