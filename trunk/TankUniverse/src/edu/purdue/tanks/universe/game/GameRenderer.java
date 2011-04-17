package edu.purdue.tanks.universe.game;

import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import edu.purdue.tanks.universe.R;
import edu.purdue.tanks.universe.controls.AnalogStick;

public class GameRenderer implements GLSurfaceView.Renderer {
	Context context;
	Vector<GameObject> gameObjects;
	public ArrayList<GameObject> mapObjects;
	char mapGrid[][] = new char[96][96];
	PlayerTank player; //copy of player info
	int bitMaps[] = { //textures
		R.drawable.tank_p,
		R.drawable.tank_e,
		R.drawable.wall,
		R.drawable.bullet,
		R.drawable.minigun,
		R.drawable.bazooka,
		R.drawable.mine,
		R.drawable.pu_health,
		R.drawable.pu_ghost,
		R.drawable.pu_multi,
		R.drawable.pu_speed,
		R.drawable.pu_radar,
		R.drawable.smoke,
		R.drawable.stick,
		R.drawable.pivot,
		R.drawable.boom
	};
	int totalObjects = bitMaps.length;//15; //number of textures
	int imageResources[] = new int[totalObjects];
	
	Tile map;
	Tile normal;
	Tile steel;
	Tile grass;
	int count = 0;
	Tile water1;
	Tile water2;
	Tile ice;
	Tile brick;
	int width = 1;
	int height = 1;
	float aspect = 1;
	AnalogStick aStick;
	PlayerTank eT;
	
	public GameRenderer (Context context, Vector<GameObject> gameObjects, char[][] mapGrid, PlayerTank player, AnalogStick aStick) {
		this.context = context;
		this.gameObjects = gameObjects;
		this.mapGrid = mapGrid;
		this.player = player;
		this.aStick = aStick;
		
		map = new Tile(context, R.drawable.map, 96.0f, 96.0f);
		normal = new Tile(context, R.drawable.normal, 1.0f, 1.0f);
		steel = new Tile(context, R.drawable.steel, 1.0f, 1.0f);
		grass = new Tile(context, R.drawable.grass, 1.0f, 1.0f);
		water1 = new Tile(context, R.drawable.water1, 1.0f, 1.0f);
		water2 = new Tile(context, R.drawable.water2, 1.0f, 1.0f);
		ice = new Tile(context, R.drawable.ice, 1.0f, 1.0f);
		brick = new Tile(context, R.drawable.wall, 1.0f, 1.0f);
	}
	
	public ArrayList<GameObject> getMapList() {
		return mapObjects;
	}
	
