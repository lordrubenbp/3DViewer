package com.rubenbp.android.a3dviewer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Clase encargada de gestionar los Fragments de la pantalla de mis objetos 3D
 */

public class MisModelosAdapter extends FragmentPagerAdapter {


    private Context mContext;

    public MisModelosAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {

            MisModelosFragment modelosFragmentAnimados = new MisModelosFragment();
            //mando a mi objeto fragment la posicion que ocupa para despues en este hacer la consulta a mi base de datos y asi mostrar los modelos correspondientes
            modelosFragmentAnimados.setTipo(0);

            return modelosFragmentAnimados;

        } else if(position==1) {
            MisModelosFragment modelosFragmentNoAnimados = new MisModelosFragment();
            modelosFragmentNoAnimados.setTipo(1);

            return modelosFragmentNoAnimados;
        }
        else
            {
                MisModelosFragment modelosFragmentSubidos = new MisModelosFragment();
                modelosFragmentSubidos.setTipo(2);

                return modelosFragmentSubidos;
            }


    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.animados);
        } else if(position==1){
            return mContext.getString(R.string.no_animados);
        }else
            {
                return mContext.getString(R.string.subidor_pormi);
            }

    }
}
