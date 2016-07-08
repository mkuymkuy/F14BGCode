package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.consts.EclipseGameCmd;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.listener.ListenerType;
import com.f14.bg.player.Player;

public class EclipseRoundListener extends EclipseOrderListener {

	public EclipseRoundListener(EclipsePlayer startPlayer) {
		super(startPlayer, ListenerType.NORMAL);
	}

	@Override
	protected int getValidCode() {
		return EclipseGameCmd.GAME_CODE_ROUND;
	}
	
	@Override
	protected BgResponse createStartListenCommand(EclipseGameMode gameMode,
			Player player) {
		EclipsePlayer p = (EclipsePlayer) player;
		BgResponse res = super.createStartListenCommand(gameMode, player);
		res.setPublicParameter("passed", p.passed);
		res.setPublicParameter("msg", "请选择行动:");
		return res;
	}
	
	@Override
	protected void doAction(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		EclipsePlayer player = action.getPlayer();
		String subact = action.getAsString("subact");
		if("EXPLORE".equals(subact)){
			this.doExplore(gameMode, action);
		}else if("INFLUENCE".equals(subact)){
			this.doInfluence(gameMode, action);
		}else if("BUILD".equals(subact)){
			this.doBuild(gameMode, action);
		}else if("MOVE".equals(subact)){
			this.doMove(gameMode, action);
		}else if("RESEARCH".equals(subact)){
			this.doResearch(gameMode, action);
		}else if("UPGRADE".equals(subact)){
			this.doUpgrade(gameMode, action);
		}else if("PASS".equals(subact)){
			this.doPass(gameMode, action);
		}else if("DO_COLONY".equals(subact)){
			//殖民
			EclipseColonyListener l = new EclipseColonyListener(player);
			this.insertInterrupteListener(l, gameMode);
		}else if("DO_TRADE".equals(subact)){
			//交易
			EclipseTradeListener l = new EclipseTradeListener(player);
			this.insertInterrupteListener(l, gameMode);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 选择探索行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException 
	 */
	protected void doExplore(EclipseGameMode gameMode, BgAction action) throws BoardGameException{
		EclipsePlayer player = action.getPlayer();
		//插入探索监听器
		EclipseExploreListener l = new EclipseExploreListener(player);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 选择扩张行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException 
	 */
	protected void doInfluence(EclipseGameMode gameMode, BgAction action) throws BoardGameException{
		EclipsePlayer player = action.getPlayer();
		//插入扩张监听器
		EclipseInfluenceListener l = new EclipseInfluenceListener(player);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 选择建造行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException 
	 */
	protected void doBuild(EclipseGameMode gameMode, BgAction action) throws BoardGameException{
		EclipsePlayer player = action.getPlayer();
		//插入建造监听器
		EclipseBuildListener l = new EclipseBuildListener(player);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 选择移动行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException 
	 */
	protected void doMove(EclipseGameMode gameMode, BgAction action) throws BoardGameException{
		EclipsePlayer player = action.getPlayer();
		//插入移动监听器
		EclipseMoveListener l = new EclipseMoveListener(player);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 选择科研行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException 
	 */
	protected void doResearch(EclipseGameMode gameMode, BgAction action) throws BoardGameException{
		EclipsePlayer player = action.getPlayer();
		//插入科研监听器
		EclipseResearchListener l = new EclipseResearchListener(player);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 选择升级行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException 
	 */
	protected void doUpgrade(EclipseGameMode gameMode, BgAction action) throws BoardGameException{
		EclipsePlayer player = action.getPlayer();
		//插入升级监听器
		EclipseUpgradeListener l = new EclipseUpgradeListener(player);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 选择跳过行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException 
	 */
	protected void doPass(EclipseGameMode gameMode, BgAction action) throws BoardGameException{
		EclipsePlayer player = action.getPlayer();
		if(this.isFirstPlayerPassed(gameMode)){
			//第一个pass的玩家为下回合的起始玩家
			gameMode.changeStartPlayer(player);
		}
		player.passed = true;
		//刷新玩家的状态信息
		gameMode.getGame().sendPlayerStatusInfo(player, null);
		
		this.setPlayerResponsedTemp(gameMode, player);
		this.checkRoundOver(gameMode);
	}
	
	/**
	 * 检测是否是第一个选择pass的玩家
	 * 
	 * @param gameMode
	 * @return
	 */
	protected boolean isFirstPlayerPassed(EclipseGameMode gameMode){
		for(EclipsePlayer player : gameMode.getGame().getValidPlayers()){
			if(player.passed){
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onInterrupteListenerOver(EclipseGameMode gameMode,
			InterruptParam param) throws BoardGameException {
		super.onInterrupteListenerOver(gameMode, param);
		EclipsePlayer player = param.get("player");
		
		if(param.get("actived")!=null){
			boolean actived = param.getBoolean("actived");
			if(actived){
				//如果效果激活过,则轮到下一个玩家行动
				this.setPlayerResponsedTemp(gameMode, player);
			}
		}else{
			//如果没有该参数,则进行的是自由行动,当前玩家可以继续行动
		}
	}
	
	/**
	 * 检查是否回合结束
	 * 
	 * @param gameMode
	 */
	protected void checkRoundOver(EclipseGameMode gameMode){
		//如果所有的玩家都选择了pass,则结束回合
		if(this.isAllPlayerPassed(gameMode)){
			this.setAllPlayerResponsed(gameMode);
		}
	}
	
	/**
	 * 检查是否所有的玩家都选择了pass
	 * 
	 * @param gameMode
	 * @return
	 */
	protected boolean isAllPlayerPassed(EclipseGameMode gameMode){
		for(EclipsePlayer p : gameMode.getGame().getValidPlayers()){
			if(!p.passed){
				return false;
			}
		}
		return true;
	}
}
