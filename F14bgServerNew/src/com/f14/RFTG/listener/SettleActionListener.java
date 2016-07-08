package com.f14.RFTG.listener;

import java.util.ArrayList;
import java.util.List;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.card.SettleAbility;
import com.f14.RFTG.consts.CardType;
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
import com.f14.bg.utils.BgUtils;

/**
 * 扩张阶段的监听器
 * 
 * @author F14eagle
 *
 */
public class SettleActionListener extends RaceActionListener {

	@Override
	protected int getValidCode() {
		return CmdConst.GAME_CODE_SETTLE;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected Class<SettleAbility> getAbility() {
		return SettleAbility.class;
	}
	
	@Override
	protected void onStartListen(RaceGameMode gameMode)
			throws BoardGameException {
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			//判断玩家是否有购买军事星球的能力
			//判断玩家是否有扩展阶段的特殊能力
			List<RaceCard> cards = player.getCardsByAbilityType(this.getAbility());
			for(RaceCard card : cards){
				SettleAbility ability = card.getAbilityByType(this.getAbility());
				if(ability.skill!=null){
					switch (ability.skill) {
					case BUY_MILITARY_WORLD: //购买军事星球的能力
						this.addMilitaryBuyAbility(player.getPosition(), ability);
						break;
					case DOUBLE_SETTLE: //双重扩张
						this.setSettleNum(player.getPosition(), 2);
						break;
					case BUY_MILITARY_COST: //购买军事星球时的费用调整
						this.setMilitaryBuyCostAbility(player.position, ability);
						break;
					default:
						break;
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
		SettleParam p = this.getParam(player.position);
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
		//取得扩张阶段指令中的子动作
		String subact = action.getAsString("subact");
		if("choose".equals(subact)){
			SettleParam p = this.getParam(player.getPosition());
			if(p!=null && !p.cards.isEmpty()){
				throw new BoardGameException("请选择弃牌进行星球的扩张!");
			}
			//选择扩张的星球
			String cardIds = action.getAsString("cardIds");
			List<RaceCard> cards = player.getCards(cardIds);
			if(cards.size()==0 || cards.size()>this.getSettleNum(player.getPosition())){
				throw new BoardGameException("扩张数量错误!");
			}
			if(RaceUtils.checkDuplicate(cards)){
				throw new BoardGameException("不能扩张相同的星球!");
			}
			for(RaceCard card : cards){
				if(card.type==CardType.MILITARY_WORLD || card.type==CardType.NON_MILITARY_WORLD){
					if(player.hasBuiltCard(card.cardNo)){
						throw new BoardGameException("不能扩张相同的星球!");
					}
				}else{
					throw new BoardGameException("扩张阶段只能选择星球!");
				}
			}
			//扩张普通星球
			p = this.createSettleParam(player, cards);
			//暂时先将这张牌打出
			player.playCards(cardIds);
			if(p.cost==0){
				//如果费用为0,则直接允许扩张
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
			String discardIds = action.getAsString("cardIds");
			List<RaceCard> discards = player.getCards(discardIds);
			SettleParam p = this.getParam(player.getPosition());
			if(p==null || p.cards.isEmpty()){
				throw new BoardGameException("请先选择要扩张的星球!");
			}
			if(discards.size()!=p.cost){
				throw new BoardGameException("弃牌数量错误,你需要弃 "+p.cost+" 张牌!");
			}
			p.discardIds = discardIds;
			//设置已回应
			this.setPlayerResponsed(gameMode, player.getPosition());
		}else if("cancel".equals(subact)){
			//取消已选择扩张的星球
			cancelCard(gameMode, player);
		}else if("pass".equals(subact)){
			cancelCard(gameMode, player);
			//设置已回应
			this.setPlayerResponsed(gameMode, player.getPosition());
		}else if("active".equals(subact)){
			//取消已选择扩张的星球
			cancelCard(gameMode, player);
			//使用卡牌的能力
			this.activeCard(gameMode, action);
			//如果已经选择了要扩张的星球,则取消选择
//			SettleParam p = this.getParam(player.getPosition());
//			if(p!=null && !p.cards.isEmpty()){
//				player.addCards(p.cards);
//				//将取消扩张的信息发送给自己
//				BgResponse res = CmdFactory.createGameResultResponse(getValidCode(), player.getPosition());
//				res.setPrivateParameter("subact", subact);
//				res.setPrivateParameter("cardIds", RaceUtils.card2String(p.cards));
//				gameMode.getGame().sendResponse(res);
//				p.cards.clear();
//				p.cost = 0;
//			}
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
		SettleParam p = this.getParam(player.getPosition());
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
		SettleAbility ability = card.getAbilityByType(this.getAbility());
		if(ability.skill==null){
			
		}else{
			switch(ability.skill){
				case DISCARD_FOR_FREE: //弃牌免费放置
					this.setTempCostAbility(player.getPosition(), ability);
					SettleParam p = this.getParam(player.getPosition());
					if(p!=null && !p.cards.isEmpty()){
						//如果已经有选择的星球,则直接降低费用
						p.cost += ability.discardCost;
						if(p.cost<0){
							p.cost = 0;
						}
					}
					actived = true;
					break;
				case DISCARD_FOR_MILITARY: //弃牌加军事力
					this.setTempMilitaryAbility(player.getPosition(), ability);
					actived = true;
					break;
				case DISCARD_HAND_FOR_MILITARY: //弃手牌加军事力
					String cardIds = action.getAsString("cardIds");
					List<RaceCard> cards = player.getCards(cardIds);
					if(cards.size()==0 || cards.size()>(ability.discardNum*ability.maxNum)){
						throw new BoardGameException("选择的卡牌数量错误!");
					}
					//将调整值保存到缓存中
					setTempMilitary(player.getPosition(), cards.size()*ability.discardMilitary);
					gameMode.getGame().discardCard(player, cardIds);
					actived = true;
					break;
				case DISCARD_TO_CONQUER_NON_MILITARY: //弃牌后可以以军事力占领非军事星球
					setTempConquerNonMilitaryAbility(player.position, ability);
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
	
	@Override
	public void onAllPlayerResponsed(RaceGameMode gameMode)
			throws BoardGameException {
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			SettleParam p = this.getParam(player.getPosition());
			if(p==null || p.cards.isEmpty()){
				//跳过扩张
				BgResponse res = CmdFactory.createGameResultResponse(getValidCode(), player.getPosition());
				res.setPublicParameter("subact", "pass");
				gameMode.getGame().sendResponse(res);
			}else{
				//执行扩张
//				if(player.hasCard(p.card.id)){
//					//如果扩张的牌还在手中则将其打出
//					gameMode.getGame().playCard(player, p.card.id);
//				}else{
					//否则的话直接将其打出
				player.addBuiltCards(p.cards);
				gameMode.getGame().sendPlayCardResponse(player, RaceUtils.card2String(p.cards));
//				}
				//将弃牌信息发送给客户端
				gameMode.getGame().discardCard(player, p.discardIds);
				gameMode.getGame().sendCardEffectResponse(player, RaceUtils.card2String(p.effectedCards));
				this.afterSettle(gameMode, player, p.cards);
			}
		}
	}
	
	/**
	 * 设置购买军事星球的能力
	 * 
	 * @param position
	 * @param ability
	 */
	private void addMilitaryBuyAbility(int position, SettleAbility ability){
		List<SettleAbility> abilities = this.getMilitaryBuyAbilities(position);
		abilities.add(ability);
	}
	
	/**
	 * 取得购买军事星球的能力
	 * 
	 * @param position
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<SettleAbility> getMilitaryBuyAbilities(int position){
		List<SettleAbility> abilities = (List<SettleAbility>)this.getPlayerParamSet(position).get("buyAbility");
		if(abilities==null){
			abilities = new ArrayList<SettleAbility>();
			this.getPlayerParamSet(position).set("buyAbility", abilities);
		}
		return abilities;
	}
	
	/**
	 * 判断该玩家是否可以购买军事星球
	 * 
	 * @param position
	 * @param card
	 * @return
	 */
	private boolean isMilitaryBuyable(int position, RaceCard card){
		List<SettleAbility> abilities = this.getMilitaryBuyAbilities(position);
		for(SettleAbility ability : abilities){
			if(ability!=null && ability.test(card)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 取得最适合指定星球的购买能力
	 * 
	 * @param position
	 * @param card
	 * @return
	 */
	private SettleAbility getFittestBuyMilitaryAbility(int position, RaceCard card){
		List<SettleAbility> abilities = this.getMilitaryBuyAbilities(position);
		SettleAbility res = null;
		for(SettleAbility ability : abilities){
			//判断该能力是否可以使用于指定的星球
			if(ability.test(card)){
				//如果该能力的优惠幅度比返回结果的大,则重新设置返回结果
				if(res==null || ability.buyCost<res.buyCost){
					res = ability;
				}
			}
		}
		return res;
	}
	
	/**
	 * 设置允许的扩张数量
	 * 
	 * @param position
	 * @param num
	 */
	private void setSettleNum(int position, int num){
		this.getPlayerParamSet(position).set("settleNum", num);
	}
	
	/**
	 * 取得允许的扩张数量,默认为1
	 * 
	 * @param position
	 */
	private int getSettleNum(int position){
		Integer res = this.getPlayerParamSet(position).getInteger("settleNum");
		return (res==null)?1:res;
	}
	
	/**
	 * 设置玩家在本回合中临时的军事力调整值
	 * 
	 * @param position
	 * @param military
	 */
	private void setTempMilitary(int position, int military){
		int res = getTempMilitary(position) + military;
		this.getPlayerParamSet(position).set("militaryValue", res);
	}
	
	/**
	 * 取得玩家在本回合中临时的军事力调整值
	 * 
	 * @param position
	 * @return
	 */
	private int getTempMilitary(int position){
		Integer res = this.getPlayerParamSet(position).getInteger("militaryValue");
		return (res==null)?0:res;
	}
	
	/**
	 * 设置玩家在本回合中临时的军事力调整能力
	 * 
	 * @param position
	 * @param ability
	 */
	private void setTempMilitaryAbility(int position, SettleAbility ability){
		this.getPlayerParamSet(position).set("military", ability);
	}
	
	/**
	 * 取得玩家在本回合中的临时军事力调整能力
	 * 
	 * @param position
	 * @return
	 */
	private SettleAbility getTempMilitaryAbility(int position){
		SettleAbility ability = (SettleAbility)this.getPlayerParamSet(position).get("military");
		return ability;
	}
	
	/**
	 * 取得玩家在本回合中扩张指定星球的全部临时军事力调整值
	 * 
	 * @param position
	 * @param card 扩张的星球
	 * @return
	 */
	private int getTempMilitaryTotal(int position, RaceCard card){
		int res = getTempMilitary(position);
		SettleAbility ability = getTempMilitaryAbility(position);
		if(ability!=null && ability.test(card)){
			res += ability.discardMilitary;
		}
		return res;
	}
	
	/**
	 * 设置玩家在本回合中临时的扩张费用调整能力
	 * 
	 * @param position
	 * @param ability
	 */
	private void setTempCostAbility(int position, SettleAbility ability){
		this.getPlayerParamSet(position).set("cost", ability);
	}
	
	/**
	 * 取得玩家在本回合中临时的扩张费用调整能力
	 * 
	 * @param position
	 * @return
	 */
	private SettleAbility getTempCostAbility(int position){
		SettleAbility ability = (SettleAbility)this.getPlayerParamSet(position).get("cost");
		return ability;
	}
	
	/**
	 * 取得玩家在本回合中扩张指定星球的临时扩张调整费用
	 * 
	 * @param position
	 * @param card 扩张的星球
	 * @return
	 */
	private int getTempCost(int position, RaceCard card){
		SettleAbility ability = getTempCostAbility(position);
		if(ability!=null && ability.test(card)){
			return ability.discardCost;
		}else{
			return 0;
		}
	}
	
	/**
	 * 设置玩家在本回合中临时的占领非军事星球的能力
	 * 
	 * @param position
	 * @param ability
	 */
	private void setTempConquerNonMilitaryAbility(int position, SettleAbility ability){
		this.getPlayerParamSet(position).set("conquerNonMilitary", ability);
	}
	
	/**
	 * 取得玩家在本回合中的临时军事力调整能力
	 * 
	 * @param position
	 * @return
	 */
	private SettleAbility getTempConquerNonMilitaryAbility(int position){
		SettleAbility ability = (SettleAbility)this.getPlayerParamSet(position).get("conquerNonMilitary");
		return ability;
	}
	
	/**
	 * 判断玩家是否可以以军事力占领指定的非军事星球
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	private boolean canConquerNonMilitaryWorld(RacePlayer player, RaceCard card){
		SettleAbility ability = this.getTempConquerNonMilitaryAbility(player.position);
		if(ability!=null && ability.test(card)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 设置购买军事星球时的费用调整能力
	 * 
	 * @param position
	 * @param ability
	 */
	private void setMilitaryBuyCostAbility(int position, SettleAbility ability){
		this.getPlayerParamSet(position).set("buyAbilityCost", ability);
	}
	
	/**
	 * 取得购买军事星球时的费用调整能力
	 * 
	 * @param position
	 * @return
	 */
	private SettleAbility getMilitaryBuyCostAbility(int position){
		return (SettleAbility)this.getPlayerParamSet(position).get("buyAbilityCost");
	}
	
	/**
	 * 取得购买军事星球时的费用调整能力
	 * 
	 * @param position
	 * @return
	 */
	private int getMilitaryBuyCost(int position){
		SettleAbility ability = this.getMilitaryBuyCostAbility(position);
		if(ability==null){
			return 0;
		}else{
			return ability.buyCost;
		}
	}
	
	/**
	 * 成功扩张后执行的动作
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @throws BoardGameException 
	 */
	private void afterSettle(RaceGameMode gameMode, RacePlayer player, List<RaceCard> cards) throws BoardGameException{
		if(player.isActionSelected(RaceActionType.SETTLE) || player.isActionSelected(RaceActionType.SETTLE_2)){
			//选择扩张指令的玩家可以在扩张后摸一张牌
			gameMode.getGame().drawCard(player, 1);
		}
		
		//扩张的牌如果是意外星球,则需要直接生产一个货物
		for(RaceCard card : cards){
			if(card.productionType==ProductionType.WINDFALL){
				gameMode.getGame().produceGood(player, card.id);
			}
			
			//检查卡牌的能力
			List<RaceCard> abilityCards = player.getCardsByAbilityType(this.getAbility());
			for(RaceCard o : abilityCards){
				//如果是本次开发的牌,则不能使用该能力
				if(cards.contains(o)){
					continue;
				}
				for(SettleAbility a : o.getAbilitiesByType(this.getAbility())){
					//建造后摸牌的能力
					if(a.afterSettleDrawNum>0){
						gameMode.getGame().drawCard(player, a.afterSettleDrawNum);
						gameMode.getGame().sendCardEffectResponse(player, o.id);
					}
				}
			}
		}
	}
	
	/**
	 * 创建玩家的开发参数
	 * 
	 * @param player
	 * @param cards
	 * @return
	 * @throws BoardGameException 
	 */
	private SettleParam createSettleParam(RacePlayer player, List<RaceCard> cards) throws BoardGameException{
		SettleParam p = new SettleParam();
		for(RaceCard card : cards){
			if(card.type==CardType.NON_MILITARY_WORLD){
				//检查玩家是否可以以军事力占领非军事星球
				if(this.canConquerNonMilitaryWorld(player, card)){
					SettleAbility ability = this.getTempConquerNonMilitaryAbility(player.position);
					int military = player.getMilitary(card) + this.getAdjustedMilitary(player, card);
					if(military>=(card.cost+ability.discardCost)){
						//可以以军事力占领该星球
						p.cards.add(card);
						continue;
					}
				}
				//非军事星球才需要计算价格
				int cost = card.cost;
				//检查牌的能力
				List<RaceCard> abilityCards = player.getCardsByAbilityType(this.getAbility());
				for(RaceCard o : abilityCards){
					for(SettleAbility a : o.getAbilitiesByType(this.getAbility())){
						//如果存在价格调整,并且建造的卡牌符合该能力的要求,则触发效果
						if(a.cost!=0 && a.test(card)){
							cost += a.cost;
							p.effectedCards.add(o);
						}
					}
				}
				//加上临时调整值
				cost += this.getTempCost(player.getPosition(), card);
				//如果费用小于0,则设为0
				if(cost<0){
					cost = 0;
				}
				p.cost += cost;
				p.cards.add(card);
			}else if(card.type==CardType.MILITARY_WORLD){
				//军事星球的计算逻辑
				int military = player.getMilitary(card) + this.getAdjustedMilitary(player, card);
				if(military<card.cost){
					//当用军事力无法占领星球时,检查是否可以购买星球
					if(this.isMilitaryBuyable(player.getPosition(), card)){
						//如果可以购买军事星球,则计算价格
						//因为可能存在多个购买军事星球的能力,取最合适的那个能力
						SettleAbility ability = this.getFittestBuyMilitaryAbility(player.position, card);
						if(ability==null){
							throw new BoardGameException("你不能购买军事星球!");
						}
						RaceCard cloneCard = card.clone();
						//克隆相同属性的普通星球,价格是军事星球价格加上购买能力的调整值
						cloneCard.type = CardType.NON_MILITARY_WORLD;
						cloneCard.cost += ability.buyCost;
						//计算购买该军事星球的价格调整
						int cost = cloneCard.cost;
						//检查牌的能力
						List<RaceCard> abilityCards = player.getCardsByAbilityType(this.getAbility());
						for(RaceCard o : abilityCards){
							for(SettleAbility a : o.getAbilitiesByType(this.getAbility())){
								//如果存在价格调整,并且建造的卡牌符合该能力的要求,则触发效果
								if(a.cost!=0 && a.test(cloneCard)){
									cost += a.cost;
									p.effectedCards.add(o);
								}
							}
						}
						//加上临时调整值
						cost += this.getTempCost(player.getPosition(), cloneCard);
						//加上购买军事星球的费用调整值
						cost += this.getMilitaryBuyCost(player.position);
						//如果费用小于0,则设为0
						if(cost<0){
							cost = 0;
						}
						p.cost += cost;
						p.cards.add(card);
					}else{
						throw new BoardGameException("你的军事力不足以占领该星球!");
					}
				}else{
					p.cards.add(card);
				}
			}
		}
		this.setParam(player.getPosition(), p);
		return p;
	}
	
	/**
	 * 取得玩家在本次扩张阶段的临时军事力修正值
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	private int getAdjustedMilitary(RacePlayer player, RaceCard card){
		int res = 0;
		//加上临时修正值
		res += this.getTempMilitaryTotal(player.getPosition(), card);
		//加上特殊能力值
		List<RaceCard> cards = player.getCardsByAbilityType(this.getAbility());
		for(RaceCard o : cards){
			for(SettleAbility a : o.getAbilitiesByType(this.getAbility())){
				if(a.military!=0 && a.test(card)){
					//调整修正值
					res += a.military;
				}
			}
		}
		return res;
	}
	
	class SettleParam{
		int cost = 0;
		List<RaceCard> cards = new ArrayList<RaceCard>();
		String discardIds;
		List<RaceCard> effectedCards = new ArrayList<RaceCard>();
	}

}
