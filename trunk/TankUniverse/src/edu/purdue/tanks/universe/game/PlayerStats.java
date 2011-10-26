package edu.purdue.tanks.universe.game;

public class PlayerStats {
	
	private int playerHealth;
	
	private int points;
	private int kills;
	public int playerId;
	public boolean isplayerAlive; // False is player dead, True is player alive
	
	public PlayerStats(int p_Id){
		playerId = p_Id;
		points = 0;
		kills = 0;
	}

	public void initializePlayerHealth(int health){
		if(health <= 0){
			//throw new NegativeHealthException();
			// I am not sure if I am allowed to throw Exception here
			// Is it better to create a new Exception class or something else????
		}
		this.playerHealth = health;
	}
	
	public void decreasePlayerHealth(){
		if(!isplayerAlive){
			//throw new PlayerDeadException()
			//same issue as in initializePlayerHealth method
		}
		this.playerHealth--;
		if(playerHealth == 0){
			isplayerAlive = false;
			
		}
	}
	
	public void increasePoints(int pointsToAdd){
		this.points += pointsToAdd;
	}
	
	public void addKill(){
		this.kills++;
	}
	
	
}
