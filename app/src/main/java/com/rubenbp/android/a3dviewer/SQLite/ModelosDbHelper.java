package com.rubenbp.android.a3dviewer.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.rubenbp.android.a3dviewer.SQLite.ModelosContract.ModelEntry;

/**
 * Created by ruben on 21/12/2017.
 */

public class ModelosDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ModelosDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "a3dviewer.db";

    private static final int DATABASE_VERSION = 1;

    public ModelosDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_MODELS_TABLE =  "CREATE TABLE " + ModelEntry.TABLE_NAME + " ("
                + ModelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ModelEntry.COLUMN_MODEL_NAME + " TEXT NOT NULL UNIQUE, "
                + ModelEntry.COLUMN_MODEL_EXTENSION + " TEXT NOT NULL, "
                + ModelEntry.COLUMN_MODEL_PATH + " TEXT NOT NULL, "
                + ModelEntry.COLUMN_MODEL_SIZE + " TEXT NOT NULL, "
                + ModelEntry.COLUMN_MODEL_ANIMATION + " TEXT NOT NULL, "
                + ModelEntry.COLUMN_MODEL_DOWNLOAD_ID + " INTEGER NOT NULL DEFAULT 0);";

        db.execSQL(SQL_CREATE_MODELS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}