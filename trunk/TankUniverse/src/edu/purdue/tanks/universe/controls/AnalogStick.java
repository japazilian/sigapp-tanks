package edu.purdue.tanks.universe.controls;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import edu.purdue.tanks.universe.game.GameObject;

public class AnalogStick extends GameObject {
	private FloatBuffer vertexBuffer;
	private float[] vertices = {  // Vertices for the square
		      -0.5f, -0.5f,  0.0f,  // 0. left-bottom
		       0.5f, -0.5f,  0.0f,  // 1. right-bottom
		      -0.5f,  0.5f,  0.0f,  // 2. left-top
		       0.5f,  0.5f,  0.0f   // 3. right-top
	};
	public short inmotion = 0;
	public float x = 0;
	public float y = 0;
	
	public AnalogStick() {
		super(GameObject.TYPE_PLAYER_TANK);
		// Setup vertex array buffer. Vertices in float. A float has 4 bytes
	    ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 4);
	    vbb.order(ByteOrder.nativeOrder()); // Use native byte order
	    vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
	         
	    vertexBuffer.put(vertices);         // Copy data into buffer
		vertexBuffer.position(0);
		x = posx;
		y = posy;
	}

	@Override
	public void draw(GL10 gl, int[] imageResources, float playerPosX, float playerPosY) {
		//super.draw();
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, imageResources[15]);
	    gl.glPushMatrix();
		gl.glTranslatef(-x, y, 17.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glPopMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, imageResources[14]);
	    gl.glPushMatrix();
		gl.glTranslatef(x, y, 16.999f);
		//gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glPopMatrix();
		gl.glBindTexture(GL10.GL_TEXTURE_2D, imageResources[13]);
	    gl.glPushMatrix();
		gl.glTranslatef(posx, posy, 17.0f);
		//gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glPopMatrix();
	  
		//gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
}
