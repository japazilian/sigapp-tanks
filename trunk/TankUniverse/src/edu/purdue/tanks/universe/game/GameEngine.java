package edu.purdue.tanks.universe.game;

import java.util.ArrayList;
import java.util.Vector;

import android.util.Log;


public class GameEngine extends Thread
{
	public boolean done = false;
	private static char[][] mapGrid;
	/**
	 * constructor for GameEngine
	 * @param gameObjects
	 */
	Vector<GameObject> gameObjects;
	public GameEngine(Vector<GameObject> gameObjects, char[][] mapGrid)
	{
		this.gameObjects = gameObjects;
		this.mapGrid = mapGrid;
	}
	
	/**
	 * The main game engine, updates the positions of all objects in GameObjects
	 */
	@Override
	public synchronized void run()
	{
		while(!done) // outer game loop
		{
			ArrayList<GameObject> removeObjects = new ArrayList<GameObject>();
			try{
				Thread.sleep(10);
				for(GameObject g : gameObjects)
				{
					if(g.needsToBeRemoved) {
						removeObjects.add(g);
					}					
					double time = System.currentTimeMillis();
					g.update(time, mapGrid);
				}
				remove(removeObjects);
			
			}catch(Exception e){}
		}
	}
	
	private synchronized void remove(ArrayList<GameObject> removeObjects) {
		for(GameObject g : removeObjects)
			gameObjects.remove(g);
	}
}
