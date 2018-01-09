package com.rubenbp.android.a3dviewer.SQLite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.rubenbp.android.a3dviewer.SQLite.ModelosContract.ModelEntry;


/**
 * Created by ruben on 21/12/2017.
 */

public class ModelosProvider extends ContentProvider {

    public static final String LOG_TAG = ModelosContract.class.getSimpleName();

    private static final int MODELS = 100;

    private static final int MODEL_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(ModelosContract.CONTENT_AUTHORITY, ModelosContract.PATH_MODELS, MODELS);

        sUriMatcher.addURI(ModelosContract.CONTENT_AUTHORITY, ModelosContract.PATH_MODELS + "/#", MODEL_ID);
    }

    private ModelosDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ModelosDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MODELS:

                cursor = database.query(ModelEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MODEL_ID:

                selection = ModelEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ModelEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MODELS:
                return insertModel(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertModel(Uri uri, ContentValues values) {
        String name = values.getAsString(ModelEntry.COLUMN_MODEL_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Model requires a name");
        }

        String extension = values.getAsString(ModelEntry.COLUMN_MODEL_EXTENSION);
        if (extension == null) {
            throw new IllegalArgumentException("Model requires a extension");
        }

        String size = values.getAsString(ModelEntry.COLUMN_MODEL_SIZE);
        if (size == null) {
            throw new IllegalArgumentException("Model requires a size");
        }

        String animation = values.getAsString(ModelEntry.COLUMN_MODEL_ANIMATION);
        if (animation == null) {
            throw new IllegalArgumentException("Model requires a animation");
        }

        String modelPath = values.getAsString(ModelEntry.COLUMN_MODEL_PATH);
        if (modelPath == null) {
            throw new IllegalArgumentException("Model requires a model path");
        }

        Integer downloadID = values.getAsInteger(ModelEntry.COLUMN_MODEL_DOWNLOAD_ID);
        if (downloadID != null && downloadID < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ModelEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MODELS:
                return updateModel(uri, contentValues, selection, selectionArgs);
            case MODEL_ID:
                selection = ModelEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateModel(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateModel(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ModelEntry.COLUMN_MODEL_NAME)) {
            String name = values.getAsString(ModelEntry.COLUMN_MODEL_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Model requires a name");
            }
        }

        if (values.containsKey(ModelEntry.COLUMN_MODEL_EXTENSION)) {
            String extension = values.getAsString(ModelEntry.COLUMN_MODEL_EXTENSION);
            if (extension == null) {
                throw new IllegalArgumentException("Model requires a extension");
            }
        }

        if (values.containsKey(ModelEntry.COLUMN_MODEL_SIZE)) {
            String size = values.getAsString(ModelEntry.COLUMN_MODEL_SIZE);
            if (size == null) {
                throw new IllegalArgumentException("Model requires a size");
            }
        }

        if (values.containsKey(ModelEntry.COLUMN_MODEL_ANIMATION)) {
            String animation = values.getAsString(ModelEntry.COLUMN_MODEL_ANIMATION);
            if (animation == null) {
                throw new IllegalArgumentException("Model requires a animation");
            }
        }

        if (values.containsKey(ModelEntry.COLUMN_MODEL_PATH)) {
            String modelPath = values.getAsString(ModelEntry.COLUMN_MODEL_PATH);
            if (modelPath == null) {
                throw new IllegalArgumentException("Model requires a model path");
            }
        }

        if (values.containsKey(ModelEntry.COLUMN_MODEL_DOWNLOAD_ID)) {
            Integer downloadID = values.getAsInteger(ModelEntry.COLUMN_MODEL_DOWNLOAD_ID);
            if (downloadID != null && downloadID < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ModelEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MODELS:

                rowsDeleted = database.delete(ModelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MODEL_ID:

                selection = ModelEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ModelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MODELS:
                return ModelEntry.CONTENT_LIST_TYPE;
            case MODEL_ID:
                return ModelEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

