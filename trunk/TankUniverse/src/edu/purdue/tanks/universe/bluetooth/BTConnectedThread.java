package edu.purdue.tanks.universe.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class BTConnectedThread extends Thread {
	private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    public Handler mHandler;
    private ArrayList<BTClient> clients;
    private BlockingQueue<byte[]> messagesToSend;
    boolean isHost;
    public boolean done = false;
    
    public BTConnectedThread(BluetoothSocket socket, Handler mHandler) {
    	// This one is for clients
    	mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.mHandler = mHandler;
        isHost = false;
        messagesToSend = new ArrayBlockingQueue<byte[]>(5000);

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;   	
    }

    public BTConnectedThread(BluetoothSocket socket, Handler mHandler,
    		ArrayList<BTClient> clients) {
    	// This one is for the host
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.mHandler = mHandler;
        this.clients = clients;
        isHost = true;
        messagesToSend = new ArrayBlockingQueue<byte[]>(5000);

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

	public void run() {
        byte[] buffer = new byte[2048];  // buffer store for the stream
        int bytes; // bytes returned from read()
        
        Thread sendMessageThread = new Thread(new Runnable() {

			public void run() {
				while(!done) {
					try {
						byte[] m = messagesToSend.take();
			            mmOutStream.write(m);
			            //Log.d("BT Send", new String(m));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
        	
        });
        sendMessageThread.start();

        // Keep listening to the InputStream until an exception occurs
        while (!done) {
            try {
            	sleep(20);
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI Activity
                mHandler.obtainMessage(0, bytes, -1, buffer) 
                        .sendToTarget();

                //Log.d("BT Receive", new String(buffer));
                //parseMessage(buffer, bytes);
                
            } catch (IOException e) {
                break;
            } catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }

    /* Call this from the main Activity to send data to the remote device */
    public void write(byte[] bytes) {
        messagesToSend.add(bytes);
    }

    /* Call this from the main Activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
