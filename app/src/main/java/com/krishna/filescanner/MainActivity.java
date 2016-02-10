package com.krishna.filescanner;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import filescanner.krishna.com.filescanner.R;

public class MainActivity extends AppCompatActivity implements TaskCallBack{

    public ShareActionProvider mShareActionProvider;
    public static final int NOT_ID = (int) System.currentTimeMillis();//3995
    private ScannerTask scannerTask = null;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb = new StringBuilder();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView currentDirTextView = (TextView) findViewById(R.id.currentDirTextView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("Back pressed.....");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOT_ID);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (scannerTask != null
                && scannerTask.getStatus() != ScannerTask.Status.FINISHED) {
            scannerTask.cancel(true);
            System.out.println("Closing async task..");
            scannerTask = null;
        }
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOT_ID);
    }

    public void startScan(View scanButton) {
        if(scannerTask != null
                && scannerTask.getStatus() != ScannerTask.Status.FINISHED){
            //Scanning in progress...
            scannerTask.cancel(true);
            ((Button)findViewById(R.id.startButton)).setText(R.string.start_scan);
        }else{
            scannerTask = new ScannerTask(this);
            scannerTask.setMainActivity(this);
            scannerTask.execute();
            ((Button)findViewById(R.id.startButton)).setText(R.string.stop_scan);
        }
        createNotification();
    }

    public void getBigFileList(View v) {
        //FileUtils.getBigFileList();
        createNotification();
    }



    private void createNotification() {
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle("File Scanner")
                        .setContentText("Scanning in progress...");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(NOT_ID, mBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);


        //startActivity(Intent.createChooser(sharingIntent, "Share via"));

        return true;
    }



    public void getContentToShare(){

        ArrayList<File> bigList = FileUtils.getBigFileList();
        sb = new StringBuilder();
        sb.append("Avg Size: " + FileUtils.getAverageFileSize() + "\n");
        Map<String, Long> freqExt = FileUtils.getFreqExt();
        sb.append("Frequent file extensions: ");
        for (String ext : freqExt.keySet()) {
            sb.append("\n\t" + ext + " : " + String.valueOf(freqExt.get(ext)));
        }
        sb.append("\n Biggest files..");
        int i=1;
        for (File f : bigList) {
            sb.append("\tFile " + i+" : " + f.getAbsolutePath() + "; Size: " + f.length() + " bytes.\n\n");
            i++;
        }

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "File Scanning Results");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sb.toString());

        mShareActionProvider.setShareIntent(sharingIntent);


    }

    @Override
    public void done() {
        getContentToShare();
    }
}
