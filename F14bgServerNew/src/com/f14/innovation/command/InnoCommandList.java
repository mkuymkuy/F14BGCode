package com.f14.innovation.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.bg.common.ParamSet;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.ActionListener;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.listener.InnoInterruptListener;
import com.f14.innovation.param.InnoCommandParam;

public class InnoCommandList extends ArrayList<InnoCommand> {
	/**
	 * 玩家是否使用过能力的标志
	 */
	private static final String PLAYER_ACTIVED = "PLAYER_ACTIVED";
	/**
	 * 玩家是否被要求过使用能力的标志
	 */
	private static final String PLAYER_DEMANDED = "PLAYER_DEMANDED";
	
	private static final long serialVersionUID = 1L;
	
	protected Map<InnoPlayer, ParamSet> playerParam = new HashMap<InnoPlayer, ParamSet>();
	
	protected InnoCommandParam commandParam = new InnoCommandParam();
	/**
	 * 回合监听器
	 */
	protected ActionListener<InnoGameMode> roundListener;
	/**
	 * 是否摸过奖励的牌
	 */
	public boolean isAddCardDrawn = false;
	/**
	 * 是否执行过没人被要求时的效果
	 */
	public boolean isNoDogmaActived = false;
	/**
	 * 主要玩家,触发效果的玩家
	 */
	protected InnoPlayer mainPlayer;
	/**
	 * 当前执行的玩家
	 */
	protected InnoPlayer currentPlayer;
	/**
	 * 触发的卡牌
	 */
	protected InnoCard mainCard;
	protected InnoGameMode gameMode;
	protected List<InnoPlayer> sharedPlayers = new ArrayList<InnoPlayer>();
	protected List<InnoPlayer> demandPlayers = new ArrayList<InnoPlayer>();
	
	public InnoCommandList(InnoGameMode gameMode, InnoPlayer mainPlayer, ActionListener<InnoGameMode> roundListener){
		this.gameMode = gameMode;
		this.roundListener = roundListener;
		this.mainPlayer = mainPlayer;
	}

	/**
	 * 取得回合监听器
	 * 
	 * @return
	 */
	public ActionListener<InnoGameMode> getRoundListener() {
		return roundListener;
	}

	/**
	 * 取得主要玩家
	 * 
	 * @return
	 */
	public InnoPlayer getMainPlayer() {
		return mainPlayer;
	}

