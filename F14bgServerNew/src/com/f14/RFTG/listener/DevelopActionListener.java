package com.f14.RFTG.listener;

import java.util.ArrayList;
import java.util.List;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.DevelopAbility;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.consts.CardType;
import com.f14.RFTG.consts.RaceActionType;
import com.f14.RFTG.mode.RaceGameMode;
import com.f14.RFTG.network.CmdConst;
import com.f14.RFTG.network.CmdFactory;
import com.f14.RFTG.utils.RaceUtils;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;

/**
 * 开发阶段的监听器
 * 
 * @author F14eagle
 *
 */
public class DevelopActionListener extends RaceActionListener {

	@Override
	protected int getValidCode() {
		return CmdConst.GAME_CODE_DEVELOP;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class<DevelopAbility> getAbility() {
		return DevelopAbility.class;
	}
	
	@Override
	protected void onStartListen(RaceGameMode gameMode)
			throws BoardGameException {
		//在开始开发阶段时,处理某些卡牌的特殊能力
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			List<RaceCard> cards = player.getCardsByAbilityType(DevelopAbility.class);
			for(RaceCard o : cards){
				for(DevelopAbility a : o.getAbilitiesByType(DevelopAbility.class)){
					if(a.onStartDrawNum>0){
						//在回合开始时摸牌
						gameMode.getGame().drawCard(player, a.onStartDrawNum);
						gameMode.getGame().sendCardEffectResponse(player, o.id);
					}
				}
			}
		}
		//检查并发送可主动使用的卡牌
		this.checkActiveCards(gameMode);
	}
	
	@Override
	protected void onReconnect(RaceGameMode gameMode, Player player)
			throws BoardGameException {
		DevelopParam p = this.getParam(player.position);
		if(p!=null && !p.cards.isEmpty()){
			//如果有选择开发的设施,则将其返回到客户端
			gameMode.getGame().sendDrawCardResponse((RacePlayer)player, BgUtils.card2String(p.cards));
		}
		//取消选择开发的设施
		this.cancelCard(gameMode, (RacePlayer)player);
	}

	@Override
	protected void doAction(RaceGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		RacePlayer player = action.getPlayer();
		//取得开发阶段指令中的子动作
		String subact = action.getAsString("subact");
		if("choose".equals(subact)){
			DevelopParam p = this.getParam(player.getPosition());
			if(p!=null && !p.cards.isEmpty()){
				throw new BoardGameException("请选择弃牌进行设施的开发!");
			}
			//选择开发的设施
			String cardIds = action.getAsString("cardIds");
			List<RaceCard> cards = player.getCards(cardIds);
			//暂时只接受1张牌的开发
			if(cards.size()!=1){
				throw new BoardGameException("只能开发1个设施!");
			}
			for(RaceCard card : cards){
				if(card.type!=CardType.DEVELOPMENT){
					throw new BoardGameException("开发阶段只能选择开发设施!");
				}
				if(player.hasBuiltCard(card.cardNo)){
					throw new BoardGameException("不能开发相同的设施!");
				}
			}
			p = this.createDevelopParam(player, cards);
			//暂时先将这些牌打出
			player.playCards(cardIds);
			if(p.cost==0){
				//如果费用为0,则直接允许开发
				//设置已回应
				this.setPlayerResponsed(gameMode, player.getPosition());
			}else{
				//只将费用信息发送给自己 
				BgResponse res = CmdFactory.createGameResultResponse(getValidCode(), player.getPosition());
				res.setPrivateParameter("subact", subact);
				res.setPrivateParameter("cardIds", cardIds);
				res.setPrivateParameter("cost", p.cost);
				gameMode.getGame().sendResponse(player, res);
			}
		}else if("discard".equals(subact)){
			//选择丢弃的手牌
			String cardIds = action.getAsString("cardIds");
			List<RaceCard> discards = player.getCards(cardIds);
			DevelopParam p = this.getParam(player.getPosition());
			if(p==null || p.cards.isEmpty()){
				throw new BoardGameException("请先选择要开发的设施!");
			}
			if(discards.size()!=p.cost){
				throw new BoardGameException("弃牌数量错误,你需要弃 "+p.cost+" 张牌!");
			}
			p.discardIds = cardIds;
			//设置已回应
			this.setPlayerResponsed(gameMode, player.getPosition());
		}else if("cancel".equals(subact)){
			//取消已选择开发的设施
			cancelCard(gameMode, player);
		}else if("pass".equals(subact)){
			//跳过行动时先取消选择的卡牌
			cancelCard(gameMode, player);
			this.setPlayerResponsed(gameMode, player.getPosition());
		}else if("active".equals(subact)){
			//取消已选择扩张的星球
			cancelCard(gameMode, player);
			//使用卡牌的能力
			this.activeCard(gameMode, action);
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}

	/**
	 * 取消选择的卡牌
	 * 
	 * @param gameMode
	 * @param player
	 */
	private void cancelCard(RaceGameMode gameMode, RacePlayer player) {
		DevelopParam p = this.getParam(player.getPosition());
		if(p!=null && !p.cards.isEmpty()){
			player.addCards(p.cards);
			//将取消开发的信息发送给自己
			BgResponse res = CmdFactory.createGameResultResponse(getValidCode(), player.getPosition());
			res.setPrivateParameter("subact", "cancel");
			res.setPrivateParameter("cardIds", RaceUtils.card2String(p.cards));
			gameMode.getGame().sendResponse(res);
			p.cards.clear();
			p.cost = 0;
		}
	}
	
	@Override
	public void onAllPlayerResponsed(RaceGameMode gameMode)
			throws BoardGameException {
		//将所有玩家的选择情况发送到客户端
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			DevelopParam p = this.getParam(player.getPosition());
			if(p==null || p.cards.isEmpty()){
				//跳过开发
				BgResponse res = CmdFactory.createGameResultResponse(getValidCode(), player.getPosition());
				res.setPublicParameter("subact", "pass");
				gameMode.getGame().sendResponse(res);
			}else{
				//执行开发
				player.addBuiltCards(p.cards);
				//将弃牌信息发送给客户端
				gameMode.getGame().discardCard(player, p.discardIds);
				//将直接出牌信息发送给客户端
				gameMode.getGame().sendPlayCardResponse(player, RaceUtils.card2String(p.cards));
				gameMode.getGame().sendCardEffectResponse(player, RaceUtils.card2String(p.effectedCards));
				this.afterDevelop(gameMode, player, p.cards);
			}
		}
	}
	
