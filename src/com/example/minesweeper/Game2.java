package com.example.minesweeper;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View.OnLongClickListener;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.example.minesweeper.Cell.Type;

import android.os.Vibrator;
import android.preference.PreferenceManager;



public class Game2 extends Activity {
	ImageButton[][] imagebuttons;
	ImageView face;
	Timer timer;
	int timeElapsed;
	TextView timeCounter;
	TextView mineCounter;
	TextView livesCounter;
	TextView scoreCounter;
	Random random;
	boolean isClockRunning;
	int noOfProperlyFlaggedCells;
	int totalNoOfFlaggedCells;
	int noOfMines;
	int noOfCellsOpen;
	Cell[][] cells;
	Player player;
    MediaPlayer bombSound;
    Vibrator vibrate;
    AlertDialog.Builder alert;
    EditText input;
    Context context;
    SQLiteDatabase db;
    SharedPreferences pref;
    boolean isSoundActive;
    boolean isHardDifficulty;
    ProgressBar progress;
    
    
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	if(isSoundActive)
    	bombSound.stop();
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if(isSoundActive)
    	bombSound.stop();
    	if(db.isOpen())
    		db.close();
    }
    
	@Override
    public  void onCreate(Bundle savedInstanceState) {
        Log.d("mylog","inside onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        player=new Player();
        imagebuttons = new ImageButton[8][8];
        face=(ImageView)findViewById(R.id.face);
        timeCounter=(TextView)findViewById(R.id.timeValue);
        mineCounter=(TextView)findViewById(R.id.minesValue);
        livesCounter=(TextView)findViewById(R.id.livesValue);
        scoreCounter=(TextView)findViewById(R.id.scoreValue);
         isClockRunning=false;
        random=new Random();
        vibrate=(Vibrator) getSystemService(VIBRATOR_SERVICE);
    	db=SQLiteDatabase.create(null);
    	context=this.getApplicationContext();
		alert=new AlertDialog.Builder(this);
		db=openOrCreateDatabase("ScoresDatabase",MODE_PRIVATE,null);
	    progress=(ProgressBar)findViewById(R.id.progressBar1);

		progress.setScrollBarStyle(getWallpaperDesiredMinimumHeight());
	    
	    pref=PreferenceManager.getDefaultSharedPreferences(this);
	    isSoundActive=pref.getBoolean("Sound", true);
	    isHardDifficulty=pref.getBoolean("Difficulty", false);

		
	    db.execSQL("CREATE TABLE IF NOT EXISTS  SCORES(name VARCHAR(8),score INT(7),time INT(4));");
	    //db.execSQL("INSERT INTO SCORES VALUES('test',100000,5);");
    	
    	alert.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				player.setName(input.getText().toString());
				db.execSQL("INSERT INTO SCORES VALUES('"+player.getName()+"',"+player.getScore()+","+timeElapsed+");");
		  	
			}
		});
    	
    	alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {		
			public void onClick(DialogInterface dialog, int which) {
			}
		});
    	
        
        cells=new Cell[8][];//memory allocation for cells
        for(int i=0;i<8;i++)
        	cells[i]=new Cell[8];
        for(int i=0;i<8;i++)
        	for(int j=0;j<8;j++)
        		cells[i][j]=new Cell();

        for(int i=0;i<=7;i++)
        	for(int j=0;j<=7;j++)
        	{
        		Log.d("linkinggggggg","imagebutton"+(i*8+j));
       			imagebuttons[i][j]=(ImageButton) findViewById(0x7f080001+(i*8+j));	//R.id.button_xx are stored in R.java as hexadecimal and findviewbyid() needs an int
        	}
        
        
        Log.d("mylog","All linking and memory allocation complete");

        try {
			AssetFileDescriptor afd;
			bombSound=new MediaPlayer();
			afd = getAssets().openFd("Bomb.mp3");
			Log.d("mytag","got assets");
			bombSound.setDataSource(afd.getFileDescriptor());
			Log.d("mytag","preparing media player");
			bombSound.prepare();
			Log.d("mytag","starting media player");
			bombSound.setLooping(false);


		} catch (IOException e) {
			e.printStackTrace();
		}

