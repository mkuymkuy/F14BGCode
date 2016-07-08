package com.f14.RFTG.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.Ability;
import com.f14.RFTG.card.ProduceAbility;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.consts.GoodType;
import com.f14.RFTG.consts.ProductionType;
import com.f14.RFTG.consts.RaceActionType;
import com.f14.RFTG.mode.RaceGameMode;
import com.f14.RFTG.network.CmdConst;
import com.f14.RFTG.network.CmdFactory;
import com.f14.RFTG.utils.RaceUtils;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

public class ProduceActionListener extends RaceActionListener {

	@Override
	protected int getValidCode() {
		return CmdConst.GAME_CODE_PRODUCE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class<ProduceAbility> getAbility() {
		return ProduceAbility.class;
	}
	
	@Override
	protected void onStartListen(RaceGameMode gameMode)
			throws BoardGameException {
		List<BgResponse> res = new ArrayList<BgResponse>();
		//开始生成阶段时,为所有可以生产的星球生产货物
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			//创建玩家的生产参数
			ProduceParam p = this.createproduceParam(player);
			//为玩家可生产货物的星球生产货物
			p.goodWorlds.addAll(this.getGoodWorlds(player));
			gameMode.getGame().produceGood(player, RaceUtils.card2String(p.goodWorlds));
			p.produceWindfallNum = this.getProduceWindfallNum(player);
			//创建生产货物的指令并发送到客户端
			BgResponse r = CmdFactory.createGameResponse(getValidCode(), player.getPosition());
			r.setPrivateParameter("produceWindfallNum", p.produceWindfallNum);
			res.add(r);
		}
		gameMode.getGame().sendResponse(res);
		
		//在阶段开始时,处理某些卡牌的特殊能力
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			List<RaceCard> cards = player.getCardsByAbilityType(ProduceAbility.class);
			for(RaceCard o : cards){
				for(ProduceAbility a : o.getAbilitiesByType(ProduceAbility.class)){
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
		//发送可以主动使用的卡牌
		BgResponse res = this.createActivedCardResponse((RacePlayer)player);
		if(res!=null){
			gameMode.getGame().sendResponse(player, res);
		}
	}

	@Override
	protected void doAction(RaceGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		RacePlayer player = action.getPlayer();
		String subact = action.getAsString("subact");
		if("produce".equals(subact)){
			//为意外星球生产货物
			ProduceParam p = this.getParam(player.getPosition());
			if(p.produceWindfallNum<=0){
				throw new BoardGameException("你不能在意外星球上生产货物!");
			}
			String cardIds = action.getAsString("cardIds");
			List<RaceCard> cards = player.getBuiltCards(cardIds);
			if(cards.size()!=1){
				throw new BoardGameException("只能在一个星球上生产货物!");
			}
			//生产货物
			this.produceWindFallGood(gameMode, player, cards.get(0));
			p.produceWindfallNum--;
		}else if("active".equals(subact)){
			//激活卡牌能力
			this.activeCard(gameMode, action);
		}else if("pass".equals(subact)){
			//将跳过的信息发送给客户端
			BgResponse res = CmdFactory.createGameResultResponse(getValidCode(), player.getPosition());
			res.setPublicParameter("subact", subact);
			gameMode.getGame().sendResponse(res);
			//设置已回应
			this.setPlayerResponsed(gameMode, player.getPosition());
		}else{
			throw new BoardGameException("无效的指令!");
		}
	}
	
	@Override
	public void onAllPlayerResponsed(RaceGameMode gameMode)
			throws BoardGameException {
		//检查所有玩家对于生产货物而生效的能力
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			int drawNum = 0;
			List<RaceCard> effectedCards = new ArrayList<RaceCard>();
			ProduceParam p = this.getParam(player.getPosition());
			List<RaceCard> cards = player.getBuiltCards();
			for(RaceCard card : cards){
				ProduceAbility ability = card.getAbilityByType(this.getAbility());
				if(ability!=null){
					if(ability.skill==null){
						//其他的能力
						if(ability.drawAfterProduced!=0 && p.goodWorlds.contains(card)){
							drawNum += ability.drawAfterProduced;
							effectedCards.add(card);
						}
					}else{
						switch(ability.skill){
						case DRAW_FOR_WORLD:
							//每个星球摸牌
							int num = RaceUtils.getValidWorldNum(cards, ability);
							if(num!=0){
								drawNum += num * ability.drawNum;
								effectedCards.add(card);
							}
							break;
						case DRAW_FOR_PRODUCED_GOOD_TYPE:
							//每个生产的货物种类摸牌
							num = this.getValidGoodTypeNum(p.goodWorlds, ability);
							if(num!=0){
								drawNum += num * ability.drawAfterProduced;
								effectedCards.add(card);
							}
							break;
						case DRAW_FOR_MUST_PRODUCED:
							//生产最多时摸牌
							if(this.isMostProduced(gameMode, player, ability)){
								drawNum += ability.drawAfterProduced;
								effectedCards.add(card);
							}
							break;
						case DRAW_FOR_PRODUCED_WORLD:
							//每个生产货物的星球摸牌
							num = this.getValidGoodNum(p.goodWorlds, ability);
							if(num!=0){
								drawNum += num * ability.drawAfterProduced;
								effectedCards.add(card);
							}
							break;
						default:
							break;
						}
					}
				}
			}
			if(drawNum!=0){
				gameMode.getGame().sendCardEffectResponse(player, RaceUtils.card2String(effectedCards));
				gameMode.getGame().drawCard(player, drawNum);
			}
		}
	}
	
	/**
	 * 判断玩家是否是生产最多符合能力的货物的星球
	 * 
	 * @param gameMode
	 * @param player
	 * @param ability
	 * @return
	 */
	private boolean isMostProduced(RaceGameMode gameMode, RacePlayer player, Ability ability){
		ProduceParam op = this.getParam(player.getPosition());
		int onum = this.getValidGoodNum(op.goodWorlds, ability);
		for(RacePlayer p : gameMode.getGame().getValidPlayers()){
			if(p==player){
				continue;
			}
			ProduceParam cp = this.getParam(p.getPosition());
			if(onum<=this.getValidGoodNum(cp.goodWorlds, ability)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 取得适用于该能力的货物数量
	 * 
	 * @param goodCards
	 * @param ability
	 * @return
	 */
	private int getValidGoodNum(List<RaceCard> goodCards, Ability ability){
		int i = 0;
		for(RaceCard card : goodCards){
			if(card.good!=null && ability.test(card)){
				i++;
			}
		}
		return i;
	}
	
	/**
	 * 取得适用于该能力的货物种类的数量
	 * 
	 * @param goodCards
	 * @param ability
	 * @return
	 */
	private int getValidGoodTypeNum(List<RaceCard> goodCards, Ability ability){
		Set<GoodType> goodType = new HashSet<GoodType>();
		for(RaceCard card : goodCards){
			if(card.good!=null && ability.test(card)){
				goodType.add(card.goodType);
			}
		}
		return goodType.size();
	}
	
	/**
	 * 激活卡牌的能力
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
		ProduceParam p = this.getParam(player.getPosition());
		//激活当前使用的卡牌
		ProduceAbility ability = card.getActiveAbilityByType(this.getAbility());
		switch(ability.skill){
			case PRODUCE_WINDFALL: //在意外星球生产货物的能力
				this.useCard(gameMode, action, card);
				break;
			case DISCARD_HAND_FOR_PRODUCE: //弃牌生产货物
				if(card.good!=null){
					throw new BoardGameException("该星球已经有货物了!");
				}
				String cardIds = action.getAsString("cardIds");
				List<RaceCard> discards = player.getCards(cardIds);
				if(discards.size()!=ability.discardNum){
					throw new BoardGameException("弃牌数量不正确!");
				}
				p.goodWorlds.add(card);
				gameMode.getGame().useCard(player, cardId);
				gameMode.getGame().discardCard(player, cardIds);
				gameMode.getGame().produceGood(player, cardId);
				break;
			default:
				throw new BoardGameException("未知的生产能力!");
		}
	}
	
	/**
	 * 使用卡牌的能力
	 * 
	 * @param gameMode
	 * @param action
	 * @param activeCard
	 * @throws BoardGameException
	 */
	private void useCard(RaceGameMode gameMode, BgAction action, RaceCard activeCard) throws BoardGameException{
		RacePlayer player = action.getPlayer();
		//ProduceParam p = this.getParam(player.getPosition());
		int num = this.getCardUseNum(player, activeCard);
		ProduceAbility ability = activeCard.getActiveAbilityByType(this.getAbility());
		String cardIds = action.getAsString("cardIds");
		List<RaceCard> cards;
		switch (ability.skill) {
			case PRODUCE_WINDFALL: //在意外星球生产货物的能力
				cards = player.getBuiltCards(cardIds);
				if(cards.size()!=1){
					throw new BoardGameException("每次只能在1个星球上生产货物!");
				}
				if(!ability.test(cards.get(0))){
					throw new BoardGameException("该能力不能用于指定的星球!");
				}
				this.produceWindFallGood(gameMode, player, cards.get(0));
				//发送生效卡牌列表到客户端
				gameMode.getGame().useCard(player, activeCard.id);
				num = 0;
				break;
			default:
				throw new BoardGameException("未知的生产能力!");
		}
		this.setCardUseNum(player, activeCard, num);
	}
	
	/**
	 * 生产意外星球的货物
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @throws BoardGameException
	 */
	private void produceWindFallGood(RaceGameMode gameMode, RacePlayer player, RaceCard card) throws BoardGameException{
		if(card.productionType!=ProductionType.WINDFALL){
			throw new BoardGameException("该星球不是意外星球!");
		}
		if(card.good!=null){
			throw new BoardGameException("该星球已经有货物了!");
		}
		gameMode.getGame().produceGood(player, card.id);
		ProduceParam p = this.getParam(player.getPosition());
		p.goodWorlds.add(card);
	}
	
	/**
	 * 取得玩家可生产货物的星球
	 * 
	 * @param player
	 * @return
	 */
	private List<RaceCard> getGoodWorlds(RacePlayer player){
		List<RaceCard> cards = new ArrayList<RaceCard>();
		//取得所有可以生产货物的星球
		for(RaceCard card : player.getBuiltCards()){
			//生产类型并且没有货物的星球可以生产货物
			if(card.productionType==ProductionType.PRODUCTION && card.good==null && !card.specialProduction){
				cards.add(card);
			}
		}
		return cards;
	}
	
	/**
	 * 取得玩家可生产意外星球货物的数量
	 * 
	 * @param player
	 * @return
	 */
	private int getProduceWindfallNum(RacePlayer player){
		int res = 0;
		if(player.isActionSelected(RaceActionType.PRODUCE)){
			res += 1;
		}
		return res;
	}
	
	/**
	 * 创建玩家的生产参数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	private ProduceParam createproduceParam(RacePlayer player){
		ProduceParam p = new ProduceParam();
		this.setParam(player.getPosition(), p);
		return p;
	}
	
	class ProduceParam{
		int produceWindfallNum = 0;
		List<RaceCard> goodWorlds = new ArrayList<RaceCard>();
	}

}
