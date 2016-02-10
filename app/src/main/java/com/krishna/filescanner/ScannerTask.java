package com.krishna.filescanner;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.NotificationCompat;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import filescanner.krishna.com.filescanner.R;

/**
 * Created by Krishna on 2/9/2016.
 */
public class ScannerTask extends AsyncTask<Void, String, Void> {

    private TextView currentDirTextView;
    private static ArrayList<File> files;
    private Activity mainActivity;
    private TaskCallBack mCallback;

    public ScannerTask(TaskCallBack callback) {

        this.mCallback = callback;
    }


    @Override
    protected void onPreExecute() {
        ((TextView) mainActivity.findViewById(R.id.currentDirTextView)).setVisibility(View.VISIBLE);

        ((TextView) mainActivity.findViewById(R.id.avgSizeText)).setVisibility(View.INVISIBLE);
        ((TextView) mainActivity.findViewById(R.id.freqExt)).setVisibility(View.INVISIBLE);
        ((TextView) mainActivity.findViewById(R.id.bigHeading)).setVisibility(View.INVISIBLE);
        ((TextView) mainActivity.findViewById(R.id.tenBigFiles)).setVisibility(View.INVISIBLE);

        FileUtils.initialize();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        files = new ArrayList<File>();
        String root_sd = Environment.getExternalStorageDirectory().toString();
        //root_sd = "/storage/emulated/0/Music";
        listfiles(root_sd);

        /*for(File f : files){
            System.out.println(f.getAbsolutePath());
        }*/
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        ((TextView) mainActivity.findViewById(R.id.currentDirTextView)).setVisibility(View.INVISIBLE);

        ((TextView) mainActivity.findViewById(R.id.avgSizeText)).setVisibility(View.VISIBLE);
        ((TextView) mainActivity.findViewById(R.id.freqExt)).setVisibility(View.VISIBLE);
        ((TextView) mainActivity.findViewById(R.id.bigHeading)).setVisibility(View.VISIBLE);
        ((TextView) mainActivity.findViewById(R.id.tenBigFiles)).setVisibility(View.VISIBLE);

        updateUI();
        updateNotification();
        mCallback.done();
    }

    private void updateNotification() {
        NotificationManager mNotificationManager =
                (NotificationManager) mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
// Sets an ID for the notification, so it can be updated
        android.support.v4.app.NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(mainActivity.getApplicationContext())
                .setContentTitle("File Scanner")
                .setContentText("Scanning completed...")
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.sym_def_app_icon);
        Intent notificationIntent = new Intent(mainActivity.getApplicationContext(), MainActivity.class);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(mainActivity.getApplicationContext(),
                0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotifyBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(
                MainActivity.NOT_ID,
                mNotifyBuilder.build());
    }

    private void updateUI() {
        ((Button) mainActivity.findViewById(R.id.startButton)).setText(R.string.start_scan);
        ArrayList<File> bigList = FileUtils.getBigFileList();
        StringBuilder sb = new StringBuilder();
        int i=1;
        for (File f : bigList) {
            sb.append("\tFile " + i+" : " + f.getAbsolutePath() + "; Size: " + f.length() + " bytes.\n\n");
            i++;
        }
        ((TextView) mainActivity.findViewById(R.id.tenBigFiles)).setText(sb.toString());
        ((TextView) mainActivity.findViewById(R.id.avgSizeText)).setText("Avg Size: " + FileUtils.getAverageFileSize());
        Map<String, Long> freqExt = FileUtils.getFreqExt();
        sb = new StringBuilder();
        sb.append("Frequent file extensions: ");
        for (String ext : freqExt.keySet()) {
            sb.append("\n\t" + ext + " : " + String.valueOf(freqExt.get(ext)));
        }
        ((TextView) mainActivity.findViewById(R.id.freqExt)).setText(sb.toString());
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        ((TextView) mainActivity.findViewById(R.id.currentDirTextView)).setText("Current Directory: " + values[0]);

    }


    public void listfiles(String directoryName) {
        publishProgress(directoryName);
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
                FileUtils.addToBigFiles(file);
                //System.out.println("File:" + file.getAbsolutePath());
            } else if (file.isDirectory()) {
                //System.out.println("Dir:" + file.getAbsolutePath());
                listfiles(file.getAbsolutePath());
            }
        }
    }


    public void setMainActivity(Activity a) {
        this.mainActivity = (MainActivity) a;
    }


}
