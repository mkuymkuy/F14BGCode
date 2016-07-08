package com.f14.tichu.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.utils.CheckUtils;
import com.f14.tichu.TichuGameMode;
import com.f14.tichu.TichuPlayer;
import com.f14.tichu.componet.TichuCard;
import com.f14.tichu.componet.TichuCardCheck;
import com.f14.tichu.componet.TichuCardGroup;
import com.f14.tichu.consts.AbilityType;
import com.f14.tichu.consts.Combination;
import com.f14.tichu.consts.TichuGameCmd;
import com.f14.tichu.consts.TichuType;
import com.f14.tichu.utils.CombinationUtil;

public class TichuRoundListener extends TichuOrderListener {
	protected List<TichuPlayer> rankPlayers = new ArrayList<TichuPlayer>();
	protected RoundManager roundManager;
	protected boolean roundEnd = false;
	
	@Override
	protected void onStartListen(TichuGameMode gameMode)
			throws BoardGameException {
		super.onStartListen(gameMode);
		//开始新的回合
		this.roundManager = new RoundManager(gameMode);
		this.roundManager.newRound();
	}
	
	@Override
	protected void onPlayerTurn(TichuGameMode gameMode, TichuPlayer player)
			throws BoardGameException {
		super.onPlayerTurn(gameMode, player);
		if(player.pass){
			//如果需要跳过,则设置玩家直接结束回合
			player.pass = false;
			this.setPlayerResponsedTemp(gameMode, player);
		}
	}

	@Override
	protected int getValidCode() {
		return TichuGameCmd.GAME_CODE_ROUND_PHASE;
	}
	
	@Override
	protected void doAction(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		
		String subact = action.getAsString("subact");
		TichuPlayer player = action.getPlayer();
		if("bomb".equals(subact)){
			//炸弹是可以抢出的,无需在自己的回合
			this.playBomb(gameMode, action);
		}else{
			super.doAction(gameMode, action);
			if("play".equals(subact)){
				//出牌
				this.playCard(gameMode, action);
			}else if("pass".equals(subact)){
				//跳过
				this.pass(gameMode, action);
			}else if("smallTichu".equals(subact)){
				//叫小地主
				gameMode.getGame().playerCallTichu(player, TichuType.SMALL_TICHU);
			}else{
				throw new BoardGameException("无效的行动指令!");
			}
		}
	}
	
	/**
	 * 玩家抢出炸弹
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void playBomb(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		TichuPlayer player = action.getPlayer();
		
		TichuCardCheck check = new TichuCardCheck(player, player.getHands().getCards());
		Collection<TichuCardGroup> bombs = check.getBombs();
		if(bombs.isEmpty()){
			throw new BoardGameException("你没有炸弹!");
		}
		//该方法中检查的是在其他玩家的回合出炸弹的情况,所以这时炸弹不能空炸
		TichuCardGroup lastGroup = this.roundManager.getLastCardGroup();
		if(lastGroup==null || lastGroup.hasCard(AbilityType.DOG)){
			//炸弹不能用来抢牌权
			throw new BoardGameException("不能用炸弹抢出牌权!");
		}else{
			//检查是否拥有比最后一轮大的炸弹,如果有,才能出
			boolean canBomb = false;
			for(TichuCardGroup bomb : bombs){
				if(bomb.compareTo(lastGroup)>0){
					canBomb = true;
					break;
				}
			}
			if(!canBomb){
				throw new BoardGameException("你没有比对方大的炸弹!");
			}
		}
		
		//添加出炸弹的中断监听器
		TichuBombActionListener l = new TichuBombActionListener(player);
		this.insertInterrupteListener(l, gameMode);
	}
	
	/**
	 * 玩家出牌
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void playCard(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		TichuPlayer player = action.getPlayer();
		String cardIds = action.getAsString("cardIds");
		CheckUtils.checkNull(cardIds, "请选择要出的牌!");
		
		List<TichuCard> cards = player.getHands().getCards(cardIds);
		//检查凤的规则
		this.roundManager.checkPhoenix(cards, action);
		
		//整理组合
		TichuCardGroup group = new TichuCardGroup(player, cards);
		this.roundManager.checkPlayCard(group);
		
		//执行出牌
		gameMode.getGame().playerPlayCards(player, group);
		this.roundManager.addCardGroup(group);
		
		//出牌完成后,检查是否有雀/狗的效果
		this.roundManager.checkDog(gameMode, player, cards);
		if(this.roundManager.checkMahJong(cards, action)){
			//如果有雀,则需要添加许愿的监听器
			TichuWishInterruptListener l = new TichuWishInterruptListener(player);
			this.insertInterrupteListener(l, gameMode);
			//完成出牌的逻辑在中断回调函数中完成
		}else{
			//完成出牌
			this.onPlayedCard(gameMode, player);
			//检查出牌是否符合许愿,如果符合则取消许愿
			if(group.hasCard(gameMode.wishedPoint)){
				gameMode.getGame().playerWishPoint(player, 0);
			}
		}
	}
	
	/**
	 * 玩家出牌后的行动
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException
	 */
	protected void onPlayedCard(TichuGameMode gameMode, TichuPlayer player) throws BoardGameException{
		if(player.hasCard()){
			//如果出牌后玩家还有手牌,则暂时完成行动
			this.setPlayerResponsedTemp(gameMode, player);
		}else{
			//如果没有手牌,则设置排名
			this.rankPlayers.add(player);
			int rank = this.rankPlayers.size();
			gameMode.getGame().playerGetRank(player, rank);
			rank += 1;
			if(rank<gameMode.getGame().getCurrentPlayerNumber()){
				if(rank==3){
					//如果这次跑掉的是第2名,则检查第1名是否和这个玩家同一组,如果是则双关
					TichuPlayer rank1 = this.rankPlayers.get(0);
					if(gameMode.isFirendlyPlayer(player, rank1)){
						//双关时玩家的得分都不算了
						for(TichuPlayer p : gameMode.getGame().getValidPlayers()){
							p.score = 0;
						}
						//直接设置回合结束
						this.setAllPlayerResponsed(gameMode);
						return;
					}
				}
				//如果不是剩余的名次不是最后1名,则设置玩家完成行动
				this.setPlayerResponsed(gameMode, player);
			}else{
				//如果是最后一名,先结算当前轮的分数
				boolean responsed = this.checkScore(gameMode);
				if(responsed){
					//则当前回合结束,设置所有玩家结束行动
					TichuPlayer nextPlayer = this.getNextAvailablePlayer(gameMode, player);
					gameMode.getGame().playerGetRank(nextPlayer, rank);
					this.setAllPlayerResponsed(gameMode);
				}else{
					//如果创建了监听器,则设置回合结束的标志
					roundEnd = true;
				}
			}
		}
	}
	