	public void loadTextures(GL10 gl) {
		gl.glGenTextures(totalObjects, imageResources, 0); // Generate OpenGL texture images
		//for (int i = 0; i < 96; i++)
		//	for (int j = 0; j < 96; j++)
		//		mapGrid[i][j] = 0;
		//if (MapLoader.load(context, R.raw.outputfile) != null)
			//mapGrid = MapLoader.load(context, R.raw.outputfile);
		Bitmap bitmap;
		for (int i = 0; i < totalObjects; i++) {
			bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(bitMaps[i]));
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, imageResources[i]);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			  
	        // Build Texture from loaded bitmap for the currently-bind texture ID
			bitmap.recycle();
		}
	}
	
	//Whenever the surface is created or re-created
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);  // Set color's clear-value to black
	    gl.glClearDepthf(1.0f);            // Set depth's clear-value to farthest
	    gl.glEnable(GL10.GL_DEPTH_TEST);   // Enables depth-buffer for hidden surface removal
	    gl.glDepthFunc(GL10.GL_LEQUAL);    // The type of depth testing to do
	    gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  // nice perspective view
	    gl.glShadeModel(GL10.GL_SMOOTH);   // Enable smooth shading of color
	    gl.glDisable(GL10.GL_DITHER);      // Disable dithering for better performance
	    gl.glEnable(GL10.GL_TEXTURE_2D);   // Enable texture (NEW)
	    
	    //Transparency ??
	    gl.glEnable(GL10.GL_BLEND);
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    //gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    //gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
	    
	    // You OpenGL|ES initialization code here
	    // ...... 
	    
	    
	    //gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
	    
	    loadTextures(gl);             // Load images into textures (NEW)
	    map.loadTexture(gl); 
	    normal.loadTexture(gl);
	    steel.loadTexture(gl);
		grass.loadTexture(gl);
		water1.loadTexture(gl);
		water2.loadTexture(gl);
		ice.loadTexture(gl);
		brick.loadTexture(gl);
	    //gl.glEnable(GL10.GL_BLEND);
	    //gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	//Called after onSurfaceCreated() or whenever the window's size changes
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		 if (height == 0) height = 1;   // To prevent divide by zero
	     float aspect = (float)width / height;
	   
	      // Set the viewport (display area) to cover the entire window
	     gl.glViewport(0, 0, width, height);
	  
	     // Setup perspective projection, with aspect ratio matches viewport
	     gl.glMatrixMode(GL10.GL_PROJECTION); // Select projection matrix
	     gl.glLoadIdentity();                 // Reset projection matrix
	     // Use perspective projection
	     GLU.gluPerspective(gl, 45, aspect, 0.1f, 100.f);
	  
	     gl.glMatrixMode(GL10.GL_MODELVIEW);  // Select model-view matrix
	     gl.glLoadIdentity();                 // Reset
	  
	     // You OpenGL|ES display re-sizing code here
	     // ......   
	     this.width = width;
	     this.height = height;
	     this.aspect = aspect;
	}
	
	//Drawing the current frame.
	public void onDrawFrame(GL10 gl) {
		// Clear color and depth buffers using clear-value set earlier
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	     
	    //resets all the matrix options
	    //!!should only be called once in one draw or never
	    gl.glLoadIdentity();                 // Reset model-view matrix ( NEW )
	    
	    /* view angle */
	    //GLU.gluLookAt(gl, gameObjects.get(0).posx, gameObjects.get(0).posy, 20.0f, gameObjects.get(0).posx, gameObjects.get(0).posy, 0.0f, 0.0f, 1.0f, 0.0f);
	    gl.glTranslatef(0.0f, 0.0f, -13.0f);
	    
	    /* print map */
	    gl.glPushMatrix();
	    gl.glTranslatef(47.5f-player.posx, 47.5f-player.posy, -0.1f);
	    map.draw(gl);
	    gl.glPopMatrix();
	    
	    gl.glPushMatrix();
	    //gl.glTranslatef(-player.posx, -player.posy, 0.0f);
	    for (int i = (int)(player.posx-10.0f);i<=(int)(player.posx+10.0f);i++) {
	    	for(int j = (int)(player.posy-10.0f);j<=(int)(player.posy+10.0f);j++) {
	    		if (i >= 0 && i<=95 && j >= 0 && j<=95) {
	    			if (mapGrid[i][j] != '0') {
	    				gl.glPushMatrix();
	    				gl.glTranslatef((float)i-player.posx, (float)j-player.posy, 0);
	    				//gl.glTranslatef(i, j, 0);
	    				//System.out.println("YES YES YES YES!");
	    				switch (mapGrid[i][j]) {
	    					case '1': steel.draw(gl);
	    						break;
	    					case '2': grass.draw(gl);
	    						break;
	    					case '3': if (count < 5)
	    								water1.draw(gl);
	    							else
	    								water2.draw(gl);
	    						break;
	    					case '4': ice.draw(gl);
	    						break;
	    					case '5': brick.draw(gl);
	    						break;
	    					default : 
	    						break;
	    				}
	    				gl.glPopMatrix();
	    			}
	    			//System.out.println("NO NO NO NO NO!");
	    		}
	    		//gl.glTranslatef(0, 1.0f, 0);
	    	}
	    	//gl.glTranslatef(1.0f, 0, 0);
	    }
	    gl.glPopMatrix();	
	    count = (count+1)%10;
	   
	   
	    try {
			/* print all objects */
			for (GameObject g:gameObjects) {
				g.draw(gl, imageResources, player.posx, player.posy);
			}
			//for (GameObject g:uiObjects) {
			//	g.draw(gl, imageResources, player.posx, player.posy);
			//}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}

}
