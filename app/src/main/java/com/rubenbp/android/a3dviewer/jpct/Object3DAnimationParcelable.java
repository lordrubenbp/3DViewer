package com.rubenbp.android.a3dviewer.jpct;

import java.util.ArrayList;

import com.threed.jpct.RGBColor;

import android.os.Parcel;
import android.os.Parcelable;

public class Object3DAnimationParcelable implements Parcelable{
	
	
	private String model3DPath;
	private String material3DPath;
	private int positionX;
	private int positionY;
	private int positionZ;
	
	private float rotationX;
	private float rotationY;
	private float rotationZ;
	private int translationX;
	private int translationY;
	private int translationZ;
	private float zoomMax;
	private float zoomMin;
	//private int zoomActivated;
	//private int rotateActivated;
	//private int moveActivated;
	private int animationSecuence;
	private int animationSpeed;
	private  ArrayList<String> texturePath;
	
	private int animationActivated;
	
	private int wideframeActivated;

	private float rotateSpeed;
	private float moveSpeed;
	private float zoomSpeed;
	private int scale;
	private String id;
	
	private RGBColor backgroundColor;
	private int rBGColor;
	private int gBGColor;
	private int bBGColor;
	

	public Object3DAnimationParcelable()
	{
		
	}
	//getters
	
	public String getModel3DPath(){return model3DPath;}
	public String getMaterial3DPath(){return material3DPath;}
	public int getPositionX(){return positionX;}
	public int getPositionY(){return positionY;}
	public int getPositionZ(){return positionZ;}
	
	public float getRotationX(){return rotationX;}
	public float getRotationY(){return rotationY;}
	public float getRotationZ(){return rotationZ;}
	
	public int getTranslationX(){return translationX;}
	public int getTranslationY(){return translationY;}
	public int getTranslationZ(){return translationZ;}
	
	public float getZoomMax(){return zoomMax;}
	public float getZoomMin(){return zoomMin;}
	
	//public boolean getZoomActivated(){ if(zoomActivated==0){return false;}else{return true;}}
	//public boolean getMoveActivated(){ if(moveActivated==0){return false;}else{return true;}}
	//public boolean getRotateActivated(){ if(rotateActivated==0){return false;}else{return true;}}

	public boolean getIsAnimated(){ if(animationActivated==0){return false;}else{return true;}}
	public int getAnimationSecuence(){return animationSecuence;}
	public int getAnimationSpeed(){return animationSpeed;}
	
	public ArrayList<String> getTexturePath(){return texturePath;}

	public boolean getWideframeActivated(){ if(wideframeActivated==0){return false;}else{return true;}}

	public float getRotateSpeed(){return rotateSpeed;}
	public float getMoveSpeed(){return moveSpeed;}
	public float getZoomSpeed(){return zoomSpeed;}

	public int getScale(){return scale;}
	public String getId(){return id;}
	
	public RGBColor getBackGroundColor(){
		
		return backgroundColor= new RGBColor(rBGColor,gBGColor,bBGColor);
	}
	
	//setters
	
	public void setModel3DPath(String path){this.model3DPath=path;}
	public void setMaterial3DPath(String path){this.material3DPath=path;}
	public void setPositionX(int value){this.positionX=value;}
	public void setPositionY(int value){this.positionY=value;}
	public void setPositionZ(int value){this.positionZ=value;}
	
	
	public void setRotationX(float value){this.rotationX=value;}
	public void setRotationY(float value){this.rotationY=value;}
	public void setRotationZ(float value){this.rotationZ=value;}
	
	public void setTranslationX(int value){this.translationX=value;}
	public void setTranslationY(int value){this.translationY=value;}
	public void setTranslationZ(int value){this.translationZ=value;}
	
	public void setZoomMax(float value){this.zoomMax=value;}
	public void setZoomMin(float value){this.zoomMin=value;}
	
	/*public void setZoomActivated(boolean value)
	{
		if(value){this.zoomActivated=1;}else{ this.zoomActivated=0;}
	}
	
	public void setMoveActivated(boolean value)
	{
		if(value){this.moveActivated=1;}else{ this.moveActivated=0;}
	}
	
	public void setRotateActivated(boolean value)
	{
		if(value){this.rotateActivated=1;}else{ this.rotateActivated=0;}
	}*/
	
