package edu.purdue.tanks.universe;


public class BTClient {
	BTConnectedThread mBTConnectedThread;
	String name;
	int id;
	int color;
	GameObject tank;
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
