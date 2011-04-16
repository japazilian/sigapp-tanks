package edu.purdue.tanks.universe.game;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;
import edu.purdue.tanks.universe.R;


public class MapLoader {
	//constructor: Call Load function
	public static int row, col;
	public final static int Normal =0;
	public final static int Steel =1;
	public final static int Grass =2;
	public final static int Water =3;
	public final static int Ice =4;
	public final static int Brick =5;
	
	public static int[][] map;
	

	
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
	public static int[][] load(Context myContext, int resId)//String fileName)
	{
		try {
			
			InputStream inStream = myContext.getResources().openRawResource(resId);
			Scanner scanner = new Scanner(inStream);
			row = scanner.nextInt();
			col = scanner.nextInt();
			map = new int[row][col];
			String s = scanner.nextLine();
			String text = "row: " + row + " col: " + col;
			for (int j=row-1;j>=0;j--)
			{
				//i = x / j = y
				s = scanner.nextLine();
				text =text +  "\n";
				for (int i=0; i<col; i++)
				{
					char c = s.charAt(i);
					//System.out.println(c+c+c+c+c);
					switch (c)
					{
						case '5': map[i][j] = 5;//Brick; 
										break;
						case '4': map[i][j] = 4;//Steel; 
										break;
						case '3': map[i][j] = 3;//Grass; 
										break;
						case '2': map[i][j] = 2;//Water; 
										break;
						case '1': map[i][j] = 1;//Ice; 
										break;
						case '0': map[i][j] = 0;//Normal;
										break;
						default: break;
					}
					text = text + map[i][j];
				}
			}
			
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(myContext, text, duration);
			toast.show();
					
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		return map;
	}
}