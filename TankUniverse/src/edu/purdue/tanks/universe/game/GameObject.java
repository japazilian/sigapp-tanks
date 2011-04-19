package edu.purdue.tanks.universe.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public abstract class GameObject {
	
	public float posx, posy, rotation;
	public int imageResource;
	public double prev_time;
	public int type;
	
	public static final int TYPE_PLAYER_TANK = 0;
	public static final int TYPE_ENEMY_TANK = 1;
	public static final int TYPE_WALL = 2;
	public static final int TYPE_BULLET = 3;
	public static final int TYPE_MINIGUN_BULLET = 4;
	public static final int TYPE_BAZOOKA = 5;
	public static final int TYPE_MINE = 6;
	public static final int TYPE_POWERUP_HEALTH = 7;
	public static final int TYPE_POWERUP_GHOST = 8;
	public static final int TYPE_POWERUP_MULTI = 9;
	public static final int TYPE_POWERUP_SPEED = 10;
	public static final int TYPE_POWERUP_RADAR = 11;
	
	public boolean needsToBeRemoved=false;

	
	public FloatBuffer texBuffer;
	public float[] texCoords = {
	         0.0f, 1.0f,  // A. left-bottom
	         1.0f, 1.0f,  // B. right-bottom
	         0.0f, 0.0f,  // C. left-top
	         1.0f, 0.0f   // D. right-top
	};
	
	public GameObject(int type) {
		this.type = type;
		
		posx = 0.0f; 
		posy = 0.0f; 
		rotation = 0.0f;
		prev_time = System.currentTimeMillis();
		
		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
	    tbb.order(ByteOrder.nativeOrder());
	    texBuffer = tbb.asFloatBuffer();
	      
	    texBuffer.put(texCoords);
	      
	    texBuffer.position(0);
	}
	
	public boolean isCollision(float tankx, float tanky, char[][] mapGrid) {
		return false;
	}
	
	public void draw(GL10 gl, int[] imageResources, float playerPosX, float playerPosY) {
		
	}
	
	public void update(double time, char[][] mapGrid) {
		
	}
}
