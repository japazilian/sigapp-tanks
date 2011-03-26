package edu.purdue.tanks.universe.bluetooth;

import edu.purdue.tanks.universe.game.GameObject;


public class BTClient {
	public BTConnectedThread mBTConnectedThread;
	public String name;
	public int id;
	public int color;
	public GameObject tank;
	public BTClient(BTConnectedThread b, int newUserId) {
		mBTConnectedThread = b;
		id = newUserId;
	}
	public BTClient(int id, String name, int color) {
		this.id = id;
		this.name = name;
		this.color = color;
	}
}
