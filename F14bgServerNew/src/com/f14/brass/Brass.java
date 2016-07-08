package com.f14.brass;

import net.sf.json.JSONObject;

import com.f14.bg.BoardGame;
import com.f14.bg.consts.BgVersion;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

public class Brass extends BoardGame<BrassPlayer, BrassGameMode> {
	
	@Override
	public BrassReport getReport() {
		return (BrassReport)super.getReport();
	}
	
	@Override
	public BrassConfig getConfig() {
		return (BrassConfig)super.getConfig();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected BrassConfig createConfig(JSONObject object)
			throws BoardGameException {
		BrassConfig config = new BrassConfig();
		config.versions.add(BgVersion.BASE);
//		String versions = object.getString("versions");
//		if(!StringUtils.isEmpty(versions)){
//			String[] vs = versions.split(",");
//			for(String v : vs){
//				config.versions.add(v);
//			}
//		}
		return config;
	}
	
	@Override
	public void initConfig() {
		BrassConfig config = new BrassConfig();
		config.versions.add(BgVersion.BASE);
		this.config = config;
	}

	@Override
	public void initConst() {
		this.players = new BrassPlayer[this.room.getMaxPlayerNumber()];
	}

	@Override
	public void initReport() {
		this.report = new BrassReport(this);
	}
	
	@Override
	protected void setupGame() throws BoardGameException {
		this.config.playerNumber = this.getCurrentPlayerNumber();
		this.gameMode = new BrassGameMode(this);
	}

	@Override
	protected void sendGameInfo(Player receiver) throws BoardGameException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void sendInitInfo(Player receiver) throws BoardGameException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void sendPlayerPlayingInfo(Player receiver)
			throws BoardGameException {
		// TODO Auto-generated method stub
		
	}

}
