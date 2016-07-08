package com.f14.TS.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.TSResourceManager;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCardDeck;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSConsts;
import com.f14.TS.consts.TSPhase;
import com.f14.bg.exception.BoardGameException;

/**
 * TS的卡牌管理器
 * 
 * @author F14eagle
 *
 */
public class CardManager {
	protected static final Logger log = Logger.getLogger(CardManager.class);
	protected TSGameMode gameMode;
	protected Map<TSPhase, TSCardDeck> cardDecks = new LinkedHashMap<TSPhase, TSCardDeck>();
	protected TSCardDeck playingDeck;
	protected TSCardDeck trashDeck;
	protected Map<Integer, TSCard> cardNoCache = new HashMap<Integer, TSCard>();
	/**
	 * 中国牌
	 */
	public TSCard chinaCard;
	/**
	 * 中国牌所有者
	 */
	public SuperPower chinaOwner;
	/**
	 * 中国牌是否可用
	 */
	public boolean chinaCanUse;
	/**
	 * 背叛者牌
	 */
	protected TSCard defactorCard;

	public CardManager(TSGameMode gameMode){
		this.gameMode = gameMode;
		this.init();
	}
	
	public TSCardDeck getPlayingDeck() {
		return playingDeck;
	}
	
	public TSCardDeck getTrashDeck() {
		return this.trashDeck;
	}

	public TSCard getChinaCard() {
		return chinaCard;
	}

	public TSCard getDefactorCard() {
		return defactorCard;
	}

	/**
	 * 初始化
	 */
	private void init(){
		//将所有的卡牌按阶段分类
		TSResourceManager res = this.gameMode.getGame().getResourceManager();
		Collection<TSCard> cards = res.getCardsInstanceByConfig(this.gameMode.getGame().getConfig());
		for(TSCard card : cards){
			TSCardDeck deck = this.getCardDeck(card.phase);
			deck.addCard(card);
			//检查并设置中国牌
			if(TSConsts.CHINA_CARD_NO==card.tsCardNo){
				this.chinaCard = card;
			}
			//检查并设置背叛者
			if(TSConsts.DEFACTOR_CARD_NO==card.tsCardNo){
				this.defactorCard = card;
			}
			this.addToCache(card);
			
		}
		
		//初始化当前牌堆,将早期牌组放入当前牌堆
		TSCardDeck d = this.getCardDeck(TSPhase.EARLY);
		//该牌堆摸完时将自动重洗弃牌堆
		this.playingDeck = new TSCardDeck(d.getCards(), true);
		//将中国牌从牌堆中移除
		try {
			this.playingDeck.takeCard(this.chinaCard.id);
		} catch (BoardGameException e) {
			log.error("没有找到中国牌!");
		}
		
		//初始化废牌堆
		this.trashDeck = new TSCardDeck();
	}
	
	/**
	 * 取得指定阶段的牌组
	 * 
	 * @param phase
	 * @return
	 */
	private TSCardDeck getCardDeck(TSPhase phase){
		TSCardDeck deck = this.cardDecks.get(phase);
		if(deck==null){
			deck = new TSCardDeck();
			this.cardDecks.put(phase, deck);
		}
		return deck;
	}
	
	/**
	 * 将指定的时期的牌组加入到牌堆并重洗
	 * 
	 * @param phase
	 */
	public void addToPlayingDeck(TSPhase phase){
		TSCardDeck deck = this.getCardDeck(phase);
		this.getPlayingDeck().addCards(deck.getCards());
		//该次洗牌不重洗弃牌堆中的牌
		this.getPlayingDeck().shuffle();
	}
	
	/**
	 * 改变中国牌的所属玩家和使用状态
	 * 
	 * @param player
	 * @param canUse
	 */
	public void changeChinaCardOwner(TSPlayer player, boolean canUse){
		this.chinaOwner = player.superPower;
		this.chinaCanUse = canUse;
	}
	
	/**
	 * 将卡牌对象加入到缓存中
	 * 
	 * @param card
	 */
	protected void addToCache(TSCard card){
		this.cardNoCache.put(card.tsCardNo, card);
	}
	
	/**
	 * 按照cardNo取得卡牌对象
	 * 
	 * @param cardNo
	 * @return
	 */
	public TSCard getCardByCardNo(int cardNo){
		return this.cardNoCache.get(cardNo);
	}
}
