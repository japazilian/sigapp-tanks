package edu.purdue.tanks.universe.game;

import java.util.HashMap;


public class Round {
	
	// this needs to be a hashmap because we need to give a pId and get the playerStats object back
	public HashMap<Integer, PlayerStats> pStats;
	public HashMap<Integer, PlayerStats> alivePlayers;
	public int roundId;
	public int roundTime;
	public boolean isRoundOver; // False is not over, True is round over
	public GameMap roundMap;
	
	public Round(HashMap<Integer, PlayerStats> playerList,int roundId, int roundTime, 
				 String roundMapId){
		this.pStats = playerList;
		this.alivePlayers = playerList;
		this.roundId = roundId;
		this.roundTime = roundTime;
		this.roundMap.mapId = roundMapId;
	}
	
	/*public void UpdatePlayer(int playerId, int addPoints, int addKills){
		if(pStats.containsKey(playerId)){
			//This means that the player we are trying to update is in the pStats Hashmap
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
	*/
}
