package com.rubenbp.android.a3dviewer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rubenbp.android.a3dviewer.jpct.JPCTActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ModeloDetails extends AppCompatActivity {

    private String URL="http://192.168.0.104/rest_service/get_data?id=";
    private String idModel="";
    private TextView nombreModeloTextView;
    private TextView extensionModeloTextView;
    private TextView tamannoModeloTextView;
    private File modeloTempFileDir;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private class ModeloFileAsyncTask extends AsyncTask<String, Void, File> {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected File doInBackground(String... urls) {

            String result="";
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            return QueryUtils.fetchModeloFile(urls[0],Integer.parseInt(idModel),getApplicationContext());


        }
        @Override
        protected void onPostExecute(File file) {

            modeloTempFileDir=file;
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


            //verifyStoragePermissions(this);

            nombreModeloTextView=(TextView)findViewById(R.id.detalle_modelo_nombre);
            extensionModeloTextView=(TextView)findViewById(R.id.detalle_modelo_extension);
            tamannoModeloTextView=(TextView)findViewById(R.id.detalle_modelo_tamaño);

            Intent intent = getIntent();

            String id= intent.getStringExtra("id");
            idModel=id;
            String nombreModelo=intent.getStringExtra("nombre");
            String extensionModelo=intent.getStringExtra("extension");
            String tamannoModelo=intent.getStringExtra("tamanno");

            nombreModeloTextView.setText("Nombre: "+nombreModelo);
            extensionModeloTextView.setText("Extension: "+extensionModelo);
            tamannoModeloTextView.setText("Tamaño: "+tamannoModelo);

            URL=URL+idModel;
            ModeloFileAsyncTask modeloFileAsyncTask= new ModeloFileAsyncTask();
            modeloFileAsyncTask.execute(URL);
            idModel=id;




    }
    public void OnClickDownloadButton(View view)
    {

        File outputFolder= new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),"3DViewer"+File.separator+"modelos"+File.separator+idModel);

        try {
            //FileUtils.moveDirectory(modeloTempFileDir,outputFolder);
            FileUtils.copyDirectory(modeloTempFileDir,outputFolder,true);
            Toast.makeText(this,"MODELO DESCARGADO ",Toast.LENGTH_SHORT).show();
            modeloTempFileDir.deleteOnExit();
            Intent intent= new Intent(this, JPCTActivity.class);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
