package com.rubenbp.android.a3dviewer.jpct;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.rubenbp.android.a3dviewer.ModelosActivity;
import com.rubenbp.android.a3dviewer.R;
import com.threed.jpct.Animation;
import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Logger;
import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

import raft.jpct.bones.Animated3D;
import raft.jpct.bones.AnimatedGroup;
import raft.jpct.bones.BonesIO;
import raft.jpct.bones.SkeletonPose;
import raft.jpct.bones.SkinClip;

public class JPCT3DObject implements GLSurfaceView.Renderer, View.OnTouchListener {

	//// VARIABLES RECIBIDAS DEL OBJECTO PARCEL///

	private String nameObject3D;
	private String materialObject3D;
	private int scaleObject3D=1;
	private ArrayList<String> texturePath;
	private int posX=0;
	private int posY=0;
	private int posZ=0;
	private float rotationX=0;
	private float rotationY=0;
	private float rotationZ=0;
	private float rotationSpeed=0.005f;
	private float zoomMax=40;
	private float zoomMin=10;
	private float zoomSpeed = 0.5f;
	private float animationSpeed = 1;
	private int animationSecuence = 1;
	public boolean animationActivated = false;
	private boolean zoomActivated = false;
	private boolean rotateActivated = false;
	private boolean moveActivated = false;
	public RGBColor bgColor;

	/// VARIABLES ANIMACION DEL OBJECTO3D///

	private AnimatedGroup masterObject3DAnimated;
	private long frameTime = System.currentTimeMillis();
	private long aggregatedTime = 0;
	private float animateSeconds = 0f;
	private static final int GRANULARITY = 25;

	/// VARIABLES DE INTEREACCION CON EL OBJETO3D

	private ScaleGestureDetector scaleGestureDetector;
	private GestureDetector gestureDetector;
	boolean followFingerCollision = false;
	boolean wideframeActivated = false;
	private float factor;
	private boolean firstScale = true;
	private float lastX, lastY;
	private boolean pauseAnimation = false;
	private static final float Z_PLANE = 0;

	/// VARIABLES DE LA ESCENA

	private GLSurfaceView glView;
	private World world;
	private FrameBuffer fb;
	private Light light;
	private JPCTActivity mActivity;
	public Object3D object3D;
	public SimpleVector cameraVector;
	private CameraOrbitController cameraController;
	private float[] bb = null;
	public ArrayList<File> texturesList= new ArrayList<File>();

	/// VARIABLES DE LA INTERFAZ DE USUARIO///

	/*
	 * private GLFont buttonFont; private View handler; private boolean
	 * blocked=false; private float posXCamera; private float posYCamera;
	 * private float posZCamera; private boolean noScale; private int numDedos;
	 * private boolean primerTouch; private int dedosTotales;
	 */
	private boolean menuActivated;
	private float distancia;
	private float unidadScala;
	private float unidadMovimiento;
	private float nPartesZoom;
	private float nPartesMove;
	private float moveSpeed = 3;
	private boolean playAnimation = true;
	protected int unidadesRecorridas = 0;
	private int dimension;
	private int aumentaPixel;
	private int dimensionAumenta;
	private int dimensionDisminuye;
	private int disminuyePixel;
	
	boolean isBones = false;
	boolean isMd2 = false;

	private static final Rect[] ANIMATION_BUTTONS = new Rect[4];
	private static final Rect[] CONTROLLER_BUTTONS = new Rect[6];

	// ---------------------------------------------------------------------------------------------------------------//

	/// CONSTRUCTOR///

