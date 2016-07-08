package com.f14.innovation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f14.bg.common.ListMap;
import com.f14.innovation.achieve.InnoAchieveChecker;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.InnoCardDeck;
import com.f14.innovation.component.InnoCardGroup;
import com.f14.innovation.consts.InnoAchieveTrigType;

/**
 * Innovation的成就管理器
 * 
 * @author F14eagle
 *
 */
public class InnoAchieveManager {
	protected InnoGameMode gameMode;
	protected InnoCardGroup achieveCards;
	protected InnoCardDeck specialAchieveCards;
	protected ListMap<InnoAchieveTrigType, InnoAchieveChecker> achieveCheckers;
	protected ListMap<InnoCard, InnoAchieveChecker> cardCheckerRelats;
	/**
	 * 临时存放检查器的list
	 */
	private List<InnoAchieveChecker> activeCheckers = new ArrayList<InnoAchieveChecker>();
	
	public InnoAchieveManager(InnoGameMode gameMode){
		this.gameMode = gameMode;
		this.init();
	}
	
	protected void init(){
		this.achieveCards = new InnoCardGroup();
		this.specialAchieveCards = new InnoCardDeck();
		this.achieveCheckers = new ListMap<InnoAchieveTrigType, InnoAchieveChecker>();
		this.cardCheckerRelats = new ListMap<InnoCard, InnoAchieveChecker>();
	}
	
	public InnoCardGroup getAchieveCards() {
		return achieveCards;
	}

	public InnoCardDeck getSpecialAchieveCards() {
		return specialAchieveCards;
	}

	/**
	 * 装载所有成就牌
	 * 
	 * @param achieveCards
	 */
	public void loadAchieveCards(Collection<InnoCard> achieveCards){
		for(InnoCard card : achieveCards){
			if(!card.getSpecial()){
				//将普通的成就牌添加到成就牌堆中
				this.achieveCards.addCard(card);
			}else{
				//特殊成就添加到特殊成就牌堆中
				this.addSpecialAchieve(card);
			}
		}
	}
	
	/**
	 * 添加特殊成就牌
	 * 
	 * @param card
	 */
	private void addSpecialAchieve(InnoCard card){
		this.specialAchieveCards.addCard(card);
		if(card.getAchieveAbility()!=null){
			InnoAchieveChecker checker = InnoClassFactory.createAchieveChecker(card.getAchieveAbility(), gameMode);
			for(InnoAchieveTrigType trigType : card.getAchieveAbility().getTrigTypes()){
				this.getAchieveCheckers(trigType).add(checker);
				this.getRelatAchieveCheckers(card).add(checker);
			}
		}
	}
	
	/**
	 * 移除特殊成就
	 * 
	 * @param card
	 */
	public void removeSpecialAchieve(InnoCard card){
		this.specialAchieveCards.removeCard(card);
		for(InnoAchieveChecker e : this.getRelatAchieveCheckers(card)){
			this.achieveCheckers.remove(e);
		}
		this.cardCheckerRelats.removeKey(card);
	}
	
	/**
	 * 取得成就检查器的list
	 * 
	 * @param trigType
	 * @return
	 */
	private List<InnoAchieveChecker> getAchieveCheckers(InnoAchieveTrigType trigType){
		return this.achieveCheckers.getList(trigType);
	}
	
	/**
	 * 取得成就牌对应的成就检查器list
	 * 
	 * @param card
	 * @return
	 */
	private List<InnoAchieveChecker> getRelatAchieveCheckers(InnoCard card){
		return this.cardCheckerRelats.getList(card);
	}
	
	/**
	 * 取得成就检查器对应的成就牌
	 * 
	 * @param checker
	 * @return
	 */
	private InnoCard getRelatCard(InnoAchieveChecker checker){
		List<InnoCard> cards = this.cardCheckerRelats.getKeyByValue(checker);
		if(cards.isEmpty()){
			return null;
		}else{
			return cards.get(0);
		}
	}
	
	/**
	 * 执行特殊成就的检查
	 * 
	 * @param trigType
	 * @param player
	 */
	public void executeAchieveChecker(InnoAchieveTrigType trigType, InnoPlayer player){
		activeCheckers.clear();
		for(InnoAchieveChecker e : this.getAchieveCheckers(trigType)){
			if(e.check(player)){
				activeCheckers.add(e);
			}
		}
		for(InnoAchieveChecker e : this.activeCheckers){
			//如果检查通过,则玩家得到成就
			InnoCard card = this.getRelatCard(e);
			gameMode.getGame().playerAddSpecialAchieveCard(player, card);
		}
	}
	
}
