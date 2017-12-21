package com.rubenbp.android.a3dviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.rubenbp.android.a3dviewer.SQLite.ModelosContract.ModelEntry;


public class SubirModeloActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentModelUri;
    private static final int EXISTING_MODEL_LOADER = 0;
    private static final int READ_REQUEST_CODE = 42;
    private boolean mModelHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mModelHasChanged = true;
            return false;
        }
    };
    EditText nombreEditText;
    TextView modelPathTextView;
    TextView tamannioTextView;
    EditText extensionEditText;
    RadioButton animadoRButton;
    RadioButton noanimadoRButton;
    String animado="animado";

    File file = null;
    File fileTemp=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_modelo);


        Intent intent = getIntent();
        mCurrentModelUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentModelUri == null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.subir_modelo));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Pet"
            setTitle(getString(R.string.editar_modelo));

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_MODEL_LOADER, null, this);
        }
        modelPathTextView =(TextView)findViewById(R.id.textView_result);
        tamannioTextView =(TextView)findViewById(R.id.subir_tamanno);
        extensionEditText =(EditText) findViewById(R.id.subir_extension);
        nombreEditText =(EditText)findViewById(R.id.subir_nombre);
        animadoRButton=(RadioButton)findViewById(R.id.radio_animado);
        noanimadoRButton=(RadioButton)findViewById(R.id.radio_noanimado);

    }

    public void onClickUpload(View view)
    {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("", "Uri: " + uri.toString());

                InputStream inputStream=null;
                try {

                    fileTemp= new File(getApplicationContext().getCacheDir().getAbsoluteFile(),"modelos_subidos"+ File.separator +"temp.zip" );
                    fileTemp.deleteOnExit();
                     //file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "3DViewer" + File.separator + "modelos" + File.separator + "modelos_subidos"+ File.separator +"archivo.zip");
                    inputStream = getContentResolver().openInputStream(uri);
                    FileUtils.copyInputStreamToFile(inputStream, fileTemp);
                   /* if(file.exists())
                    {
                        file.delete();
                    }*/
                   // FileUtils.moveFile(fileTemp,file);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(fileTemp.exists())
                {

                    tamannioTextView.setText(FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(fileTemp)));
                    modelPathTextView.setText(fileTemp.getAbsolutePath());

                }


            }
        }
    }

    public void onRadioButtonClicked(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_animado:
                if (checked)
                    animado="animado";
                    break;
            case R.id.radio_noanimado:
                if (checked)
                    animado="no animado";
                    break;
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                ModelEntry._ID,
                ModelEntry.COLUMN_MODEL_NAME,
                ModelEntry.COLUMN_MODEL_EXTENSION,
                ModelEntry.COLUMN_MODEL_ANIMATION,
                ModelEntry.COLUMN_MODEL_PATH,
                ModelEntry.COLUMN_MODEL_DOWNLOAD_ID,
                ModelEntry.COLUMN_MODEL_SIZE,
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentModelUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex( ModelEntry.COLUMN_MODEL_NAME);
            int extensionColumnIndex = cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_EXTENSION);
            int sizeColumnIndex = cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_SIZE);
            int animadoColumnIndex = cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_ANIMATION);
            int modelPathColumnIndex= cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_PATH);


            // Extract out the value from the Cursor for the given column index
            String nameModel = cursor.getString(nameColumnIndex);
            String extensionModel = cursor.getString(extensionColumnIndex);
            String sizeModel= cursor.getString(sizeColumnIndex);
            String animadoModel= cursor.getString(animadoColumnIndex);
            String pathModel= cursor.getString(modelPathColumnIndex);

            // Update the views on the screen with the values from the database


            nombreEditText.setText(nameModel);
            tamannioTextView.setText(sizeModel);
            extensionEditText.setText(extensionModel);
            modelPathTextView.setText(pathModel);
            animado=animadoModel;


            switch (animado) {
                case "animado":
                   animadoRButton.setChecked(true);
                    break;
                case "noanimado":
                   noanimadoRButton.setChecked(true);
                    break;
                default:
                    animadoRButton.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        nombreEditText.setText("");
        extensionEditText.setText("");
        tamannioTextView.setText("");
        modelPathTextView.setText("");
        animadoRButton.setChecked(true);
    }
    private void saveModel() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nombreEditText.getText().toString().trim();
        String extensionString = extensionEditText.getText().toString().trim();
        String sizeString = tamannioTextView.getText().toString().trim();
        String modelPathString = modelPathTextView.getText().toString().trim();
        String animadoString = animado.trim();

        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentModelUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(extensionString) &&
                TextUtils.isEmpty(sizeString) && TextUtils.isEmpty(modelPathString)&& TextUtils.isEmpty(animadoString) ) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();

        values.put(ModelEntry.COLUMN_MODEL_NAME, nameString);
        values.put(ModelEntry.COLUMN_MODEL_EXTENSION, extensionString);
        values.put( ModelEntry.COLUMN_MODEL_ANIMATION, animadoString);
        values.put( ModelEntry.COLUMN_MODEL_PATH, modelPathString);
        values.put( ModelEntry.COLUMN_MODEL_ANIMATION, animadoString);
        values.put( ModelEntry.COLUMN_MODEL_SIZE, sizeString);
        //son modelos no descargados
        values.put( ModelEntry.COLUMN_MODEL_DOWNLOAD_ID,0);
        // If the weight is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.


        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentModelUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(ModelEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "CAGADA",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "TODO CORRECTO",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentModelUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this,"NADA INSERTADO",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "INSERTADO PERFECT",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentModelUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveModel();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mModelHasChanged) {
                    NavUtils.navigateUpFromSameTask(SubirModeloActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(SubirModeloActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteModel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteModel() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentModelUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentModelUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