	/**
	 * Constructor de la clase
	 * 
	 * @param glView
	 * @param mActivity
	 * @throws Throwable
	 */
	public JPCT3DObject(GLSurfaceView glView, JPCTActivity mActivity) {

		this.glView = glView;
		this.mActivity = mActivity;

		if (world != null)
			return;
		world = new World();

		// primero cargar el objeto3D y dependiendo de si tiene animacion o no y
		// si es un bones o un md2 permitir que pase a animacion

		int color_int = ContextCompat.getColor(mActivity.getApplicationContext(), R.color.colorPrimary);

		bgColor= new RGBColor(Color.red(color_int),Color.green(color_int),Color.blue(color_int));


        extractDatasFromDir(mActivity.direccion);

        loadTexturesFromDir();

		object3D = load3DObject(nameObject3D, materialObject3D);

		// posicionar el objecto3D justo despues de colocar la camara

		posInitObject3D();

		// centro del objecto3D en base a su boundingBox

		SimpleVector p = new SimpleVector(0, 0, 0);
		p.x = (bb[0] + bb[1]) / 2.0f;
		p.y = (bb[2] + bb[3]) / 2.0f;
		p.z = (bb[4] + bb[5]) / 2.0f;
		object3D.setCenter(p);
		world.setAmbientLight(127, 127, 127);
		world.buildAllObjects();

		scaleGestureDetector = new ScaleGestureDetector(mActivity, new OnScaleGestureListener() {

			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {

			}

			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				/*
				 * noScale=false; if(firstScale){
				 * //world.getCamera().moveCamera(Camera.CAMERA_MOVEIN,
				 * distance); factor =scaleObject3D; firstScale=false; } else {
				 * factor=oldFactor; }
				 * 
				 */
				return true;

			}

			@Override
			public boolean onScale(ScaleGestureDetector detector) {

				float posxX = object3D.getTranslation().x;
				float posyY = object3D.getTranslation().y;
				float poszZ = object3D.getTranslation().z;

				Log.v("UNIDAD DE ESCALA", unidadScala + "");

				object3D.clearTranslation();

				float scaleFactor = detector.getScaleFactor() - 1;
				factor += scaleFactor;

				if (detector.getScaleFactor() >= 1) {
					// aleja
					if (unidadesRecorridas < zoomMax) {
						object3D.translate(posxX, posyY, poszZ + unidadScala);
						unidadesRecorridas++;
					} else {
						object3D.translate(posxX, posyY, poszZ);
					}
				} else {
					// acerca

					if (unidadesRecorridas > -zoomMin) {
						object3D.translate(posxX, posyY, poszZ - unidadScala);
						unidadesRecorridas--;
					} else {
						object3D.translate(posxX, posyY, poszZ);
					}

				}

				/*
				 * Log.v("FACTOR", factor+""); if(factor<=zoomMin) {
				 * object3D.setScale(zoomMin); factor=zoomMin;
				 * 
				 * }else if(factor>=zoomMax) { object3D.setScale(zoomMax);
				 * factor=zoomMax; } else { object3D.setScale(factor); }+/
				 * oldFactor=factor; return true; /*if(zoomActivated){
				 * 
				 * 
				 * currentScaleFactorApplied = Math.max(zoomMax,
				 * Math.min(object3D.getScale() *
				 * detector.getScaleFactor(),zoomMin));
				 * 
				 * 
				 * object3D.setScale(currentScaleFactorApplied);
				 * 
				 * Log.d("SCALA-OBJECT3D", detector.getScaleFactor()+ ""); }
				 */

				return false;
			}
		});

