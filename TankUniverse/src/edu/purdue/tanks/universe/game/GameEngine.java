package edu.purdue.tanks.universe.game;

import java.util.Vector;


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
		synchronized(gameObjects) {
			while(!done) // outer game loop
			{
				try{Thread.sleep(30);}catch(Exception e){}
				for(GameObject g:gameObjects)
				{
					if(g.needsToBeRemoved) {
						removeObject(g);
					}					
					double time = System.currentTimeMillis();
					g.update(time, mapGrid);
				}
			}
		}
	}
	
	public void removeObject(GameObject o) {
		gameObjects.remove(o);
	}
}