Log.d("mytag","calling start");
       
        start();//initialize all variables, image views  and text views 
        
        //set OnClickListeners...
        face.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				start();
			}
		});
        
        face.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View arg0) {
				won();
				return false;
			}
		});
        
		Log.d("mytag","created media player");
		
        
        for(int i=0;i<8;i++)
        	for(int j=0;j<8;j++)
        	{
        		imagebuttons[i][j].setOnLongClickListener(new OnLongClickListener() {
					
					public boolean onLongClick(View arg0) {
						int i,j,id=arg0.getId();
						for(i=0;i<8;i++)
						{
							if(imagebuttons[i][0].getId()>id)
							{
								break;
							}
						}
						i--;
						for(j=0;j<8;j++)
							if(imagebuttons[i][j].getId()==id)
								break;
						//now we have the i,j of the button that was clicked
						if(!cells[i][j].getHasFlag())//set flag  if not set
						{
							if(totalNoOfFlaggedCells==noOfMines)
							{
								return true;
							}
							imagebuttons[i][j].setImageResource(R.drawable.flag);
							imagebuttons[i][j].setClickable(false);
							cells[i][j].setHasFlag(true);
							
							totalNoOfFlaggedCells++;
							if(cells[i][j].getHasMine())
								noOfProperlyFlaggedCells++;
								
							if(totalNoOfFlaggedCells==noOfMines && totalNoOfFlaggedCells==noOfProperlyFlaggedCells)
								won();
						}
						else //remove flag if set
						{
							imagebuttons[i][j].setImageResource(R.drawable.plain_green);
							imagebuttons[i][j].setClickable(true);
							cells[i][j].setHasFlag(false);

							totalNoOfFlaggedCells--;
							mineCounter.setText(Integer.toString(noOfMines-totalNoOfFlaggedCells));
							if(cells[i][j].getHasMine())
								noOfProperlyFlaggedCells--;
							
							if(totalNoOfFlaggedCells==noOfMines && totalNoOfFlaggedCells==noOfProperlyFlaggedCells)
								won();
							
						}
						return true;
					}
				});
        	
        		
        		imagebuttons[i][j].setOnClickListener(new OnClickListener() {
					
					public void onClick(View arg0) {
						int i,j,id=arg0.getId();
						for(i=0;i<8;i++)
						{
							if(imagebuttons[i][0].getId()>id)
							{
								break;
							}
						}
						i--;
						for(j=0;j<8;j++)
							if(imagebuttons[i][j].getId()==id)
								break;
						//now we have the i,j of the button that was clicked
						if(cells[i][j].getHasFlag())
							return;
						cells[i][j].setIsClicked(true);
						imagebuttons[i][j].setClickable(false);
						imagebuttons[i][j].setLongClickable(false);
						if(cells[i][j].getHasMine())//you are dead unless you have an extra life
						{
							if(cells[i][j].type==Type.DESTRUCTIVE_MINE)
								{
								//POP UP A DIALOG SAYING YOU ARE DEAD!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
								//now you can't click any button.turn clickable to false
								dead();
									return ;
								}
							else if(cells[i][j].type==Type.LIFE_SUCKER_MINE)
							{//only if mine is life sucker
								if(player.getNoOfLives()>1)
								{
									player.setNoOfLives(player.getNoOfLives()-1);
									livesCounter.setText(Integer.toString(player.getNoOfLives()));
									noOfMines--;
									noOfCellsOpen++;
									mineCounter.setText(Integer.toString(noOfMines-totalNoOfFlaggedCells));
									imagebuttons[i][j].setImageResource(R.drawable.life_sucker_mine);
									progress.setProgress((100*noOfCellsOpen/(64-noOfMines)));
									return;
								}
								else
								{
									player.setNoOfLives(0);
									livesCounter.setText(Integer.toString(player.getNoOfLives()));
									dead();
								}
							}
							else if(cells[i][j].type==Type.SCORE_DAMAGING_MINE)
							{
								player.setScore(player.getScore()/2);
								scoreCounter.setText(Integer.toString(player.getScore()));
								imagebuttons[i][j].setImageResource(R.drawable.score_damaging_mine);
							}
								
								
							}
						else//i.e. if cell does not have a mine
						{
							//noOfCellsOpen--;
							
							int cellsOpened=openUptheCells(i,j);
							if(cellsOpened==1)
								player.setScore(player.getScore()+10*noOfMines);
							else if(cellsOpened<=5)
								player.setScore(player.getScore()+20*cellsOpened*noOfMines);
							else if(cellsOpened<=10)
								player.setScore(player.getScore()+40*cellsOpened*noOfMines);
							else 
								player.setScore(player.getScore()+80*cellsOpened*noOfMines);
								
							scoreCounter.setText(Integer.toString(player.getScore()));
							
							progress.setProgress((100*noOfCellsOpen/(64-noOfMines)));
							
							if((noOfCellsOpen + noOfMines)==64)
							{
//YOU WON THE GAME !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
								won();
							}
						}
						
					}
					
					private void dead() {
						vibrate.vibrate(1200);
						face.setImageResource(R.drawable.sad);
						if(isSoundActive)
						bombSound.start();
						if(isClockRunning)
						{
							timer.cancel();
							isClockRunning=false;
						}
						for(int i2=0;i2<8;i2++)
							for(int j2=0;j2<8;j2++)
								{
									imagebuttons[i2][j2].setClickable(false);
									imagebuttons[i2][j2].setLongClickable(false);
									if(cells[i2][j2].getHasMine())
									{
										if(cells[i2][j2].type==Type.DESTRUCTIVE_MINE)
											imagebuttons[i2][j2].setImageResource(R.drawable.destructive_mine);
										else if(cells[i2][j2].type==Type.LIFE_SUCKER_MINE)
										{
											imagebuttons[i2][j2].setImageResource(R.drawable.life_sucker_mine);
										}
										else if(cells[i2][j2].type==Type.SCORE_DAMAGING_MINE)
											imagebuttons[i2][j2].setImageResource(R.drawable.score_damaging_mine);
									}
								}
						
										
					}

									//OnClickListeners set.
					
					
					private int openUptheCells(int i, int j){
						int cellsOpened=1;
						imagebuttons[i][j].setClickable(false);
						imagebuttons[i][j].setLongClickable(false);
						cells[i][j].setIsClicked(true);
						noOfCellsOpen++;
						
						if (cells[i][j].type==Type.EXTRA_LIFE)
						{
							imagebuttons[i][j].setImageResource(R.drawable.heart);
							player.setNoOfLives(player.getNoOfLives()+1);
							livesCounter.setText(Integer.toString(player.getNoOfLives()));
							return 1;
						}
						
						int value=cells[i][j].getNoOfAdjMines();
						if(value==0)
						{
							if(cells[i][j].type==Type.PLAIN)
								imagebuttons[i][j].setImageResource(R.drawable.smile);
							int[][] vec={{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1}};
							int pos1,pos2;
							for(int i2=0;i2<=7;i2++)
							{
								pos1=i+vec[i2][0];
								pos2=j+vec[i2][1];
								if(pos1>=0 && pos1<=7 && pos2>=0 && pos2<=7)
								{
									if(imagebuttons[pos1][pos2].isClickable())
									{
										cellsOpened+=openUptheCells(pos1, pos2);
									}
								}
							}
							return cellsOpened;
						}
						else if(value==1)
								{
									if(cells[i][j].type!=Type.EXTRA_LIFE)
									imagebuttons[i][j].setImageResource(R.drawable.image1);
								}
						else if(value==2)
								{
							if(cells[i][j].type!=Type.EXTRA_LIFE)
									imagebuttons[i][j].setImageResource(R.drawable.image2);
								}
						else if(value==3)
								{
							if(cells[i][j].type!=Type.EXTRA_LIFE)
									imagebuttons[i][j].setImageResource(R.drawable.image3);
								}
						else	if(value==4)
							{
							if(cells[i][j].type!=Type.EXTRA_LIFE)
								imagebuttons[i][j].setImageResource(R.drawable.image4);
							}
						else
						{
						if(cells[i][j].type!=Type.EXTRA_LIFE)
							imagebuttons[i][j].setImageResource(R.drawable.image5);
						}
					
						return 1;//only 1 cell was opened
							
					}

				});
        		
        	}
       Log.d("mylog","end");
       
       }

	private void won() {
		player.setHasWon(true);
		if(isClockRunning)
		{
		timer.cancel();
		isClockRunning=false;
		}
		for(int i2=0;i2<8;i2++)
			for(int j2=0;j2<8;j2++)
			{
				imagebuttons[i2][j2].setClickable(false);
				imagebuttons[i2][j2].setLongClickable(false);
				if(cells[i2][j2].getHasMine() && !cells[i2][j2].getIsClicked())
					imagebuttons[i2][j2].setImageResource(R.drawable.flag);

				}
		face.setImageResource(R.drawable.won_tease);
		
		input = new EditText(context); 
		alert.setTitle("You WON!!");
    	alert.setMessage("Enter name");
    	alert.setView(input);
    	alert.show();
 /*
    	if(db.isOpen())
    		db.close();
 
    	Log.d("Databaseeeee","closed database");
    	db=openOrCreateDatabase("ScoresDatabase", MODE_PRIVATE,null);
    	Log.d("Databaseeeee","database opened");
    	
    	Log.d("Databaseeeee","values inserted");
*/
	}



	private void start() {
		Log.d("mytag","in Start()");
		player.start();
		if(isSoundActive)
		{
			if(bombSound.isPlaying())
			{
				bombSound.pause();
			}	
			bombSound.seekTo(0);
		}
		Log.d("mytag","sound check done");

		timeElapsed=0;
		if(isClockRunning)
		timer.cancel();
		isClockRunning=true;
		timer=new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {         
	        @Override
	        public void run() {
	            runOnUiThread(new Runnable()
	            {
	                public void run()
	                {
	                    timeCounter.setText(Integer.toString(timeElapsed));
	                    timeElapsed++;                
	                }
	            });
	        }
	    }, 1000, 1000);
		Log.d("mytag","Clock timer Scheduler set");


		livesCounter.setText(Integer.toString(player.getNoOfLives()));
		scoreCounter.setText(Integer.toString(player.getScore()));
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
				{
					cells[i][j].initialise();
					imagebuttons[i][j].setImageResource(R.drawable.plain_green);
					imagebuttons[i][j].setClickable(true);
					imagebuttons[i][j].setLongClickable(true);
				}

		face.setImageResource(R.drawable.fine);
		noOfCellsOpen=0;
        setMines();
        noOfProperlyFlaggedCells=0;
        totalNoOfFlaggedCells=0;
        setBonusLives();
		progress.setProgress(0);

		Log.d("mytag","exiting start()");
	
	}

	private void setBonusLives() {
		int extraLives=random.nextInt(3); //atmost 2 extra lives
	        for(int k=1;k<=extraLives;k++)
	       {
	           int i2,j2;
	    	   i2=random.nextInt(8);
	    	   j2=random.nextInt(8);
	    	   
	    	   if(cells[i2][j2].getHasMine())
	    	   { 
	    		   k--;
	    		   continue;
	    	   }
	    	   //else
	    	   cells[i2][j2].setType(Type.EXTRA_LIFE);
	       }
	       
	}

	private void setMines() {
		
		Log.d("mytag","setting noofmines");

		int diff=7;
		if(isHardDifficulty)
			diff*=2;
		noOfMines=diff+random.nextInt(diff);
       
		Log.d("mytag","noOfmines set");

		mineCounter.setText(Integer.toString(noOfMines-totalNoOfFlaggedCells));    
        for(int k=0;k<noOfMines;k++)
       {
           int i2,j2;
    	   i2=random.nextInt(8);
    	   j2=random.nextInt(8);
    	   
    	   if(cells[i2][j2].getHasMine() || cells[i2][j2].getNoOfAdjMines()>=4)
    	   { 
    		   Log.d("mines","true .................");
    		   k--;
    		   continue;
    	   }
    	   //else
    	   Log.d("mylog","false "+cells[i2][j2].getNoOfAdjMines());
     	   cells[i2][j2].setHasMine(true);
     	   int typeOfMine=random.nextInt(3);
     	   if(typeOfMine==0)
     		   cells[i2][j2].type=Type.DESTRUCTIVE_MINE;
     	   else if(typeOfMine==1)
     		   cells[i2][j2].type=Type.LIFE_SUCKER_MINE;
     	   else
     		   cells[i2][j2].type=Type.SCORE_DAMAGING_MINE;
    	   incrementAdjCells(i2,j2);
    	   
    	   //comment the one line below
    	 //  imagebuttons[i2][j2].setImageResource(R.drawable.mine);
           Log.d("mylog","image set");
    	   
       }//for ends.. all mines set! Ready to Blow  :D
        for(int i2=0;i2<8;i2++)
        	for(int j2=0;j2<8;j2++)
        		cells[i2][j2].setNoOfAdjMines(getAdjMinesCount(i2,j2));
		
	}

	private int getAdjMinesCount(int i, int j) {
		int count=0;
		int[][] vec={{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1}};
		int i2,pos1,pos2;
		for(i2=0;i2<=7;i2++)
		{
			pos1=i+vec[i2][0];
			pos2=j+vec[i2][1];
			if(pos1>=0 && pos1<=7 && pos2>=0 && pos2<=7 && cells[pos1][pos2].getHasMine())
			{
				count++;
			}
		}
		return count;
	}
	private void incrementAdjCells(int i, int j) {
		Log.d("mylog","inside incrementAdjCells");
 	   
		int[][] vec={{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1}};
		int i2,pos1,pos2;
		for(i2=0;i2<=7;i2++)
		{
			pos1=i+vec[i2][0];
			pos2=j+vec[i2][1];
			if(pos1>=0 && pos1<=7 && pos2>=0 && pos2<=7)
			{
				cells[pos1][pos2].setNoOfAdjMines(cells[pos1][pos2].getNoOfAdjMines()+1);
				Log.d("mines","incremented no of adj cells"+pos1+pos2);
			}
		}
	}
	
}//class MainActivity ends
