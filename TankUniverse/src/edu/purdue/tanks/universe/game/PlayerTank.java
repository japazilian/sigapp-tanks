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
		float lx = tankx + .2f*(float)(Math.cos((vr + 135.0f)* Math.PI/180.0));
		float ly = tanky + .2f*(float)(Math.sin((vr + 135.0f)* Math.PI/180.0));;
		float rx = tankx + .2f*(float)(Math.cos((vr + 45.0f)* Math.PI/180.0));
		float ry = tanky + .2f*(float)(Math.sin((vr + 45.0f)* Math.PI/180.0));;
		int mapx = (int)Math.round(tankx);
		int mapy = (int)Math.round(tanky);
		int minx = Math.round(Math.min(lx, rx)); 
		int miny = Math.round(Math.min(ly, ry));
		int maxx = Math.round(Math.max(lx, rx));
		int maxy = Math.round(Math.max(ly, ry));
		//boolean collision = false;
		
		for (int i = minx; i<=maxx;i++) {
			for (int j = miny; j<=maxy;j++) {
				if (!(mapGrid[i][j] == '0'|| mapGrid[i][j] == '2' || mapGrid[i][j] == '4'))
					return true;
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
		
		return true;
	}
	
	@Override
	public void update(double time, char[][] mapGrid) {
		super.update(time, mapGrid);
		rotation = vr;
		float tempx = posx + (float)(time - prev_time)*(0.001f)*vx;
		float tempy = posy + (float)(time - prev_time)*(0.001f)*vy;
		
		//float y = tempy + .2f*(float)(Math.sin((vr + 90.0f)* Math.PI/180.0));//(Math.sin((player.rotation + 90.0f) * Math.PI/180.0));
		/*float lx = tempx + .2f*(float)(Math.cos((vr + 135.0f)* Math.PI/180.0));
		float ly = tempy + .2f*(float)(Math.sin((vr + 135.0f)* Math.PI/180.0));;
		float rx = tempx + .2f*(float)(Math.cos((vr + 45.0f)* Math.PI/180.0));
		float ry = tempy + .2f*(float)(Math.sin((vr + 45.0f)* Math.PI/180.0));;
		*/
		
		if (tempx>=0 && tempy>=0 && tempx<=95 && tempy<=95) // in map check grids
		{
			if (isCollision(posx, posy ,mapGrid)) {
				posx -= .2f*(float)(Math.cos((vr + 90.0f)* Math.PI/180.0));
				posy -= .2f*(float)(Math.sin((vr + 90.0f)* Math.PI/180.0));;
			}
			else {
				if (!isCollision(tempx, posy ,mapGrid))
					posx = tempx; 
				if (!isCollision(posx, tempy ,mapGrid)) 
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
		else { // out of the map
			if(tempx>=0 && tempx<=95)
				posx = tempx;
			else if (tempy>=0 && tempy<=95)
				posy = tempy;
		}
		
		prev_time = time;
		//Log.d("tank", "!!!!!!!!!!("+ tempx +","+tempy+")/("+ lx +"," + ly + ")/(" + rx+"," + ry + ")");
	}

}
