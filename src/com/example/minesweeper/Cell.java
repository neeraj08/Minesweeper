package com.example.minesweeper;

public class Cell 
{
	private boolean hasMine;
	private boolean hasFlag;
	private int noOfAdjMines;
	private boolean isClicked;
	enum Type {PLAIN,EXTRA_LIFE,DESTRUCTIVE_MINE,LIFE_SUCKER_MINE,SCORE_DAMAGING_MINE};
	Type type;
	public Cell() {
		initialise();
		}
	void initialise()
	{
		hasMine=false;
		hasFlag=false;
		isClicked=false;
		noOfAdjMines=0;
		type=Type.PLAIN;
	}
	public void setHasMine(boolean state)
	{
		hasMine=state;
	}
	public boolean getHasMine()
	{
		return hasMine;
	}
	public void setIsClicked(boolean state)
	{
		isClicked=state;
	}
	public boolean getIsClicked()
	{
		return isClicked;
	}

	public void setHasFlag(boolean state)
	{
		hasFlag=state;
	}
	public boolean getHasFlag()
	{
		return hasFlag;
	}
	public void setNoOfAdjMines(int x)
	{
		noOfAdjMines=x;
	}
	public int getNoOfAdjMines()
	{
		return noOfAdjMines;
	}
	public void setType(Type type2)
	{
		type=type2;
	}
	public Type getType()
	{
		return type;
	}



}
