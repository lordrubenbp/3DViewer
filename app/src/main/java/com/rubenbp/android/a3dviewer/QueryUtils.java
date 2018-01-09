package com.rubenbp.android.a3dviewer;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by ruben on 22/11/2017.
 */

public class QueryUtils {


    private static final String LOG_TAG ="URL format";
    private static Context context;


    private QueryUtils() {
    }



    public static File unZipIt(File zipFile,Context context){


        byte[] buffer = new byte[1024];

        Log.v("NOMBRE FILE", zipFile.getName());
        String[]splitIdModelo=zipFile.getName().split("\\.");
        String idModelo=splitIdModelo[0];

        //File outputFolder= new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),"modelos"+File.separator+idModelo);
        File outputFolder= new File(context.getCacheDir().getAbsoluteFile(),"modelos"+File.separator+idModelo);


        Log.v("OUTPUTFOLDER",outputFolder.getAbsolutePath());

        try{



            if(!outputFolder.exists()){
                outputFolder.mkdir();
            }

            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);


                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
            zipFile.delete();

        }catch(IOException ex){
            ex.printStackTrace();
        }

        Log.v("UNZIP","DONE");
        return outputFolder;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static File fetchModeloFile(String requestUrl, int id, Context applicationContext)
    {
        context=applicationContext;
        URL url = createUrl(requestUrl);

       File file=null;
       File dirTempFile=null;
        try {
            file = makeHttpRequestForFile(url,id);
            Log.v("RESPONSE",file.getName());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        dirTempFile=unZipIt(file,context);

        return dirTempFile;

    }

    public static List<Modelo> fetchModeloData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
            Log.v("RESPONSE",jsonResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Modelo> modelos = extractFeatureFromJson(jsonResponse);

        return modelos;
    }
    private static List<Modelo> extractFeatureFromJson(String modeloJSON) {
        if (TextUtils.isEmpty(modeloJSON)) {
            return null;
        }
        List<Modelo> modelos = new ArrayList<>();

        try {


            JSONArray modelosArray = new JSONArray(modeloJSON);


            for (int i = 0; i < modelosArray.length(); i++) {


                JSONObject currentModelo = modelosArray.getJSONObject(i);

                int id= currentModelo.getInt("id");

                String nombre=currentModelo.getString("nombre");

                String tipo=currentModelo.getString("tipo");

                String tamanno=currentModelo.getString("tamaño");

                String extension=currentModelo.getString("extension");

                Modelo modelo= new Modelo(id,nombre,tipo,tamanno,extension);

                modelos.add(modelo);

            }

        } catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return modelos;
    }
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private static File makeHttpRequestForFile(URL url, int id) throws IOException {

        String idString=id+".zip";

        File file = new File(context.getCacheDir().getAbsoluteFile(), idString);

        Log.v("FILE",file.getAbsolutePath());

        if(file.exists())
        {
            Log.v("FILE","EXISTE");
        }
        else

            Log.v("FILE",file.getAbsolutePath());

        if (url == null) {
            return file;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                FileUtils.copyInputStreamToFile(inputStream, file);

               Log.v("TAMAÑO_ARCHIVO",FileUtils.byteCountToDisplaySize(file.length()));

            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the model3D JSON results.", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {

                inputStream.close();
            }
        }
        return file;
    }
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the model3D JSON results.", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {

                inputStream.close();
            }
        }
        return jsonResponse;
    }



    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

   public static void deleteRecursive(File dir)
    {
        Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                File temp = new File(dir, children[i]);
                if (temp.isDirectory())
                {
                    Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
                    deleteRecursive(temp);
                }
                else
                {
                    Log.d("DeleteRecursive", "Delete File" + temp.getPath());
                    boolean b = temp.delete();
                    if (b == false)
                    {
                        Log.d("DeleteRecursive", "DELETE FAIL");
                    }
                }
            }

        }
        dir.delete();
    }

}
