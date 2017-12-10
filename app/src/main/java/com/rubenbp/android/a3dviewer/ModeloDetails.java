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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rubenbp.android.a3dviewer.jpct.JPCTActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ModeloDetails extends AppCompatActivity {

    private String URL="http://192.168.0.104/rest_service/get_data?id=";
    //private String URL="http://10.143.155.77/rest_service/get_data?id=";

    private String idModel="";
    private TextView nombreModeloTextView;
    private TextView extensionModeloTextView;
    private TextView tamannoModeloTextView;
    private Button visor3DButton;
    private Button visorRVButton;
    private Button descargarButton;
    private File modeloTempFileDir;
    private File modeloAbsolutFileDir;
    private boolean descargarButtonState;

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
            visor3DButton.setEnabled(true);
            visorRVButton.setEnabled(true);
            descargarButton.setEnabled(true);
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
            visor3DButton=(Button)findViewById(R.id.detalle_button_visor3D);
            visorRVButton=(Button)findViewById(R.id.detalle_button_realidad_virtual);
            descargarButton=(Button)findViewById(R.id.detalle_button_descargar);

            visor3DButton.setEnabled(false);
            visorRVButton.setEnabled(false);
            descargarButton.setEnabled(false);


            Intent intent = getIntent();

            String id= intent.getStringExtra("id");
            idModel=id;
            String nombreModelo=intent.getStringExtra("nombre");
            Log.v("NOMBREmODELO",nombreModelo);
            String extensionModelo=intent.getStringExtra("extension");
            String tamannoModelo=intent.getStringExtra("tamanno");

            nombreModeloTextView.setText(getResources().getString(R.string.nombre_detalle)+" "+nombreModelo);
            extensionModeloTextView.setText(getResources().getString(R.string.extension_detalle)+" "+extensionModelo);
            tamannoModeloTextView.setText(getResources().getString(R.string.tamanno_detalle)+" "+tamannoModelo);
            modeloAbsolutFileDir= new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "3DViewer" + File.separator + "modelos" + File.separator + idModel);


            URL=URL+idModel;
            final ModeloFileAsyncTask modeloFileAsyncTask= new ModeloFileAsyncTask();
            modeloFileAsyncTask.execute(URL);


            //Cuando pulso accede a el archivo temporal del modelo y se lo manda a el activity
            visor3DButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent= new Intent(getApplicationContext(), JPCTActivity.class);
                    intent.putExtra("hola",modeloTempFileDir);
                    startActivity(intent);
                }
            });
            //Cuando pulso muevo el archivo temporal a la ubicacion fija
            // TODO Debo hacer que sepa que cuando se descarga, cambie el boton a borrar y el path de los modelos pase a el absoluto
            descargarButtonState=false;
            descargarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //descarga
                    if(!descargarButtonState) {
                        try {
                            FileUtils.copyDirectory(modeloTempFileDir, modeloAbsolutFileDir, true);
                            modeloTempFileDir.deleteOnExit();
                            Toast.makeText(getApplicationContext(), "MODELO DESCARGADO ", Toast.LENGTH_SHORT).show();
                            descargarButton.setBackgroundColor(getResources().getColor(R.color.red_button));
                            descargarButton.setText(getResources().getString(R.string.borrar_button));
                            descargarButtonState=true;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //borra
                    }else
                        {
                            try {
                                //antes de poder borrar un directorio debo borrar su contenido
                                // TODO tengo un bug que no me deja borrar una carpeta cuando hay otra dentro
                                FileUtils.cleanDirectory(modeloAbsolutFileDir);
                                modeloAbsolutFileDir.delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //modeloAbsolutFileDir.delete();
                            Toast.makeText(getApplicationContext(), "MODELO BORRADO ", Toast.LENGTH_SHORT).show();
                            descargarButton.setBackgroundColor(getResources().getColor(R.color.green_button));
                            descargarButton.setText(getResources().getString(R.string.descargar_button));
                            descargarButtonState=false;

                        }


                }
            });






    }
    /*public void OnClickDownloadButton(View view)
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
    }*/
}
