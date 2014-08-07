package com.bloc.tetrisblocs.block;

public class LBlock extends Block {

	public LBlock(int x, int y) {
		super(x, y);
	}
	
	@Override
	public int getHorizontalSize() {
		return 2;
	}
	
	@Override
	public int getVerticalSize() {
		return 3;
	}
	
	@Override
	public boolean[][] getBlockGrid() {
		return new boolean[][] { 
			{ true, true },
			{ true, false },
			{ true, false }
		};
	}
}