	/**
	 * 成功开发后执行的动作
	 * 
	 * @param player
	 * @param cards
	 * @throws BoardGameException 
	 */
	private void afterDevelop(RaceGameMode gameMode, RacePlayer player, List<RaceCard> cards) throws BoardGameException{
		for(@SuppressWarnings("unused") RaceCard card : cards){
			List<RaceCard> abilityCards = player.getCardsByAbilityType(DevelopAbility.class);
			for(RaceCard o : abilityCards){
				//如果是本次开发的牌,则不能使用该能力
				if(cards.contains(o)){
					continue;
				}
				for(DevelopAbility a : o.getAbilitiesByType(DevelopAbility.class)){
					if(a.afterDevelopDrawNum>0){
						//成功开发后摸牌
						gameMode.getGame().drawCard(player, a.afterDevelopDrawNum);
						gameMode.getGame().sendCardEffectResponse(player, o.id);
					}
				}
			}
		}
	}
	
	/**
	 * 使用卡牌的能力
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	private void activeCard(RaceGameMode gameMode, BgAction action) throws BoardGameException{
		RacePlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		RaceCard card = player.getBuiltCard(cardId);
		if(!card.isAbilitiesActive(this.getAbility())){
			throw new BoardGameException("不能使用该卡牌!");
		}
		int useNum = this.getCardUseNum(player, card);
		if(useNum<=0){
			throw new BoardGameException("该卡牌的使用次数已经用完了!");
		}
		//应用可以使用的能力
		boolean actived = false;
		DevelopAbility ability = card.getAbilityByType(this.getAbility());
		if(ability.skill==null){
			
		}else{
			switch(ability.skill){
				case DISCARD_FOR_DEVELOP_COST: //弃牌调整开发费用
					this.setTempDevelopAbility(player.getPosition(), ability);
					actived = true;
					break;
			}
		}
		
		//如果使用了该技能,则发送信息到客户端
		if(actived){
			//将卡牌的使用次数设为0
			this.setCardUseNum(player, card, 0);
			gameMode.getGame().useCard(player, card.id);
			//需要弃牌的话就弃掉该牌
			if(ability.discardAfterActived){
				gameMode.getGame().discardPlayedCard(player, card.id);
			}
		}
	}
	
	/**
	 * 设置玩家在本回合中临时的开发调整能力
	 * 
	 * @param position
	 * @param ability
	 */
	private void setTempDevelopAbility(int position, DevelopAbility ability){
		this.getPlayerParamSet(position).set("development", ability);
	}
	
	/**
	 * 取得玩家在本回合中的临时开发调整能力
	 * 
	 * @param position
	 * @return
	 */
	private DevelopAbility getTempDevelopAbility(int position){
		DevelopAbility ability = (DevelopAbility)this.getPlayerParamSet(position).get("development");
		return ability;
	}
	
	/**
	 * 取得玩家在本回合中开发指定设施的全部临时费用调整值
	 * 
	 * @param position
	 * @param card
	 * @return
	 */
	private int getTempDevelopCostTotal(int position, RaceCard card){
		int res = 0;
		DevelopAbility ability = getTempDevelopAbility(position);
		if(ability!=null && ability.test(card)){
			res += ability.discardCost;
		}
		return res;
	}
	
	/**
	 * 创建玩家的开发参数
	 * 
	 * @param player
	 * @param cards
	 * @return
	 */
	private DevelopParam createDevelopParam(RacePlayer player, List<RaceCard> cards){
		DevelopParam p = new DevelopParam();
		
		for(RaceCard card : cards){
			int cost = card.cost;
			if(player.isActionSelected(RaceActionType.DEVELOP) || player.isActionSelected(RaceActionType.DEVELOP_2)){
				cost -= 1;
			}
			
			//检查牌的能力
			List<RaceCard> builtCards = player.getCardsByAbilityType(DevelopAbility.class);
			for(RaceCard o : builtCards){
				for(DevelopAbility a : o.getAbilitiesByType(DevelopAbility.class)){
					//如果存在价格调整,并且建造的卡牌符合该能力的要求,则触发效果
					if(a.cost!=0 && a.test(card)){
						cost += a.cost;
						p.effectedCards.add(o);
					}
				}
			}
			
			//检查临时的开发费用调整值
			cost += this.getTempDevelopCostTotal(player.position, card);
			
			//如果费用小于0,则设为0
			if(cost<0){
				cost = 0;
			}
			p.cost += cost;
			p.cards.add(card);
		}
		this.setParam(player.getPosition(), p);
		return p;
	}
	
	class DevelopParam{
		int cost = 0;
		List<RaceCard> cards = new ArrayList<RaceCard>();;
		String discardIds;
		List<RaceCard> effectedCards = new ArrayList<RaceCard>();
	}

}
