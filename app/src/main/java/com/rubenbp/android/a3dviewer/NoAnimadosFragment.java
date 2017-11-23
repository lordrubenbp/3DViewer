package com.rubenbp.android.a3dviewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class NoAnimadosFragment extends Fragment {


    public final String URL="http://192.168.0.104/rest_service/get_all_modelos_noanimados";

    private ModeloAdapter mAdapter;
    private class ModeloAsyncTask extends AsyncTask<String, Void, List<Modelo>> {

        @Override
        protected List<Modelo> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Modelo> result = QueryUtils.fetchModeloData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Modelo> data) {
            // Clear the adapter of previous earthquake data
            // TODO Falta el ModeloAdapter que se ocupa de coger los datos extraidos y colocarlos en el layout
            mAdapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }

    public NoAnimadosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.modelos_list, container, false);


        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);

        ModeloAsyncTask task = new ModeloAsyncTask();
        task.execute(URL);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new ModeloAdapter(this.getContext(), new ArrayList<Modelo>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // AQUI DEBO LLAMAR AL ACTIVITY QUE MUESTRA DETALLE
            }
        });

        return rootView;
    }



}