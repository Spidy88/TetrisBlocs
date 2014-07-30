package com.bloc.tetrisblocs.block;

public class Block {
	private int mX = 0;
	private int mY = 0;
	
	public Block(int x, int y) {
		mX = x;
		mY = y;
	}
	
	public int getX() {
		return mX;
	}
	public void setX(int x) {
		mX = x;
	}
	public int getY() {
		return mY;
	}
	public void setY(int y) {
		mY = y;
	}
}
