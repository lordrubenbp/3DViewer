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
    //private  String URL="http://192.168.0.104/rest_service/get_all_modelos_animados";
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

        // Hide loading indicator because the data has been loaded

        mProgressBar.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."


        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
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


            // Clear the ListView as a new query will be kicked off
            mAdapter.clear();

            // Hide the empty state text view as the loading indicator will be displayed
            mErrorMessage.setVisibility(View.GONE);

            // Show the loading indicator while new data is being fetched

            mProgressBar.setVisibility(View.VISIBLE);

            // Restart the loader to requery the USGS as the query settings have been updated
            getLoaderManager().restartLoader(MODELO_LOADER_ID, null, this);

    }


    public ModelosFragment() {
        // Required empty public constructor
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

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new ModeloAdapter(this.getContext(), new ArrayList<Modelo>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        mGridView.setAdapter(mAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        // And register to be notified of preference changes
        // So we know when the user has adjusted the query settings
        prefs.registerOnSharedPreferenceChangeListener(this);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // AQUI DEBO LLAMAR AL ACTIVITY QUE MUESTRA DETALLE

                // Find the current earthquake that was clicked on
                Modelo currentModelo = mAdapter.getItem(position);

                //Toast.makeText(getActivity(),currentModelo.getId()+"",Toast.LENGTH_LONG).show();

                // Create a new intent to view the earthquake URI

                Intent intent= new Intent(getActivity(),ModeloDetails.class);
                intent.putExtra("id",currentModelo.getId()+"");
                Log.v("NOMBRE",currentModelo.getNombre());
                intent.putExtra("nombreEditText",currentModelo.getNombre());
                long tamanno= Long.parseLong(currentModelo.getTamanno());
                intent.putExtra("tamanno", FileUtils.byteCountToDisplaySize(tamanno));
                intent.putExtra("extensionEditText",currentModelo.getExtension());


                startActivity(intent);

                // Send the intent to launch a new activity
                //startActivity(websiteIntent);

            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MODELO_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible

            mProgressBar.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mErrorMessage.setVisibility(View.VISIBLE);
        }

        return rootView;


    }

   /* //evento que se lanza cuando el fragment no es visible
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser&& (mErrorMessage.getVisibility()==View.VISIBLE)) {
                task= new ModeloAsyncTask();
                task.execute(URL);
                Log.v("ModelosFragment", "Reintentar conexion");

            }
        }
    }*/

    public void setURL(String URL)
    {
        this.URL=URL;
    }



}
