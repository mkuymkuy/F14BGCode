package com.f14.RFTG.listener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.Ability;
import com.f14.RFTG.card.ConsumeAbility;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.card.RaceDeck;
import com.f14.RFTG.card.TradeAbility;
import com.f14.RFTG.consts.GoodType;
import com.f14.RFTG.consts.RaceActionType;
import com.f14.RFTG.consts.Skill;
import com.f14.RFTG.mode.RaceGameMode;
import com.f14.RFTG.network.CmdConst;
import com.f14.RFTG.network.CmdFactory;
import com.f14.RFTG.utils.RaceUtils;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.ActionStep;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;

public class ConsumeActionListener extends RaceActionListener {

	@Override
	protected int getValidCode() {
		return CmdConst.GAME_CODE_CONSUME;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class<ConsumeAbility> getAbility() {
		return ConsumeAbility.class;
	}
	
	@Override
	protected void onStartListen(RaceGameMode gameMode)
			throws BoardGameException {
		List<BgResponse> res = new ArrayList<BgResponse>();
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			//创建消费阶段的参数
			ConsumeParam p = this.createConsumeParam(player);
			BgResponse r = CmdFactory.createGameResponse(getValidCode(), player.getPosition());
			r.setPrivateParameter("tradeNum", p.tradeNum);
			res.add(r);
		}
		gameMode.getGame().sendResponse(res);
		
		//在阶段开始时,处理某些卡牌的特殊能力
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			List<RaceCard> cards = player.getCardsByAbilityType(ConsumeAbility.class);
			for(RaceCard o : cards){
				for(ConsumeAbility a : o.getAbilitiesByType(ConsumeAbility.class)){
					if(a.onStartDrawNum>0){
						//在回合开始时摸牌
						gameMode.getGame().drawCard(player, a.onStartDrawNum);
						gameMode.getGame().sendCardEffectResponse(player, o.id);
					}
					if(a.onStartVp!=0){
						//在回合开始时得到VP
						ConsumeParam p = this.getParam(player.position);
						gameMode.getGame().getVP(player, a.onStartVp*p.factor);
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
		super.onReconnect(gameMode, player);
	}
	
	@Override
	protected void doAction(RaceGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		String subact = action.getAsString("subact");
		RacePlayer player = action.getPlayer();
		ConsumeParam p = this.getParam(player.getPosition());
		if(p==null){
			throw new BoardGameException("参数未初始化!");
		}
		if("trade".equals(subact)){
			//交易
			if(p.tradeNum<=0){
				throw new BoardGameException("不能进行交易!");
			}
			String cardIds = action.getAsString("cardIds");
			List<RaceCard> cards = player.getBuiltCards(cardIds);
			if(cards.size()!=1){
				throw new BoardGameException("每次只能交易一个货物!");
			}
			//清空生效卡牌列表,因为可能会有多次交易
			p.effectedCards.clear();
			//交易货物
			this.tradeGood(gameMode, player, cards.get(0), true);
			//发送生效卡牌列表到客户端
			gameMode.getGame().sendCardEffectResponse(player, RaceUtils.card2String(p.effectedCards));
			p.tradeNum -= 1;
		}else{
			//如果使用交易能力,则在执行其他动作前,必须判断是否可以交易
			//如果可以交易就必须先执行交易
			this.checkTradeSkill(player);
			if("active".equals(subact)){
				//激活卡牌能力
				this.activeCard(gameMode, action);
			}else if("pass".equals(subact)){
				//跳过之前必须判断是否还有剩余的可消费货物
				//消费阶段必须消费掉所有可以消费的货物
				this.checkConsumePass(gameMode, player);
				//将跳过的信息发送给客户端
				BgResponse res = CmdFactory.createGameResultResponse(getValidCode(), player.getPosition());
				res.setPublicParameter("subact", subact);
				gameMode.getGame().sendResponse(res);
				//设置已回应
				this.setPlayerResponsed(gameMode, player.getPosition());
			}else if("gamble".equals(subact)){
				//执行赌博能力
				this.gamble(gameMode, action);
			}else{
				throw new BoardGameException("无效的指令!");
			}
		}
	}
	
	/**
	 * 检查是否可以结束消费阶段,如果还剩可消费的货物则不能跳过
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException
	 */
	private void checkConsumePass(RaceGameMode gameMode, RacePlayer player) throws BoardGameException{
		List<RaceCard> cards = player.getBuiltCardsWithGood();
		if(!cards.isEmpty()){
			//还有存货时,检查可用的能力
			List<RaceCard> activeCards = player.getActiveCardsByAbilityType(this.getAbility());
			for(RaceCard activeCard : activeCards){
				if(this.getCardUseNum(player, activeCard)>0){
					//如果该能力还可用
					ConsumeAbility ability = activeCard.getActiveAbilityByType(this.getAbility());
					switch(ability.skill){
					case TRADE:
					case CONSUME_REMAINING:
					case DIFFERENT_GOOD_CONSUME_2:
						//交易和消费剩余所有货物的能力
						//有剩余的牌时必须消费掉
						throw new BoardGameException("还有剩余的货物可以进行消费,不能结束消费!");
					case CONSUME:
						//普通的消费能力
						int num = this.getValidGoodNum(cards, ability);
						if(num>=ability.goodNum){
							throw new BoardGameException("还有剩余的货物可以进行消费,不能结束消费!");
						}
						break;
					case DIFFERENT_GOOD_CONSUME:
						//消费不同类型货物的能力
						num = this.getValidGoodTypeNum(cards, ability);
						if(num>=ability.goodNum){
							throw new BoardGameException("还有剩余的货物可以进行消费,不能结束消费!");
						}
						break;
					case SELF_CONSUME:
						//消费自己的货物
						if(activeCard.good!=null){
							throw new BoardGameException("还有剩余的货物可以进行消费,不能结束消费!");
						}
						break;
					}
				}
			}
		}
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
	 * 检查玩家是否先进行了交易,如果没有交易则抛出异常
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException
	 */
	private void checkTradeSkill(RacePlayer player) throws BoardGameException{
		ConsumeParam p = this.getParam(player.getPosition());
		if(p.tradeNum>0){
			if(player.hasGood()){
				throw new BoardGameException("必须先进行交易!");
			}
		}
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
		ConsumeParam p = this.getParam(player.getPosition());
		//激活当前使用的卡牌
		ConsumeAbility ability = card.getActiveAbilityByType(this.getAbility());
		switch(ability.skill){
			case TRADE: //交易能力
			case CONSUME: //消费能力
			case DISCARD_HANDS_FOR_VP: //弃手牌换VP
			case DIFFERENT_GOOD_CONSUME: //消费不同类型的货物
			case DISCARD_HANDS_FOR_CARD: //弃手牌换牌
			case DIFFERENT_GOOD_CONSUME_2: //消费不同类型的货物2
				this.useCard(gameMode, action, card);
				break;
			case CONSUME_REMAINING: //消费剩余的所有货物
				List<RaceCard> goodCards = player.getBuiltCardsWithGood();
				if(goodCards.isEmpty()){
					throw new BoardGameException("没有货物,不能消费!");
				}
				//得到的VP为消费掉的货物数量-1
				int vp = goodCards.size()-1;
				if(vp<=0){
					vp = 0;
				}
				vp *= p.factor;
				gameMode.getGame().useCard(player, card.id);
				gameMode.getGame().discardGood(player, RaceUtils.card2String(goodCards));
				gameMode.getGame().getVP(player, vp);
				//减去使用次数,这个需要在将来调整
				this.setCardUseNum(player, card, 0);
				break;
			case SELF_CONSUME:
				//消费自己货物的能力
				if(card.good==null){
					throw new BoardGameException("该星球没有货物!");
				}
				gameMode.getGame().useCard(player, card.id);
				gameMode.getGame().discardGood(player, card.id);
				gameMode.getGame().getVP(player, ability.vp*p.factor);
				//减去使用次数,这个需要在将来调整
				this.setCardUseNum(player, card, 0);
				break;
			default:
				throw new BoardGameException("未知的消费能力!");
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
		ConsumeParam p = this.getParam(player.getPosition());
		int num = this.getCardUseNum(player, activeCard);
		ConsumeAbility ability = activeCard.getActiveAbilityByType(this.getAbility());
		String cardIds = action.getAsString("cardIds");
		List<RaceCard> cards;
//		//按照激活类型取得相应的卡牌对象
//		switch (ability.activeType) {
//		case TARGET_GOOD:
//			cards = player.getGoods(cardIds);
//			break;
//		case TARGET_HAND_CARD:
//			cards = player.getCards(cardIds);
//			break;
//		case TARGET_PLAYED_CARD:
//			cards = player.getBuiltCards(cardIds);
//			break;
//		default:
//			cards = new ArrayList<RaceCard>();
//		}
		switch (ability.skill) {
			case TRADE:
				//使用交易能力
				cards = player.getBuiltCards(cardIds);
				if(cards.size()!=1){
					throw new BoardGameException("交易数量不正确!");
				}
				p.effectedCards.clear();
				this.tradeGood(gameMode, player, cards.get(0), ability.tradeWithSkill);
				//发送生效卡牌列表到客户端
				gameMode.getGame().useCard(player, activeCard.id);
				gameMode.getGame().sendCardEffectResponse(player, RaceUtils.card2String(p.effectedCards));
				num = 0;
				break;
			case CONSUME:
				//使用消费能力
				cards = player.getBuiltCards(cardIds);
				//检查是否可以进行消费,如果不能消费则抛出异常
				this.checkConsume(player, ability, cards);
				gameMode.getGame().useCard(player, activeCard.id);
				gameMode.getGame().discardGood(player, cardIds);
				if(ability.drawNum>0){
					gameMode.getGame().drawCard(player, (cards.size()/ability.goodNum)*ability.drawNum);
				}
				if(ability.vp>0){
					gameMode.getGame().getVP(player, (cards.size()/ability.goodNum)*ability.vp*p.factor);
				}
				num = 0;
				break;
			case DIFFERENT_GOOD_CONSUME:
				//消费不同类型货物的能力
				cards = player.getBuiltCards(cardIds);
				if(cards.size()!=ability.goodNum){
					throw new BoardGameException("消费的货物数量错误!");
				}
				//检查货物的类型是否都不相同
				for(RaceCard c1 : cards){
					for(RaceCard c2 : cards){
						if(c1!=c2 && c1.goodType==c2.goodType){
							throw new BoardGameException("必须选择不同类型的货物进行消费!");
						}
					}
				}
				gameMode.getGame().useCard(player, activeCard.id);
				gameMode.getGame().discardGood(player, cardIds);
				gameMode.getGame().getVP(player, ability.vp*p.factor);
				num = 0;
				break;
			case DISCARD_HANDS_FOR_VP:
				//弃手牌换VP
				cards = player.getCards(cardIds);
				if(cards.size()==0 || cards.size()>(ability.discardNum*ability.maxNum)){
					throw new BoardGameException("选择的卡牌数量错误!");
				}
				gameMode.getGame().useCard(player, activeCard.id);
				gameMode.getGame().discardCard(player, cardIds);
				gameMode.getGame().getVP(player, cards.size());
				num = 0;
				break;
			case DISCARD_HANDS_FOR_CARD:
				//弃手牌换牌
				cards = player.getCards(cardIds);
				if(cards.size()==0 || cards.size()>(ability.discardNum*ability.maxNum)){
					throw new BoardGameException("选择的卡牌数量错误!");
				}
				gameMode.getGame().useCard(player, activeCard.id);
				gameMode.getGame().discardCard(player, cardIds);
				gameMode.getGame().drawCard(player, ability.drawNum);
				num = 0;
				break;
			case DIFFERENT_GOOD_CONSUME_2:
				//消费不同类型货物的能力2
				cards = player.getBuiltCards(cardIds);
				if(cards.size()<1 || cards.size()>4){
					throw new BoardGameException("消费的货物数量错误!");
				}
				//检查货物的类型是否都不相同
				for(RaceCard c1 : cards){
					for(RaceCard c2 : cards){
						if(c1!=c2 && c1.goodType==c2.goodType){
							throw new BoardGameException("必须选择不同类型的货物进行消费!");
						}
					}
				}
				//消费能力是强制的,检查是否存在可以消费而未消费的货物
				this.checkDifferentConsume(player, cards);
				gameMode.getGame().useCard(player, activeCard.id);
				gameMode.getGame().discardGood(player, cardIds);
				gameMode.getGame().getVP(player, cards.size()*ability.vp*p.factor);
				gameMode.getGame().drawCard(player, cards.size()*ability.drawNum);
				num = 0;
				break;
			default:
				throw new BoardGameException("未知的消费能力!");
		}
		this.setCardUseNum(player, activeCard, num);
	}
	
	/**
	 * 检查不同类型货物的消费能力
	 * 
	 * @param player
	 * @param goodCards
	 * @throws BoardGameException
	 */
	private void checkDifferentConsume(RacePlayer player, List<RaceCard> goodCards) throws BoardGameException{
		Set<GoodType> playerGoodTypes = new HashSet<GoodType>();
		Set<GoodType> consumeGoodTypes = new HashSet<GoodType>();
		for(RaceCard card : goodCards){
			if(card.good==null){
				throw new BoardGameException("该星球没有货物!");
			}
			consumeGoodTypes.add(card.goodType);
		}
		for(RaceCard card : player.getBuiltCards()){
			if(card.good!=null){
				playerGoodTypes.add(card.goodType);
			}
		}
		if(consumeGoodTypes.size()!=playerGoodTypes.size()
			|| !playerGoodTypes.containsAll(consumeGoodTypes)){
			throw new BoardGameException("使用消费能力时必须消费掉所有可以消费的货物!");
		}
	}
	
	/**
	 * 检查消费能力是否可以进行,如果不能消费则抛出异常
	 * 
	 * @param player
	 * @param ability
	 * @param goodCards
	 * @throws BoardGameException
	 */
	private void checkConsume(RacePlayer player, ConsumeAbility ability, List<RaceCard> goodCards) throws BoardGameException{
		//货物数量为0,或者超过最大交易数量时,报错
		if(goodCards.size()==0 || goodCards.size()>(ability.goodNum*ability.maxNum)){
			throw new BoardGameException("消费的货物数量错误!");
		}
		//货物数量不能被每次的交易数量整除,报错
		if(goodCards.size()%ability.goodNum!=0){
			throw new BoardGameException("消费的货物数量错误!");
		}
		for(RaceCard card : goodCards){
			if(card.good==null){
				throw new BoardGameException("该星球没有货物!");
			}
			if(!ability.test(card)){
				throw new BoardGameException("激活的能力不适用于该星球!");
			}
		}
		//消费能力是强制的,必须用完
		if(goodCards.size()<(ability.goodNum*ability.maxNum)){
			//当消费货物的数量小于消费能力允许的上限时,需要检查是否有其他可以消费但是没有消费的货物
			List<RaceCard> allGoodCards = player.getBuiltCardsWithGood(ability);
			if(allGoodCards.size()>goodCards.size()){
				throw new BoardGameException("使用消费能力时必须消费掉所有可以消费的货物!");
			}
		}
	}
	
	/**
	 * 交易货物
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param withTradeSkill
	 * @throws BoardGameException
	 */
	private void tradeGood(RaceGameMode gameMode, RacePlayer player, RaceCard card, boolean withTradeSkill) throws BoardGameException{
		if(card.good==null){
			throw new BoardGameException("该星球没有货物!");
		}
		int drawNum = this.getBaseTradeValue(player, card);
		if(withTradeSkill){
			drawNum += this.getSkillTradeValue(player, card);
		}
		gameMode.getGame().drawCard(player, drawNum);
		gameMode.getGame().discardGood(player, card.id);
	}
	
	/**
	 * 取得玩家交易货物后可以取得的基本牌数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	private int getBaseTradeValue(RacePlayer player, RaceCard card){
		int res = 0;
		switch(card.goodType){
			case NOVELTY:
				res += 2;
				break;
			case RARE:
				res += 3;
				break;
			case GENES:
				res += 4;
				break;
			case ALIEN:
				res += 5;
				break;
		}
		return res;
	}
	
	/**
	 * 取得玩家交易货物后可以取得的交易技能调整的牌数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	private int getSkillTradeValue(RacePlayer player, RaceCard card){
		int res = 0;
		//计算交易能力的调整值
		ConsumeParam p = this.getParam(player.getPosition());
		p.effectedCards.clear();
		List<RaceCard> cards = player.getCardsByAbilityType(TradeAbility.class);
		for(RaceCard o : cards){
			for(TradeAbility a : o.getAbilitiesByType(TradeAbility.class)){
				if(a.skill==null){
					//如果该能力指定必须要本星球交易才能生效的话,则跳过
					if(a.current && card!=o){
						continue;
					}
					//交易能力的调整值
					if(a.drawNum!=0 && a.test(card)){
						res += a.drawNum;
						p.effectedCards.add(o);
					}
				}else{
					switch (a.skill) {
					case DRAW_FOR_WORLD:
						//每个星球摸牌
						int num = RaceUtils.getValidWorldNum(player.getBuiltCards(), a.worldCondition);
						if(num!=0){
							res += num * a.drawNum;
							p.effectedCards.add(o);
						}
						break;
					default:
						break;
					}
				}
			}
		}
		return res;
	}
	
	/**
	 * 取得玩家可以交易货物的数量
	 * 
	 * @param player
	 * @return
	 */
	private int getTradeNum(RacePlayer player){
		int res = 0;
		//选择消费1的玩家可以交易1个货物
		if(player.isActionSelected(RaceActionType.CONSUME_1)){
			res += 1;
		}
		//其他卡牌的能力也能提升交易量
		return res;
	}
	
	/**
	 * 创建玩家的消费参数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	private ConsumeParam createConsumeParam(RacePlayer player){
		ConsumeParam p = new ConsumeParam();
		p.tradeNum = this.getTradeNum(player);
		//如果选择
		if(player.isActionSelected(RaceActionType.CONSUME_2)){
			p.factor = 2;
		}
		this.setParam(player.getPosition(), p);
		return p;
	}
	
	/**
	 * 执行赌博行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	private void gamble(RaceGameMode gameMode, BgAction action) throws BoardGameException{
		RacePlayer player = action.getPlayer();
		ConsumeParam p = this.getParam(player.getPosition());
		if(p.gambled){
			throw new BoardGameException("你已经赌过了,不能再赌了!");
		}
		if(!player.hasSkill(Skill.SPECIAL_GAMBLE)){
			throw new BoardGameException("只有拥有赌博能力时才能赌博!");
		}
		String cardIds = action.getAsString("cardIds");
		List<RaceCard> cards = player.getCards(cardIds);
		if(cards.size()!=1){
			throw new BoardGameException("必须选择一张手牌作为赌注!");
		}
		RaceCard card = cards.get(0);
		if(card.cost<1 || card.cost>6){
			throw new BoardGameException("赌注的费用必须在1-6之间!");
		}
		p.card = card;
		//暂时先弃掉赌注
		player.playCard(card.id);
		gameMode.getGame().sendDiscardResponse(player, card.id);
		//翻开赌注价格数量的牌
		cards = gameMode.draw(p.card.cost);
		p.cards.setDefaultCards(cards);
		this.addActionStep(gameMode, player, new GambleStep());
	}
	
	class ConsumeParam{
		int tradeNum = 0;
		int factor = 1;
		Set<RaceCard> effectedCards = new HashSet<RaceCard>();
		boolean gambled = false;
		RaceCard card;
		RaceDeck cards = new RaceDeck();
	}
	
	/**
	 * 消费阶段的子步骤
	 * 
	 * @author F14eagle
	 *
	 */
	enum ConsumeStep{
		/**
		 * 赌博阶段
		 */
		GAMBLE_STEP
	}
	
	/**
	 * 赌博的步骤
	 * 
	 * @author F14eagle
	 *
	 */
	class GambleStep extends ActionStep<RaceGameMode>{

		@Override
		public int getActionCode() {
			return CmdConst.GAME_CODE_GAMBLE;
		}
		
		@Override
		public String getStepCode() {
			return ConsumeStep.GAMBLE_STEP.toString();
		}
		
		@Override
		protected void onStepStart(RaceGameMode gameMode, Player player)
				throws BoardGameException {
			ConsumeParam p = getParam(player.position);
			//将赌注和翻开的牌发送到客户端
			BgResponse res = this.createStepStartResponse(gameMode, player);
			res.setPublicParameter("betIds", p.card.id);
			res.setPublicParameter("cardIds", BgUtils.card2String(p.cards.getCards()));
			res.setPublicParameter("winGamble", this.winGamble(p));
			gameMode.getGame().sendResponse(res);
		}
		
		@Override
		protected void onStepOver(RaceGameMode gameMode, Player player)
				throws BoardGameException {
			BgResponse res = this.createStepOverResponse(gameMode, player);
			gameMode.getGame().sendResponse(res);
		}

		@Override
		protected void doAction(RaceGameMode gameMode, BgAction action)
				throws BoardGameException {
			RacePlayer player = action.getPlayer();
			String subact = action.getAsString("subact");
			ConsumeParam p = getParam(player.position);
			if(subact.equals("gamble")){
				//选择赌注
				if(!this.winGamble(p)){
					throw new BoardGameException("你输掉了赌博,不能选择奖励!");
				}
				String cardIds = action.getAsString("cardIds");
				List<RaceCard> cards = p.cards.getCards(cardIds);
				if(cards.size()!=1){
					throw new BoardGameException("你只能选择一张牌作为赌博的奖励!");
				}
				//将选择的奖励连同赌注一起给玩家
				List<RaceCard> rewards = new ArrayList<RaceCard>(cards);
				rewards.add(p.card);
				gameMode.getGame().getCard(player, rewards);
				//将其余的牌放入弃牌堆
				p.cards.getCards().removeAll(cards);
				gameMode.discard(p.cards.getCards());
			}else if(subact.equals("pass")){
				//直接结束该行动,将赌注和翻开的牌放入弃牌堆
				gameMode.discard(p.card);
				gameMode.discard(p.cards.getCards());
			}else{
				throw new BoardGameException("无效的指令!");
			}
			//完成赌博,设置参数
			p.gambled = true;
			p.card = null;
			p.cards = null;
		}

		/**
		 * 判断是否赢得赌博
		 * 
		 * @param p
		 * @return
		 */
		private boolean winGamble(ConsumeParam p){
			//如果翻开的牌中有比赌注费用高的牌,则赢得赌博
			for(RaceCard c : p.cards.getCards()){
				if(c.cost>p.card.cost){
					return true;
				}
			}
			return false;
		}

	}
}
