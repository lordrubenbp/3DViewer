package com.rubenbp.android.a3dviewer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.content.Loader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rubenbp.android.a3dviewer.SQLite.ModelosContract;
import com.rubenbp.android.a3dviewer.jpct.JPCTActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Clase que pertenece a la pantalla de detalle de aquellos que pertenecen a la pantalla de descargar modelo
 */
public class ModeloDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //url de la REST API para hacer una consulta por un id concreto
    private String URL="http://192.168.0.104/rest_service/get_data?id=";

    //private String URL="http://10.143.155.77/rest_service/get_data?id=";

    //id del modelo en la REST API
    private String idModel="";

    //elementos graficos
    private TextView nombreModeloTextView;
    private TextView extensionModeloTextView;
    private TextView tamannoModeloTextView;
    private Button visor3DButton;
    private Button descargarButton;
    //archivo temporal donde se vuelca el archivo de la REST API para ver previsualizado
    private File modeloTempFileDir;
    //archivo donde se almacena el modelo si se decide descargar
    private File modeloAbsolutFileDir;

    private static final int DOWNLOAD_MODEL_LOADER_ = 0;

    //variable de control para saber si el modelo ya ha sido descargado anteriormente
    private boolean modelDownloaded=false;

    //variables donde se almacenan el resultado de la consulta a la base de datos local
    private String animationType;
    private String nombreModelo;
    private String extensionModelo;
    private String tamannoModelo;

    //Loader de la base de datos local
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //creo una consulta con el campo downloadID unicamente para comprobar si ya esta presente
        String[] projection = {
                ModelosContract.ModelEntry.COLUMN_MODEL_DOWNLOAD_ID,

    };
        String[] donwloadID=new String[1];
        donwloadID[0]=idModel;
        return new CursorLoader(getApplicationContext(),
                ModelosContract.ModelEntry.CONTENT_URI,
                projection,
                ModelosContract.ModelEntry.COLUMN_MODEL_DOWNLOAD_ID + "=?",
                donwloadID,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        //cuando acabo la consulta si no obtengo ningun resultado es que el modelo no ha sido descargado previamente

        if (cursor == null || cursor.getCount() < 1) {

            modelDownloaded=false;

            Toast.makeText(this,"modelo no descargado antes",Toast.LENGTH_SHORT).show();

            return;

        }else
            {
                Toast.makeText(this,"modelo ya descargado ",Toast.LENGTH_SHORT).show();
                modelDownloaded=true;
            }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {



    }

   // Hilo que hace la peticion a la REST API con el id que tengo
    private class ModeloFileAsyncTask extends AsyncTask<String, Void, File> {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        //esta funcion me devolvera un objeto file, donde se encuentra el modelo de la REST API, de forma termporal
        protected File doInBackground(String... urls) {

            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            return QueryUtils.fetchModeloFile(urls[0],Integer.parseInt(idModel),getApplicationContext());


        }
        @Override
        protected void onPostExecute(File file) {

            modeloTempFileDir=file;
            //Una vez tenga el archivo del modelo en una carpeta temporal esta disponible para previsualizar
            visor3DButton.setEnabled(true);
            //Y para descargar
            descargarButton.setEnabled(true);
        }
    }


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modelo_details);



            nombreModeloTextView=(TextView)findViewById(R.id.detalle_modelo_nombre);
            extensionModeloTextView=(TextView)findViewById(R.id.detalle_modelo_extension);
            tamannoModeloTextView=(TextView)findViewById(R.id.detalle_modelo_tamaño);
            visor3DButton=(Button)findViewById(R.id.detalle_button_visor3D);
            descargarButton=(Button)findViewById(R.id.detalle_button_descargar);

            //hasta que no haga la consulta a la REST API estos botones estas inactivos
            visor3DButton.setEnabled(false);
            descargarButton.setEnabled(false);

            //capturo el id del elemento de la pantalla Descargar modelo, elegido
            Intent intent = getIntent();
            String id= intent.getStringExtra("id");
            idModel=id;

            //Fuerzo el inicio del loader, asi consulto en mi base de datos local
            getLoaderManager().initLoader(DOWNLOAD_MODEL_LOADER_, null, this);

            nombreModelo=intent.getStringExtra("nombreEditText");
            extensionModelo=intent.getStringExtra("extensionEditText");
            tamannoModelo=intent.getStringExtra("tamanno");
            animationType=intent.getStringExtra("animationType");

            if(animationType.equals("noanimado"))
            {
                animationType="no animado";
            }

            nombreModeloTextView.setText(getResources().getString(R.string.nombre_detalle)+" "+nombreModelo);
            extensionModeloTextView.setText(getResources().getString(R.string.extension_detalle)+" "+extensionModelo);
            tamannoModeloTextView.setText(getResources().getString(R.string.tamanno_detalle)+" "+tamannoModelo);
            modeloAbsolutFileDir= new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "3DViewer" + File.separator + "modelos" + File.separator + idModel);

            URL=URL+idModel;
            final ModeloFileAsyncTask modeloFileAsyncTask= new ModeloFileAsyncTask();
            modeloFileAsyncTask.execute(URL);

            //Cuando pulso accede a el archivo temporal del modelo y se lo manda a el activity de JPCT
            visor3DButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent= new Intent(getApplicationContext(), JPCTActivity.class);
                    intent.putExtra("modelpath",modeloTempFileDir);
                    intent.putExtra("origin","download");
                    startActivity(intent);
                }
            });

            descargarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //String[] id = {idModel};
                    //getContentResolver().delete(ModelosContract.ModelEntry.CONTENT_URI,ModelosContract.ModelEntry.COLUMN_MODEL_DOWNLOAD_ID+"=?", id);

                   if(modelDownloaded)
                    {
                        Toast.makeText(getApplicationContext(),"Modelo ya descargado",Toast.LENGTH_SHORT).show();

                    }else
                        {

                            modelDownloaded=true;

                            try {
                                FileUtils.copyDirectory(modeloTempFileDir, modeloAbsolutFileDir, true);
                                QueryUtils.deleteRecursive(modeloTempFileDir);
                                saveDownloadModelToDB();


                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }


                }
            });

    }

    private void saveDownloadModelToDB()
    { ContentValues values = new ContentValues();

        values.put(ModelosContract.ModelEntry.COLUMN_MODEL_NAME,nombreModelo);
        values.put(ModelosContract.ModelEntry.COLUMN_MODEL_EXTENSION, extensionModelo);
        values.put( ModelosContract.ModelEntry.COLUMN_MODEL_DOWNLOAD_ID,idModel );
        values.put( ModelosContract.ModelEntry.COLUMN_MODEL_PATH, modeloAbsolutFileDir.getAbsolutePath());
        values.put( ModelosContract.ModelEntry.COLUMN_MODEL_ANIMATION, animationType);
        values.put( ModelosContract.ModelEntry.COLUMN_MODEL_SIZE,tamannoModelo);

        Uri newUri = getContentResolver().insert(ModelosContract.ModelEntry.CONTENT_URI, values);

        if (newUri == null) {

            Toast.makeText(this, R.string.error_insercion,
                    Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, R.string.todo_correcto,
                    Toast.LENGTH_SHORT).show();
        }

    }

}
