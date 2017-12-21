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

    /** Tag for the log messages */
    public static final String LOG_TAG = ModelosContract.class.getSimpleName();

    /** URI matcher code for the content URI for the pets table */
    private static final int MODELS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int MODEL_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(ModelosContract.CONTENT_AUTHORITY, ModelosContract.PATH_MODELS, MODELS);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(ModelosContract.CONTENT_AUTHORITY, ModelosContract.PATH_MODELS + "/#", MODEL_ID);
    }

    /** Database helper object */
    private ModelosDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ModelosDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MODELS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(ModelEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MODEL_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ModelEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ModelEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
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

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertModel(Uri uri, ContentValues values) {
        // Check that the name is not null
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


        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(ModelEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
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
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ModelEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateModel(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updateModel(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
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

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ModelEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MODELS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ModelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MODEL_ID:
                // Delete a single row given by the ID in the URI
                selection = ModelEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ModelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
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

