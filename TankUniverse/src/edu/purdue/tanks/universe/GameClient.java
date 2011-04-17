package edu.purdue.tanks.universe;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;
import edu.purdue.tanks.universe.bluetooth.BTClient;
import edu.purdue.tanks.universe.bluetooth.BTConnectThread;
import edu.purdue.tanks.universe.bluetooth.BTConnectedThread;
import edu.purdue.tanks.universe.controls.AnalogStick;
import edu.purdue.tanks.universe.game.EnemyTank;
import edu.purdue.tanks.universe.game.GameEngine;
import edu.purdue.tanks.universe.game.GameObject;
import edu.purdue.tanks.universe.game.GameRenderer;
import edu.purdue.tanks.universe.game.MapLoader;
import edu.purdue.tanks.universe.game.PlayerTank;
import edu.purdue.tanks.universe.game.Projectile;

public class GameClient extends Activity implements OnTouchListener {
	
	private BluetoothAdapter mBluetoothAdapter;
	private Context ctx;
	private BTConnectThread mBTConnectThread;
	private BTConnectedThread mBTConnectedThread;
	private boolean hostFound = false;
	private int clientID;
	private ArrayList<BTClient> clients;
	private enum Mode {Lobby, Game};
	private Mode mode;
	
	// Lobby elements
	private ProgressDialog progressDialog;
	private TextView[] tv_clients;
	
	// Game elements
	private GLSurfaceView mGLSurfaceView; //layout.game holds this
	private GameRenderer renderer; //the game renderer
	private PlayerTank player; //Copy of the player(me)'s information
	private static Vector<GameObject> gameObjects; //copy of the the GameObjects
	private static ArrayList<GameObject> mapObjects; //copy of the the mapObjects
	//private static ArrayList<GameObject> uiObjects; //copy of the the UI
	private GameEngine gameEngine;
	boolean running  = true;
	private Thread sendUpdatesThread;
	
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
	
	private final int ACTIVITY_BT_ENABLE = 1;
	
