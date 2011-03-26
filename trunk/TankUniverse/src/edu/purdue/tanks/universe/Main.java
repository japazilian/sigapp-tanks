package edu.purdue.tanks.universe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Main extends Activity implements OnClickListener, ColorPickerDialog.OnColorChangedListener{
    private EditText username;
    private int color;
	// Hello World
    //Main Cooment
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button colorButton = (Button)findViewById(R.id.btn_pickColor);
        colorButton.setOnClickListener(this);
        
        Button hostButton = (Button)findViewById(R.id.btn_host);
        hostButton.setOnClickListener(this);
        
        Button joinButton = (Button)findViewById(R.id.btn_join);
        joinButton.setOnClickListener(this);
        
        username = (EditText)findViewById(R.id.edit_username);
    }
    
	public void onClick(View v) {	
		if(v.getId() == R.id.btn_pickColor) {
			ColorPickerDialog dialog = new ColorPickerDialog(this, 
					((ColorPickerDialog.OnColorChangedListener)this), color);
			dialog.show();
		}
		if(v.getId() == R.id.btn_join)
		{
			if(username.getText().toString().length() == 0)
				return;
			Intent i = new Intent(this, GameClient.class);
			i.putExtra("username", username.getText().toString());
			i.putExtra("color", color);
			startActivity(i);
		}
		else if(v.getId() == R.id.btn_host)
		{
			if(username.getText().toString().length() == 0)
				return;
			Intent i = new Intent(this, GameHost.class);
			i.putExtra("username", username.getText().toString());
			i.putExtra("color", color);
			startActivity(i);
		}		
	}

	public void colorChanged(int color) {
		this.color = color;
		
		Button colorButton = (Button)findViewById(R.id.btn_pickColor);
        colorButton.setBackgroundColor(color);
		
	}
    
}