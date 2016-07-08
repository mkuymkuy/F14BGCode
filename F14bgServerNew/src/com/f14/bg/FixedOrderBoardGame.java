package com.f14.bg;

import com.f14.bg.player.Player;

/**
 * 固定顺序的桌游类型
 * 该类型的游戏在游戏开始时即决定了玩家的行动顺序,在游戏中将不再改变
 * 
 * @author F14eagle
 *
 * @param <P>
 * @param <A>
 * @param <GM>
 */
public abstract class FixedOrderBoardGame<P extends Player, GM extends GameMode> extends BoardGame<P, GM> {
	/**
	 * 起始玩家
	 */
	protected P startPlayer;
	/**
	 * 当前回合玩家
	 */
	protected P currentPlayer;
	
	/**
	 * 取得起始玩家
	 * 
	 * @return
	 */
	public P getStartPlayer() {
		return startPlayer;
	}
	
	/**
	 * 取得当前玩家
	 * 
	 * @return
	 */
	public P getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * 打乱玩家顺序并决定起始玩家
	 */
	@Override
	public void regroupPlayers() {
		super.regroupPlayers();
		this.startPlayer = this.getPlayer(0);
		this.currentPlayer = this.startPlayer;
	}
	
	/**
	 * 取得指定玩家的下一个玩家
	 * 
	 * @param player
	 * @return
	 */
	public P getNextPlayer(P player){
		int i = this.validPlayers.indexOf(player);
		if(i==-1){
			return null;
		}else{
			if(i==(this.validPlayers.size()-1)){
				i = 0;
			}else{
				i += 1;
			}
			return this.validPlayers.get(i);
		}
	}
	
	/**
	 * 前进到下一玩家
	 * 
	 * @return
	 */
	public P nextPlayer(){
		this.currentPlayer = this.getNextPlayer(this.currentPlayer);
		return this.currentPlayer;
	}
}
