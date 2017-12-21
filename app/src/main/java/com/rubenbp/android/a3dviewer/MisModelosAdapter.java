package com.rubenbp.android.a3dviewer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ruben on 21/12/2017.
 */

public class MisModelosAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;

    /**
     * Create a new {@link MisModelosAdapter} object.
     *
     * @param context is the context of the app
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    public MisModelosAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {

            MisModelosFragment modelosFragmentAnimados = new MisModelosFragment();
            modelosFragmentAnimados.setTipo(0);

            //return new ModelosFragment();
            return modelosFragmentAnimados;

        } else if(position==1) {
            MisModelosFragment modelosFragmentNoAnimados = new MisModelosFragment();
            modelosFragmentNoAnimados.setTipo(1);

            //return new NoAnimadosFragment();
            return modelosFragmentNoAnimados;
        }
        else
            {
                MisModelosFragment modelosFragmentSubidos = new MisModelosFragment();
                modelosFragmentSubidos.setTipo(2);

                //return new NoAnimadosFragment();
                return modelosFragmentSubidos;
            }


    }
    /**
     * Return the total number of pages.
     */
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
