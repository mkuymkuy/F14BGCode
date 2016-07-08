package com.f14.RFTG.listener;

import java.util.List;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.Ability;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.card.RaceDeck;
import com.f14.RFTG.consts.ProductionType;
import com.f14.RFTG.consts.StartWorldType;
import com.f14.RFTG.manager.RaceResourceManager;
import com.f14.RFTG.mode.RaceGameMode;
import com.f14.RFTG.network.CmdConst;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;

public class StartingWorldListener extends RaceActionListener {

	@Override
	protected <A extends Ability> Class<A> getAbility() {
		return null;
	}

	@Override
	protected int getValidCode() {
		return CmdConst.GAME_CODE_STARTING_WORLD;
	}
	
	@Override
	protected void beforeStartListen(RaceGameMode gameMode)
			throws BoardGameException {
		//为所有玩家抽取2个待选起始星球
		RaceResourceManager rm = gameMode.getGame().getResourceManager();
		List<RaceCard> startWorlds = rm.getStartCards(gameMode.getGame().getConfig());
		RaceDeck normalDeck = new RaceDeck();
		RaceDeck militaryDeck = new RaceDeck();
		//按照起始星球类型分2个牌堆
		for(RaceCard card : startWorlds){
			if(card.startWorldType==StartWorldType.NORMAL){
				normalDeck.getCards().add(card);
			}else if(card.startWorldType==StartWorldType.MILITARY){
				militaryDeck.getCards().add(card);
			}
		}
		normalDeck.shuffle();
		militaryDeck.shuffle();
		//为所有玩家从2个牌堆中各抽取1张牌待选
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			//为玩家创建星球参数
			WorldParam p = this.createWorldParam(player);
			p.startWorlds.getCards().add(normalDeck.draw());
			p.startWorlds.getCards().add(militaryDeck.draw());
		}
		//将剩下的起始星球和其他牌洗混后,发给玩家起始手牌
		//将其他所有的牌和选剩下的起始星球牌作为默认牌堆
		List<RaceCard> defaultCards = rm.getOtherCards(gameMode.getGame().getConfig());
		defaultCards.addAll(normalDeck.getCards());
		defaultCards.addAll(militaryDeck.getCards());
		gameMode.raceDeck.setDefaultCards(defaultCards);
		gameMode.raceDeck.reset();
		//给所有玩家发起始手牌
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			WorldParam p = this.getParam(player.position);
			List<RaceCard> cards = gameMode.draw(gameMode.getStartNumber());
			p.hands.getCards().addAll(cards);
		}
	}
	
	@Override
	protected BgResponse createStartListenCommand(RaceGameMode gameMode, Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		//将用户选择星球的参数发送到客户端
		WorldParam p = this.getParam(player.position);
		res.setPrivateParameter("startWorldIds", BgUtils.card2String(p.startWorlds.getCards()));
		res.setPrivateParameter("handIds", BgUtils.card2String(p.hands.getCards()));
		return res;
	}
	
	@Override
	protected void doAction(RaceGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		RacePlayer player = action.getPlayer();
		String startWorldIds = action.getAsString("startWorldIds");
		String handIds = action.getAsString("handIds");
		WorldParam p = this.getParam(player.position);
		List<RaceCard> discardWorlds = p.startWorlds.getCards(startWorldIds);
		if(discardWorlds.size()!=1){
			throw new BoardGameException("你必须选择1个起始星球!");
		}
		RaceCard startWorld = this.getStartWorld(p.startWorlds.getCards(), discardWorlds);
		if(startWorld==null){
			throw new BoardGameException("未知的起始星球!");
		}
		List<RaceCard> discardHands = p.hands.getCards(handIds);
		int num = p.hands.size() - startWorld.startHandNum;
		if(discardHands.size()!=num){
			throw new BoardGameException("弃牌数量错误,你需要弃 "+num+" 张牌!");
		}
		p.startWorld = startWorld;
		gameMode.discard(discardWorlds);
		//玩家得到起始手牌
		p.hands.getCards().removeAll(discardHands);
		gameMode.getGame().getCard(player, p.hands.getCards());
		gameMode.discard(discardHands);
		this.setPlayerResponsed(gameMode, player.position);
	}
	
	@Override
	public void onAllPlayerResponsed(RaceGameMode gameMode)
			throws BoardGameException {
		//展示所有玩家的起始星球
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			WorldParam p = this.getParam(player.position);
			player.addBuiltCard(p.startWorld);
			gameMode.getGame().sendDirectPlayCardResponse(player, p.startWorld.id);
			//如果该星球是意外星球,则直接生产一个货物
			if(p.startWorld.productionType==ProductionType.WINDFALL){
				gameMode.getGame().produceGood(player, p.startWorld.id);
			}
		}
	}
	
	/**
	 * 取得选择的起始星球
	 * 
	 * @param startWorlds
	 * @param discardWorlds
	 * @return
	 */
	private RaceCard getStartWorld(List<RaceCard> startWorlds, List<RaceCard> discardWorlds){
		for(RaceCard c1 : startWorlds){
			for(RaceCard c2 : discardWorlds){
				if(c1!=c2){
					return c1;
				}
			}
		}
		return null;
	}
	
	/**
	 * 为玩家创建选择星球的参数
	 * 
	 * @param player
	 * @return
	 */
	private WorldParam createWorldParam(RacePlayer player){
		WorldParam p = new WorldParam();
		this.setParam(player.position, p);
		return p;
	}
	
	class WorldParam{
		RaceDeck startWorlds = new RaceDeck();
		RaceDeck hands = new RaceDeck();
		
		RaceCard startWorld;
	}

}
