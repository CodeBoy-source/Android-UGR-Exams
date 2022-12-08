package com.example.sqlprototype;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.os.Environment;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sqlprototype.p2.HomeFragment;
import com.example.sqlprototype.p3.DialogFlowFragment;
import com.example.sqlprototype.p4.FindPattern;
import com.example.sqlprototype.p4.GpsEtsiitHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.example.sqlprototype.p4.RecordPattern;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    GpsEtsiitHomeFragment gpsEtsiitHomeFragment = new GpsEtsiitHomeFragment();
    DialogFlowFragment dialogFlowFragment = new DialogFlowFragment();
    RecordPattern recordPatternFragment = new RecordPattern();
    public static final Integer RecordAudioRequestCode = 1;

    // How many times we have to press home button
    public static Integer debuggingThreshold = 10;
    // Amount of time we have to wait = 3 seconds
    public static Integer debuggingTimerReset = 3;
    // Boolean to know if timer is ticking
    public static boolean is_running = false;
    private static boolean checkDebuggingPermission = true;
    // Counter for the debuggin page:
    public static Integer debuggingCounter = 0;
    // Executor Service to reset the counter after 3 seconds
    public static ScheduledExecutorService executorService;


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Nothing for now.
                } else {
                    Toast.makeText(this, "[WARNING]: ALLOW permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        // Initialize debugging page variables
        executorService = Executors.newSingleThreadScheduledExecutor();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.menuHome:
                    recordPatternFragment.unregisterListeners();
                    gpsEtsiitHomeFragment.cancelTxtSpeech();
                    gpsEtsiitHomeFragment.unregisterListener();
                    treatDebuggingInterface();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

                    return true;
                case R.id.menuDialog:
                    recordPatternFragment.unregisterListeners();
                    gpsEtsiitHomeFragment.unregisterListener();
                    gpsEtsiitHomeFragment.cancelTxtSpeech();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container,dialogFlowFragment).commit();
                    return true;
                case R.id.mapa:
                    recordPatternFragment.unregisterListeners();
                    gpsEtsiitHomeFragment.cancelTxtSpeech();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, gpsEtsiitHomeFragment).commit();
                    return true;
               // case R.id.pattern:
               //     getSupportFragmentManager().beginTransaction().replace(R.id.container,recordPatternFragment).commit();
               //     return true;
            }
            return false;
        });
    }

    public static void ResetDebuggerCounter(){
        debuggingCounter = 0;
        is_running=false;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void treatDebuggingInterface(){
        if(checkDebuggingPermission) {
            debuggingCounter += 1;
            if(!is_running) {
                executorService.schedule(MainActivity::ResetDebuggerCounter, debuggingTimerReset, TimeUnit.SECONDS);
                is_running=true;
            }
            if(debuggingCounter>=debuggingThreshold) {
                String[] permissionsStorage = {Manifest.permission.MANAGE_EXTERNAL_STORAGE};
                int requestExternalStorage = 1;
                int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissionsStorage, requestExternalStorage);
                }
                recordPatternFragment.registerListeners();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, recordPatternFragment).commit();
            }else
                getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
        }
    }
}
