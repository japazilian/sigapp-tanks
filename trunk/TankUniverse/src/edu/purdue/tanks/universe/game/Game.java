package edu.purdue.tanks.universe.game;

import java.util.HashMap;
import java.util.List;


public class Game {
	
	public static final int HIT_POINTS = 10;
	public static final int KILL_POINTS = 100;
	
	public HashMap<Integer, PlayerStats> pStats;
	public List<Round> rounds;
	public boolean isGameOver;
	public Round currentRound;
		
	public Game(List<Integer> playerIds, List<Integer> roundIds,int roundTime, List<String> mapIds){
		for(Integer playerId : playerIds){
			// This populates the pStats Hashmap with playerId and PlayerStats Object
			pStats.put(playerId, new PlayerStats(playerId));
		}
		for(int i=0; i < roundIds.size(); i++){
			// This populates the rounds array with correct round data
			// Each round has the playerStats array, roundIds,roundTime, and the mapId
			rounds.add(new Round(pStats,roundIds.get(i),roundTime,mapIds.get(i)));
		}
	}
	
	public void startGame(){
		if(rounds.isEmpty()){
			//throw new NoRoundsInitializedYetException
		}
		currentRound = rounds.get(0);
		isGameOver = false;
	}
	
	public void endGame(){
		isGameOver = true;
	}
	
	public void playerHit(int playerIdHit, int bulletPlayerId){
		PlayerStats playerWhoGotHit = currentRound.pStats.get(playerIdHit);
		PlayerStats playerWhoShot = currentRound.pStats.get(bulletPlayerId);
		playerWhoGotHit.decreasePlayerHealth();
		playerWhoShot.increasePoints(HIT_POINTS);
		if(!playerWhoGotHit.isplayerAlive){
			playerWhoShot.addKill();
			playerWhoShot.increasePoints(KILL_POINTS);
		}
	}
	
	public void playerKilled(int playerId){
		PlayerStats playerKilled = currentRound.pStats.get(playerId);
		playerKilled.isplayerAlive = false;
		
	}
	
	
}
