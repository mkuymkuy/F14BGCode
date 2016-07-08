package com.f14.RFTG.listener;

import java.util.ArrayList;
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
 * 回合结束时弃牌的监听器
 * 
 * @author F14eagle
 *
 */
public class RoundDiscardActionListener extends RaceActionListener {

	@Override
	protected int getValidCode() {
		return CmdConst.GAME_CODE_ROUND_DISCARD;
	}
	
	@Override
	protected <A extends Ability> Class<A> getAbility() {
		return null;
	}
	
	@Override
	protected void initListeningPlayers(RaceGameMode gameMode) {
		//检查所有玩家的手牌是否超过上限,只需要给超过上限的玩家发送弃牌指令
		for(RacePlayer p : gameMode.getGame().getValidPlayers()){
			if(p.getHandSize()>gameMode.getHandsLimit(p)){
				this.setNeedPlayerResponse(p.position, true);
			}else{
				this.setNeedPlayerResponse(p.position, false);
			}
		}
	}
	
	@Override
	protected void onStartListen(RaceGameMode gameMode)
			throws BoardGameException {
		List<BgResponse> res = new ArrayList<BgResponse>();
		BgResponse r = CmdFactory.createGameResponse(getValidCode(), -1);
		for(RacePlayer player : gameMode.getGame().getValidPlayers()){
			r.setPublicParameter(player.getPosition()+"", (player.getHandSize()-gameMode.getHandsLimit(player)));
		}
		res.add(r);
		gameMode.getGame().sendResponse(res);
	}
	
	@Override
	protected void doAction(RaceGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		RacePlayer player = action.getPlayer();
		String discardIds = action.getAsString("cardIds");
		List<RaceCard> discards = player.getCards(discardIds);
		if((player.getHandSize() - discards.size()) != gameMode.getHandsLimit(player)){
			throw new BoardGameException("弃牌数量错误,你需要弃 "+(player.getHandSize()-gameMode.getHandsLimit(player))+" 张牌!");
		}
		player.roundDiscardNum = discards.size();
		//将弃牌信息发送到客户端
		gameMode.getGame().discardCard(player, discardIds);
		//将该玩家的是否回应设置为已回应
		this.setPlayerResponsed(gameMode, action.getPlayer().getPosition());
	}
	
}
