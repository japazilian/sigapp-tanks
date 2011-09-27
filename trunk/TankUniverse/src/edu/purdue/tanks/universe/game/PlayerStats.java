package edu.purdue.tanks.universe.game;

public class PlayerStats {
	
	public int points;
	public int kills;
	public int playerId;
	
	public PlayerStats(int p_Id){
		playerId = p_Id;
		points = 0;
		kills = 0;
	}	
}
