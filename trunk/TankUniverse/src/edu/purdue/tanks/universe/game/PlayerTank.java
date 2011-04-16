package edu.purdue.tanks.universe.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class PlayerTank extends GameObject {
	private FloatBuffer vertexBuffer;
	public float vr = 0;
	public float vx = 0;
	public float vy = 0;
	public float speed = 5;
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
	public boolean isCollision(int tankx, int tanky) {
		return super.isCollision(tankx, tanky);
	}

	@Override
	public void update(double time) {
		super.update(time);
		rotation = vr;
		posx += (float)(time - prev_time)*(0.001)*vx; 
		posy += (float)(time - prev_time)*(0.001)*vy;
		prev_time = time;
		//Log.d("tank", (time-prev_time) + "/" + vx + "/" + "/" + vy);
	}

}
