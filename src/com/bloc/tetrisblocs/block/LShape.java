package com.bloc.tetrisblocs.block;

public class LShape {
	public static final String SPRITE1 = "shape_l1.png";
	public static final String SPRITE2 = "shape_l2.png";
	
	public boolean canMoveLeft() {
		return true;
	}
	public boolean canMoveRight() {
		return true;
	}
	public boolean canMoveDown() {
		return true;
	}
	
	public int getBlockHeight() {
		return 3;
	}
	public int getBlockWidth() {
		return 2;
	}
}
