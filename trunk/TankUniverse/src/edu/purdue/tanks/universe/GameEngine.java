package edu.purdue.tanks.universe;

import java.util.ArrayList;

public class GameEngine extends Thread
{
	/**
	 * constructor for GameEngine
	 * @param gameObjects
	 */
	ArrayList <GameObject> gameObjects;
	public GameEngine(ArrayList <GameObject> gameObjects)
	{
		this.gameObjects = gameObjects;
	}
	
	/**
	 * The main game engine, updates the positions of all objects in GameObjects
	 */
	@Override
	public void run()
	{
		while(true) // outer game loop
		{
			try{Thread.sleep(30);}catch(Exception e){}
			for(GameObject g:gameObjects)
			{
				double time = System.currentTimeMillis();
				g.update(time);
			}
		}
	}
}