	/**
	 * 取得当前执行的玩家
	 * 
	 * @return
	 */
	public InnoPlayer getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(InnoPlayer currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	/**
	 * 取得主要卡牌
	 * 
	 * @return
	 */
	public InnoCard getMainCard() {
		return mainCard;
	}

	/**
	 * 设置主要卡牌
	 * 
	 * @param mainCard
	 */
	public void setMainCard(InnoCard mainCard) {
		this.mainCard = mainCard;
	}

	public List<InnoPlayer> getSharedPlayers() {
		return sharedPlayers;
	}
	
	/**
	 * 判断玩家是否在可分享的玩家列表中
	 * 
	 * @param player
	 * @return
	 */
	public boolean isSharedPlayer(InnoPlayer player){
		return this.getSharedPlayers().contains(player);
	}

	public List<InnoPlayer> getDemandPlayers() {
		return demandPlayers;
	}

	/**
	 * 返回第一个对象
	 * 
	 * @return
	 */
	public InnoCommand push(){
		if(this.isEmpty()){
			return null;
		}else{
			return this.remove(0);
		}
	}
	
	/**
	 * 重置命令列表
	 */
	public void reset(){
		this.clear();
		this.playerParam.clear();
		//this.commandParam.reset();
		this.mainCard = null;
		this.isAddCardDrawn = false;
		this.isNoDogmaActived = false;
		this.sharedPlayers.clear();
		this.demandPlayers.clear();
	}
	
	/**
	 * 取得玩家的参数集
	 * 
	 * @param player
	 * @return
	 */
	public ParamSet getPlayerParamSet(InnoPlayer player){
		ParamSet param = this.playerParam.get(player);
		if(param==null){
			param = new ParamSet();
			this.playerParam.put(player, param);
		}
		return param;
	}
	
	/**
	 * 设置玩家激活过能力
	 * 
	 * @param player
	 */
	public void setPlayerActived(InnoPlayer player){
		this.getPlayerParamSet(player).set(PLAYER_ACTIVED, true);
	}
	
	/**
	 * 设置玩家被要求过执行能力
	 * 
	 * @param player
	 */
	public void setPlayerDomanded(InnoPlayer player){
		this.getPlayerParamSet(player).set(PLAYER_DEMANDED, true);
	}
	
	/**
	 * 检查玩家是否激活过能力
	 * 
	 * @param player
	 * @return
	 */
	public boolean isPlayerActived(InnoPlayer player){
		Boolean res = this.getPlayerParamSet(player).getBoolean(PLAYER_ACTIVED);
		if(res==null){
			return false;
		}else{
			return res;
		}
	}
	
	/**
	 * 判断是否有指定玩家以外的玩家,激活过能力
	 * 
	 * @param player
	 * @return
	 */
	public boolean isOtherPlayerActived(InnoPlayer player){
		//只要有任意敌对玩家激活过,就返回true
		for(InnoPlayer p : this.playerParam.keySet()){
			if(p!=player && gameMode.isEnemy(p, player)){
				boolean actived = this.isPlayerActived(p);
				if(actived){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 检查玩家是否被要求执行过能力
	 * 
	 * @param player
	 * @return
	 */
	public boolean isPlayerDemanded(InnoPlayer player){
		Boolean res = this.getPlayerParamSet(player).getBoolean(PLAYER_DEMANDED);
		if(res==null){
			return false;
		}else{
			return res;
		}
	}
	
	/**
	 * 判断是否有指定玩家以外的玩家,被要求执行过能力
	 * 
	 * @param player
	 * @return
	 */
	public boolean isOtherPlayerDemanded(InnoPlayer player){
		//只要有任意玩家激活过,就返回true
		for(InnoPlayer p : this.playerParam.keySet()){
			if(p!=player){
				boolean actived = this.isPlayerDemanded(p);
				if(actived){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 重置指令参数
	 */
	public void resetCommandParam(){
		this.commandParam.reset();
		this.currentPlayer = null;
	}

	public InnoCommandParam getCommandParam() {
		return commandParam;
	}
	
	/**
	 * 插入监听器
	 * 
	 * @param al
	 * @param gameMode
	 * @throws BoardGameException
	 */
	public void insertInterrupteListener(InnoInterruptListener al, InnoGameMode gameMode) throws BoardGameException{
		al.setCommandList(this);
		this.getRoundListener().insertInterrupteListener(al, gameMode);
	}
	
	/**
	 * 设置dogma效果列表
	 */
	public void setDogmaCommandList(){
		InnoCard card = this.getMainCard();
		InnoPlayer player = this.getMainPlayer();
		
		InnoIcon mainIcon = card.getMainIcon();
		int playerIcon = player.getIconCount(mainIcon);
		//设置所有玩家所在的行动列表
		for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
			if(p==player){
				continue;
			}
			int icons = p.getIconCount(mainIcon);
			if(icons>=playerIcon){
				this.sharedPlayers.add(p);
			}else if(gameMode.isEnemy(player, p)){
				this.demandPlayers.add(p);
			}
		}
		//按顺序结算每一条效果
		for(InnoAbilityGroup group : card.getAbilityGroups()){
			//取得下一顺位的玩家,能力从该玩家开始结算
			InnoPlayer currPlayer = gameMode.getGame().getNextPlayersByOrder(player);
			do{
				switch(group.activeType){
				case NORMAL:{	//普通类型的能力
					//普通能力,只需要符号数量大于等于触发玩家的符号数量,就可以蹭
					if(this.sharedPlayers.contains(currPlayer)){
						InnoCommand cmd = new InnoCommand(currPlayer, player, group, card);
						this.add(cmd);
					}
				}break;
				case DEMAND:{	//要求类型的能力
					//要求能力,当符号数量小于触发玩家的符号数量时,需要执行
					//并且只会对敌对玩家生效
					if(this.demandPlayers.contains(currPlayer)){
						InnoCommand cmd = new InnoCommand(currPlayer, player, group, card);
						this.add(cmd);
					}
				}break;
				}
				currPlayer = gameMode.getGame().getNextPlayersByOrder(currPlayer);
			}while(currPlayer!=player);
			
			switch(group.activeType){
			case NORMAL:{	//普通类型的能力
				//触发玩家只处理普通能力
				InnoCommand cmd = new InnoCommand(player, player, group, card);
				this.add(cmd);
			}break;
			}
		}
	}
	
}
