package com.rubenbp.android.a3dviewer;

import android.content.ContentUris;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

/**
 * Clase que representa a cada fragment que aparece en la pantalla de mis objetos 3D
 */
public class MisModelosFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private GridView mGridView;
    private ModelosCursorAdapter mAdapter;
    private static final int MODEL_LOADER = 0;
    //variable que recibo del activity que llama a los fragment y que determina que consulta a la base de datos realizo en este fragment
    private int tipo=0;

    public MisModelosFragment()
    {

    }

    /**
     * Funcion que recibe el tipo de fragment del adapter
     * @param tipo
     */
    public void setTipo(int tipo)
    {
        this.tipo=tipo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.modelos_list, container, false);

        //Layout donde se van a mostrar los diferentes objetos 3D
        mGridView = (GridView) rootView.findViewById(R.id.gridview);

        mAdapter = new ModelosCursorAdapter(this.getContext(),null);

        mGridView.setAdapter(mAdapter);

        //fuerzo a que se lance el loader adecuado a las bases de datos sqlite

        getLoaderManager().initLoader(MODEL_LOADER, null, this);

        //Cada vez que pulso un elemento...

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), SubirModeloActivity.class);
                //cada vez que pulse alguno de los elementos de la lista, se pasa su enlace correspondiente en la base de datos
                Uri currentPetUri = ContentUris.withAppendedId(ModelosContract.ModelEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);
                startActivity(intent);
            }
        });
        return rootView;

    }
        @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
      //projection sera donde yo ponga todas aquellas columnas que quiero que se vean afectadas en la consulta
        String[] projection = {
                ModelosContract.ModelEntry._ID,
                ModelosContract.ModelEntry.COLUMN_MODEL_NAME,
                ModelosContract.ModelEntry.COLUMN_MODEL_EXTENSION,
                ModelosContract.ModelEntry.COLUMN_MODEL_ANIMATION,
                ModelosContract.ModelEntry.COLUMN_MODEL_PATH,
                ModelosContract.ModelEntry.COLUMN_MODEL_DOWNLOAD_ID,
                ModelosContract.ModelEntry.COLUMN_MODEL_SIZE,
        };

        //dependiendo de el tipo de fragment en el que yo me mueva, hare una consulta u otra
            if(tipo==0) {
                //para los objetos animados
                String[] animado = new String[1];
                animado[0] = "animado";
                return new CursorLoader(getActivity(),
                        ModelosContract.ModelEntry.CONTENT_URI,
                        projection,
                        ModelosContract.ModelEntry.COLUMN_MODEL_ANIMATION + "=?",
                        animado,
                        null);
            }
            else if(tipo==1)
            {
                //para los objetos no animados
                String[] animado = new String[1];
                animado[0] = "no animado";
                return new CursorLoader(getActivity(),
                        ModelosContract.ModelEntry.CONTENT_URI,
                        projection,
                        ModelosContract.ModelEntry.COLUMN_MODEL_ANIMATION + "=?",
                        animado,
                        null);
            }
            else
                {
                    //para los objetos subidos solo por mi
                    String[] downloadID = new String[1];
                    downloadID[0] = "0";
                    return new CursorLoader(getActivity(),
                            ModelosContract.ModelEntry.CONTENT_URI,
                            projection,
                            ModelosContract.ModelEntry.COLUMN_MODEL_DOWNLOAD_ID + "=?",
                            downloadID,
                            null);
                }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //Cuando termine la carga de la base de datos, se carga el resultado en la lista de modelos
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);

    }

}
