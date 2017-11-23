package com.rubenbp.android.a3dviewer;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ruben on 22/11/2017.
 */

 public class ModeloAdapter extends ArrayAdapter<Modelo> {

    public ModeloAdapter(@NonNull Context context, @NonNull List<Modelo> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.modelo_simple_layout, parent, false);
        }

        Modelo currentModelo = getItem(position);


        TextView modeloName=(TextView)listItemView.findViewById(R.id.modelo_nombre);
        modeloName.setText(currentModelo.getNombre());

        TextView modeloTipo=(TextView)listItemView.findViewById(R.id.modelo_tipo);
        modeloTipo.setText(currentModelo.getTipo());

        ImageView modeloImagen=(ImageView)listItemView.findViewById(R.id.modelo_img);
        modeloImagen.setImageResource(R.drawable.objeto3d);



        return listItemView;
    }
}
