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

	public static JPCTActivity master = null;
	private GLSurfaceView glView;
	private JPCT3DObject renderer;
	public String model3DPath;
	public String material3DPath;
	public boolean moveObject;
	public boolean rotateObject;
	public boolean scaleObject;
	public boolean wideframeObject;
	
	
	@Override
protected void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		
		/*model3DPath="LEGO_Man.obj";
		material3DPath="LEGO_Man.mtl";
		moveObject=true;
		scaleObject=true;
		rotateObject=true;
		wideframeObject=true;*/
		//Log.v("JPCTActivity", model3DPath);
		//Log.v("JPCTActivity", material3DPath);
		
		/*model3DPath=extras.getString("3Dmodel");
		material3DPath=extras.getString("3Dmaterial");
		moveObject=extras.getBoolean("moveObject");
		rotateObject=extras.getBoolean("rotateObject");
		scaleObject=extras.getBoolean("scaleObject");
		wideframeObject=extras.getBoolean("wideframeObject");*/
		
		
		glView = new GLSurfaceView(this);
		glView.setEGLContextClientVersion(2);
		
		//configura el formato de RGB
		//glView.setEGLConfigChooser(new ConfigChooser(8, 8, 8, 8, 16,0));
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
//convierto las rutas que me pasa el componente de appinventor a inputStream para que las pueda usar el JPCTObject
public InputStream getModel3D(String nameAsset) throws IOException
{
	return this.getApplicationContext().getAssets().open(nameAsset);
	//return this.getAssets().open("LEGO_Man.obj");
	//return this.getAssets().open("tarantula2.obj");
	
	
}
public InputStream getMaterial3D(String materialAsset) throws IOException
{
	return this.getApplicationContext().getAssets().open(materialAsset);
	//return this.getAssets().open("LEGO_Man.mtl");
	//return this.getAssets().open("tarantula2.mtl");
	
	
}
//replantear funcion para que saque dos arrays, uno de texturas y otro de objetos3D encontrados, todo esto mediante las extensiones
public void getTextures3D(ArrayList<String> textures3D) throws IOException
{
	String[] listOfFiless=this.getApplicationContext().getAssets().list("");
	Arrays.sort(listOfFiless);
	Log.d("TEXTURE-NAME",listOfFiless[0]);
	
	
	for(int i=0;i<listOfFiless.length;i++)
	{
		Log.v("FILES ",listOfFiless[i]+"");
		if(listOfFiless[i].toLowerCase().endsWith("jpg"))
		{
			textures3D.add(listOfFiless[i]);
		}
	}
	
	/*for (File file : listOfFiless) {
	    if (file.isFile()) {
	    	textures3D.add(file.getName());
	    	Log.d("TEXTURE-NAME", file.getName());
	    }
	}*/
	
}
///////////////////////////////////////////////////////////////////////
@Override
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
	
	
	}

/*private void copy(Object src) {
	try {
		Logger.log("Copying data from master Activity!");
		Field[] fs = src.getClass().getDeclaredFields();
		for (Field f : fs) {
			f.setAccessible(true);
			f.set(this, f.get(src));
		}
	} catch (Exception e) {
		throw new RuntimeException(e);
	}
}*/
//The config chooser.
/*private static class ConfigChooser implements
    GLSurfaceView.EGLConfigChooser
{
    public ConfigChooser(int r, int g, int b, int a, int depth, int stencil)
    {
        mRedSize = r;
        mGreenSize = g;
        mBlueSize = b;
        mAlphaSize = a;
        mDepthSize = depth;
        mStencilSize = stencil;
    }
    
    
    private EGLConfig getMatchingConfig(EGL10 egl, EGLDisplay display,
        int[] configAttribs)
    {
        // Get the number of minimally matching EGL configurations
        int[] num_config = new int[1];
        egl.eglChooseConfig(display, configAttribs, null, 0, num_config);
        
        int numConfigs = num_config[0];
        if (numConfigs <= 0)
            throw new IllegalArgumentException("No matching EGL configs");
        
        // Allocate then read the array of minimally matching EGL configs
        EGLConfig[] configs = new EGLConfig[numConfigs];
        egl.eglChooseConfig(display, configAttribs, configs, numConfigs,
            num_config);
        
        // Now return the "best" one
        return chooseConfig(egl, display, configs);
    }
    
    
    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display)
    {
        // This EGL config specification is used to specify 2.0
        // rendering. We use a minimum size of 4 bits for
        // red/green/blue, but will perform actual matching in
        // chooseConfig() below.
        final int EGL_OPENGL_ES2_BIT = 0x0004;
        final int[] s_configAttribs_gl20 = { EGL10.EGL_RED_SIZE, 4,
                EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4,
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL10.EGL_NONE };
            
        return getMatchingConfig(egl, display, s_configAttribs_gl20);
    }
    
    
    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
        EGLConfig[] configs)
    {
        for (EGLConfig config : configs)
        {
            int d = findConfigAttrib(egl, display, config,
                EGL10.EGL_DEPTH_SIZE, 0);
            int s = findConfigAttrib(egl, display, config,
                EGL10.EGL_STENCIL_SIZE, 0);
            
            // We need at least mDepthSize and mStencilSize bits
            if (d < mDepthSize || s < mStencilSize)
                continue;
            
            // We want an *exact* match for red/green/blue/alpha
            int r = findConfigAttrib(egl, display, config,
                EGL10.EGL_RED_SIZE, 0);
            int g = findConfigAttrib(egl, display, config,
                EGL10.EGL_GREEN_SIZE, 0);
            int b = findConfigAttrib(egl, display, config,
                EGL10.EGL_BLUE_SIZE, 0);
            int a = findConfigAttrib(egl, display, config,
                EGL10.EGL_ALPHA_SIZE, 0);
            
            if (r == mRedSize && g == mGreenSize && b == mBlueSize
                && a == mAlphaSize)
                return config;
        }
        
        return null;
    }
    
    
    private int findConfigAttrib(EGL10 egl, EGLDisplay display,
        EGLConfig config, int attribute, int defaultValue)
    {
        
        if (egl.eglGetConfigAttrib(display, config, attribute, mValue))
            return mValue[0];
        
        return defaultValue;
    }
    
    // Subclasses can adjust these values:
    protected int mRedSize;
    protected int mGreenSize;
    protected int mBlueSize;
    protected int mAlphaSize;
    protected int mDepthSize;
    protected int mStencilSize;
    private int[] mValue = new int[1];
}*/

}
