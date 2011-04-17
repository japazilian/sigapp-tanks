package edu.purdue.tanks.universe;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import edu.purdue.tanks.universe.bluetooth.BTAcceptThread;
import edu.purdue.tanks.universe.bluetooth.BTClient;
import edu.purdue.tanks.universe.controls.AnalogStick;
import edu.purdue.tanks.universe.game.EnemyTank;
import edu.purdue.tanks.universe.game.GameEngine;
import edu.purdue.tanks.universe.game.GameObject;
import edu.purdue.tanks.universe.game.GameRenderer;
import edu.purdue.tanks.universe.game.MapLoader;
import edu.purdue.tanks.universe.game.PlayerTank;
import edu.purdue.tanks.universe.game.Projectile;

public class GameHost extends Activity implements OnClickListener, OnTouchListener {
	
	private BluetoothAdapter mBluetoothAdapter;
	private Context ctx;
	private BTAcceptThread mBTAcceptThread;
	private ArrayList<BTClient> clients;
	private enum Mode {Lobby, Game};
	private Mode mode;
	
	// Lobby elements
	private TextView[] tv_clients;
	private ImageButton btn_start;
	
		// Game elements
		private GLSurfaceView mGLSurfaceView; //layout.game holds this
		private GameRenderer renderer; //the game renderer
		private PlayerTank player; //Copy of the player(me)'s information
		private static Vector<GameObject> gameObjects; //copy of the the GameObjects
		private static ArrayList<GameObject> mapObjects; //copy of the the mapObjects
		//private static ArrayList<GameObject> uiObjects; //copy of the the UI
		private GameEngine gameEngine;
		boolean running  = true;
		private Thread[] sendUpdatesThread;
		
		/* display attributes */
		Display display; 
		float width;
		float height;
		float aStickX;
		float aStickY;
		float aButtonX;
		float aButtonY;
		
		/* sound */
		private MediaPlayer bullet_sound;
		
		//temporary for input handling
		TextView tv;
		float vr = 0;
		float vx = 0;
		float vy = 0;
		AnalogStick aStick;
		char mapGrid[][] = new char[96][96];
	
