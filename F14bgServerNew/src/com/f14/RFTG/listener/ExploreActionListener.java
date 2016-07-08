package com.f14.RFTG.listener;

import java.util.ArrayList;
import java.util.List;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.ExploreAbility;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.consts.RaceActionType;
import com.f14.RFTG.consts.Skill;
import com.f14.RFTG.mode.RaceGameMode;
import com.f14.RFTG.network.CmdConst;
import com.f14.RFTG.network.CmdFactory;
import com.f14.RFTG.utils.RaceUtils;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 探索阶段的监听器
 * 
 * @author F14eagle
 *
 */
public class ExploreActionListener extends RaceActionListener {

	@Override
	protected int getValidCode() {
		return CmdConst.GAME_CODE_EXPLORE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected Class<ExploreAbility> getAbility() {
		return ExploreAbility.class;
	}
	
	@Override
	protected void onStartListen(RaceGameMode gameMode) throws BoardGameException {
		//设置所有玩家的摸牌和弃牌数量
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			ExploreParam p = this.createExploreParam(player);
			p.drawnCards = gameMode.draw(p.drawNum);
			//如果有特殊能力生效的卡,则把这些卡返回到客户端
			if(!p.effectedCards.isEmpty()){
				gameMode.getGame().sendCardEffectResponse(player, RaceUtils.card2String(p.effectedCards));
			}
			player.addCards(p.drawnCards);
			gameMode.getGame().sendDrawCardResponse(player, RaceUtils.card2String(p.drawnCards));
			//将摸牌和弃牌的数量发送到客户端
			BgResponse res = CmdFactory.createGameResponse(this.getValidCode(), player.getPosition());
			res.setPrivateParameter("cardIds", RaceUtils.card2String(p.drawnCards));
			res.setPrivateParameter("exploreHand", p.exploreHand);
			gameMode.getGame().sendResponse(player, res);
		}
		//刷新牌堆数量
		gameMode.getGame().sendRefreshDeckResponse();
	}
	
	@Override
	protected void onReconnect(RaceGameMode gameMode, Player player)
			throws BoardGameException {
		//将摸牌和弃牌的数量发送到客户端
		ExploreParam p = this.getParam(player.position);
		if(p!=null){
			BgResponse res = CmdFactory.createGameResponse(this.getValidCode(), player.getPosition());
			res.setPrivateParameter("cardIds", RaceUtils.card2String(p.drawnCards));
			res.setPrivateParameter("exploreHand", p.exploreHand);
			gameMode.getGame().sendResponse(player, res);
		}
	}

	@Override
	protected void doAction(RaceGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		RacePlayer player = action.getPlayer();
		ExploreParam p = this.getParam(player.getPosition());
		String cardIds = action.getAsString("cardIds");
		List<RaceCard> cards = player.getCards(cardIds);
		if(cards.size()!=p.discardNum){
			throw new BoardGameException("弃牌数量不正确,你需要弃 "+p.discardNum+" 张牌!");
		}
		if(!p.exploreHand && !p.drawnCards.containsAll(cards)){
			throw new BoardGameException("不能从手牌中选择弃牌!");
		}
		//将丢弃的牌放入弃牌堆
		gameMode.getGame().discardCard(player, cardIds);
		//将玩家的回应状态设为已回应
		this.setPlayerResponsed(gameMode, action.getPlayer().getPosition());
	}
	
	/**
	 * 创建玩家的探索参数
	 * 
	 * @param player
	 * @return
	 */
	private ExploreParam createExploreParam(RacePlayer player){
		ExploreParam p = new ExploreParam();
		if(player.isActionSelected(RaceActionType.EXPLORE_1)){
			p.drawNum += 1;
			p.keepNum += 1;
		}
		if(player.isActionSelected(RaceActionType.EXPLORE_2)){
			p.drawNum += 5;
		}
		//将玩家该阶段的能力牌的效果应用到参数中
		List<RaceCard> cards = player.getCardsByAbilityType(this.getAbility());
		for(RaceCard o : cards){
			ExploreAbility a = o.getAbilityByType(this.getAbility());
			if(a.drawNum!=0 || a.keepNum!=0){
				p.drawNum += a.drawNum;
				p.keepNum += a.keepNum;
				p.effectedCards.add(o);
			}
			//设置是否可以探索手牌的参数
			if(a.getSkill()==Skill.EXPLORE_HAND){
				p.exploreHand = true;
			}
		}
		p.discardNum = p.drawNum - p.keepNum;
		this.setParam(player.getPosition(), p);
		return p;
	}
	
	/**
	 * 探索用的参数
	 * 
	 * @author F14eagle
	 *
	 */
	class ExploreParam{
		int drawNum = 2;
		int keepNum = 1;
		int discardNum = 1;
		List<RaceCard> drawnCards;
		List<RaceCard> effectedCards = new ArrayList<RaceCard>();
		boolean exploreHand = false;
	}

}
