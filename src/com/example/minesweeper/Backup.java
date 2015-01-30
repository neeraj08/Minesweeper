package com.example.minesweeper;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;
import android.view.View.OnLongClickListener;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.example.minesweeper.Cell.Type;

public class Backup extends Activity {
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
    	
        cells=new Cell[8][];
        for(int i=0;i<8;i++)
        	cells[i]=new Cell[8];
        for(int i=0;i<8;i++)
        	for(int j=0;j<8;j++)
        		cells[i][j]=new Cell();

        for(int i=0;i<=7;i++)
        	for(int j=1;j<=8;j++)
        	{
       			imagebuttons[i][j-1]=(ImageButton) findViewById(0x7f070000+(i*8+j));	//R.id.button_xx are stored in R.java as hexadecimal and findviewbyid() needs an int
        	}
        
        Log.d("mylog","All linking and memory allocation complete");
        
       
        start();//initialize all variables, image views  and text views 
        
        //set OnClickListeners...
        face.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				start();
			}
		});
        
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
							if(cells[i][j].getHasMine())
								noOfProperlyFlaggedCells--;
							
							if(totalNoOfFlaggedCells==noOfMines && totalNoOfFlaggedCells==noOfProperlyFlaggedCells)
								won();
							
						}
						return false;
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
									mineCounter.setText(Integer.toString(noOfMines));
									imagebuttons[i][j].setImageResource(R.drawable.life_sucker_mine);
									return;
								}
								else
									dead();
							}
							else if(cells[i][j].type==Type.SCORE_DAMAGING_MINE)
							{
								player.setScore(player.getScore()/2);
								scoreCounter.setText(Integer.toString(player.getScore()));
								imagebuttons[i][j].setImageResource(R.drawable.score_damaging_mine);
							}
								
								
							}
						else
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
								player.setScore(player.getScore()+100*cellsOpened*noOfMines);
								
							scoreCounter.setText(Integer.toString(player.getScore()));
							
							
							if((noOfCellsOpen + noOfMines)==64)
							{
//YOU WON THE GAME !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
								won();
							}
						}
						
					}
					
					private void dead() {
						face.setImageResource(R.drawable.sad);
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
						else
							{
							if(cells[i][j].type!=Type.EXTRA_LIFE)
								imagebuttons[i][j].setImageResource(R.drawable.image4);
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
				if(cells[i2][j2].getHasMine())
					imagebuttons[i2][j2].setImageResource(R.drawable.flag);

				}
		face.setImageResource(R.drawable.won_tease);
	
	}



	private void start() {
		
		player.start();
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
		noOfMines=7+random.nextInt(8);
        mineCounter.setText(Integer.toString(noOfMines));    
        for(int k=0;k<noOfMines;k++)
       {
           int i2,j2;
    	   i2=random.nextInt(8);
    	   j2=random.nextInt(8);
    	   
    	   if(cells[i2][j2].getHasMine() || cells[i2][j2].getNoOfAdjMines()>=3)
    	   { 
    		   k--;
    		   continue;
    	   }
    	   //else
    	   Log.d("mylog","if false");
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
				cells[pos1][pos2].setNoOfAdjMines(cells[pos1][pos2].getNoOfAdjMines()+1);;
			}
		}
	}
	
}//class MainActivity ends
