package com.f14.innovation.listener;

import com.f14.F14bg.network.CmdFactory;
import com.f14.F14bg.utils.SystemUtil;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.ConditionResult;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.listener.ListenerType;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.command.InnoCommand;
import com.f14.innovation.command.InnoCommandList;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoConsts;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.utils.InnoUtils;

/**
 * Innovation的回合监听器
 * 
 * @author F14eagle
 *
 */
public class InnoRoundListener extends InnoOrderListener {

	public InnoRoundListener(InnoPlayer startPlayer) {
		super(startPlayer, ListenerType.NORMAL);
	}

	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_ROUND;
	}
	
	@Override
	protected void beforeStartListen(InnoGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//为所有玩家创建回合参数
		for(Player player : gameMode.getGame().getValidPlayers()){
			InnoPlayer p = (InnoPlayer)player;
			RoundParam param = new RoundParam(gameMode, p);
			//如果是起始玩家,则只有一个行动
			if(p.firstAction){
				param.ap = 1;
				p.firstAction = false;
			}
			this.setParam(player, param);
		}
	}
	
	@Override
	protected void onPlayerTurn(InnoGameMode gameMode, InnoPlayer player)
			throws BoardGameException {
		super.onPlayerTurn(gameMode, player);
		//玩家回合开始时,需要清空所有玩家的回合垫底/计分牌数
		for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
			p.clearRoundCount();
		}
	}
	
	@Override
	protected void sendStartListenCommand(InnoGameMode gameMode, Player player,
			Player receiver) {
		super.sendStartListenCommand(gameMode, player, receiver);
		this.sendRefreshApResponse(gameMode, (InnoPlayer)player);
	}
	
	@Override
	protected BgResponse createStartListenCommand(InnoGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		RoundParam param = this.getParam(player.position);
		//发送当前的行动点数
		res.setPublicParameter("ap", param.ap);
		return res;
	}
	
	/**
	 * 发送刷新玩家行动点数的消息
	 * 
	 * @param gameMode
	 * @param player
	 */
	protected void sendRefreshApResponse(InnoGameMode gameMode, InnoPlayer player){
		BgResponse res = CmdFactory.createGameResponse(InnoGameCmd.GAME_CODE_REFRESH_AP, player.position);
		RoundParam param = this.getParam(player.position);
		//发送当前的行动点数
		res.setPublicParameter("ap", param.ap);
		gameMode.getGame().sendResponse(res);
	}
	
	@Override
	protected void doAction(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		String subact = action.getAsString("subact");
		if("DRAW_CARD".equals(subact)){
			this.drawCard(gameMode, action);
		}else if("MELD_CARD".equals(subact)){
			this.meldCard(gameMode, action);
		}else if("ACHIEVE".equals(subact)){
			this.drawAchieveCard(gameMode, action);
		}else if("DOGMA".equals(subact)){
			this.dogmaCard(gameMode, action);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	/**
	 * 刷新行动点数
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void refreshAp(InnoGameMode gameMode, InnoPlayer player) throws BoardGameException{
		RoundParam p = this.getParam(player);
		p.ap -= 1;
		this.sendRefreshApResponse(gameMode, player);
		this.onInterrupteListenerOver(gameMode, null);
	}
	
	/**
	 * 摸牌
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void drawCard(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		int level = action.getAsInt("level");
		InnoUtils.checkLevel(level);
		
		boolean isEmpty = gameMode.getDrawDecks().getCardDeck(level).isEmpty();
		if(isEmpty){
			level += 1;
		}
		
		int maxLevel = this.getMaxAvailableLevel(gameMode, player);
		if(level!=maxLevel && level<=InnoConsts.MAX_LEVEL){
			//调试模式下不限制摸牌等级
			if(!SystemUtil.isDebugMode()){
				throw new BoardGameException("你只能摸"+maxLevel+"级的牌!");
			}
		}
		
		gameMode.getGame().playerDrawCard(player, level, 1);
		this.refreshAp(gameMode, player);
	}
	
	/**
	 * 取得玩家可以摸的最高等级的牌
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	protected int getMaxAvailableLevel(InnoGameMode gameMode, InnoPlayer player){
		int maxLevel = player.getMaxLevel();
		while(maxLevel<InnoConsts.MAX_LEVEL && gameMode.getDrawDecks().getCardDeck(maxLevel).isEmpty()){
			maxLevel += 1;
		}
		return maxLevel;
	}
	
	/**
	 * 合并手牌
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void meldCard(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		InnoCard card = player.getHands().getCard(cardId);
		gameMode.getGame().playerMeldHandCard(player, card);
		this.refreshAp(gameMode, player);
	}
	
	/**
	 * 拿成就牌
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void drawAchieveCard(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		InnoCard card = gameMode.getAchieveManager().getAchieveCards().getCard(cardId);
		int maxLevel = player.getMaxLevel();
		if(card.level>maxLevel){
			throw new BoardGameException("你只能拿"+maxLevel+"级的成就牌!");
		}
		if(player.getScore()<card.level*5){
			throw new BoardGameException("你的分数不够拿这张成就牌!");
		}
		gameMode.getGame().playerDrawAchieveCard(player, card);		
		this.refreshAp(gameMode, player);
	}
	
	/**
	 * 取得当前行动玩家对应的命令列表
	 * 
	 * @return
	 */
	protected InnoCommandList getCurrentCommandList(){
		RoundParam p = this.getParam(this.getListeningPlayer());
		return p.commandList;
	}
	
	/**
	 * 使用卡牌的能力
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void dogmaCard(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		String colorString = action.getAsString("color");
		InnoColor color = InnoColor.valueOf(colorString);
		InnoCard card = player.getTopCard(color);
		CheckUtils.checkNull(card, "没有找到该颜色对应的置顶牌!");
		//发送dogma的效果
		gameMode.getGame().playerDogmaCard(player, card);
		//如果存在,则处理该牌的能力
		if(!card.getAbilityGroups().isEmpty()){
			InnoCommandList commandList = this.getCurrentCommandList();
			//清理命令列表以供使用
			commandList.reset();
			commandList.setMainCard(card);
			commandList.setDogmaCommandList();
			//处理命令列表
			this.processCommandList(gameMode, commandList);
		}
		this.refreshAp(gameMode, player);
	}
	
	/**
	 * 处理命令列表
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	public void processCommandList(InnoGameMode gameMode, InnoCommandList commandList) throws BoardGameException{
		InnoCommand cmd = commandList.push();
		while(cmd!=null){
			//处理命令
			gameMode.getGame().processInnoCommand(cmd, commandList);
			if(this.isInterruped()){
				//如果监听器被打断了,则中断命令列表的处理
				break;
			}
			cmd = commandList.push();
		}
	}
	
	@Override
	protected void onInterrupteListenerOver(InnoGameMode gameMode,
			InterruptParam param) throws BoardGameException {
		if(!this.isInterruped()){
			if(!this.isAllPlayerResponsed()){
				//如果所有玩家都已经回应,则不需要再进行处理了
				//如果该监听器不在被打断状态,
				InnoCommandList commandList = this.getCurrentCommandList();
				if(commandList.isEmpty()){
					InnoPlayer player = this.getListeningPlayer();
					RoundParam p = this.getParam(player);
					
					//如果还没有执行过被要求效果后的能力,则需要触发对应的方法
					if(!commandList.isNoDogmaActived){
						//如果有人被要求,就执行TRUE对应的方法,否则执行ELSE
						ConditionResult cr = (commandList.isOtherPlayerDemanded(player))?ConditionResult.TRUE:ConditionResult.ELSE;
						if(commandList.getMainCard()!=null){
							InnoAbilityGroup abilityGroup = commandList.getMainCard().getDogmaResultAbilitiyGroup(cr);
							if(abilityGroup!=null){
								commandList.isNoDogmaActived = true; //该参数在dogma时会被重置
								
								//无论是TRUE或ELSE,都只对可以分享的人执行
								InnoCard card = commandList.getMainCard();
								//取得下一顺位的玩家,能力从该玩家开始结算
								InnoPlayer currPlayer = gameMode.getGame().getNextPlayersByOrder(player);
								do{
									switch(abilityGroup.activeType){
									case NORMAL:{	//普通类型的能力
										//只处理普通能力
										if(commandList.isSharedPlayer(currPlayer)){
											InnoCommand cmd = new InnoCommand(currPlayer, player, abilityGroup, card);
											commandList.add(cmd);
										}
									}break;
									}
									currPlayer = gameMode.getGame().getNextPlayersByOrder(currPlayer);
								}while(currPlayer!=player);	
								//当前触发的玩家总是可以使用能力
								switch(abilityGroup.activeType){
								case NORMAL:{	//普通类型的能力
									//只处理普通能力
									InnoCommand cmd = new InnoCommand(player, player, abilityGroup, card);
									commandList.add(cmd);
								}break;
								}
								
								//处理命令列表
								this.processCommandList(gameMode, commandList);
								//处理完成后检查是否可以结束回合
								this.onInterrupteListenerOver(gameMode, param);
								return;
							}
						}
					}
					//如果有别人蹭过该能力,并且还没摸过牌,则给触发玩家摸个牌
					if(!commandList.isAddCardDrawn && commandList.isOtherPlayerActived(player)){
						commandList.isAddCardDrawn = true;	//该参数在dogma时会被重置
						int level = player.getMaxLevel();
						gameMode.getGame().playerDrawCard(player, level, 1);
					}
					//如果没有待处理的命令,则检查当前行动玩家的AP,如果用完,则设置行动结束
					if(p.ap<=0){
						this.setPlayerResponsed(gameMode, player);
					}else{
						//否则重置命令参数
						commandList.reset();
					}
				}else{
					//如果存在命令,则继续处理命令
					this.processCommandList(gameMode, commandList);
					//处理完成后检查是否可以结束回合
					this.onInterrupteListenerOver(gameMode, param);
				}
			}
		}
	}
	
	/**
	 * 玩家的回合参数
	 * 
	 * @author F14eagle
	 *
	 */
	class RoundParam{
		/**
		 * 默认有2个行动
		 */
		int ap = 2;
		protected InnoPlayer player;
		protected InnoCommandList commandList;
		protected InnoGameMode gameMode;
		
		RoundParam(InnoGameMode gameMode, InnoPlayer player){
			this.player = player;
			this.gameMode = gameMode;
			this.init();
		}
		
		void init(){
			this.commandList = new InnoCommandList(gameMode, player, InnoRoundListener.this);
		}
		
		void checkAp() throws BoardGameException{
			if(ap<=0){
				throw new BoardGameException("行动点已经用完了!");
			}
		}
	}

}
