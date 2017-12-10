package com.rubenbp.android.a3dviewer.jpct;




import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;


public class JPCTActivity extends Activity {


	private GLSurfaceView glView;
	private JPCT3DObject renderer;

    public File direccion;
	private String materialObject3D;
	private String nameObject3D;
	private boolean animationActivated;
	private ArrayList<File> texturesList = new ArrayList<File>();


	@Override
protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();

		 direccion= (File) extras.getSerializable("hola");
		//Log.v("DIRECCIONNNN",direccion.getAbsolutePath());

		glView = new GLSurfaceView(this);
		glView.setEGLContextClientVersion(2);
		
		//configura el formato de RGB


		glView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				// Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
				// back to Pixelflinger on some device (read: Samsung I7500)
				int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});



		renderer= new JPCT3DObject(glView,this);

		glView.setRenderer(renderer);
		
		setContentView(glView);
		
		glView.setOnTouchListener(renderer);
		
	}



/*@Override
public void onResume(){
	    super.onResume();
	    Log.v("JPCTActivity", "OnResume");
	    if (glView != null) {
	    	glView.setVisibility(View.VISIBLE);
	    	glView.onResume();
		}

	}
@Override
public void onPause() {
	super.onPause();
	Log.v("JPCTActivity", "OnPause");
	if (glView != null) {
		glView.setVisibility(View.INVISIBLE);
		glView.onPause();
	}

}
@Override
protected void onDestroy() {
	Log.v("JPCTActivity", "OnDestroy");
	super.onDestroy();
	
	
	}
@Override
protected void onStop() {
	Log.v("JPCTActivity", "OnStop");
	super.onStop();
	
	
	}*/

}
