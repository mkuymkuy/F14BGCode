package com.f14.F14bg.network;

import com.f14.F14bg.consts.GameType;

public class ClientInfo {
	public String version;
	public GameType gameType;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public GameType getGameType() {
		return gameType;
	}
	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}
}