	public void setIsAnimated(boolean value)
	{
		if(value){this.animationActivated=1;}else{ this.animationActivated=0;}
	}

	public void setWideframeActivated(boolean value)
	{
		if(value){this.wideframeActivated=1;}else{ this.wideframeActivated=0;}
	}
	
	public void setAnimationSecuence(int value){this.animationSecuence=value;}
	public void setAnimationSpeed(int value){this.animationSpeed=value;}
	
	public void setTexturePath(ArrayList<String> value){this.texturePath=value;}
	
	public void setRotateSpeed(float value){this.rotateSpeed=value;}
	public void setMoveSpeed(float value){this.moveSpeed=value;}
	public void setZoomSpeed(float value){this.zoomSpeed=value;}


	public void setScale(int value){this.scale=value;}
	public void setId(String value){this.id=value;}
	
	public void setBGColor(int r, int g, int b)
	{
		this.rBGColor=r;
		this.gBGColor=g;
		this.bBGColor=b;
	}

	//write object values to parcel for storage
	public void writeToParcel(Parcel dest, int flags){
	    
		dest.writeString(model3DPath);
		dest.writeString(material3DPath);
		dest.writeInt(positionX);
		dest.writeInt(positionY);
		dest.writeInt(positionZ);
		
		dest.writeFloat(rotationX);
		dest.writeFloat(rotationY);
		dest.writeFloat(rotationZ);
		
		dest.writeInt(translationX);
		dest.writeInt(translationY);
		dest.writeInt(translationZ);
		
		dest.writeFloat(zoomMax);
		dest.writeFloat(zoomMin);
		
		//dest.writeInt(zoomActivated);
		//dest.writeInt(moveActivated);
		//dest.writeInt(rotateActivated);
		dest.writeInt(animationActivated);
		dest.writeInt(animationSecuence);
		dest.writeInt(animationSpeed);
		
		dest.writeSerializable(texturePath);
		dest.writeInt(wideframeActivated);
	
		dest.writeFloat(rotateSpeed);
		dest.writeFloat(moveSpeed);
		dest.writeFloat(zoomSpeed);

		dest.writeInt(scale);
		dest.writeString(id);
		
		dest.writeInt(rBGColor);
		dest.writeInt(gBGColor);
		dest.writeInt(bBGColor);
		
	}

	//constructor used for parcel
	@SuppressWarnings("unchecked")
	public Object3DAnimationParcelable(Parcel parcel){
	    
		model3DPath = parcel.readString();
		material3DPath= parcel.readString();
		positionX=parcel.readInt();
		positionY=parcel.readInt();
		positionZ=parcel.readInt();
		
		rotationX=parcel.readFloat();
		rotationY=parcel.readFloat();
		rotationZ=parcel.readFloat();
		
		translationX=parcel.readInt();
		translationY=parcel.readInt();
		translationZ=parcel.readInt();
		
		zoomMax=parcel.readFloat();
		zoomMin=parcel.readFloat();
		
		//zoomActivated=parcel.readInt();
		//moveActivated=parcel.readInt();
		//rotateActivated=parcel.readInt();
		
		animationActivated=parcel.readInt();
		animationSecuence=parcel.readInt();
		animationSpeed=parcel.readInt();
	
		texturePath=(ArrayList<String>) parcel.readSerializable();
		wideframeActivated=parcel.readInt();
		
		rotateSpeed=parcel.readFloat();
		moveSpeed=parcel.readFloat();
		zoomSpeed=parcel.readFloat();
		scale=parcel.readInt();
		id=parcel.readString();
		
		rBGColor=parcel.readInt();
		gBGColor=parcel.readInt();
		bBGColor=parcel.readInt();
		
		
	}

	//creator - used when un-parceling our parcle (creating the object)
	public static final Parcelable.Creator<Object3DAnimationParcelable> CREATOR = new Parcelable.Creator<Object3DAnimationParcelable>(){

	    @Override
	    public Object3DAnimationParcelable createFromParcel(Parcel parcel) {
	        return new Object3DAnimationParcelable(parcel);
	    }

	    @Override
	    public Object3DAnimationParcelable[] newArray(int size) {
	        return new Object3DAnimationParcelable[size];
	    }
	};

	//return hashcode of object
	public int describeContents() {
	    return hashCode();
	}


}
