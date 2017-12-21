package com.rubenbp.android.a3dviewer;

import android.content.ContentUris;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


import com.rubenbp.android.a3dviewer.SQLite.ModelosContract;
import com.rubenbp.android.a3dviewer.SQLite.ModelosCursorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruben on 21/12/2017.
 */

public class MisModelosFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private GridView mGridView;
    private ModelosCursorAdapter mAdapter;
    private static final int MODEL_LOADER = 0;
    private int tipo=0;

    public MisModelosFragment()
    {

    }

    public void setTipo(int tipo)
    {
        this.tipo=tipo;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.modelos_list, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);

        mAdapter = new ModelosCursorAdapter(this.getContext(),null,tipo);

        mGridView.setAdapter(mAdapter);

        getLoaderManager().initLoader(MODEL_LOADER, null, this);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(getActivity(), SubirModeloActivity.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link PetEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.pets/pets/2"
                // if the pet with ID 2 was clicked on.
                Uri currentPetUri = ContentUris.withAppendedId(ModelosContract.ModelEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPetUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });
        return rootView;

    }
        @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                ModelosContract.ModelEntry._ID,
                ModelosContract.ModelEntry.COLUMN_MODEL_NAME,
                ModelosContract.ModelEntry.COLUMN_MODEL_EXTENSION,
                ModelosContract.ModelEntry.COLUMN_MODEL_ANIMATION,
                ModelosContract.ModelEntry.COLUMN_MODEL_PATH,
                ModelosContract.ModelEntry.COLUMN_MODEL_DOWNLOAD_ID,
                ModelosContract.ModelEntry.COLUMN_MODEL_SIZE,
        };

        // This loader will execute the ContentProvider's query method on a background thread
            if(tipo==0) {
                String[] animado = new String[1];
                animado[0] = "animado";
                return new CursorLoader(getActivity(),   // Parent activity context
                        ModelosContract.ModelEntry.CONTENT_URI,         // Query the content URI for the current pet
                        projection,             // Columns to include in the resulting Cursor
                        ModelosContract.ModelEntry.COLUMN_MODEL_ANIMATION + "=?",                   // No selection clause
                        animado,                   // No selection arguments
                        null);                  // Default sort order
            }
            else if(tipo==1)
            {
                String[] animado = new String[1];
                animado[0] = "no animado";
                return new CursorLoader(getActivity(),   // Parent activity context
                        ModelosContract.ModelEntry.CONTENT_URI,         // Query the content URI for the current pet
                        projection,             // Columns to include in the resulting Cursor
                        ModelosContract.ModelEntry.COLUMN_MODEL_ANIMATION + "=?",                   // No selection clause
                        animado,                   // No selection arguments
                        null);                  // Default sort order
            }
            else
                {
                    String[] downloadID = new String[1];
                    downloadID[0] = "0";
                    return new CursorLoader(getActivity(),   // Parent activity context
                            ModelosContract.ModelEntry.CONTENT_URI,         // Query the content URI for the current pet
                            projection,             // Columns to include in the resulting Cursor
                            ModelosContract.ModelEntry.COLUMN_MODEL_DOWNLOAD_ID + "=?",                   // No selection clause
                            downloadID,                   // No selection arguments
                            null);                  // Default sort order
                }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);

    }

}
