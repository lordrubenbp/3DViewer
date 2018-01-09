package com.rubenbp.android.a3dviewer.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rubenbp.android.a3dviewer.R;
import com.rubenbp.android.a3dviewer.SQLite.ModelosContract.ModelEntry;

/**
 * Created by ruben on 21/12/2017.
 */

public class ModelosCursorAdapter extends CursorAdapter {


    //0 animado
    //1 noanimado
    //2 subidos

    /**
     * Clase que parsea los datos obtenidos de la consulta a la base de datos, a la interfaz de usuario, es decir a la ficha o tarjeta de cada modelo
     * @param context
     * @param c
     */


    public ModelosCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.modelo_simple_layout, parent, false);
    }

  //funcion en la que se recorre el cursor resultante de la consulta a la base de datos y determina que campo de la base de datos se corresponde con cada elemento
  //de la interfaz
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //capturo los elementos de la interfaz que me interesa que sean rellenados por los datos resultante de la consulta a la base de datos
        TextView nameModelo = (TextView) view.findViewById(R.id.modelo_nombre);
        TextView animadoModelo = (TextView) view.findViewById(R.id.modelo_tipo);
        ImageView modeloImagen=(ImageView)view.findViewById(R.id.modelo_img);

        //identifico las  columnas de la base de datos
        int nameColumnIndex = cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_NAME);
        int animadoColumnIndex = cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_ANIMATION);
        int donwloadIdColumnIndex =cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_DOWNLOAD_ID);

        //obtengo el valor de cada columna
        String modeloName = cursor.getString(nameColumnIndex);

        String modeloAnimado = cursor.getString(animadoColumnIndex);

        int modeloDownloadid=cursor.getInt(donwloadIdColumnIndex);

        //En mi aplicacion todos aquellos modelos cuya downloadID sea 0, son subidos por mi, asi soy capaz de filtrar aqui el color a mostrar en la lista
        if(modeloDownloadid==0)
        {
            modeloImagen.setBackgroundColor( context.getResources().getColor(R.color.green_button));

        }

        //Doy a cada elementos de la interfaz el valor obtenido en la base de datos
        nameModelo.setText(modeloName);
        animadoModelo.setText(modeloAnimado);
        modeloImagen.setImageResource(R.drawable.objeto3d);
    }
}
