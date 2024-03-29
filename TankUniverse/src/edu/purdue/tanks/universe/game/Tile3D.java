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
public class Tile3D {
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
	   
	   // Constructor - Setup the vertex buffer
	   public Tile3D(Context context, int id, float w, float h) {
	      // Setup vertex array buffer. Vertices in float. A float has 4 bytes
	      ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 4);
	      vbb.order(ByteOrder.nativeOrder()); // Use native byte order
	      vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
	      
	      imageFileID = id;
	      bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(imageFileID));
	      //bitmap 
	      /*InputStream is= context.getResources().openRawResource(imageFileIDs);
	      try {
	          bitmap = BitmapFactory.decodeStream(is);

	      } finally {
	          //Always clear and close
	          try {
	              is.close();
	              is = null;
	          } catch (IOException e) {
	          }
	      }*/
	         int imgWidth = bitmap.getWidth();
	         int imgHeight = bitmap.getHeight();
	         float faceWidth = w;
	         float faceHeight = h;
	         // Adjust for aspect ratio
	         if (imgWidth > imgHeight) {
	            faceHeight = faceHeight * imgHeight / imgWidth; 
	         }
	         else {
	            faceWidth = faceWidth * imgWidth / imgHeight;
	         }
	         float faceLeft = -faceWidth / 2;
	         float faceRight = -faceLeft;
	         float faceTop = faceHeight / 2;
	         float faceBottom = -faceTop;
	         
	        
	         
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
		   gl.glFrontFace(GL10.GL_CCW);    // Front face in counter-clockwise orientation
		      gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
		      gl.glCullFace(GL10.GL_BACK);    // Cull the back face (don't display) 
		   
		      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		      gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		      gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Enable texture-coords-array (NEW)
		      gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer); // Define texture-coords buffer (NEW)
		      
		      
		      //gl.glEnable(GL10.GL_BLEND);
			  //  gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		      
		      
		      
		      // front
		      gl.glPushMatrix();
		      gl.glTranslatef(0.0f, 0.0f, 0.5f);
		      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);
		      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		      gl.glPopMatrix();
		  
		      // left
		      gl.glPushMatrix();
		      gl.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
		      gl.glTranslatef(0.0f, 0.0f, 0.5f);
		      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		      gl.glPopMatrix();
		  
		      // back
		      gl.glPushMatrix();
		      gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		      gl.glTranslatef(0.0f, 0.0f, 0.5f);
		      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		      gl.glPopMatrix();
		  
		      // right
		      gl.glPushMatrix();
		      gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
		      gl.glTranslatef(0.0f, 0.0f, 0.5f);
		      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		      gl.glPopMatrix();
		  
		      // top
		      gl.glPushMatrix();
		      gl.glRotatef(270.0f, 1.0f, 0.0f, 0.0f);
		      gl.glTranslatef(0.0f, 0.0f, 0.5f);
		      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		      gl.glPopMatrix();
		  
		      // bottom
		      gl.glPushMatrix();
		      gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
		      gl.glTranslatef(0.0f, 0.0f, 0.5f);
		      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		      gl.glPopMatrix();
		      
		      //gl.glDisable(GL10.GL_BLEND);
		      gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  // Disable texture-coords-array (NEW)
		      gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		      gl.glDisable(GL10.GL_CULL_FACE);
		      
	   }
	   public void loadTexture(GL10 gl) {
		   gl.glGenTextures(1, textureID, 0); // Generate texture-ID array

		      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);   // Bind to texture ID
		      // Set up texture filters
		      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		      gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		  
		      // Construct an input stream to texture image "res\drawable\nehe.png"
		      
		      // Build Texture from loaded bitmap for the currently-bind texture ID
		      GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		      //bitmap.recycle();
		      
		}
	}
