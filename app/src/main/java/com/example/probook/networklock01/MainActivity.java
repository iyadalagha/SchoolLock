package com.example.probook.networklock01;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import messages.LockMessage;
import messages.UnlockMessage;
import messages.UserInfo;
import utility.ClassFinder;

public class MainActivity extends AppCompatActivity {

    Client client;
    private static final int ADMIN_INTENT = 15;
    private static final String description = "Sample Administrator description";
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, MyAdminReceiver.class);


        client = new Client();
        client.start();
        Kryo kryo = client.getKryo();
        List<Class<?>> classes = ClassFinder.getClassesOfPackage(getApplicationContext(), "messages");
        for(Class<?> c: classes){
            kryo.register(c);
        }
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, description);
        startActivityForResult(intent, ADMIN_INTENT);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //InetAddress address = client.discoverHost(500, 600);
                        //System.out.println(address);


                        try {
                            client.connect(5000, "192.168.82.1", 500, 600);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        UserInfo info = new UserInfo();
                        info.setUserId("120123399");
                        info.setUserName("Ahmed");
                        client.sendTCP(info);
                        client.addListener(new Listener() {
                            public void received(Connection connection, Object object) {

                                if (object instanceof LockMessage) {

                                    boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
                                    if (isAdmin) {
                                        mDevicePolicyManager.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                                        mDevicePolicyManager.setPasswordMinimumLength(mComponentName, 5);
                                        mDevicePolicyManager.resetPassword("123456", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                                        mDevicePolicyManager.lockNow();
                                        handler.post(new Runnable() {

                                            @Override
                                            public void run() {
                                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
                                                final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                                            }
                                        });

                                    } else {
                                        Log.i("addd", "Not admin..........");
                                    }
                                } else if (object instanceof UnlockMessage) {
                                    mDevicePolicyManager.setPasswordQuality(mComponentName, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
                                    mDevicePolicyManager.setPasswordMinimumLength(mComponentName, 0);
                                    mDevicePolicyManager.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
                                        }
                                    });
                                }
                            }
                        });

                    }
                }).start();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
            }
        }
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
}
