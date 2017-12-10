package com.rubenbp.android.a3dviewer;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by ruben on 10/12/2017.
 */

public class ModeloLoader extends AsyncTaskLoader<List<Modelo>> {

    private String mUrl;


    public ModeloLoader(Context context,String url) {
        super(context);
        this.mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Override
    public List<Modelo> loadInBackground() {

        // Don't perform the request if there are no URLs, or the first URL is null.
        if (mUrl==null) {
            return null;
        }


        List<Modelo> result = QueryUtils.fetchModeloData(mUrl);

        return result;
    }
}