	/**
	 * 玩家跳过
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void pass(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		TichuPlayer player = action.getPlayer();
		this.roundManager.checkPass(player);
		gameMode.getGame().playerPass(player);
		
		boolean setResponsed = true;
		if(this.isLastPassedPlayer(gameMode, player)){
			//如果是本轮最后一个跳过的玩家,则结算当前轮的分数
			setResponsed = checkScore(gameMode);
		}
		
		//暂时完成行动
		if(setResponsed){
			this.setPlayerResponsedTemp(gameMode, player);
		}
	}

	/**
	 * 判断本轮的得分情况
	 * 
	 * @param gameMode
	 * @return
	 * @throws BoardGameException
	 */
	private boolean checkScore(TichuGameMode gameMode)
			throws BoardGameException {
		boolean setResponsed = true;
		gameMode.getGame().clearAllPlayerPlayedCard();
		int score = this.roundManager.getCurrentScore();
		if(score!=0){
			TichuCardGroup group = this.roundManager.getLastCardGroup();
			TichuPlayer lastPlayer = group.owner;
			//如果最后一轮牌中有龙,则需要由最后出牌的玩家选择将分数交给谁
			if(group.hasCard(AbilityType.DRAGON)){
				TichuScoreInterruptListener l = new TichuScoreInterruptListener(lastPlayer, score);
				this.insertInterrupteListener(l, gameMode);
				//插入了监听器,则在回调函数中设置玩家行动完成
				setResponsed = false;
			}else{
				gameMode.getGame().playerGetScore(lastPlayer, score);
			}
		}
		this.roundManager.newRound();
		return setResponsed;
	}
	
	
	