	public static final int MESSAGE_RECEIVED = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("Tank", "TankClient - onCreate");
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
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(true);
		progressDialog.setTitle("Connecting...");
		
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
		}
	}
	
	/**
	 * For host, we set it as discoverable so clients can find it's MAC 
	 * address which is required for making a connection
	 */
	private void startSearch() {
		// Do progress dialog stuff
		progressDialog.setMessage("Searching for Host...");
		progressDialog.show(); 
		
		// Do actual searching
		mBluetoothAdapter.setName("TankClient:"+
				getIntent().getStringExtra("username"));
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        mBluetoothAdapter.startDiscovery();
	}
	
	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    @Override
		public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	        	// do progress dialog stuff
	        	progressDialog.setMessage("Found host. Creating Connection...");
	        	progressDialog.show();
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice hostBTDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            if(hostBTDevice.getName().startsWith("TankHost")) {
	            	hostFound = true;
	            	mBTConnectThread = new BTConnectThread(hostBTDevice, 
	            			mBluetoothAdapter, mHandler);
	            	mBTConnectThread.start();
	            	while((mBTConnectedThread = mBTConnectThread.getConnectedThread()) == null) {
	            		/*try {
							wait(30);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}*/
	            	}
	            }
	            progressDialog.cancel();
	        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
        		// This is called when discovery finished
	        	if(!hostFound)
	        		toastError("Couldn't Find Host.");
	        }
	    }
	};
	
	/**
	 * Anything that comes from the Bluetooth connection gets sent to the 
	 * handler, so I take the message and decide what to do accordingly.
	 */
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	byte[] msgBuf = (byte[]) msg.obj;
            // construct a string from the buffer
            String message = new String(msgBuf).trim();
        	Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
        	if(message.startsWith(LobbyConstants.newUserAssignId)) {
        		String[] messageParts = message.split(":");
        		clientID = Integer.parseInt(messageParts[1]);
        		firstContact(clientID);
        	}
        	else if(message.startsWith(LobbyConstants.clientUpdate)) {
        		// update the lobby to show all the clients
        		String[] messageParts = message.split(":");
        		String[] clientList = messageParts[1].split("\\.");
        		clients.clear();
        		for(int i=0; i<clientList.length; i++) {
        			String[] clientParts = clientList[i].split(",");
        			clients.add(new BTClient(
        					Integer.parseInt(clientParts[0]), 
        					clientParts[1], 
        					Integer.parseInt(clientParts[2])));
        		}
        		updateUsersUI();
        	}
        	else if(message.startsWith(LobbyConstants.hostDisconnect)) {
        		toastError("Host disconnected.");
        	}
        	else if(message.startsWith(LobbyConstants.gameStart)) {
        		initializeGame();
        	}
        	java.util.Arrays.fill((byte[]) msg.obj, (byte) 0);
        }
    };
    
	/**
	 * Send a message to host for the first time
	 * @param clientID 
	 */
	private void firstContact(int clientID) {
		String s = LobbyConstants.newUserJoinId + clientID + "," 
			+ this.getIntent().getStringExtra("username") + "," + 
			this.getIntent().getIntExtra("color", 0);
		mBTConnectedThread.write(s.getBytes());		
	}
	
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
		if(mBTConnectedThread != null) {
			mBTConnectedThread.write((LobbyConstants.clientDisconnect +
						clientID).getBytes());
			mBTConnectedThread.done = true;
		}
		if (mode == Mode.Game) {
			if (gameEngine != null)
				gameEngine.done = true;
			if (sendUpdatesThread != null)
				sendUpdatesDone = true;
		}
		super.onStop();
		this.finish();
	}

	/**
	 * Shows a toast of the error then closes activity.
	 * @param s : String to display to the user, " Exiting..." follows.
	 */
	private void toastError(String s) {
		s = s + " Exiting...";
		Toast.makeText(ctx, s, Toast.LENGTH_LONG).show();
		progressDialog.cancel();
    	this.onStop();
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
			if(c.id == clientID) {
				c.tank = new PlayerTank();
				player = (PlayerTank) c.tank;
			}
			else
				c.tank = new EnemyTank();
			gameObjects.add(c.tank);
		}
		mBTConnectedThread.mHandler = mGameHandler;
		
	    /* identify screen size */
        display = getWindowManager().getDefaultDisplay(); 
    	width = display.getWidth();
    	height = display.getHeight();
    	aStickX = width/8.0f;
    	aStickY = height*7.0f/8.0f;
    	aButtonX = aStickX*7.0f;
    	aButtonY = aStickY;
	    
	    /* initialize player */
	    // player = new PlayerTank();
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
        
        char mapGrid[][] = new char[96][96];
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
    	
    	sendUpdatesThread = new Thread(new Runnable(){
			public void run() {
				while(!sendUpdatesDone) {
		    		try {
		    			Thread.sleep(20);
		    			String update = LobbyConstants.clientPosition;
						update = update + clientID + "," + player.posx + "," 
		    					+ player.posy + "," + player.rotation;
		    			mBTConnectedThread.write(update.getBytes());
		    		} catch (Exception e) { }
		    	}					
			}
		});
		sendUpdatesThread.start();	
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
		 	}
		 }
		 
		 tv.setText(width+"x"+height+"\ninput:"+(int)ev.getRawX()+"/"+(int)ev.getRawY() + "\n"+"vr="+vr +"\n"+n);
		 
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
        	
        	if(message.startsWith(LobbyConstants.playerLocations)) {
        		message = message.substring(message.lastIndexOf("p"));
        		String[] realMessage = message.split(":");
        		String[] allClients = realMessage[1].split(";");
        		for(int i=0; i<allClients.length; i++) {
        			String[] clientParts = allClients[i].split(",");
        			int id = Integer.parseInt(clientParts[0]);
        			if(id == clientID)
        				continue;
            		for(BTClient c : clients) {
            			if(c.id == id) {
            				try {
								c.tank.posx = Float.parseFloat(clientParts[1]);
								c.tank.posy = Float.parseFloat(clientParts[2]);
								c.tank.rotation = Float.parseFloat(clientParts[3]);
	            				break;
							} catch (NumberFormatException e) {
								e.printStackTrace();
							}
            			}
            		}
        			
        		}
        	}
        	else if(message.startsWith(LobbyConstants.hostDisconnect)) {
        		toastError("Host disconnected.");
        	}
        		
            java.util.Arrays.fill((byte[]) msg.obj, (byte) 0); //flush buffer
        }
    };
}
