package edu.purdue.tanks.universe.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


public class Projectile extends GameObject {
	private FloatBuffer vertexBuffer;
	private float[] vertices = {  // Vertices for the square
		      -0.375f, -0.375f,  0.0f,  // 0. left-bottom
		       0.375f, -0.375f,  0.0f,  // 1. right-bottom
		      -0.375f,  0.375f,  0.0f,  // 2. left-top
		       0.375f,  0.375f,  0.0f   // 3. right-top
	};
	public int idtype;
	public float vel = 0.50f;
	
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
		for (int i = 0; i < 5; i++) {
			gl.glTranslatef(0, -0.5f, 0);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		}
		gl.glPopMatrix();
	  
		//gl.glDisable(GL10.GL_CULL_FACE);
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
		//switch()
		double deltatime = time - prev_time;
		posx += (float)((Math.cos(rotation + 90.0f) * Math.PI/180.0) * vel)*deltatime; // vel(h) * cos(theta) = vx(a)*time
		posy += (float)((Math.sin(rotation + 90.0f) * Math.PI/180.0) * vel)*deltatime; // vel(h) * sin(theta) = vx(o)*time
		prev_time = time;
		
		if(posx > 50 || posx < -50 || posy > 50 || posy < -50)
			this.needsToBeRemoved = true;
		
		/*if(timer > 0 && timer < 100) {
			posx += 0.5f*(float)(Math.cos((rotation + 90.0f) * Math.PI/180.0));;
			posy += 0.5f*(float)(Math.sin((rotation + 90.0f) * Math.PI/180.0));;
			timer++;
		}
		else 
			timer = 0;*/
		
	}

}
