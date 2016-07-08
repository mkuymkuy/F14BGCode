package com.f14.RFTG.listener;

import java.util.List;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.Ability;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.mode.RaceGameMode;
import com.f14.RFTG.network.CmdConst;
import com.f14.RFTG.network.CmdFactory;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;

/**
 * 游戏开始时弃牌的监听器
 * 
 * @author F14eagle
 *
 */
public class StartingDiscardListener extends RaceActionListener {

	@Override
	protected int getValidCode() {
		return CmdConst.GAME_CODE_STARTING_DISCARD;
	}
	
	@Override
	protected <A extends Ability> Class<A> getAbility() {
		return null;
	}
	
	@Override
	protected void onStartListen(RaceGameMode gameMode)
			throws BoardGameException {
		//将所有玩家需要丢弃卡牌的数量返回到客户端
		BgResponse res = CmdFactory.createGameResponse(this.getValidCode(), -1);
		for(RacePlayer o : gameMode.getGame().getValidPlayers()){
			res.setPublicParameter(o.getPosition()+"", o.getStartWorld().startHandNum);
		}
		gameMode.getGame().sendResponse(res);
	}
	
	@Override
	protected void doAction(RaceGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		RacePlayer player = action.getPlayer();
		String cardIds = action.getAsString("cardIds");
		List<RaceCard> discards = player.getCards(cardIds);
		if((player.getHandSize() - discards.size()) != player.getStartWorld().startHandNum){
			throw new BoardGameException("弃牌数量错误,你需要弃 "+(player.getHandSize() - player.getStartWorld().startHandNum)+" 张牌!");
		}
		//将弃牌信息发送到客户端
		gameMode.getGame().discardCard(player, cardIds);
		//将该玩家的是否回应设置为已回应
		this.setPlayerResponsed(gameMode, action.getPlayer().getPosition());
	}
	
}
