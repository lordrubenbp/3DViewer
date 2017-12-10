package com.rubenbp.android.a3dviewer;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class CategoryAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;
    private  String urlAnimados="http://192.168.0.104/rest_service/get_all_modelos_animados";
    private String urlNoAnimados="http://192.168.0.104/rest_service/get_all_modelos_noanimados";

    //private String urlNoAnimados="http://10.143.155.77/rest_service/get_all_modelos_noanimados";
    //private  String urlAnimados="http://10.143.155.77/rest_service/get_all_modelos_animados";




    /**
     * Create a new {@link CategoryAdapter} object.
     *
     * @param context is the context of the app
     * @param fm is the fragment manager that will keep each fragment's state in the adapter
     *           across swipes.
     */
    public CategoryAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * Return the {@link Fragment} that should be displayed for the given page number.
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {

            ModelosFragment modelosFragmentAnimados = new ModelosFragment();
            modelosFragmentAnimados.setURL(urlAnimados);
            //return new ModelosFragment();
            return modelosFragmentAnimados;

        } else {
            ModelosFragment modelosFragmentNoAnimados = new ModelosFragment();
            modelosFragmentNoAnimados.setURL(urlNoAnimados);
            //return new NoAnimadosFragment();
            return modelosFragmentNoAnimados;
        }


    }
    /**
     * Return the total number of pages.
     */
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.animados);
        } else {
            return mContext.getString(R.string.no_animados);
        }

    }
}