	/**
	 * 检查该玩家是否是最后一个pass的玩家
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	protected boolean isLastPassedPlayer(TichuGameMode gameMode, TichuPlayer player){
		TichuPlayer lastPlayer = this.roundManager.getLastCardGroup().owner;
		List<TichuPlayer> players = gameMode.getGame().getPlayersByOrder(player);
		for(TichuPlayer p : players){
			if(p==player){
				continue;
			}
			if(p==lastPlayer){
				return true;
			}else{
				//如果该玩家没有回应,则继续查找下一个
				if(!this.isPlayerResponsed(p.position)){
					return false;
				}
			}
		}
		return false;
	}
	
	@Override
	protected void onInterrupteListenerOver(TichuGameMode gameMode,
			InterruptParam param) throws BoardGameException {
		super.onInterrupteListenerOver(gameMode, param);
		Integer code = param.getInteger("validCode");
		if(code!=null){
			int validCode = code;
			switch(validCode){
			case TichuGameCmd.GAME_CODE_GIVE_SCORE:{
				//给分
				if(this.roundEnd){
					//如果回合结束,则结束所有玩家的行动
					this.setAllPlayerResponsed(gameMode);
				}else{
					//否则结束当前玩家的行动
					this.setPlayerResponsedTemp(gameMode, listeningPlayer);
				}
				break;}
			case TichuGameCmd.GAME_CODE_WISH_POINT:{
				//如果是许愿,则设置许愿的数
				TichuPlayer player = param.get("player");
				int wishedPoint = param.getInteger("wishedPoint");
				gameMode.getGame().playerWishPoint(player, wishedPoint);
				//当前玩家完成出牌行动
				this.onPlayedCard(gameMode, player);
				break;}
			case TichuGameCmd.GAME_CODE_BOMB_PHASE:{
				//如果是炸弹,则检查是否出了炸弹
				TichuPlayer player = param.get("player");
				TichuCardGroup bomb = param.get("bomb");
				if(bomb!=null){
					//执行出炸弹
					gameMode.getGame().playerPlayCards(player, bomb);
					this.roundManager.addCardGroup(bomb);
					//并将当前玩家设置为出炸弹的玩家
					this.setCurrentListeningPlayer(gameMode, player);
					//然后由出炸弹的玩家的下家继续出牌
					this.onPlayedCard(gameMode, player);
				}
				break;}
			}
		}
	}
	
	/**
	 * 出牌回合的一些内容管理
	 * 
	 * @author F14eagle
	 *
	 */
	class RoundManager{
		TichuGameMode gameMode;
		int round = 0;
		List<List<TichuCardGroup>> cardGroups = new ArrayList<List<TichuCardGroup>>();
		
		public RoundManager(TichuGameMode gameMode){
			this.gameMode = gameMode;
		}
		
		/**
		 * 新的回合开始
		 */
		void newRound(){
			List<TichuCardGroup> list = new ArrayList<TichuCardGroup>();
			this.cardGroups.add(list);
			round++;
		}
		
		/**
		 * 取得当前回合的出的牌
		 * 
		 * @return
		 */
		List<TichuCardGroup> getCurrentCardGroups(){
			int i = round - 1;
			if(i<0){
				return null;
			}else{
				return this.cardGroups.get(i);
			}
		}
		
		/**
		 * 取得当前回合最后一次出的牌
		 * 
		 * @return
		 */
		TichuCardGroup getLastCardGroup(){
			List<TichuCardGroup> group = this.getCurrentCardGroups();
			if(group==null || group.isEmpty()){
				return null;
			}else{
				return group.get(group.size()-1);
			}
		}
		
		/**
		 * 添加出牌
		 * 
		 * @param group
		 */
		void addCardGroup(TichuCardGroup group){
			List<TichuCardGroup> list = this.getCurrentCardGroups();
			if(list!=null){
				list.add(group);
			}
		}
		
		/**
		 * 判断是否可以这样出牌
		 * 
		 * @param group
		 * @throws BoardGameException
		 */
		void checkPlayCard(TichuCardGroup group) throws BoardGameException{
			//判断是否符合出牌规则...
			if(!group.isValidCombination()){
				throw new BoardGameException("你不能这样出牌!");
			}
			TichuCardGroup lastGroup = this.getLastCardGroup();
			//如果没人出过牌,则可以出牌
			if(lastGroup!=null){
				//只能按照相同组合出牌,除了炸蛋...
				if(group.combination!=lastGroup.combination && group.combination!=Combination.BOMBS){
					throw new BoardGameException("你不能这样出牌!");
				}
				if(group.combination!=Combination.BOMBS && group.cards.size()!=lastGroup.cards.size()){
					throw new BoardGameException("你不能这样出牌!");
				}
				if(group.compareTo(lastGroup)<=0){
					throw new BoardGameException("出牌必须比上一把大!");
				}
			}
			//出炸弹时无需检查许愿情况
			if(this.gameMode.wishedPoint>0 && group.combination!=Combination.BOMBS){
				//如果存在许愿,则检查出牌中是否有许愿的数
				if(!group.hasCard(this.gameMode.wishedPoint)){
					//如果没有,则检查玩家是否可以出该许愿的牌,如果可以则抛出异常
					if(this.checkWishedPoint(group.owner)){
						throw new BoardGameException("存在许愿,你必须出许愿的牌!");
					}
				}
			}
		}
		
		/**
		 * 检查是否可以跳过
		 * 
		 * @param player
		 * @throws BoardGameException
		 */
		void checkPass(TichuPlayer player) throws BoardGameException{
			TichuCardGroup group = this.getLastCardGroup();
			if(group==null){
				throw new BoardGameException("请出牌!");
			}
			//如果存在许愿数,则检查玩家是否拥有该许愿数的可出牌组合
			if(this.checkWishedPoint(player)){
				throw new BoardGameException("存在许愿,你必须出牌!");
			}
		}
		
