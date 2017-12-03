package com.rubenbp.android.a3dviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruben on 22/11/2017.
 */

public class AnimadosFragment extends Fragment {

    private final String URL="http://192.168.0.104/rest_service/get_all_modelos_animados";
    private ProgressBar mProgressBar;
    private GridView mGridView;
    private TextView mErrorMessage;
    private ModeloAsyncTask task;


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
        protected void onPreExecute() {

            mProgressBar.setVisibility(View.VISIBLE);
            mErrorMessage.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(List<Modelo> data) {
            // Clear the adapter of previous earthquake data
            // TODO Falta el ModeloAdapter que se ocupa de coger los datos extraidos y colocarlos en el layout

            if(data==null)
            {
                Log.v("CONEXION","todo mallll");
                mErrorMessage.setVisibility(View.VISIBLE);
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            //mGridView.setVisibility(View.VISIBLE);
            mAdapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }

    public AnimadosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.modelos_list, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        mErrorMessage=(TextView)rootView.findViewById(R.id.error_message);

        task = new ModeloAsyncTask();
        task.execute(URL);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new ModeloAdapter(this.getContext(), new ArrayList<Modelo>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        mGridView.setAdapter(mAdapter);

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
                startActivity(intent);

                // Send the intent to launch a new activity
                //startActivity(websiteIntent);

            }
        });

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible()) {
            // If we are becoming invisible, then...
            if (!isVisibleToUser&& (mErrorMessage.getVisibility()==View.VISIBLE)) {
                task= new ModeloAsyncTask();
                task.execute(URL);
                Log.v("AnimadosFragment", "Reintentar conexion");

            }
        }
    }



}
