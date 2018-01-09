package com.rubenbp.android.a3dviewer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
Clase encargada de gestionar los distintos Fragments del apartado de Descargas de modelos 3D
 */
public class DescargarModelosAdapter extends FragmentPagerAdapter {


    private Context mContext;
    //las dos url del servidor donde se listan los modelos de un tipo y otro
    private  String urlAnimados="http://192.168.0.104/rest_service/get_all_modelos_animados";
    private String urlNoAnimados="http://192.168.0.104/rest_service/get_all_modelos_noanimados";

    //private String urlNoAnimados="http://10.143.155.77/rest_service/get_all_modelos_noanimados";
    //private  String urlAnimados="http://10.143.155.77/rest_service/get_all_modelos_animados";


    /**
     *
     * @param context
     * @param fm
     */
    public DescargarModelosAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }


    @Override
    public Fragment getItem(int position) {
        if (position == 0) {

            //creo el objeto del fragment a mostrar
            ModelosFragment modelosFragmentAnimados = new ModelosFragment();
            //le paso la url correspondiente a ese fragment. Esto lo hago porque hasta que no se carga el fragment no se inicia por lo que puedo setear sus parametros con el objeto creado
            modelosFragmentAnimados.setURL(urlAnimados);
            return modelosFragmentAnimados;

        } else {
            //creo el objeto del fragment a mostrar
            ModelosFragment modelosFragmentNoAnimados = new ModelosFragment();
            //le paso la url correspondiente a ese fragment. Esto lo hago porque hasta que no se carga el fragment no se inicia por lo que puedo setear sus parametros con el objeto creado
            modelosFragmentNoAnimados.setURL(urlNoAnimados);
            return modelosFragmentNoAnimados;
        }


    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //dependiendo de en que pantalla me mueva se muestra un nombre de pantalla u otro
        if (position == 0) {
            return mContext.getString(R.string.animados);
        } else {
            return mContext.getString(R.string.no_animados);
        }

    }
}