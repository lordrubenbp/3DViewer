package com.rubenbp.android.a3dviewer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ModeloDetails extends AppCompatActivity {

    private String URL="http://192.168.0.104/rest_service/get_data?id=";
    private String idModel="";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private class ModeloFileAsyncTask extends AsyncTask<String, Void, String> {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected String doInBackground(String... urls) {

            String result="";
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            QueryUtils.fetchModeloFile(urls[0],Integer.parseInt(idModel));

            return result;
        }
        @Override
        protected void onPostExecute(String string) {

        }
    }

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modelo_details);


            verifyStoragePermissions(this);

            Intent intent = getIntent();

            String id= intent.getStringExtra("id");

            URL=URL+id;

            ModeloFileAsyncTask modeloFileAsyncTask= new ModeloFileAsyncTask();
            modeloFileAsyncTask.execute(URL);

            idModel=id;
            Toast.makeText(this,id,Toast.LENGTH_LONG).show();
    }
}