		/**
		 * 检查玩家是否拥有符合许愿的牌
		 * 
		 * @param player
		 * @return
		 */
		boolean checkWishedPoint(TichuPlayer player){
			if(this.gameMode.wishedPoint>0 && player.hasCard(this.gameMode.wishedPoint)){
				double wishedPoint = this.gameMode.wishedPoint;
				double compareValue = 0;
				TichuCardGroup group = this.getLastCardGroup();
				if(group!=null){
					compareValue = group.getCompareValue();
				}
				TichuCardCheck check = new TichuCardCheck(player, player.getHands().getCards());
				if(group!=null && group.combination!=Combination.BOMBS){
					//如果上一轮的牌不是炸弹,则如果你有炸弹的话,肯定是可以出牌的
					if(!check.getBombs(wishedPoint).isEmpty()){
						return true;
					}
				}
				if(group==null || group.combination==Combination.SINGLE){
					//如果是单张牌,只需要检查玩家是否有单张牌即可
					return check.hasCard(wishedPoint, compareValue);
				}
				switch(group.combination){
				case PAIR:
					return check.hasPairs(wishedPoint, compareValue);
				case TRIO:
					return check.hasTrios(wishedPoint, compareValue);
				case FULLHOUSE:
					return check.hasFullhouses(wishedPoint, compareValue);
				case STRAIGHT:
					return check.hasStraight(wishedPoint, compareValue, group.cards.size());
				case GROUP_PAIRS:
					return check.hasPairGroup(wishedPoint, compareValue, group.cards.size());
				case BOMBS:
					return check.hasBomb(wishedPoint, compareValue);
				default:
					return true;
				}
			}
			return false;
		}
		
		/**
		 * 取得当前回合打出牌的分数
		 * 
		 * @return
		 */
		int getCurrentScore(){
			int res = 0;
			List<TichuCardGroup> list = this.getCurrentCardGroups();
			for(TichuCardGroup group : list){
				for(TichuCard card : group.cards){
					res += card.score;
				}
			}
			return res;
		}
		
		/**
		 * 出牌时检查是否有凤,并设置相应的值
		 * 
		 * @param cards
		 * @param action
		 * @throws BoardGameException
		 */
		protected void checkPhoenix(List<TichuCard> cards, BgAction action) throws BoardGameException{
			//检查出的牌中是否有凤
			TichuCard pheonix = CombinationUtil.getCard(cards, AbilityType.PHOENIX);
			if(pheonix!=null){
				if(cards.size()==1){
					//如果是单张,则自动设置凤的值,比任意牌大0.5
					TichuCardGroup group = this.getLastCardGroup();
					if(group==null){
						pheonix.point = 1.5;
					}else{
						pheonix.point = group.keyCard.point + 0.5;
					}
					//值最多不能超过17,因为龙的值是20
					pheonix.point = Math.min(17, pheonix.point);
				}else{
					//如果是多张,则从参数中取得玩家指定的凤的值
					int point = action.getAsInt("point");
					if(point<2 || point>14){
						throw new BoardGameException("请选择正确的值!");
					}
					pheonix.point = point;
					//将牌重新排序
					Collections.sort(cards);
				}
			}
		}
		
		/**
		 * 出牌时检查是否有雀
		 * 
		 * @param cards
		 * @param action
		 * @throws BoardGameException
		 */
		protected boolean checkMahJong(List<TichuCard> cards, BgAction action) throws BoardGameException{
			//检查出的牌中是否有雀
			TichuCard card = CombinationUtil.getCard(cards, AbilityType.MAH_JONG);
			if(card!=null){
				return true;
			}else{
				return false;
			}
		}
		
		/**
		 * 出牌时检查是否有狗,并设置相应的值
		 * 
		 * @param player
		 * @param cards
		 * @throws BoardGameException
		 */
		protected void checkDog(TichuGameMode gameMode, TichuPlayer player, List<TichuCard> cards) throws BoardGameException{
			//检查出的牌中是否有狗
			TichuCard card = CombinationUtil.getCard(cards, AbilityType.DOG);
			if(card!=null){
				//有狗的话,给当前玩家的下家需要跳过出牌...
				TichuPlayer nextPlayer = gameMode.getGame().getNextPlayersByOrder(player);
				nextPlayer.pass = true;
				this.newRound();
			}
		}
		
		/**
		 * 判断当前轮出的牌中是否有指定能力的牌
		 * 
		 * @param abilityType
		 * @return
		 */
		protected boolean isCurrentHasCard(AbilityType abilityType){
			List<TichuCardGroup> list = this.getCurrentCardGroups();
			for(TichuCardGroup group : list){
				if(group.hasCard(abilityType)){
					return true;
				}
			}
			return false;
		}
		
	}

}
