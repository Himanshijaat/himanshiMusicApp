package com.example.hp.musicapp;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nihaskalam.progressbuttonlibrary.CircularProgressButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hp on 12/29/2017.
 */

public class Demi extends Activity {

    private String TAG = MainActivity.class.getSimpleName();

    Button bDownload;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder build;
    int id = 1;
    ProgressBar progressBar;
    TextView tv;
    Drawable drawable;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demi);
        bDownload=findViewById(R.id.button_download);
        progressBar=findViewById(R.id.circularDProgressbar);
        tv = (TextView) findViewById(R.id.tv);

        Resources res = getResources();
        drawable = res.getDrawable(R.drawable.progressbar_round);
        progressBar.setProgress(0);   // Main Progress
        progressBar.setSecondaryProgress(100); // Secondary Progress
        progressBar.setMax(100); // Maximum Progress
        progressBar.setProgressDrawable(drawable);

        bDownload.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {


                bDownload.setVisibility(View.INVISIBLE);
             // setProgress(progressBar);
                progressBar.setProgress(0);   // Main Progress
                progressBar.setSecondaryProgress(100); // Secondary Progress
                progressBar.setMax(100); // Maximum Progress
                progressBar.setProgressDrawable(drawable);

                mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                build = new NotificationCompat.Builder(getApplicationContext());
                build.setContentTitle("Downloading")
                        .setContentText("Download in progress")
                        .setSmallIcon(R.mipmap.ic_launcher);
                new GetContacts().execute();

            }
        });
    }
    /**
     * Async task class to get json by making HTTP call
     */

    private class GetContacts extends AsyncTask<Void, Integer, Integer> {

        @SuppressLint("NewApi")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(progress_bar_type);

            // Displays the progress bar for the first time.
            build.setProgress(100, 0, false);
            mNotifyManager.notify(id, build.build());

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onProgressUpdate(Integer... values) {
            build.setProgress(100, values[0], false);
            mNotifyManager.notify(id, build.build());

            progressBar.setProgress(values[0]);
            //tv.setText(values[0]+"%");

            super.onProgressUpdate(values);
        }

        @Override
        protected Integer doInBackground(Void... arg0) {

            URL url = null;

            try {
                url = new URL("http://arbon.in/songsDownload.php");

                String data = URLEncoder.encode("user_name", "UTF-8")
                        + "=" + URLEncoder.encode("apper", "UTF-8");
                data += "&" + URLEncoder.encode("password", "UTF-8")
                        + "=" + URLEncoder.encode("P@ssw0rd", "UTF-8");
                data += "&" + URLEncoder.encode("song_id", "UTF-8")
                        + "=" + URLEncoder.encode("SG3", "UTF-8");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                Log.d("hiiiiiiiii", String.valueOf(url));

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();

                String jsonStr=null;
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line + "\n");
                }

                Log.d("data", "sb data "+sb);

                int i;
                for (i = 0; i <= 100; i++) {
                    // Sets the progress indicator completion percentage
                    publishProgress(i);
                    // Sleep for 5 milliseconds
                    Thread.sleep(250);
                }

                jsonStr = sb.toString();
                if (jsonStr != null) {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String name = jsonObj.getString("fileName");
                    String data1 = jsonObj.getString("data");
                    String mime = jsonObj.getString("mime");

                    File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/" + name);
                    FileOutputStream os = new FileOutputStream(file, true);
                    os.write(Base64.decode(data1, Base64.NO_WRAP));

                    os.flush();
                    os.close();

                    Log.d(TAG, name);
                    Log.d(TAG, String.valueOf(file));
                    Log.d(TAG, mime);

                }else {
                        Log.d("msg","no data get");
                    }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute (Integer result) {
            super.onPostExecute(result);

            build.setContentText("Download complete");
            // Removes the progress bar

            build.setProgress(0, 0, false);
            mNotifyManager.notify(id, build.build());

            progressBar.setProgress(100);

            bDownload.setVisibility(View.VISIBLE);

        }

    }

}
