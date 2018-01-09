package com.rubenbp.android.a3dviewer;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Clase de la pantalla de mis modelos 3D
 */
public class MisModelosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_modelos);

        //al ser un activity con Fragments y al haber decidido que esos fragment se muestren con tabs, debo referenciar al viewpager de mi interfaz
        ViewPager viewPager = (ViewPager) findViewById(R.id.mis_modelos_viewpager);

        //Crea un objeto del adaptador que sepa cual fragment debe ir en cada pagina, se encargara de gestionar los fragment dependiendo de mis acciones
        MisModelosAdapter adapter = new MisModelosAdapter(this, getSupportFragmentManager());

        //asignar el adaptador a el viewpager
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.mis_modelos_tabs);

        tabLayout.setupWithViewPager(viewPager);
    }
}