	private final int ACTIVITY_BT_ENABLE = 1;
	private final int ACTIVITY_DISCOVERY_ENABLE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Tank", "TankHost - onCreate");
		super.onCreate(savedInstanceState);
		mode = Mode.Lobby;
		setContentView(R.layout.lobby);
		ctx = this.getApplicationContext();
		initializeLobbyGUI();
		initializeBluetooth();
	}
	
	/**
	 * Initialize all the gui elements in code to be controlled later
	 */
	private void initializeLobbyGUI() {
		// Yea yea, it's not a gui element, sue me
		clients = new ArrayList<BTClient>(); 
		clients.add(new BTClient(0, 
				getIntent().getStringExtra("username"), 
				getIntent().getIntExtra("color", 0)));
		
		btn_start = (ImageButton)findViewById(R.id.ImageButton01);
		btn_start.setOnClickListener(this);
		
		tv_clients = new TextView[10];
		tv_clients[0] = (TextView)findViewById(R.id.user00);
		tv_clients[1] = (TextView)findViewById(R.id.user01);
		tv_clients[2] = (TextView)findViewById(R.id.user02);
		tv_clients[3] = (TextView)findViewById(R.id.user03);
		tv_clients[4] = (TextView)findViewById(R.id.user04);
		tv_clients[5] = (TextView)findViewById(R.id.user05);
		tv_clients[6] = (TextView)findViewById(R.id.user06);
		tv_clients[7] = (TextView)findViewById(R.id.user07);
		tv_clients[8] = (TextView)findViewById(R.id.user08);
		tv_clients[9] = (TextView)findViewById(R.id.user09);
		
		updateUsersUI();
	}
	
	/**
	 * Initialize Bluetooth, ask user to turn on bluetooth if necessary
	 */
	private void initializeBluetooth() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        	String s = "Device does not support Bluetooth.";
        	toastError(s);
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
            		BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ACTIVITY_BT_ENABLE); 
        }
        else {
        	startSearch();
        }
	}
	
	/**
	 * For host, we set it as discoverable so clients can find it's MAC 
	 * address which is required for making a connection
	 */
	private void startSearch() {
		mBluetoothAdapter.setName("TankHost");
		Intent discoverableIntent = new
		Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		startActivityForResult(discoverableIntent, ACTIVITY_DISCOVERY_ENABLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
		case ACTIVITY_BT_ENABLE:
			if(resultCode == Activity.RESULT_OK)
				startSearch();
			else {
				String s = "Need to activiate Bluetooth for this game.";
				toastError(s);
			}
			break;
		case ACTIVITY_DISCOVERY_ENABLE:
			if(resultCode == Activity.RESULT_CANCELED) {
				String s = "Host needs to be in Discovery Mode so other" +
					" clients can find it";
				toastError(s);
			}
			else {
				mBTAcceptThread = new BTAcceptThread(
						mBluetoothAdapter, mHandler, clients);
				mBTAcceptThread.start();
			}				
			break;
		}
	}
	
	/**
	 * Anything that comes from the Bluetooth connection gets sent to the 
	 * handler, so I take the message and decide what to do accordingly.
	 */
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	byte[] writeBuf = (byte[]) msg.obj;
            // construct a string from the buffer
            String message = new String(writeBuf).trim();
        	Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
        	
        	if(message.startsWith(LobbyConstants.newUserJoinId)) {
        		String[] messageParts = message.split(":");
        		String[] userParts = messageParts[1].split(",");
        		int newUserId = Integer.parseInt(userParts[0]);
        		
        		// giving newly connected user his username
        		for(BTClient c : clients) {
        			if(c.id == newUserId) {
        				c.name = userParts[1];
        				c.color = Integer.parseInt(userParts[2]);
        				break;
        			}
        		}
        		
        		// sending everyone the new list
        		String clientUpdate = LobbyConstants.clientUpdate;
        		for(BTClient c : clients) {
        			clientUpdate = clientUpdate + c.id + "," + c.name + "," +
        				c.color + ".";
        		}
        		for(BTClient c : clients) {
        			if(c.mBTConnectedThread != null) // is null for the host
        				c.mBTConnectedThread.write(clientUpdate.getBytes());
        		}
        		updateUsersUI();
        	}
        	else if(message.startsWith(LobbyConstants.clientDisconnect)){
        		String[] messageParts = message.split(":");
        		
        		// figure out which one it was
        		int disconnectId = Integer.parseInt(messageParts[1]);
        		for(BTClient c : clients) {
        			if(c.id == disconnectId) {
        				clients.remove(c);
        				break;
        			}
        		}
        		
        		// sending everyone the new list
        		String clientUpdate = LobbyConstants.clientUpdate;
        		for(BTClient c : clients) {
        			clientUpdate = clientUpdate + c.id + "," + c.name + "," +
    				c.color + ".";
        		}
        		for(BTClient c : clients) {
        			if(c.mBTConnectedThread != null) // is null for the host
        				c.mBTConnectedThread.write(clientUpdate.getBytes());
        		}
        		updateUsersUI();
        		
        	}
            java.util.Arrays.fill((byte[]) msg.obj, (byte) 0); //flush buffer
        }
    };
    
    /**
	 * Received a message about a user update, need to update the UI
	 */
	private void updateUsersUI() {
		int index = 0;
		for(BTClient c : clients) {
			tv_clients[index].setVisibility(View.VISIBLE);
			tv_clients[index].setText(c.name);
			tv_clients[index].setTextColor(c.color);
			index++;
		}
		for(;index<10;index++) {
			tv_clients[index].setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onStop() {
		Log.d("Tank", "onStop");
		for(BTClient c : clients) {
			if(c.mBTConnectedThread == null) //host doesn't have one
				continue;
			c.mBTConnectedThread.write(LobbyConstants.hostDisconnect.getBytes());
			c.mBTConnectedThread.done = true;
		}
		if (mode == Mode.Lobby) {
			if (mBTAcceptThread != null)
				mBTAcceptThread.done = true;
		}
		if (mode == Mode.Game) {
			if (gameEngine != null)
				gameEngine.done = true;
			if (sendUpdatesThread != null) {
				//for(int i=0; i<sendUpdatesThread.length; i++)
				//	sendUpdatesThread[i].interrupt();
				sendUpdatesDone = true;
			}
		}
		clients.clear();
		super.onStop();
		this.finish();
	}
	
	/**
	 * Shows a toast of the error then closes activity.
	 * @param s : String to display to the user, " Game exiting." follows.
	 */
	private void toastError(String s) {
		s = s + " Game exiting.";
		Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
    	this.onStop();
	}

	public void onClick(View v) {
		btn_start.setVisibility(4);
		btn_start.setClickable(false);
		// Intent i = new Intent(this, Game.class);	
		// startActivity(i);
		mode = Mode.Game;
		for(BTClient c : clients) {
			if(c.mBTConnectedThread == null)
				continue;
			c.mBTConnectedThread.write(LobbyConstants.gameStart.getBytes());
		}
		mBTAcceptThread.interrupt();
		initializeGame();
	}

	/**
	 * ---------ALL GAME RELATED METHODS START HERE----------
	 */
	
	private boolean sendUpdatesDone = false;
	private long lastBulletShot = 0;
	
	/**
	 * Used to inialize elements that were originally done in the onCreate
	 * of the separate Game Activity
	 */
	private void initializeGame() {
		setContentView(R.layout.game);
		gameObjects = new Vector<GameObject>();
		mapObjects = new ArrayList<GameObject>();
		for(BTClient c : clients) {
			if(c.id == 0) {
				c.tank = new PlayerTank();
				player = (PlayerTank) c.tank;
			}
			else {
				c.tank = new EnemyTank();
				c.mBTConnectedThread.mHandler = mGameHandler;
			}
			gameObjects.add(c.tank);
		}
		
	    /* identify screen size */
        display = getWindowManager().getDefaultDisplay(); 
    	width = display.getWidth();
    	height = display.getHeight();
    	aStickX = width/8.0f;
    	aStickY = height*7.0f/8.0f;
    	aButtonX = aStickX*7.0f;
    	aButtonY = aStickY;
	    
	    /* initialize player */
	    //player = new PlayerTank();
        player.posx = 5.0f;
        player.posy = 5.0f;
        //gameObjects.add(player);
        
        aStick = new AnalogStick();
        aStick.posx = -0.00275f*width;
        aStick.posy = -0.00275f*height;
        aStick.x = -0.00275f*width;
        aStick.y = -0.00275f*height;
        //gameObjects.add(aStick);
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // had to initialize and add them in the list before
        // initializing the renderer to avoid weird opengl results
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        
        //char mapGrid[][] = new char[96][96];
        for (int i = 0; i < 96; i++)
    		for (int j = 0; j < 96; j++)
    			mapGrid[i][j] = '0';
        if (MapLoader.load(this, R.raw.outputfile) != null)
        	mapGrid = MapLoader.load(this, R.raw.outputfile);
        
        /* initialize renderer */
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsurfaceview);//new GLSurfaceView(this);
        renderer = (new GameRenderer(this, gameObjects, mapGrid, player, aStick)); 
        mGLSurfaceView.setRenderer(renderer);
        mGLSurfaceView.setOnTouchListener(this);
        
        /* textview for testing purpose */
        tv = (TextView)findViewById(R.id.TextView01);
     	
    	/* initialize sound */
    	bullet_sound = new MediaPlayer();
    	bullet_sound = MediaPlayer.create(getBaseContext(), R.raw.barret);
    	
    	gameEngine = new GameEngine(gameObjects, mapGrid);
    	gameEngine.start();
		
    	sendUpdatesThread = new Thread[clients.size()-1];
    	int index = 0;
    	for(final BTClient c : clients) {
    		if(c.mBTConnectedThread == null)
    			continue;
    		Thread updateClientThread = new Thread(new Runnable(){
    			public void run() {
    				while(!sendUpdatesDone) {
    		    		try {
    		    			Thread.sleep(20);
    		    			String update = LobbyConstants.playerLocations;
    		    			for(BTClient c : clients) {
    		    				update = update + c.id + "," + c.tank.posx + "," 
    		    					+ c.tank.posy + "," + c.tank.rotation + ";";
    		    			}
    		    			for(BTClient c : clients) {
    		    				if(c.id != 0)
    	    		    			c.mBTConnectedThread.write(update.getBytes());
    		    			}
    		    		} catch (Exception e) { }
    		    	}					
    			}
    		});
    		sendUpdatesThread[index++] = updateClientThread;
    		updateClientThread.start();	
    	}
	}

	public boolean onTouch(View v, MotionEvent ev) {
		if((ev.getAction() == MotionEvent.ACTION_UP || ev.getX(0) > width/2.0f || ev.getY(0) < height/2.0f) && ev.getPointerCount() == 1) {
			 player.vx = 0;
			 player.vy = 0;
			 player.inmotion = 0;
			 if (Math.abs(aButtonX-ev.getX(0)) < 50 && Math.abs(aButtonY-ev.getY(0)) < 50) {
				 if(System.currentTimeMillis() - lastBulletShot > .75*1000) {			 
					 bullet_sound.seekTo(0);
					 Projectile p = new Projectile(GameObject.TYPE_BULLET);
					 p.posx = player.posx;
					 p.posy = player.posy;
					 p.rotation = player.rotation;
					 addToGameObjects(p);
					 bullet_sound.start();
					 lastBulletShot = System.currentTimeMillis();
					 Log.d("Engine", "adding at: "+player.posx+", "+player.posy);
			 	}
			 }
		 }
		 else if (ev.getX(0) < width/2.0f && ev.getY(0) > height/2.0f) {
			 //90 degree is subtracted because the image of the tank is facing north
			 player.vr = (float)(Math.atan2(-(ev.getRawY() - aStickY), (ev.getRawX() - aStickX)) *180.0/Math.PI) - 90.0f;
			 player.vx = player.speed*(float)(Math.cos((player.rotation + 90.0f) * Math.PI/180.0));
			 player.vy = player.speed*(float)(Math.sin((player.rotation + 90.0f) * Math.PI/180.0));	
			 player.inmotion = 1;
		 }
		 else {
			 player.vx = 0;
			 player.vy = 0;
			 player.inmotion = 0;
		 }
		 int n;
		 if ((n = ev.getPointerCount()) != 1 && Math.abs(aButtonX-ev.getX(1)) < 30 && Math.abs(aButtonY-ev.getY(1)) < 30) {
			 if(System.currentTimeMillis() - lastBulletShot > .75*1000) {			 
				 bullet_sound.seekTo(0);
				 Projectile p = new Projectile(GameObject.TYPE_BULLET);
				 p.posx = player.posx;
				 p.posy = player.posy;
				 p.rotation = player.rotation;
				 addToGameObjects(p);
				 bullet_sound.start();
				 lastBulletShot = System.currentTimeMillis();
				 Log.d("Engine", "adding at: "+player.posx+", "+player.posy);
		 	}
		 }
		 
		 tv.setText(width+"x"+height+"\ninput:"+(int)ev.getRawX()+"/"+(int)ev.getRawY() + "\n"+"pos="+player.posx+"/"+ player.posy+"\n"+n
				 +"\nmap"+mapGrid[(int)player.posx][(int)player.posy]);
		 
		 return true;
	}

	private synchronized void addToGameObjects(GameObject o) {
		gameObjects.add(o);		
	}

	/**
	 * Anything that comes from the Bluetooth connection gets sent to the 
	 * handler, so I take the message and decide what to do accordingly.
	 */
	private final Handler mGameHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	byte[] writeBuf = (byte[]) msg.obj;
            // construct a string from the buffer
            String message = new String(writeBuf).trim();
        	//Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
        	
        	if(message.startsWith(LobbyConstants.clientPosition)) {
        		String[] messageParts = message.split(":")[1].split(",");
        		int id = Integer.parseInt(messageParts[0]);
        		for(BTClient c : clients) {
        			if(c.id == id) {
        				try {
							c.tank.posx = Float.parseFloat(messageParts[1]);
							c.tank.posy = Float.parseFloat(messageParts[2]);
							c.tank.rotation = Float.parseFloat(messageParts[3]);
							break;
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
        			}
        		}
        	}
        	//TODO need to do one for if client drops in game
        		
            java.util.Arrays.fill((byte[]) msg.obj, (byte) 0); //flush buffer
        }
    };
}
