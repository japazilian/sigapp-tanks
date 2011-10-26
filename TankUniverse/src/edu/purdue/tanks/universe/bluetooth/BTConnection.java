package edu.purdue.tanks.universe.bluetooth;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BTConnection {
	// ArrayList<Hosts> searchForHosts
	// boolean connectToHost(Host)
	// sendMessage()
	// receiveMessage()
	// serverAccept
	
	private BluetoothAdapter mBluetoothAdapter;
	private Activity mActivity;
	
	private final int ACTIVITY_BT_ENABLE = 1;
	private final int ACTIVITY_DISCOVERY_ENABLE = 2;
	
	/**
	 * Initializes a bluetooth connection
	 * @param activity
	 * @throws Exception
	 */
	public BTConnection(Activity activity) throws Exception{
		mActivity = activity;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        	String s = "Device does not support Bluetooth.";
        	throw new Exception(s);
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
            		BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, ACTIVITY_BT_ENABLE);
        }
	}
	
	/**
	 * Called by a client when they want to see a list of Hosts to connect
	 * to
	 * @return
	 */
	public ArrayList<BTHost> searchForHosts() {
		return null;
		
	}
	
	/**
	 * Called by a server to accept a single client, blocks until one connects
	 * @return
	 */
	public BTClient acceptClient() {
		return null;
	}

}
