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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.rubenbp.android.a3dviewer.SQLite.ModelosContract.ModelEntry;
import com.rubenbp.android.a3dviewer.jpct.JPCTActivity;


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
    ImageView imagenImageView;
    String animado="animado";
    String modelPath="";
    String oldObjectName="";

    File modeloFileTempZip =null;
    File modeloFileTempUnzip=null;
    private Button visor3DButton;
    private File modeloAbsolutFileDir;
    private File modeloAbsolutFileDirOld;
    private File modeloAbsolutFileDirNew;

    private class ModeloFileAsyncTask extends AsyncTask<String, Void, File> {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected File doInBackground(String... urls) {

            String result="";
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            File asyncFileTemp= new File(urls[0]);

            return QueryUtils.unZipIt(asyncFileTemp,getApplicationContext());


        }
        @Override
        protected void onPostExecute(File file) {

            modeloFileTempUnzip=file;
            //aqui una vez terminada la descompresion deberia de habilitar el boton
            visor3DButton.setEnabled(true);
            visor3DButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent= new Intent(getApplicationContext(), JPCTActivity.class);
                    intent.putExtra("hola",modeloFileTempUnzip);
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modelo_upload);

        Intent intent = getIntent();
        mCurrentModelUri = intent.getData();
        visor3DButton=(Button)findViewById(R.id.upload_button_visor3D);

        imagenImageView=(ImageView)findViewById(R.id.subir_imagen);

        if (mCurrentModelUri == null) {

            setTitle(getString(R.string.subir_modelo));
            visor3DButton.setEnabled(false);

            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.editar_modelo));

            imagenImageView.setBackgroundColor(getResources().getColor(R.color.green_button));

            getLoaderManager().initLoader(EXISTING_MODEL_LOADER, null, this);
        }
        modelPathTextView =(TextView)findViewById(R.id.textView_result);
        tamannioTextView =(TextView)findViewById(R.id.subir_tamanno);
        extensionEditText =(EditText) findViewById(R.id.subir_extension);
        nombreEditText =(EditText)findViewById(R.id.subir_nombre);
        animadoRButton=(RadioButton)findViewById(R.id.radio_animado);
        noanimadoRButton=(RadioButton)findViewById(R.id.radio_noanimado);


    }

    public void onClickUploadFile(View view)
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

            imagenImageView.setBackgroundColor(getResources().getColor(R.color.green_button));
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("", "Uri: " + uri.toString());

                InputStream inputStream=null;
                try {

                    modeloFileTempZip = new File(getApplicationContext().getCacheDir().getAbsoluteFile(),"modelos_subidos"+ File.separator +"temp.zip" );
                    inputStream = getContentResolver().openInputStream(uri);
                    FileUtils.copyInputStreamToFile(inputStream, modeloFileTempZip);
                    final ModeloFileAsyncTask modeloFileAsyncTask= new ModeloFileAsyncTask();
                    modeloFileAsyncTask.execute(modeloFileTempZip.getAbsolutePath());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(modeloFileTempZip.exists())
                {
                    tamannioTextView.setText(FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(modeloFileTempZip)));

                }


            }
        }
    }

    public void onRadioButtonClicked(View view) {


        boolean checked = ((RadioButton) view).isChecked();


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

        String[] projection = {
                ModelEntry._ID,
                ModelEntry.COLUMN_MODEL_NAME,
                ModelEntry.COLUMN_MODEL_EXTENSION,
                ModelEntry.COLUMN_MODEL_ANIMATION,
                ModelEntry.COLUMN_MODEL_PATH,
                ModelEntry.COLUMN_MODEL_DOWNLOAD_ID,
                ModelEntry.COLUMN_MODEL_SIZE,
        };

        return new CursorLoader(this,
                mCurrentModelUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex( ModelEntry.COLUMN_MODEL_NAME);
            int extensionColumnIndex = cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_EXTENSION);
            int sizeColumnIndex = cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_SIZE);
            int animadoColumnIndex = cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_ANIMATION);
            int modelPathColumnIndex= cursor.getColumnIndex(ModelEntry.COLUMN_MODEL_PATH);

            String nameModel = cursor.getString(nameColumnIndex);
            oldObjectName=nameModel;
            String extensionModel = cursor.getString(extensionColumnIndex);
            String sizeModel= cursor.getString(sizeColumnIndex);
            String animadoModel= cursor.getString(animadoColumnIndex);
            final String pathModel= cursor.getString(modelPathColumnIndex);

            nombreEditText.setText(nameModel);
            tamannioTextView.setText(sizeModel);
            extensionEditText.setText(extensionModel);
            modelPath=pathModel;
            modeloAbsolutFileDir= new File(modelPath);
            Toast.makeText(this,pathModel,Toast.LENGTH_SHORT).show();
            visor3DButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent= new Intent(getApplicationContext(), JPCTActivity.class);
                    intent.putExtra("modelpath",modeloAbsolutFileDir);
                    intent.putExtra("origin","mymodels");
                    startActivity(intent);
                }
            });
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

        nombreEditText.setText("");
        extensionEditText.setText("");
        tamannioTextView.setText("");

        animadoRButton.setChecked(true);
        modelPath="";
    }

    private void saveModel() {

        String nameString = nombreEditText.getText().toString().trim();
        String extensionString = extensionEditText.getText().toString().trim();
        String sizeString = tamannioTextView.getText().toString().trim();
        String modelPathString =modelPath;
        String animadoString = animado.trim();

        if (mCurrentModelUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(extensionString) &&
                TextUtils.isEmpty(sizeString) && TextUtils.isEmpty(modelPathString)&& TextUtils.isEmpty(animadoString) ) {

            return;
        }

        ContentValues values = new ContentValues();

        values.put(ModelEntry.COLUMN_MODEL_NAME, nameString);
        values.put(ModelEntry.COLUMN_MODEL_EXTENSION, extensionString);
        values.put( ModelEntry.COLUMN_MODEL_ANIMATION, animadoString);
        values.put( ModelEntry.COLUMN_MODEL_PATH, modelPathString);
        values.put( ModelEntry.COLUMN_MODEL_SIZE, sizeString);
        //son modelos no descargados
        values.put( ModelEntry.COLUMN_MODEL_DOWNLOAD_ID,0);

        if (mCurrentModelUri == null) {
            modeloAbsolutFileDir= new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "3DViewer" + File.separator + "modelos" + File.separator + "subidos"+ File.separator +nameString);
            try {
                FileUtils.copyDirectory(modeloFileTempUnzip, modeloAbsolutFileDir, true);
                modelPath=modeloAbsolutFileDir.getAbsolutePath();
                values.put( ModelEntry.COLUMN_MODEL_PATH, modelPath);
                Log.v("ABSOLUTE-PATH",modelPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Uri newUri = getContentResolver().insert(ModelEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, R.string.error_insercion,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.todo_correcto,
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            //cuando salve la modificacion de objeto a el cual le he cambiado el nombre, deberia de cambiar la carpeta de nombre de origen por el nuevo nombre

            if(!nameString.equals(oldObjectName))
            {
                modeloAbsolutFileDirOld= new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "3DViewer" + File.separator + "modelos" + File.separator + "subidos"+ File.separator +oldObjectName);
                modeloAbsolutFileDirNew= new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "3DViewer" + File.separator + "modelos" + File.separator + "subidos"+ File.separator +nameString);
                try {
                    FileUtils.copyDirectory(modeloAbsolutFileDirOld, modeloAbsolutFileDirNew, true);
                    modelPath=modeloAbsolutFileDirNew.getAbsolutePath();
                    values.put( ModelEntry.COLUMN_MODEL_PATH, modelPath);
                   QueryUtils.deleteRecursive(modeloAbsolutFileDirOld);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            int rowsAffected = getContentResolver().update(mCurrentModelUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this,R.string.nada_insertado,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.insercion_correcta,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentModelUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:

                saveModel();

                finish();
                return true;

            case R.id.action_delete:

                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:

                if (!mModelHasChanged) {
                    NavUtils.navigateUpFromSameTask(SubirModeloActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(SubirModeloActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteModel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteModel() {

        if (mCurrentModelUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentModelUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}
