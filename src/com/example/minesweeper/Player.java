package com.example.minesweeper;

public class Player
{
	int noOfLives;
	int score;
	String name;
	boolean hasWon;
	Player()
	{
		start();
	}
	public void start()
	{
		noOfLives=1;
		score=0;
		name="";
		hasWon=false;
		//initialiseNoOfMines();
	}
	boolean getHasWon()
	{
		return hasWon;
	}
	void setHasWon(boolean state)
	{
		hasWon=state;
	}

	
	int getScore()
	{
		return score;
	}
	void setScore(int x)
	{
		score=x;
	}
	
	int getNoOfLives()
	{
		return noOfLives;
	}
	void setNoOfLives(int x)
	{
		noOfLives=x;
	}
	void setName(String x)
	{
		name=x;
	}
	String getName()
	{
		return name;
	}
/*
	int getNoofMines()
	{
		return noOfMines;
	}
	private void initialiseNoOfMines() {
		Random random=new Random();
		noOfMines=7+random.nextInt(4);
		
	}
	*/
	
}
