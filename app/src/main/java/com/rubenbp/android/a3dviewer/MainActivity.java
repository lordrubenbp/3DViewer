package com.rubenbp.android.a3dviewer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.rubenbp.android.a3dviewer.jpct.JPCTActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de la pantalla principal de la app
 */
public class MainActivity extends AppCompatActivity {

    //Array donde se almacenan todos los permisos que necesito en la app para despues ser requeridos al usuario
    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };
    //Layouts de la interfaz que necesitan ser declarados para poder interactuar con ellos al ser pulsados
    LinearLayout button_download03d;
    LinearLayout button_upload03d;
    LinearLayout button_my03d;

    /**
     * Funcion que pide al usuario los permisos necesarios para la app
     * @return
     */
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            return;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //chequea que los permisos han sido garantizados, si ya se han garantizado no se volver a mostrar
        checkPermissions();

        //le doy a cada Layaout su referencia en la interfaz
        button_download03d= (LinearLayout) findViewById(R.id.main_activity_downloadO3D);
        button_upload03d=(LinearLayout)findViewById(R.id.main_activity_uploadO3D);
        button_my03d=(LinearLayout)findViewById(R.id.main_activity_myO3D);

        //Especifico que hace cada Layout al ser pulsados
        button_download03d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(getApplicationContext(),ModelosActivity.class);
                startActivity(intent);
            }
        });
        button_upload03d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),SubirModeloActivity.class);
                startActivity(intent);

            }
        });
        button_my03d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(getApplicationContext(),MisModelosActivity.class);
                startActivity(intent);
            }
        });


    }



}
