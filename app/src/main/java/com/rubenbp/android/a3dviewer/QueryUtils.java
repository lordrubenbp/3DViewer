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
import java.util.ArrayList;
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



    public static File unZipIt(File zipFile){

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

            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                //System.out.println("file unzip : "+ newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
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

            //System.out.println("Done");

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
        // Create URL object
        URL url = createUrl(requestUrl);

       File file=null;
       File dirTempFile=null;
        try {
            file = makeHttpRequestForFile(url,id);
            Log.v("RESPONSE",file.getName());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        dirTempFile=unZipIt(file);

        return dirTempFile;

    }

    public static List<Modelo> fetchModeloData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
            Log.v("RESPONSE",jsonResponse);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<Modelo> modelos = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return modelos;
    }
    private static List<Modelo> extractFeatureFromJson(String modeloJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(modeloJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Modelo> modelos = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {


            JSONArray modelosArray = new JSONArray(modeloJSON);

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < modelosArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
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
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
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

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static File makeHttpRequestForFile(URL url, int id) throws IOException {

        String idString=id+".zip";

        //File file= new File(Environment.getExternalStorageDirectory()+idString);

        //File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), idString);

        File file = new File(context.getCacheDir().getAbsoluteFile(), idString);



        Log.v("FILE",file.getAbsolutePath());

        if(file.exists())
        {
            Log.v("FILE","EXISTE");
        }
        else
            //file.createNewFile();
            Log.v("FILE",file.getAbsolutePath());


        // If the URL is null, then return early.
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

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
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
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return file;
    }
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
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

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
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
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
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


}
