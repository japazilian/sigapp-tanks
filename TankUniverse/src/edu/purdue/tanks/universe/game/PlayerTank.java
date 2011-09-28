package edu.purdue.tanks.universe.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class PlayerTank extends GameObject {
	private FloatBuffer vertexBuffer;
	public float vr = 0;
	public float vx = 0;
	public float vy = 0;
	public float speed = 4;
	private float[] vertices = {  // Vertices for the square
		      -0.5f, -0.5f,  0.0f,  // 0. left-bottom
		       0.5f, -0.5f,  0.0f,  // 1. right-bottom
		      -0.5f,  0.5f,  0.0f,  // 2. left-top
		       0.5f,  0.5f,  0.0f   // 3. right-top
	};
	public short inmotion = 0;
	
	public PlayerTank() {
		super(GameObject.TYPE_PLAYER_TANK);
		// Setup vertex array buffer. Vertices in float. A float has 4 bytes
	    ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 4);
	    vbb.order(ByteOrder.nativeOrder()); // Use native byte order
	    vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
	         
	    vertexBuffer.put(vertices);         // Copy data into buffer
		vertexBuffer.position(0);
		prev_time = System.currentTimeMillis();
		
	}

	@Override
	public void draw(GL10 gl, int[] imageResources, float playerPosX, float playerPosY) {
		//super.draw();
		gl.glFrontFace(GL10.GL_CCW);    // Front face in counter-clockwise orientation
		gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
		gl.glCullFace(GL10.GL_BACK);    // Cull the back face (don't display) 
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, imageResources[GameObject.TYPE_PLAYER_TANK]);
	      
		gl.glPushMatrix();
		//gl.glTranslatef(posx, posy, 0.0f);
		gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glPopMatrix();
	  
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	@Override
	public boolean isCollision(float tankx, float tanky, char[][] mapGrid) {
		int mapx = (int)Math.floor(tankx);//(int)Math.round(tankx);
		int mapy = (int)Math.floor(tanky);;//(int)Math.round(tanky);
		
		int temp = mapGrid[mapx][mapy];
		if (!(temp == '0'|| temp == '2' || temp == '4'))
			return true;
		else {
			for (int i = (int)Math.max(0, mapx-1); i <= (int)Math.min(95, mapx+1); i++) {
				for (int j = (int)Math.max(0, mapy-1); j <= (int)Math.min(95, mapy+1); j++) {
					temp = mapGrid[i][j];
					if (!(temp == '0'|| temp == '2' || temp == '4')){
						//Log.d("tank", tankx+","+tanky);
						if ((tankx > i+.5f-.7f && tankx < i+.5f+.7f) && (tanky > j+.5f-.7f && tanky < j+.5f+.7f)) {
							//Log.d("tank", "hmm");
							return true;
						}
					}
				}
			}
		}
		
		if (mapGrid[mapx][mapy]=='0') {  //normal
			speed = 4;
			return false;
		}
		else if (mapGrid[mapx][mapy]=='2') { //grass
			speed = 3;
			return false;
		}
		else if (mapGrid[mapx][mapy]=='4') { //magic ice that makes you faster. yeah!
			speed = 5;
			return false;
		}
		
		Log.d("tank", "WARNING");
		return true;
	}
	
	@Override
	public void update(double time, char[][] mapGrid) {
		super.update(time, mapGrid);
		rotation = vr;
		float deltatime = (float)((time - prev_time)/1000f);
		
		float tempx = posx + deltatime*vx;
		float tempy = posy + deltatime*vy;
		
		if (tempx>0.5f && tempy>0.5f && tempx<95.5f && tempy<95.5f) // in map check grids
		{
			if (isCollision(posx, posy , mapGrid)) { //push back when tank is in an illegal position
				posx -= .06f*(float)(Math.cos((vr + 90.0f)* Math.PI/180.0));
				posy -= .06f*(float)(Math.sin((vr + 90.0f)* Math.PI/180.0));;
			}
			else {
				if (!isCollision(tempx, posy , mapGrid))
					posx = tempx; 
				if (!isCollision(posx, tempy , mapGrid)) 
					posy = tempy; 
			}
			/*
			if (!isCollision(lx, ly ,mapGrid) && !isCollision(rx, ry ,mapGrid)){
				posx = tempx; 
				posy = tempy;
			}*/
			//if (!isCollision(posx, tempy ,mapGrid) ) 
			//	posy = tempy;
			
		}
		else if (tempx>0.5f && tempx<95.5f)
		{
			if (isCollision(posx, posy , mapGrid)) { //push back when tank is in an illegal position
				posx -= .06f*(float)(Math.cos((vr + 90.0f)* Math.PI/180.0));
				posy -= .06f*(float)(Math.sin((vr + 90.0f)* Math.PI/180.0));;
			}
			else {
				if (!isCollision(tempx, posy ,mapGrid))
					posx = tempx;  
			}
		}
		else if (tempy>0.5f && tempy<95.5f)
		{
			if (isCollision(posx, posy , mapGrid)) { //push back when tank is in an illegal position
				posx -= .06f*(float)(Math.cos((vr + 90.0f)* Math.PI/180.0));
				posy -= .06f*(float)(Math.sin((vr + 90.0f)* Math.PI/180.0));;
			}
			else {
				if (!isCollision(posx, tempy , mapGrid)) 
					posy = tempy;   
			}
		}
		else { // out of the map
			//if(tempx>0 && tempx<95)
			//	posx = tempx;
			//else if (tempy>0 && tempy<95)
			//	posy = tempy;
		}
		
		prev_time = time;
	}

}
