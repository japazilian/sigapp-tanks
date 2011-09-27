package edu.purdue.tanks.universe.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
/*
 * A square drawn in 2 triangles (using TRIANGLE_STRIP).
 */
public class Tile {
	   private FloatBuffer vertexBuffer, texBuffer;  // Buffer for vertex-array
	   
	   private float[] texCoords = {
		         0.0f, 1.0f,  // A. left-bottom
		         1.0f, 1.0f,  // B. right-bottom
		         0.0f, 0.0f,  // C. left-top
		         1.0f, 0.0f   // D. right-top
		      };
	   private float[] vertices = {  // Vertices for the square
	      -1.0f, -1.0f,  0.0f,  // 0. left-bottom
	       1.0f, -1.0f,  0.0f,  // 1. right-bottom
	      -1.0f,  1.0f,  0.0f,  // 2. left-top
	       1.0f,  1.0f,  0.0f   // 3. right-top
	   };
	   private int imageFileID;// = R.drawable.boom;
	   private int[] textureID = new int[1];
	   private Bitmap bitmap;
	   
	   private float w,h;
	   private float faceWidth;
       private float faceHeight;
	   private float faceLeft;
	   private float faceRight;
	   private float faceTop;
	   private float faceBottom;
	   
	   // Constructor - Setup the vertex buffer
	   public Tile(Context context, int id, float w, float h) {
	      // Setup vertex array buffer. Vertices in float. A float has 4 bytes
	      ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 4);
	      vbb.order(ByteOrder.nativeOrder()); // Use native byte order
	      vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
	      
	      imageFileID = id;
	      bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(imageFileID));
	      
	         int imgWidth = bitmap.getWidth();
	         int imgHeight = bitmap.getHeight();
	         
	         this.w = w;
	         this.h = h;
	         faceWidth = w;
	         faceHeight = h;
	         // Adjust for aspect ratio
	         if (imgWidth > imgHeight) {
	            faceHeight = faceHeight * imgHeight / imgWidth; 
	         }
	         else {
	            faceWidth = faceWidth * imgWidth / imgHeight;
	         }
	         faceLeft = -faceWidth / 2;
	         faceRight = -faceLeft;
	         faceTop = faceHeight / 2;
	         faceBottom = -faceTop;
	          
	         // Define the vertices for this face
	         float[] vertices = {
	            faceLeft,  faceBottom, 0.0f,  // 0. left-bottom-front
	            faceRight, faceBottom, 0.0f,  // 1. right-bottom-front
	            faceLeft,  faceTop,    0.0f,  // 2. left-top-front
	            faceRight, faceTop,    0.0f,  // 3. right-top-front
	         };
	         vertexBuffer.put(vertices);         // Copy data into buffer
		      vertexBuffer.position(0);           // Rewind
	      
	      ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
	      tbb.order(ByteOrder.nativeOrder());
	      texBuffer = tbb.asFloatBuffer();
	      
	      texBuffer.put(texCoords);
	      
	      texBuffer.position(0);   // Rewind
	   
	   }
	  
	   // Render the shape
	   public void draw(GL10 gl) {
	      // Enable vertex-array and define its buffer
		   //loadTexture(gl);   
		   gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		   gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		   gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		   gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
		      //gl.glEnable(GL10.GL_BLEND);
			    //gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
			    //gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		      
		      
		      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);
		      //gl.glBindTexture(GL10.GL_TEXTURE_2D, imageResources[13]);
		      
		      gl.glPushMatrix();
		      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		      gl.glPopMatrix();
		      //System.out.println("TID:"+textureID[0]);
	      //gl.glColor4f(0.5f, 0.5f, 1.0f, 1.0f);      // Set the current color (NEW)
	      // Draw the primitives from the vertex array directly
	      //gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
		      //gl.glDisable(GL10.GL_BLEND);
		      gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		      gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	   }
	   
public void draw(GL10 gl, float xc, float yc, float x0, float y0, float x1, float y1) {
		   
		   gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		   gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		   
		   float tw = x1*2/w;
		   float th = y1*2/h;
		   float xl = Math.max(((xc)/w -tw/2f)*8f, 0);
		   float xr = Math.min(((xc)/w +tw/2f)*8f, 8);
		   float yb = Math.min((1f-(yc)/h +th/2f)*8f, 8);
		   float yt = Math.max((1f-(yc)/h -th/2f)*8f, 0);
		   
		   if (xl == 0)
			   x0 -= ((xc)/w -tw/2f)*w;
		   else if (xr == 8)
			   x1 -= ((xc)/w +tw/2f -1)*w;
		   if (yb == 8)
			   y0 += (1f-(yc)/h +th/2f -1)*h;
		   else if (yt == 0)
			   y1 += (1f-(yc)/h -th/2f)*h;   
		   
		   float[] vertices = {
				   x0, y0, 0.0f,  // 0. left-bottom-front
				   x1, y0, 0.0f,  // 1. right-bottom-front
				   x0, y1,	0.0f,  // 2. left-top-front
				   x1, y1, 0.0f,  // 3. right-top-front
		   };

		   float[] fvertices = {
				   xl, yb, // 0. left-bottom-front
				   xr, yb,  // 1. right-bottom-front
				   xl, yt,  // 2. left-top-front
				   xr, yt,  // 3. right-top-front
		   };
		   
		   vertexBuffer.clear();
		   vertexBuffer.put(vertices);
		   vertexBuffer.position(0);
		   texBuffer.clear();
		   texBuffer.put(fvertices);
		   texBuffer.position(0);
		   gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		   gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
		      
		   gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);
		      
		   gl.glPushMatrix();
		   	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		   gl.glPopMatrix();
		   
		   gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		   gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	   }
	   
	   public void loadTexture(GL10 gl) {
		      //gl.glGenTextures(6, textureIDs, 0); // Generate texture-ID array for 6 IDs
		   
		   gl.glGenTextures(1, textureID, 0);
		      // Generate OpenGL texture images
		   
		   gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);
		   gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		   gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		   GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		   

		   
		         // Build Texture from loaded bitmap for the currently-bind texture ID
		   
		   bitmap.recycle();
		      
		   }
	}
