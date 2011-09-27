package edu.purdue.tanks.universe.game;

import java.util.HashMap;
import java.util.List;

public class Round {
	
	// this needs to be a hashmap because we need to give a pId and get the playerStats object back
	public HashMap<Integer, PlayerStats> pStats; 
	public int gameTime;
	public boolean roundOver; // False is not over, True is round over
	public boolean playerAlive; // False is player dead, True is player alive
	
	public Round(List<PlayerStats> playerList, int gameTime){
		for(PlayerStats player : playerList){
			pStats.put(player.playerId, player);
		}
		this.gameTime = gameTime;
	}
	
	public void UpdatePlayer(int playerId, int addPoints, int addKills){
		if(pStats.containsKey(playerId)){
			//This means that the player we are trying to update is in the pStats Hashmap
			int currentPoints = pStats.get(playerId).points;
			int currentKills = pStats.get(playerId).kills;
			pStats.get(playerId).points += addPoints;
			pStats.get(playerId).kills += addKills;
		} else {
			//Player is not in hashmap.
			//I'm not sure if players will be created somewhere else. If this is the case then
				// an error needs to be thrown here, instead of adding the player
			// If we want updatePlayer to add a player then the code for adding the player to the
				// hashmap will go here
		}
	}
}
