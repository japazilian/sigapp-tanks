package edu.purdue.tanks.universe.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;


public class Projectile extends GameObject {
	//variables for the collision detection
	private float new_posx = 0;
	private float new_posy = 0;
	private float deltaTime = 0;
	
	private FloatBuffer vertexBuffer;
	private float[] vertices = {  // Vertices for the square
		      -0.175f, -0.175f,  0.0f,  // 0. left-bottom
		       0.175f, -0.175f,  0.0f,  // 1. right-bottom
		      -0.175f,  0.175f,  0.0f,  // 2. left-top
		       0.175f,  0.175f,  0.0f   // 3. right-top
	};
	public int idtype;
	public float vel = 7.0f;
	
	public Projectile(int type) {
		super(type);
		idtype = type;
		// Setup vertex array buffer. Vertices in float. A float has 4 bytes
	    ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 4);
	    vbb.order(ByteOrder.nativeOrder()); // Use native byte order
	    vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
	         
	    vertexBuffer.put(vertices);         // Copy data into buffer
		vertexBuffer.position(0);
	}
	
	@Override
	public void draw(GL10 gl, int[] imageResources, float playerPosX, float playerPosY) {
		//super.draw();
		//gl.glFrontFace(GL10.GL_CCW);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		//gl.glEnable(GL10.GL_CULL_FACE);
		//gl.glCullFace(GL10.GL_BACK);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, imageResources[idtype]);
	      
		gl.glPushMatrix();
		gl.glTranslatef(posx - playerPosX, posy - playerPosY, 0.0f);
		gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, imageResources[12]);
		/*for (int i = 0; i < 5; i++) {
			gl.glTranslatef(0, -0.5f, 0);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		}*/
		gl.glPopMatrix();
	  
		//gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}

	@Override
	public boolean isCollision(float tankx, float tanky, char[][] mapGrid) {
		//possibly requires different method of detection
		//return super.isCollision(tankx, tanky, mapGrid);
		boolean collision = false;
		
		float earliestEvent = deltaTime;
		
		float colx = 0;
		float coly = 0;
		
		//check if it is hitting any environment 
		//collision between the projectile and player
		
		//map
		int firstXC = (int)(Math.min(posx, new_posx));//first mapGrid to search
		int lastXC = (int)(Math.max(posx, new_posx));
		int firstYC = (int)(Math.min(posy, new_posy));
		int lastYC = (int)(Math.max(posy, new_posy));
		
		for (int i = firstXC; i <= lastXC; i++) {
			for (int j = firstYC; j <= lastYC; j++) {
				char temp = mapGrid[i][j];
				if (!(temp == '0'|| temp == '2' || temp == '3' || temp == '4')) {
					//Log.d("tank", "pro");
					if (posx == new_posx) {
						if (posy < new_posy) { //possible collision from bottom
							float time = (float)(j-posy)/((new_posy-posy)/deltaTime);
							float nx = posx;
						
							if (time <= earliestEvent) {
								if (nx >= i && nx <= i+1)  {
									earliestEvent = time;
									colx = nx;
									coly = j;
									collision = true;
								}
							}
						}
						else if (posy > new_posy) { //possible collision from top
							float time = (float)(j+1-posy)/((new_posy-posy)/deltaTime);
							float nx = posx;
						
							if (time <= earliestEvent) {
								if (nx >= i && nx <= i+1)  {
									earliestEvent = time;
									colx = nx;
									coly = j+1;
									collision = true;
								}
							}
						}
					}
					else {
						//Log.d("tank", "pro");
						float slope = 0;
						float y_int = 0;
						if (posx < new_posx) {
							slope = (new_posy-posy)/(new_posx-posx);
							y_int = posy-slope*posx;
						}
						else if (posx > new_posx) {
							slope = (posy-new_posy)/(posx-new_posx);
							y_int = posy-slope*posx;
						}
						Log.d("tank", "slope:"+slope);
						if (posx < new_posx) { //possible collision from left
							float time = (float)(i-posx)/((new_posx-posx)/deltaTime);
							float ny = i*slope+y_int;
						
							if (time <= earliestEvent) {
								//Log.d("tank", "left");
								if (ny >= j && ny <= j+1)  {
									earliestEvent = time;
									colx = i;
									coly = ny;
									collision = true;
								}
							}
						}
						else if (posx > new_posx) { //possible collision from right
							float time = (float)(i+1-posx)/((new_posx-posx)/deltaTime);
							float ny = (i+1)*slope+y_int;
						
							if (time <= earliestEvent) {
								//Log.d("tank", "right");
								if (ny >= j && ny <= j+1)  {
									earliestEvent = time;
									colx = i+1;
									coly = ny;
									collision = true;
								}
							}
						}
						if (posy < new_posy) { //possible collision from bottom
							float time = (float)(j-posy)/((new_posy-posy)/deltaTime);
							float nx = (j-y_int)/slope;
						
							if (time <= earliestEvent) {
								//Log.d("tank", "bottom");
								if (nx >= i && nx <= i+1)  {
									earliestEvent = time;
									colx = nx;
									coly = j;
									collision = true;
								}
							}
						}
						else if (posy > new_posy) { //possible collision from top
							float time = (float)(j+1-posy)/((new_posy-posy)/deltaTime);
							float nx = (j+1-y_int)/slope;
						
							if (time <= earliestEvent) {
								//Log.d("tank", "top:"+nx+","+i+","+(i+1));
								if (nx >= i && nx <= i+1)  {
									earliestEvent = time;
									colx = nx;
									coly = j+1;
									collision = true;
								}
							}
						}
					}
				}
			}
		}
		
		
		return collision;
	}
	
	@Override
	public void update(double time, char[][] mapGrid) {
		super.update(time, mapGrid);
		//switch()
		deltaTime = (float)((time - prev_time)/1000f);
		new_posx = posx + (float)((Math.cos((rotation+90)* Math.PI/180.0) * vel)*deltaTime); // vel(h) * cos(theta) = vx(a)*time
		new_posy = posy + (float)((Math.sin((rotation+90)* Math.PI/180.0) * vel)*deltaTime); // vel(h) * sin(theta) = vx(o)*time
		
		if(new_posx > 96 || new_posx < 0 || new_posy > 96 || new_posy < 0)
			this.needsToBeRemoved = true;
		else {
			if (isCollision(new_posx, new_posy, mapGrid)) {
				this.needsToBeRemoved = true;
			}
			else {
				posx = new_posx;
				posy = new_posy;
			}
		}
	
		prev_time = time;
	}

}
