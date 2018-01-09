package com.rubenbp.android.a3dviewer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruben on 22/11/2017.
 */

public  class ModelosFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Modelo>>,
        SharedPreferences.OnSharedPreferenceChangeListener  {

    private static final int MODELO_LOADER_ID =1 ;

    private String URL="";
    private ProgressBar mProgressBar;
    private GridView mGridView;
    private TextView mErrorMessage;


    private ModeloAdapter mAdapter;

    @Override
    public android.support.v4.content.Loader<List<Modelo>> onCreateLoader(int id, Bundle args) {



        mProgressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        return new ModeloLoader(this.getContext(),URL);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Modelo>> loader, List<Modelo> modelos) {

        mProgressBar.setVisibility(View.GONE);

        mAdapter.clear();

        if (modelos != null && !modelos.isEmpty()) {
            mAdapter.addAll(modelos);
        }
        else
            {
                mErrorMessage.setText("sin modelos disponibles");
                mErrorMessage.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Modelo>> loader) {

        mAdapter.clear();
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            mAdapter.clear();

            mErrorMessage.setVisibility(View.GONE);

            mProgressBar.setVisibility(View.VISIBLE);

            getLoaderManager().restartLoader(MODELO_LOADER_ID, null, this);

    }


    public ModelosFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.modelos_list, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mErrorMessage=(TextView)rootView.findViewById(R.id.error_message);

        //task = new ModeloAsyncTask();
        //task.execute(URL);

        mAdapter = new ModeloAdapter(this.getContext(), new ArrayList<Modelo>());

        mGridView.setAdapter(mAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        prefs.registerOnSharedPreferenceChangeListener(this);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                Modelo currentModelo = mAdapter.getItem(position);

                Intent intent= new Intent(getActivity(),ModeloDetails.class);
                intent.putExtra("id",currentModelo.getId()+"");
                Log.v("NOMBRE",currentModelo.getNombre());
                intent.putExtra("nombreEditText",currentModelo.getNombre());
                long tamanno= Long.parseLong(currentModelo.getTamanno());
                intent.putExtra("tamanno", FileUtils.byteCountToDisplaySize(tamanno));
                intent.putExtra("extensionEditText",currentModelo.getExtension());
                intent.putExtra("animationType",currentModelo.getTipo());


                startActivity(intent);

            }
        });


        ConnectivityManager connMgr = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {

            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(MODELO_LOADER_ID, null, this);
        } else {

            mProgressBar.setVisibility(View.GONE);

            mErrorMessage.setVisibility(View.VISIBLE);
        }

        return rootView;


    }


    public void setURL(String URL)
    {
        this.URL=URL;
    }



}
