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

    /** Name of the database file */
    private static final String DATABASE_NAME = "a3dviewer.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ModelosDbHelper}.
     *
     * @param context of the app
     */
    public ModelosDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_MODELS_TABLE =  "CREATE TABLE " + ModelEntry.TABLE_NAME + " ("
                + ModelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ModelEntry.COLUMN_MODEL_NAME + " TEXT NOT NULL UNIQUE, "
                + ModelEntry.COLUMN_MODEL_EXTENSION + " TEXT NOT NULL, "
                + ModelEntry.COLUMN_MODEL_PATH + " TEXT NOT NULL, "
                + ModelEntry.COLUMN_MODEL_SIZE + " TEXT NOT NULL, "
                + ModelEntry.COLUMN_MODEL_ANIMATION + " TEXT NOT NULL, "
                + ModelEntry.COLUMN_MODEL_DOWNLOAD_ID + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_MODELS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}