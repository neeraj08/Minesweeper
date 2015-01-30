package com.example.minesweeper;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;


public class MainActivity extends Activity {
	MediaPlayer player;
	int audioPosition;
	SharedPreferences pref;
    boolean isSoundActive;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	// TODO Auto-generated method stub
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	if(isSoundActive)
    	{
    		audioPosition=player.getCurrentPosition();
    		player.pause();
    	}
    }
    @Override
    protected void onPause() {
    	super.onPause();
    	if(isSoundActive)
    	{
    		audioPosition=player.getCurrentPosition();
    		player.pause();
    	}
    }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isSoundActive)
		{
			player.seekTo(audioPosition);
			player.start();
		}
	}
    protected void onCreate(android.os.Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.menu);

    	audioPosition=0;
    	pref=PreferenceManager.getDefaultSharedPreferences(this);
 	    isSoundActive=pref.getBoolean("Sound", true);
 	    
    	
		Log.d("mytag","inside onCreate");
    	try {
    		Log.d("mytag","creating media player");
    			AssetFileDescriptor afd;
    			player=new MediaPlayer();
        		Log.d("mytag","created media player");
    			afd = getAssets().openFd("Harry.mp3");
    			Log.d("mytag","got assets");
    			player.setDataSource(afd.getFileDescriptor());
        		Log.d("mytag","preparing media player");
    			player.prepare();
        		Log.d("mytag","starting media player");
    			if(isSoundActive)
        		player.start();
    			player.setLooping(true);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
    
	Button startButton=(Button) findViewById(R.id.button1);
	Button leaderBoardButton=(Button) findViewById(R.id.button2);
	Button helpButton=(Button) findViewById(R.id.button3);
	Button exitButton=(Button) findViewById(R.id.button4);
	Button settingsButton=(Button)findViewById(R.id.button5);
	
	
	
	startButton.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			if(isSoundActive)
			player.pause();
			startActivity(new Intent(MainActivity.this,Game.class) );

			
		}
	});

	leaderBoardButton.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			if(isSoundActive)
			player.pause();
			Log.d("lead", "caling leaderboard");
			startActivity(new Intent(MainActivity.this,LeaderBoard.class) );

			
		}
	});

	settingsButton.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			if(isSoundActive)
			player.stop();
			startActivity(new Intent(MainActivity.this,Settings.class) );
		}
	});

	helpButton.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			if(isSoundActive)
			player.pause();
			startActivity(new Intent(MainActivity.this,Help.class) );

			
		}
	});

	exitButton.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			if(isSoundActive)
			player.stop();
			System.exit(0);;
		}
	});
	

};
	
	

}
