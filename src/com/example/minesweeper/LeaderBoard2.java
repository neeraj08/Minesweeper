package com.example.minesweeper;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class LeaderBoard2 extends Activity {

	SQLiteDatabase db;
	String name;
	int score;
	int time;
	TextView [] names;
	TextView [] scores;
	TextView [] times;
	Button clearTheScores;
	Button sortByTime;
	Button sortByScore;
	Cursor c;
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();		
		db.close();
	}
	
	
protected void onCreate(android.os.Bundle savedInstanceState) {
	
	Log.d("LeaderBoard","calling super cons");

	super.onCreate(savedInstanceState);
	
	Log.d("LeaderBoard","inside oncreate");
	
	
	setContentView(R.layout.leaderboard);
	Log.d("lead","in leaderboard oncreate");
	names=new TextView[4];
	scores=new TextView[4];
	times=new TextView[4];
	clearTheScores=(Button)findViewById(R.id.clearScores);
	sortByTime=(Button)findViewById(R.id.sortTime);
	sortByScore=(Button)findViewById(R.id.sortScore);
	
	
	Log.d("LeaderBoard","variables init done");
	
	
	int noOfRows=4;
	int i=0;
	
		Log.d("leader...","linking "+(0x7f07004f+4*i));
		names[0]=(TextView)findViewById(R.id.namefiled2);
		names[1]=(TextView)findViewById(R.id.namefiled3);
		names[2]=(TextView)findViewById(R.id.namefiled4);
		names[3]=(TextView)findViewById(R.id.namefiled5);
		
		
		Log.d("leader...","linking "+(0x7f070050+4*i));
		scores[0]=(TextView)findViewById(R.id.scorefild2);
		scores[1]=(TextView)findViewById(R.id.scorefild3);
		scores[2]=(TextView)findViewById(R.id.scorefild4);
		scores[3]=(TextView)findViewById(R.id.scorefild5);
		

		Log.d("leader...","linking "+(0x7f070051+4*i));
		times[0]=(TextView)findViewById(R.id.timefiled2);
		times[1]=(TextView)findViewById(R.id.timefiled3);
		times[2]=(TextView)findViewById(R.id.timefiled4);
		times[3]=(TextView)findViewById(R.id.timefiled5);
		

		i=0;
	
	db=openOrCreateDatabase("ScoresDatabase", MODE_PRIVATE,null);
		c=db.rawQuery("SELECT * FROM SCORES ORDER BY time", null);
	
	if(c.moveToFirst())
		do
	{
		name=c.getString(c.getColumnIndex("name"));
		score=c.getInt(c.getColumnIndex("score"));
		time=c.getInt(c.getColumnIndex("time"));
		names[i].setText(name);
		scores[i].setText(Integer.toString(score));
		times[i].setText(Integer.toString(time));
		i++;
	}while(c.moveToNext() && i<noOfRows);
	
	clearTheScores.setOnClickListener(new OnClickListener() {
		
		public void onClick(View arg0) {
			db.execSQL("DELETE FROM SCORES;");
			Intent intent = getIntent();
		    finish();
		    startActivity(intent);
			
		}

	});
	

	sortByScore.setOnClickListener(new OnClickListener() {
		
		public void onClick(View arg0) {
			db=openOrCreateDatabase("ScoresDatabase", MODE_PRIVATE,null);
			c=db.rawQuery("SELECT * FROM SCORES ORDER BY score DESC", null);
		int i=0;
		if(c.moveToFirst())
			do
		{
			name=c.getString(c.getColumnIndex("name"));
			score=c.getInt(c.getColumnIndex("score"));
			time=c.getInt(c.getColumnIndex("time"));
			names[i].setText(name);
			scores[i].setText(Integer.toString(score));
			times[i].setText(Integer.toString(time));
			i++;
		}while(c.moveToNext() && i<4);

		}
	});

	sortByTime.setOnClickListener(new OnClickListener() {
		
		public void onClick(View arg0) {
			db=openOrCreateDatabase("ScoresDatabase", MODE_PRIVATE,null);
			c=db.rawQuery("SELECT * FROM SCORES ORDER BY time", null);
		int i=0;
		if(c.moveToFirst())
			do
		{
			name=c.getString(c.getColumnIndex("name"));
			score=c.getInt(c.getColumnIndex("score"));
			time=c.getInt(c.getColumnIndex("time"));
			names[i].setText(name);
			scores[i].setText(Integer.toString(score));
			times[i].setText(Integer.toString(time));
			i++;
		}while(c.moveToNext() && i<4);

		}
	});
	
			
//use  list view		???
};//onCreate() ends
	
	

}