		gestureDetector = new GestureDetector(mActivity, new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDoubleTap(MotionEvent e) {

				posInitObject3D();
				return false;
			}

		});

	}



	// TODO tengo que darle una pensada a esta funcion para que me extraiga todo lo que necesito, tanto ficheros ocmo texturas
	private void extractDatasFromDir(File filesdir) {

		File[] listOfFiles = filesdir.listFiles();

		for (File file : listOfFiles) {
			if (file.isFile()) {

				if(file.getName().toLowerCase().endsWith("mtl"))
				{
					materialObject3D=file.getName();
				}else if(file.getName().toLowerCase().endsWith("obj")||
						file.getName().toLowerCase().endsWith("md2")||
						file.getName().toLowerCase().endsWith("3ds")||
						file.getName().toLowerCase().endsWith("asc")||
						file.getName().toLowerCase().endsWith("bones"))
				{
					nameObject3D=file.getName();
				}
				Log.v("ARCHIVOS",file.getName());
			}
			else if(file.isDirectory()&&file.getName().toLowerCase().equals("texturas"))
			{
				animationActivated=true;

				File[]listOfTextures=file.listFiles();
				for(File filesTexturas:listOfTextures)
				{
					//Log.v("FILES-TEXTURA",filesTexturas.getName());
					texturesList.add(filesTexturas);
				}


			}
		}


	}
	/// METODOS DE CARGA DE ANIMACIONES DEL OBJECTO3D EN LA ESCENA///

	/**
	 * Funcion que inicia la animacion del objecto3D, siempre que este sea un
	 * objeto serializado
	 */
	private void animateObjects() {

		long now = System.currentTimeMillis();
		aggregatedTime += (now - frameTime);
		frameTime = now;

		if (aggregatedTime > 1000) {
			aggregatedTime = 0;
		}

		while (aggregatedTime > GRANULARITY) {
			aggregatedTime -= GRANULARITY;
			animateSeconds += GRANULARITY * 0.001f * animationSpeed;

		}
		// condicional de control por si me paso del numero de animaciones
		// disponibles
		if (animationSecuence > masterObject3DAnimated.getSkinClipSequence().getSize()) {
			animationSecuence = 0;
		}
		if (animationSecuence > 0 && masterObject3DAnimated.getSkinClipSequence().getSize() >= animationSecuence) {
			float clipTime = masterObject3DAnimated.getSkinClipSequence().getClip(animationSecuence - 1).getTime();
			if (animateSeconds > clipTime) {
				animateSeconds = 0;
			}
			float index = animateSeconds / clipTime;
			masterObject3DAnimated.animateSkin(index, animationSecuence);
			/*
			 * if (useMeshAnim) { for (AnimatedGroup group :
			 * object3DAnimatedList) { for (Animated3D a : group) {
			 * a.animate(index, animation);
			 * 
			 * 
			 * }
			 * 
			 * } } else { for (AnimatedGroup group : object3DAnimatedList) {
			 * group.animateSkin(index, animation); // if
			 * (!group.isAutoApplyAnimation()) // group.applyAnimation(); } }
			 */

		} else {
			animateSeconds = 0f;
		}
	}

	/*
	 * Funcion que calcula el boundingBox del objeto animado, debe hacerse de
	 * forma diferente a la funcion especifica de un solo objecto3D
	 */
	protected float[] calcBoundingBox() {
		float[] box = null;

		for (Animated3D skin : masterObject3DAnimated) {
			float[] skinBB = skin.getMesh().getBoundingBox();

			if (box == null) {
				box = skinBB;
			} else {
				// x
				box[0] = Math.min(box[0], skinBB[0]);
				box[1] = Math.max(box[1], skinBB[1]);
				// y
				box[2] = Math.min(box[2], skinBB[2]);
				box[3] = Math.max(box[3], skinBB[3]);
				// z
				box[4] = Math.min(box[4], skinBB[4]);
				box[5] = Math.max(box[5], skinBB[5]);
			}

		}
		return box;
	}

	/**
	 * Funcion que lee del objeto serializado y carga las diferentes animaciones
	 * encontradas
	 */
	private void createMeshKeyFrames() {
		Config.maxAnimationSubSequences = masterObject3DAnimated.getSkinClipSequence().getSize() + 1; // +1
																										// for
																										// whole
																										// sequence

		int keyframeCount = 0;
		final float deltaTime = 0.1f; // max time between frames

		for (SkinClip clip : masterObject3DAnimated.getSkinClipSequence()) {
			float clipTime = clip.getTime();
			int frames = (int) Math.ceil(clipTime / deltaTime) + 1;
			keyframeCount += frames;
		}

		Animation[] animations = new Animation[masterObject3DAnimated.getSize()];
		for (int i = 0; i < masterObject3DAnimated.getSize(); i++) {
			animations[i] = new Animation(keyframeCount);
			animations[i].setClampingMode(Animation.USE_CLAMPING);
		}
		System.out.println("------------ keyframeCount: " + keyframeCount +
		 ", mesh size: " + masterObject3DAnimated.getSize());
		int count = 0;

		int sequence = 0;
		for (SkinClip clip : masterObject3DAnimated.getSkinClipSequence()) {
			float clipTime = clip.getTime();
			int frames = (int) Math.ceil(clipTime / deltaTime) + 1;
			float dIndex = 1f / (frames - 1);

			for (int i = 0; i < masterObject3DAnimated.getSize(); i++) {
				animations[i].createSubSequence(clip.getName());
			}
			// System.out.println(sequence + ": " + clip.getName() + ", frames:
			// " + frames);
			for (int i = 0; i < frames; i++) {
				masterObject3DAnimated.animateSkin(dIndex * i, sequence + 1);

				for (int j = 0; j < masterObject3DAnimated.getSize(); j++) {
					Mesh keyframe = masterObject3DAnimated.get(j).getMesh().cloneMesh(true);
					keyframe.strip();
					animations[j].addKeyFrame(keyframe);
					count++;
					// System.out.println("added " + (i + 1) + " of " + sequence
					// + " to " + j + " total: " + count);
				}
			}
			sequence++;
		}
		for (int i = 0; i < masterObject3DAnimated.getSize(); i++) {
			masterObject3DAnimated.get(i).setAnimationSequence(animations[i]);
		}
		masterObject3DAnimated.get(0).getSkeletonPose().setToBindPose();
		masterObject3DAnimated.get(0).getSkeletonPose().updateTransforms();
		masterObject3DAnimated.applySkeletonPose();
		masterObject3DAnimated.applyAnimation();

		Logger.log("created mesh keyframes, " + keyframeCount + "x" + masterObject3DAnimated.getSize());
	}

	/// METODO DE CARGA DE VARIABLES RECIBIDAS///

	/**
	 * Funcion que lee las variables de configuracion del objeto3D que se quiere
	 * v
	 */


	private void loadTextureButtons() throws Exception {


			Texture texture;
			String textureName;

			//recorrer la carpeta de assets,y cargar todos los botones pero preferiblemente ponerle en la carpeta raw

			Field[] fields=R.raw.class.getFields();
			for(int count=0; count < fields.length; count++){

				textureName=fields[count].getName();
				Log.v("TEXTURA-RAW",fields[count].getName());
				//TextureManager.getInstance().removeTexture(textureName);
				texture = new Texture(BitmapHelper.rescale(
						BitmapHelper.loadImage(mActivity.getApplicationContext().getResources().openRawResource(fields[count].getInt(fields[count]))), dimension,
						dimension), true);
				texture.keepPixelData(true);
				TextureManager.getInstance().addTexture(textureName, texture);


			}


	}


	private void loadTextureButtonsOld(String buttonName) throws Exception {
		try {
			TextureManager.getInstance().removeTexture(buttonName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.v("TEXTURA BOTONES", "textura aun no declarada" + " " + buttonName);
		}

		String buttonNamee[]=buttonName.split("\\.");
		try {
			Texture texture;
			String textureName;
			textureName = buttonNamee[0];

			texture = new Texture(BitmapHelper.rescale(
					BitmapHelper.loadImage(mActivity.getApplicationContext().getAssets().open(buttonName)), dimension,
					dimension), true);
			// texture = new Texture(mActivity.getAssets().open(textureName));
			texture.keepPixelData(true);
			TextureManager.getInstance().addTexture(textureName, texture);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Funcion que carga todas las texturas encontradas en el objeto parcel de
	 * configuracion
	 * 
	 * @throws Exception
	 */
	private void loadTexturesFromDir() {
		TextureManager.getInstance().flush();

		if (texturesList != null) {

			for (int x = 0; x < texturesList.size(); x++) {
				Texture texture;

				try {
					texture = new Texture(new FileInputStream(texturesList.get(x)));
					//texture.keepPixelData(true);
					TextureManager.getInstance().addTexture(texturesList.get(x).getName(), texture);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	/**
	 * Funcion que carga y devuelve el objeto3D sea o no serializado
	 * 
	 * @param nameObject
	 * @param materialObject
	 * @return
	 * @throws Throwable
	 */
	private Object3D load3DObject(String nameObject, String materialObject) {

		Object3D myObject3D = null;
        File outputFolderName=new File(mActivity.direccion.getAbsolutePath()+File.separator+nameObject);
		File outputFolderMaterial=new File(mActivity.direccion.getAbsolutePath()+File.separator+materialObject);

		/*
		 * Creacion de los objetos
		 */
		try {
			if (nameObject.toLowerCase().endsWith("obj")) {
				if (materialObject.toLowerCase().endsWith("mtl")) {
					myObject3D = Object3D.mergeAll(Loader.loadOBJ(new FileInputStream(outputFolderName),
							new FileInputStream(outputFolderMaterial), 1f));
				}
			} else if (nameObject.toLowerCase().endsWith("md2")) {
				myObject3D = Object3D.mergeAll(Loader.loadMD2(new FileInputStream(outputFolderName), 1f));
				isMd2 = true;
			} else if (nameObject.toLowerCase().endsWith("3ds")) {
				myObject3D = Object3D.mergeAll(Loader.load3DS(new FileInputStream(outputFolderName), 1f));

			} else if (nameObject.toLowerCase().endsWith("asc")) {
				myObject3D = Object3D.mergeAll(Loader.loadASC(new FileInputStream(outputFolderName), 1f, false));

			} else {
				isBones = true;
				masterObject3DAnimated = BonesIO
						.loadGroup(new FileInputStream(outputFolderName));
				masterObject3DAnimated.setSkeletonPose(new SkeletonPose(masterObject3DAnimated.get(0).getSkeleton()));
				masterObject3DAnimated.getRoot().translate(world.getCamera().getPosition());
				myObject3D = masterObject3DAnimated.getRoot();
				myObject3D.calcCenter();
				myObject3D.compile();

				for (Animated3D a : masterObject3DAnimated) {

					// para detectar la collision de mi dedo con el objeto u
					// objectos que conforman la animacion, debo recorrer uno a
					// uno y aplicarle el setcollision

					a.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);

				}

				if (animationActivated) {
					Log.v("CONTROL","1");
					createMeshKeyFrames();
					Log.v("CONTROL","2");

				}

			}
			if (!isBones) {
				if (isMd2 && animationActivated) {
					myObject3D.strip();
					myObject3D.build();

				}
				if (texturePath != null) {
					//myObject3D.setTexture(texturePath);
				}

				world.addObject(myObject3D);
				bb = myObject3D.getMesh().getBoundingBox();
			} else {
				if (texturesList != null) {
					for (Animated3D a : masterObject3DAnimated) {

						String nameObjectText = a.getName();

						Log.v("CONTROL","3");
						for (File textureFile : texturesList) {

							String textName=textureFile.getName();
							Log.v("NOMBRE OBJETO", nameObjectText);
							Log.v("NOMBRE TEXTURA", textName);

							String textNamee[]=textName.split("\\.");

							if (nameObjectText.toLowerCase().contains(textNamee[0].toLowerCase())) {

								a.setTexture(textName);

							}

						}

					}
				}
				masterObject3DAnimated.addToWorld(world);

				bb = this.calcBoundingBox();

			}
			myObject3D.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		} catch (IOException e) {

			e.printStackTrace();
		}
		myObject3D.calcCenter();
		myObject3D.calcNormals();
		myObject3D.translate(posX, posY, posZ);
		return myObject3D;
	}

	/// METODOS CONFIGURACION DE LA ESCENA///

	/**
	 * Funcion que calcula la distancia entre la camara y el objeto3D, necesaria
	 * para encuadrar el objeto3D
	 * 
	 * @param c
	 * @param buffer
	 * @param height
	 * @param objectHeight
	 * @return
	 */
	protected float calcDistance(Camera c, FrameBuffer buffer, float height, float objectHeight) {
		float h = height / 2f;
		float os = objectHeight / 2f;

		Camera cam = new Camera();
		cam.setFOV(c.getFOV());
		SimpleVector p1 = Interact2D.project3D2D(cam, buffer, new SimpleVector(0f, os, 1f));
		float y1 = p1.y - buffer.getCenterY();
		float z = (1f / h) * y1;

		return z;
	}

	/**
	 * Funcion que posiciona la camara para visualizar correctamente el objeto3D
	 */
	protected void autoAdjustCamera() {

		float groupHeight = bb[3] - bb[2];
		float groupWidth = bb[1] - bb[0];
		
		
		Log.v("HEIGHT",groupHeight+"" );
		Log.v("WIDTH",groupWidth+"" );
		

		if (groupHeight >= groupWidth) {

			cameraController.cameraRadius = calcDistance(world.getCamera(), fb, fb.getHeight() / 1.5f, groupHeight);
			cameraController.minCameraRadius = groupHeight / 10f;
			cameraController.cameraTarget.y = (bb[3] + bb[2]) / 2;
		} else if (groupWidth >= groupHeight) {

			cameraController.cameraRadius = calcDistance(world.getCamera(), fb, fb.getWidth() / 1.5f, groupWidth);
			cameraController.minCameraRadius = groupWidth / 10f;
			cameraController.cameraTarget.y = (bb[0] + bb[1]) / 2;
		}

		
		// cameraController.cameraTarget.y = (bb[3] + bb[2]);

		cameraController.placeCamera();

	}

	/// METODOS DE CARGA DEL ESCENARIO EN 3D///

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		Log.d("OBJECT3D", "cargando shaders");

		/*
		 * Paint paint = new Paint(); paint.setAntiAlias(true);
		 * paint.setTypeface(Typeface.create((String)null, Typeface.BOLD));
		 * paint.setTextSize(50); buttonFont = new GLFont(paint, "+-");
		 */

		/*
		 * Las siguientes dos lineas son necesarias en AI2 para que este
		 * localice donde se encuentran los shaders
		 */

		//ShaderLocator s = new ShaderLocator(this.mActivity.getAssets());
		//GLSLShader.setShaderLocator(s);

	}

	public void calcularDimensionBotonesRelativaPantalla(int width, int nDivisiones) {
		// 6
		dimension = width / nDivisiones;
		dimensionAumenta = dimension;
		dimensionDisminuye = dimension;
		aumentaPixel = 0;
		disminuyePixel = 0;

		while ((dimensionAumenta & (dimensionAumenta - 1)) != 0 && (dimensionAumenta & (dimensionAumenta - 1)) != 0) {
			aumentaPixel++;
			dimensionAumenta++;
		}

		while ((dimensionDisminuye & (dimensionDisminuye - 1)) != 0
				&& (dimensionDisminuye & (dimensionDisminuye - 1)) != 0) {
			disminuyePixel++;
			dimensionDisminuye--;
		}

		if (disminuyePixel <= aumentaPixel) {
			dimension = dimensionDisminuye;
		} else {
			dimension = dimensionAumenta;
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		Log.v("ENTRANDO_SURFACE","ENTRADO");
		if (width > height) {
			calcularDimensionBotonesRelativaPantalla(width, 16);
		} else {
			calcularDimensionBotonesRelativaPantalla(width, 8);
		}

		try {
			loadTextureButtons();
			/*loadTextureButtonsOld("play.png");
			loadTextureButtonsOld("pause.png");
			loadTextureButtonsOld("rewind.png");
			loadTextureButtonsOld("forward.png");

			loadTextureButtonsOld("playoff.png");
			loadTextureButtonsOld("pauseoff.png");
			loadTextureButtonsOld("rewindoff.png");
			loadTextureButtonsOld("forwardoff.png");

			loadTextureButtonsOld("menuoff.png");
			loadTextureButtonsOld("menuon.png");
			loadTextureButtonsOld("moveoff.png");
			loadTextureButtonsOld("moveon.png");
			loadTextureButtonsOld("rotateon.png");
			loadTextureButtonsOld("rotateon.png");
			loadTextureButtonsOld("scaleon.png");
			loadTextureButtonsOld("scaleoff.png");
			loadTextureButtonsOld("back.png");
			loadTextureButtonsOld("reset.png");*/

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		fb = new FrameBuffer(width, height);
		
		cameraController = new CameraOrbitController(world.getCamera());
		cameraController.cameraAngle = 0;
		float heightBound = (bb[3] - bb[2]);
		light = new Light(world);
		light.setPosition(new SimpleVector(0, -heightBound / 2, heightBound));
		
		autoAdjustCamera();
		cameraController.placeCamera();
		distancia = object3D.getTransformedCenter().calcSub(world.getCamera().getPosition()).length();
		nPartesZoom = 100 - ((zoomSpeed - 1) * 10.0f);
		nPartesMove = 100 - ((moveSpeed - 1) * 10.0f);
		Log.v("DISTANCIA", distancia + "");
		Log.v("NUMERO DE PARTES", nPartesZoom + "");
		unidadScala = distancia / nPartesZoom;
		unidadMovimiento = distancia / nPartesMove;

		Log.d("CAMARA ANCHO", width + "");
		Log.d("DIMENSION", dimension + "");

		ANIMATION_BUTTONS[0] = new Rect((width / 2) - (dimension * 2), height - dimension, (width / 2) - dimension,
				height);
		// play
		ANIMATION_BUTTONS[1] = new Rect((width / 2) - dimension, height - dimension, (width / 2), height);
		// pause
		ANIMATION_BUTTONS[2] = new Rect((width / 2), height - dimension, (width / 2) + dimension, height);
		// forward
		ANIMATION_BUTTONS[3] = new Rect((width / 2) + dimension, height - dimension, (width / 2) + (dimension * 2),
				height);

		// menu
		CONTROLLER_BUTTONS[0] = new Rect(0, 0, dimension, dimension);
		// move
		CONTROLLER_BUTTONS[1] = new Rect(0, dimension, dimension, (dimension * 2));
		// rotate
		CONTROLLER_BUTTONS[2] = new Rect(0, (dimension * 2), dimension, (dimension * 3));
		// zoom
		CONTROLLER_BUTTONS[3] = new Rect(0, (dimension * 3), dimension, (dimension * 4));
		// reset
		CONTROLLER_BUTTONS[4] = new Rect(0, (dimension * 4), dimension, (dimension * 5));
		// back
		CONTROLLER_BUTTONS[5] = new Rect(0, (dimension * 5), dimension, (dimension * 6));

	}

	@Override
	// se pinta cada frame
	public void onDrawFrame(GL10 gl) {

		float anim=0;
		// Log.v("CAMERA POSITION", world.getCamera().getPosition().toString());
		if (animationActivated) {

			if(isBones){
			animateObjects();
			}

		}

		fb.clear(bgColor);

		world.renderScene(fb);
		if (!wideframeActivated) {
			world.draw(fb);
		} else {
			world.drawWireframe(fb, RGBColor.WHITE, 1, false);

		}

		paintButtonsScene(fb);
		fb.display();

	}

	public void paintButtonsScene(FrameBuffer fb) {

		if (animationActivated) {
			fb.blit(TextureManager.getInstance().getTexture("forwardoff"), 0, 0, (fb.getWidth() / 2 + dimension),
					fb.getHeight() - dimension, dimension, dimension, FrameBuffer.OPAQUE_BLITTING);
			fb.blit(TextureManager.getInstance().getTexture("rewindoff"), 0, 0,
					(fb.getWidth() / 2) - (dimension * 2), fb.getHeight() - dimension, dimension, dimension,
					FrameBuffer.OPAQUE_BLITTING);

			if (pauseAnimation) {
				fb.blit(TextureManager.getInstance().getTexture("pause"), 0, 0, (fb.getWidth() / 2),
						fb.getHeight() - dimension, dimension, dimension, FrameBuffer.OPAQUE_BLITTING);
				fb.blit(TextureManager.getInstance().getTexture("playoff"), 0, 0, (fb.getWidth() / 2) - dimension,
						fb.getHeight() - dimension, dimension, dimension, FrameBuffer.OPAQUE_BLITTING);

			} else {
				fb.blit(TextureManager.getInstance().getTexture("pauseoff"), 0, 0, (fb.getWidth() / 2),
						fb.getHeight() - dimension, dimension, dimension, FrameBuffer.OPAQUE_BLITTING);

			}
			if (playAnimation) {
				fb.blit(TextureManager.getInstance().getTexture("play"), 0, 0, (fb.getWidth() / 2) - dimension,
						fb.getHeight() - dimension, dimension, dimension, FrameBuffer.OPAQUE_BLITTING);
				fb.blit(TextureManager.getInstance().getTexture("pauseoff"), 0, 0, (fb.getWidth() / 2),
						fb.getHeight() - dimension, dimension, dimension, FrameBuffer.OPAQUE_BLITTING);

			} else {
				fb.blit(TextureManager.getInstance().getTexture("playoff"), 0, 0, (fb.getWidth() / 2) - dimension,
						fb.getHeight() - dimension, dimension, dimension, FrameBuffer.OPAQUE_BLITTING);

			}

		}
		if (!menuActivated) {
			fb.blit(TextureManager.getInstance().getTexture("menuoff"), 0, 0, 0, 0, dimension, dimension,
					FrameBuffer.OPAQUE_BLITTING);
		} else {
			fb.blit(TextureManager.getInstance().getTexture("menuon"), 0, 0, 0, 0, dimension, dimension,
					FrameBuffer.OPAQUE_BLITTING);
			fb.blit(TextureManager.getInstance().getTexture("reset"), 0, 0, 0, (dimension * 4), dimension,
					dimension, FrameBuffer.OPAQUE_BLITTING);
			fb.blit(TextureManager.getInstance().getTexture("back"), 0, 0, 0, (dimension * 5), dimension, dimension,
					FrameBuffer.OPAQUE_BLITTING);

			// MOVE
			if (!moveActivated) {
				fb.blit(TextureManager.getInstance().getTexture("moveoff"), 0, 0, 0, dimension, dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);
			} else {
				fb.blit(TextureManager.getInstance().getTexture("moveon"), 0, 0, 0, dimension, dimension, dimension,
						FrameBuffer.OPAQUE_BLITTING);

				fb.blit(TextureManager.getInstance().getTexture("rotateoff"), 0, 0, 0, (dimension * 2), dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);
				fb.blit(TextureManager.getInstance().getTexture("scaleoff"), 0, 0, 0, (dimension * 3), dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);
			}
			// ROTATE
			if (!rotateActivated) {
				fb.blit(TextureManager.getInstance().getTexture("rotateoff"), 0, 0, 0, (dimension * 2), dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);
			} else {
				fb.blit(TextureManager.getInstance().getTexture("rotateon"), 0, 0, 0, (dimension * 2), dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);

				fb.blit(TextureManager.getInstance().getTexture("moveoff"), 0, 0, 0, dimension, dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);
				fb.blit(TextureManager.getInstance().getTexture("scaleoff"), 0, 0, 0, (dimension * 3), dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);
			}
			// ZOOM
			if (!zoomActivated) {
				fb.blit(TextureManager.getInstance().getTexture("scaleoff"), 0, 0, 0, (dimension * 3), dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);
			} else {
				fb.blit(TextureManager.getInstance().getTexture("scaleon"), 0, 0, 0, (dimension * 3), dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);

				fb.blit(TextureManager.getInstance().getTexture("moveoff"), 0, 0, 0, dimension, dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);
				fb.blit(TextureManager.getInstance().getTexture("rotateoff"), 0, 0, 0, (dimension * 2), dimension,
						dimension, FrameBuffer.OPAQUE_BLITTING);
			}
		}
	}

	public boolean controllerButtonListener(MotionEvent event) {

		if (CONTROLLER_BUTTONS[0].contains((int) event.getX(), (int) event.getY())) {
			if (!menuActivated) {
				menuActivated = true;
			} else {
				menuActivated = false;
			}
			return false;
		}

		if (menuActivated) {
			if (CONTROLLER_BUTTONS[1].contains((int) event.getX(), (int) event.getY())) {
				if (!moveActivated) {
					moveActivated = true;
					rotateActivated = false;
					zoomActivated = false;
				} else {
					moveActivated = false;
				}
				return false;
			} else if (CONTROLLER_BUTTONS[2].contains((int) event.getX(), (int) event.getY())) {
				if (!rotateActivated) {
					moveActivated = false;
					rotateActivated = true;
					zoomActivated = false;
				} else {
					rotateActivated = false;
				}

				return false;

			} else if (CONTROLLER_BUTTONS[3].contains((int) event.getX(), (int) event.getY())) {
				if (!zoomActivated) {
					moveActivated = false;
					rotateActivated = false;
					zoomActivated = true;
				} else {
					zoomActivated = false;
				}

				return false;
			} else if (CONTROLLER_BUTTONS[4].contains((int) event.getX(), (int) event.getY())) {
				posInitObject3D();

				return false;
			} else if (CONTROLLER_BUTTONS[5].contains((int) event.getX(), (int) event.getY())) {
				if(mActivity.origin.equals("download")) {
					Intent intent = new Intent(mActivity.getApplicationContext(), ModelosActivity.class);
					mActivity.startActivity(intent);
				}else
					{
						mActivity.finish();
					}


				return false;
			}
		}

		return true;
	}

	public boolean animationButtonListener(MotionEvent event) {
		boolean result = true;
		if (ANIMATION_BUTTONS[0].contains((int) event.getX(), (int) event.getY())) {
			if (animationSpeed > 0 && !pauseAnimation) {
				animationSpeed = animationSpeed - 0.2f;
			}
			result = false;
		} else if (ANIMATION_BUTTONS[1].contains((int) event.getX(), (int) event.getY())) {
			animationSpeed = 1f;
			pauseAnimation = false;
			playAnimation = true;
			result = false;
		} else if (ANIMATION_BUTTONS[2].contains((int) event.getX(), (int) event.getY())) {
			animationSpeed = 0f;
			pauseAnimation = true;
			playAnimation = false;
			result = false;

		} else if (ANIMATION_BUTTONS[3].contains((int) event.getX(), (int) event.getY())) {
			if (animationSpeed < 3 && !pauseAnimation) {
				animationSpeed = animationSpeed + 0.2f;
			}
			result = false;
		}
		return result;
	}
	/// METODO DE CAPTURA DE EVENTOS TOUCH///

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (!controllerButtonListener(event)) {
			return false;
		}

		if (animationActivated) {

			if (!animationButtonListener(event)) {
				return false;
			}
		}

		if (zoomActivated) {
			scaleGestureDetector.onTouchEvent(event);

		}
		gestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = event.getX();
			lastY = event.getY();
			onCollisionDetected((int) event.getX(), (int) event.getY());
			Log.d("TOUCH-ACTION", "bajado");
			return true;
		case MotionEvent.ACTION_UP:
			followFingerCollision = false;
			Log.d("TOUCH-ACTION", "levantado");
			return true;
		case MotionEvent.ACTION_MOVE:

			final float dx = event.getX() - lastX;
			final float dy = event.getY() - lastY;
			lastX = event.getX();
			lastY = event.getY();

			// .........modificacion..............//
			if (rotateActivated) {
				glView.queueEvent(new Runnable() {

					@Override
					public void run() {
						rotateObjectBy(dx, dy);
					}

				});
			}
			if (moveActivated) {

				moveCamera(dx, dy);


			}
			return true;
		}

		return false;
	}

	/**
	 * Funcion que devuelve el vector en el que se ha tocado la pantalla
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private SimpleVector touchToVector(int x, int y) {

		SimpleVector pos = null;
		// y = fb.getHeight() - y;

		SimpleVector dir = Interact2D.reproject2D3DWS(world.getCamera(), fb, x, y).normalize();

		pos = world.getCamera().getPosition();
		Log.d("RAYCAST", "tocado");
		float a = (Z_PLANE - pos.z) / dir.z;

		float xn = pos.x + a * dir.x;
		float yn = pos.y + a * dir.y;

		pos = new SimpleVector(xn, yn, Z_PLANE);

		return pos;

	}

	/// METODOS DE TRANSFORMACION ESPACIAL DEL OBJETO3D Y LAS LUCES///

	/**
	 * Funcion que detecta si donde se ha tocado hay un objeto3D
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean onCollisionDetected(int x, int y) {

		SimpleVector dirCol = Interact2D.reproject2D3DWS(world.getCamera(), fb, x, y).normalize();
		float f = world.calcMinDistance(world.getCamera().getPosition(), dirCol, 1000);
		Log.d("RAYCAST", f + "");
		if (f != Object3D.COLLISION_NONE) {

			followFingerCollision = true;

		} else {
			followFingerCollision = false;
		}
		return followFingerCollision;
	}

	/**
	 * Funcion que va cambiando dinamicamente la posicion de la luz de la escena
	 * en base a donde se toque la pantalla
	 * 
	 * @param x
	 * @param y
	 */
	private void moveLightBy(int x, int y) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		light.setPosition(world.getCamera().getPosition());
		SimpleVector lightVector = touchToVector(x, y);
		lightVector = new SimpleVector(lightVector.x, lightVector.y, world.getCamera().getPosition().z);
		light.setPosition(lightVector);
	}

	/**
	 * Funcion que permite arrastrar un objeto3D en base de donde se ha tocado y
	 * si ese punto corresponde al objeto3D
	 * 
	 * @param x
	 * @param y
	 * 
	 */
	private void moveObjectBy(int x, int y) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.v("MOVIENDO", "moviendooo");
		object3D.clearTranslation();
		object3D.translate(touchToVector(x, y));

	}

	private void moveCamera(float dx, float dy) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (dy > 0) {
			world.getCamera().moveCamera(Camera.CAMERA_MOVEUP, unidadMovimiento * (Math.abs(dy) / 10f));
		} else if (dy < 0) {
			world.getCamera().moveCamera(Camera.CAMERA_MOVEDOWN, unidadMovimiento * (Math.abs(dy) / 10f));
		}

		if (dx > 0) {
			world.getCamera().moveCamera(Camera.CAMERA_MOVELEFT, unidadMovimiento * (Math.abs(dx) / 10f));

		} else if (dx < 0) {
			world.getCamera().moveCamera(Camera.CAMERA_MOVERIGHT, unidadMovimiento * (Math.abs(dx) / 10f));
		}
	}

	/**
	 * Funcion que rota en el ejex y el ejey el objeto3D
	 * 
	 * @param dx
	 * @param dy
	 */
	private void rotateObjectBy(float dx, float dy) {
		dx = dx * -1;
		dy = dy * -1;

		// Log.d("ARA", dx + "");
		// Log.d("ARA", dy + "");

		object3D.rotateX(dy * rotationSpeed);

		object3D.rotateY(dx * rotationSpeed);

	}

	/**
	 * Funcion que resetea los valores del objecto3D a como se configuro
	 * inicialmente
	 */
	public void posInitObject3D() {
		object3D.clearTranslation();
		object3D.translate(posX, posY, posZ);
		object3D.clearRotation();
		object3D.rotateX((float) (rotationX * Math.PI / 180));
		object3D.rotateY((float) (rotationY * Math.PI / 180));
		object3D.rotateZ((float) (rotationZ * Math.PI / 180));
		// object3D.rotateX((float) (Math.PI));
		object3D.scale(scaleObject3D);
		unidadesRecorridas = 0;

		firstScale = true;
	}
}
