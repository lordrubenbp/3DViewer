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

    private int tipo =0;

    //0 animado
    //1 noanimado
    //2 subidos
    /**
     * Constructs a new {@link ModelosCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ModelosCursorAdapter(Context context, Cursor c, int tipo) {
        super(context, c, 0 /* flags */);
        this.tipo=tipo;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.modelo_simple_layout, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameModelo = (TextView) view.findViewById(R.id.modelo_nombre);
        TextView animadoModelo = (TextView) view.findViewById(R.id.modelo_tipo);
        ImageView modeloImagen=(ImageView)view.findViewById(R.id.modelo_img);


        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_NAME);
        int animadoColumnIndex = cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_ANIMATION);
        int donwloadIdColumnIndex =cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_DOWNLOAD_ID);

        // Read the pet attributes from the Cursor for the current pet
        String modeloName = cursor.getString(nameColumnIndex);

        String modeloAnimado = cursor.getString(animadoColumnIndex);

        int modeloDownloadid=cursor.getInt(donwloadIdColumnIndex);

        if(modeloDownloadid==0)
        {
            modeloImagen.setBackgroundColor( context.getResources().getColor(R.color.green_button));

        }

        // Update the TextViews with the attributes for the current pet
        nameModelo.setText(modeloName);
        animadoModelo.setText(modeloAnimado);
        modeloImagen.setImageResource(R.drawable.objeto3d);
    }
}
