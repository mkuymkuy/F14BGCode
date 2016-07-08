package com.f14.bg.listener;

import java.util.ArrayList;
import java.util.List;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.GameMode;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.PlayerState;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 按顺序逐个执行命令的监听器
 * 
 * @author F14eagle
 *
 */
public abstract class OrderActionListener<GM extends GameMode, P extends Player> extends ActionListener<GM> {
	protected List<P> playerOrder = new ArrayList<P>();
	protected P listeningPlayer;

	public OrderActionListener() {
		super();
	}

	public OrderActionListener(ListenerType listenerType) {
		super(listenerType);
	}
	
	/**
	 * 取得当前正在监听的玩家
	 * 
	 * @return
	 */
	public P getListeningPlayer(){
		return this.listeningPlayer;
	}

	/**
	 * 向当前玩家发送监听指令
	 * 
	 * @param gameMode
	 */
	public synchronized void sendListenerCommand(GM gameMode){
		//将所有需要返回输入的玩家的回应状态设为false,不需要返回的设为true
		for(Player p : gameMode.getGame().getValidPlayers()){
			boolean valid = this.isActionPositionValid(p.getPosition());
			this.setPlayerResponsed(p.getPosition(), !valid);
		}
		//取得玩家执行序列
		this.playerOrder = this.getPlayersByOrder(gameMode);
		//向序列中的下一个玩家发送监听指令
		try {
			this.sendNextListenerCommand(gameMode);
		} catch (BoardGameException e) {
			log.error(e, e);
		}
	}
	
	@Override
	protected void doAction(GM gameMode, BgAction action)
			throws BoardGameException {
		Player player = action.getPlayer();
		if(player!=listeningPlayer){
			throw new BoardGameException("你还不能执行该行动!");
		}
	}
	
	@Override
	public void setPlayerResponsed(GM gameMode, int position) {
		super.setPlayerResponsed(gameMode, position);
		//给下一位玩家发送监听消息
		try {
			this.sendNextListenerCommand(gameMode);
		} catch (BoardGameException e) {
			log.error(e, e);
		}
	}
	
	/**
	 * 设置玩家暂时完成输入并由下一位玩家开始输入
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException
	 */
	protected void setPlayerResponsedTemp(GM gameMode, P player) throws BoardGameException{
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_PLAYER_RESPONSED, player.position);
		this.setListenerInfo(res);
		gameMode.getGame().sendResponse(res);
		this.setPlayerState(gameMode, player, PlayerState.NONE);
		//gameMode.getGame().setPlayerState(player.position, PlayerState.NONE);
		this.sendNextListenerCommand(gameMode);
	}
	
	/**
	 * 为等待序列中的下一个玩家发送开始监听的指令
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	protected synchronized void sendNextListenerCommand(GM gameMode) throws BoardGameException{
		listeningPlayer = null;
		while(listeningPlayer==null && !isAllPlayerResponsed()){
			if(this.playerOrder.isEmpty()){
				this.playerOrder = this.getPlayersByOrder(gameMode);
			}
			while(!this.playerOrder.isEmpty()){
				P player = this.playerOrder.remove(0);
				if(this.isActionPositionValid(player.position) && !this.isPlayerResponsed(player.position)){
					//如果需要该玩家回应,则发送开始监听的指令给该玩家
					if(this.beforeListeningCheck(gameMode, player)){
						listeningPlayer = player;
						break;
					}else{
						//如果不需要回应,则设置为已回应
						this.setPlayerResponsed(player.position);
						this.setPlayerState(gameMode, player, PlayerState.RESPONSED);
						try {
							this.onPlayerResponsed(gameMode, player);
						} catch (BoardGameException e) {
							log.error(e, e);
						}
					}
				}
			}
		}
		if(listeningPlayer!=null){
			this.sendStartListenCommand(gameMode, listeningPlayer, null);
			this.onPlayerStartListen(gameMode, listeningPlayer);
//			BgResponse res = this.createStartListenCommand(gameMode, listeningPlayer);
//			this.sendResponse(gameMode, res);
			this.setPlayerState(gameMode, listeningPlayer, PlayerState.INPUTING);
			//gameMode.getGame().setPlayerState(listeningPlayer.position, PlayerState.INPUTING);
			this.onPlayerTurn(gameMode, listeningPlayer);
		}
	}
	
	/**
	 * 取得玩家的行动序列
	 * 
	 * @param <P>
	 * @param gameMode
	 * @return
	 */
	protected abstract List<P> getPlayersByOrder(GM gameMode);
	
	/**
	 * 玩家回合开始时的行动
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException
	 */
	protected void onPlayerTurn(GM gameMode, P player) throws BoardGameException{
		
	}
	
	/**
	 * 发送重新连接时发送给玩家的指令
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	@Override
	public void sendReconnectResponse(GM gameMode, Player player) throws BoardGameException{
		//重新连接时,先向玩家发送回合开始的指令
		gameMode.getGame().sendResponse(player, this.createPhaseStartCommand(gameMode));
		//向玩家发送监听器的一些额外信息
		this.sendPlayerListeningInfo(gameMode, player);
		//发送所有玩家的监听状态
		this.sendAllPlayersState(gameMode, player);
		//如果玩家是当前监听玩家,并且不是旁观状态,则可能需要向其发送监听指令
		if(this.listeningPlayer==player && gameMode.getGame().isPlayingGame(player)){
			//需要回应的话则发送监听指令
			boolean responsed = this.isPlayerResponsed(player.position);
			if(!responsed){
				this.sendStartListenCommand(gameMode, player, player);
				this.onPlayerStartListen(gameMode, listeningPlayer);
				this.onReconnect(gameMode, player);
			}
		}
		
		//检查是否有中断监听器,如果有则发送监听指令
		ActionListener<GM> al = this.getCurrentInterruptListener();
		if(al!=null){
			al.sendReconnectResponse(gameMode, player);
		}
	}
	
	/**
	 * 取得指定玩家的下一个还未完成监的听玩家
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	public P getNextAvailablePlayer(GM gameMode, P player){
		List<P> players = this.getPlayersByOrder(gameMode);
		int count = players.size();
		for(int i=0;i<count;i++){
			if(players.get(i)==player){
				//从指定玩家开始找下一个待监听的玩家
				for(int j=1;j<=count;j++){
					int index = (i+j)%count;
					P p = players.get(index);
					if(this.isActionPositionValid(p.position) && !this.isPlayerResponsed(p.position)){
						return p;
					}
				}
				break;
			}
		}
		return null;
	}
	
	/**
	 * 发送当前监听器中,所有玩家的监听状态
	 * 
	 * @param gameMode
	 * @throws BoardGameException
	 */
	public void sendCurrentPlayerListeningResponse(GM gameMode) throws BoardGameException{
		//重新连接时,先向玩家发送回合开始的指令
		gameMode.getGame().sendResponse(null, this.createPhaseStartCommand(gameMode));
		//向玩家发送监听器的一些额外信息
		this.sendPlayerListeningInfo(gameMode, null);
		//发送所有玩家的监听状态
		this.sendAllPlayersState(gameMode, null);
		
		//当前监听玩家,则可能需要向其发送监听指令
		if(listeningPlayer!=null){
			boolean responsed = this.isPlayerResponsed(listeningPlayer.position);
			if(!responsed){
				this.sendStartListenCommand(gameMode, listeningPlayer, listeningPlayer);
				this.onPlayerStartListen(gameMode, listeningPlayer);
				this.onReconnect(gameMode, listeningPlayer);
			}
		}
	}
	
}
