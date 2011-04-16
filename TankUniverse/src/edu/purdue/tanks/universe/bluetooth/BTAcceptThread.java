package edu.purdue.tanks.universe.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import edu.purdue.tanks.universe.LobbyConstants;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class BTAcceptThread extends Thread {
	private BluetoothAdapter mBluetoothAdapter;
	private Handler mHandler;
	private final BluetoothServerSocket mmServerSocket;
    private UUID MY_UUID = 
    	UUID.fromString("eee87450-316e-11e0-91fa-0800200c9a66");
    private String NAME = "TankUniverse";
    private ArrayList<BTClient> clients;
    private int newUserId = 1; // host has client id 0 
    public boolean done = false;

    public BTAcceptThread(BluetoothAdapter bta, Handler mHandler, 
    		ArrayList<BTClient> clients) {
    	mBluetoothAdapter = bta;
    	this.mHandler = mHandler;
    	this.clients = clients;
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
            		NAME, MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }

	public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (!done) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {	}
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                //manageConnectedSocket(socket);
            	BTConnectedThread mCT = new BTConnectedThread(
            			socket, mHandler, clients);
            	mCT.start();
            	String newUser = LobbyConstants.newUserAssignId+newUserId;
            	mCT.write(newUser.getBytes());
            	clients.add(new BTClient(mCT, newUserId));
            	newUserId++;
                /*try { TODO need to figure out where to close this
					mmServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
            }
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
        	interrupt();
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}
