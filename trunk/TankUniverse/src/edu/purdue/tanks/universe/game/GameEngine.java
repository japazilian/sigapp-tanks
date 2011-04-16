package edu.purdue.tanks.universe.game;

import java.util.ArrayList;


public class GameEngine extends Thread
{
	public boolean done = false;
	/**
	 * constructor for GameEngine
	 * @param gameObjects
	 */
	ArrayList<GameObject> gameObjects;
	public GameEngine(ArrayList <GameObject> gameObjects)
	{
		this.gameObjects = gameObjects;
	}
	
	/**
	 * The main game engine, updates the positions of all objects in GameObjects
	 */
	@Override
	public synchronized void run()
	{
		while(!done) // outer game loop
		{
			try{Thread.sleep(30);}catch(Exception e){}
			for(GameObject g:gameObjects)
			{
				if(g.needsToBeRemoved) {
					gameObjects.remove(g);
				}					
				double time = System.currentTimeMillis();
				g.update(time);
			}
		}
	}
}
