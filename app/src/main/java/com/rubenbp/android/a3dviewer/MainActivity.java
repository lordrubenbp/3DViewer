package com.rubenbp.android.a3dviewer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
TODO 1 Ver si puedo poner una patron de fondo en vez de un color plano
TODO 2 Ver porque el texto se muestra de forma diferente segun resolucion pantalla aun habiendolo puesto bien con sp
TODO 3 Crear GridView donde se volcaran todos los objetos que encuentre en mi BD
TODO 4 Crear un API REST que ataque a mi base de datos para bajar y subir los objetos 3D
TODO 5 Ver como poder subir un objeto 3D a mi base de datos
TODO 6 Hacer pruebas y adaptar correctamente JPCT-AE a esta aplicacion
TODO 7 Crear pantalla donde se muestra el detalle del objeto 3D
TODO 8 Programar las pantallas fragments con sus correspondientes tabs superiores
*/

public class MainActivity extends AppCompatActivity {

    private int requestCode;
    private int grantResults[];


    public class get3DFiles  extends AsyncTask<URL,Void,File>
    {

        @Override
        protected File doInBackground(URL... urls) {

            File file=null;
            //establecemos la conexión con el destino
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) urls[0].openConnection();
                //establecemos el método jet para nuestra conexión
                //el método setdooutput es necesario para este tipo de conexiones
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                //por último establecemos nuestra conexión y cruzamos los dedos <img draggable="false" class="emoji" alt="😛" src="https://s.w.org/images/core/emoji/72x72/1f61b.png">
                urlConnection.connect();


                //File SDCardRoot = Environment.getExternalStorageDirectory();

                Context context=MainActivity.this;
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"ejemplo.html");

                Log.v("PATH ",file.getAbsolutePath());
                //utilizaremos un objeto del tipo fileoutputstream
                //para escribir el archivo que descargamos en el nuevo
                FileOutputStream fileOutput = new FileOutputStream(file);

                //leemos los datos desde la url
                InputStream inputStream = urlConnection.getInputStream();

                //obtendremos el tamaño del archivo y lo asociaremos a una
                //variable de tipo entero
                int totalSize = urlConnection.getContentLength();
                int downloadedSize = 0;

                //creamos un buffer y una variable para ir almacenando el
                //tamaño temporal de este
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                //ahora iremos recorriendo el buffer para escribir el archivo de destino
                //siempre teniendo constancia de la cantidad descargada y el total del tamaño
                //con esto podremos crear una barra de progreso
                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {

                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    Log.v("DESCARGADO", String.valueOf(downloadedSize));


                }
                //cerramos
                fileOutput.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;

        }

        @Override
        protected void onPostExecute(File file) {

            String cadena;
            FileReader f = null;
            try {
                f = new FileReader(file);
                BufferedReader b = new BufferedReader(f);
                while((cadena = b.readLine())!=null) {
                   Log.v("CADENA",cadena);
                }
                b.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }


    // TODO 9 Mirar como puedo pedir el permiso en tiempo real, sino en versiones superiores a android 6 petara

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        get3DFiles getFile= new get3DFiles();

        try {
            URL url = new URL("http://www.robotstxt.org/robotstxt.html");
            getFile.execute(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }




    }

}
