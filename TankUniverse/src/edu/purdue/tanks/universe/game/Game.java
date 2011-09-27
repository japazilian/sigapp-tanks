package edu.purdue.tanks.universe.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
	
	public HashMap<Integer, PlayerStats> pStats;
	public List<Round> rounds;
	public List<Map> maps; //Map class that will contain File
	
	public Game(HashMap<Integer, PlayerStats> playerStats, List<Round> rounds, List<Map> maps)
	{
		pStats = playerStats;
		this.rounds = rounds;
		this.maps = maps;
	}
}
