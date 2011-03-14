package edu.purdue.tanks.universe;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class BTConnectThread extends Thread {
	private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BTConnectedThread mBTConnectedThread;
    private UUID MY_UUID = 
    	UUID.fromString("eee87450-316e-11e0-91fa-0800200c9a66");

    public BTConnectThread(BluetoothDevice device, BluetoothAdapter bta, 
    		Handler mHandler) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        mBluetoothAdapter = bta;
        this.mHandler = mHandler;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mmSocket);
        mBTConnectedThread = new BTConnectedThread(mmSocket, mHandler);
        mBTConnectedThread.start();
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }

	public BTConnectedThread getConnectedThread() {
		return mBTConnectedThread;
	}
}
