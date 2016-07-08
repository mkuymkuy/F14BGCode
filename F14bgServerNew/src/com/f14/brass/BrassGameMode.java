package com.f14.brass;

import com.f14.bg.BoardGame;
import com.f14.bg.GameMode;
import com.f14.bg.exception.BoardGameException;

public class BrassGameMode extends GameMode {
	
	public BrassGameMode(BoardGame<?, ?> boardgame) {
		super(boardgame);
	}

	@Override
	protected boolean isGameOver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void round() throws BoardGameException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setupGame() throws BoardGameException {
		// TODO Auto-generated method stub

	}

}
