package edu.purdue.tanks.universe.game;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;


public class MapLoader {
	//constructor: Call Load function
	public static int row, col;
	public final static int Normal =0;
	public final static int Steel =1;
	public final static int Grass =2;
	public final static int Water =3;
	public final static int Ice =4;
	public final static int Brick =5;
	
	public static char[][] map;
	

	
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
	public static char[][] load(Context myContext, int resId)//String fileName)
	{
		try {
			
			//File f = new File("/sdcard/Tankmaps/output.txt");
			InputStream inStream = myContext.getResources().openRawResource(resId);//new FileInputStream(f);
			//Scanner scanner = new Scanner(inStream);
			BufferedReader buf = new BufferedReader(new InputStreamReader(inStream));
			//row = scanner.nextInt();
			//col = scanner.nextInt();
			String size = buf.readLine();
			String[] sizes = size.split(" ");
			row = Integer.parseInt(sizes[0]);
			col = Integer.parseInt(sizes[1]);
			map = new char[row][col];
			String s = buf.readLine();
			String text = "row: " + row + " col: " + col;
			for (int j=row-1;j>=0;j--)
			{
				//i = x / j = y
				s = buf.readLine();
				text =text +  "\n";
				for (int i=0; i<col; i++)
				{
					char c = s.charAt(i);
					//Log.d("Tank", "reading row: "+j+" col: "+i+" char: "+c);
					//System.out.println(c+c+c+c+c);
					switch (c)
					{
						case '5': map[i][j] = '5';//Brick; 
										break;
						case '4': map[i][j] = '4';//Steel; 
										break;
						case '3': map[i][j] = '3';//Grass; 
										break;
						case '2': map[i][j] = '2';//Water; 
										break;
						case '1': map[i][j] = '1';//Ice; 
										break;
						case '0': map[i][j] = '0';//Normal;
										break;
						default: break;
					}
					//text = text + map[i][j];
				}
			}
			
			//int duration = Toast.LENGTH_LONG;
			//Toast toast = Toast.makeText(myContext, text, duration);
			//toast.show();
					
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		return map;
	}
}