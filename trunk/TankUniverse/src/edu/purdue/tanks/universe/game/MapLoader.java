package edu.purdue.tanks.universe.game;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.content.Context;
import android.widget.Toast;


public class MapLoader {
	//constructor: Call Load function
	public int row, col;
	public final static int Normal =0;
	public final static int Steel =1;
	public final static int Grass =2;
	public final static int Water =3;
	public final static int Ice =4;
	public final static int Brick =5;
	
	public int[][] map;
	
	MapLoader(Context myContext, String fileName)
	{
		load(myContext, fileName);
	}
	
	//load the map from the text file.
	/**
	 * example: 
	 * 12 12
		BBBBBBBBBBBB
		B B        B
		B B  BB    B
		B B  SS    B
		B    BB    B
		B  BBGGBB  B
		B  IIBBII  B
		B  IGGGGI  B
		B        B B
		B        B B
		B        B B
		BBBBBBBBBBBB
	 */
	public void load(Context myContext, String fileName)
	{
		try {
			InputStream inStream = myContext.getAssets().open(fileName);
			Scanner scanner = new Scanner(inStream);
			row = scanner.nextInt();
			col = scanner.nextInt();
			map = new int[row][col];
			String s = scanner.nextLine();
			String text = "row: " + row + " col: " + col;
			for (int i=0;i<row;i++)
			{
				s = scanner.nextLine();
				text =text +  "\n";
				for (int j=0; j<col; j++)
				{
					char c = s.charAt(j);
					switch (c)
					{
						case 'B': map[i][j] = Brick; break;
						case 'S': map[i][j] = Steel; break;
						case 'G': map[i][j] = Grass; break;
						case 'W': map[i][j] = Water; break;
						case 'I': map[i][j] = Ice; break;
						case ' ': map[i][j] = Normal; break;
						default: break;
					}
					text = text + map[i][j];
				}
			}
			
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(myContext, text, duration);
			toast.show();
					
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}