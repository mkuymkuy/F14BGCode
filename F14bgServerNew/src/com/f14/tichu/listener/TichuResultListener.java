package com.f14.tichu.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.tichu.TichuGameMode;
import com.f14.tichu.TichuPlayer;
import com.f14.tichu.TichuPlayerGroup;
import com.f14.tichu.consts.TichuGameCmd;
import com.f14.tichu.consts.TichuType;

public class TichuResultListener extends TichuActionListener {
	protected TichuGameMode gameMode;
	protected BgResponse resultResponse;
	
	public TichuResultListener(TichuGameMode gameMode){
		this.gameMode = gameMode;
		this.createResultResponse();
	}
	
	/**
	 * 创建回合结果的参数
	 */
	protected void createResultResponse(){
		BgResponse res = this.createSubactResponse(null, "loadParam");
		//将所有玩家按照排名排序
		List<TichuPlayer> rankPlayers = new ArrayList<TichuPlayer>();
		rankPlayers.addAll(gameMode.getGame().getValidPlayers());
		Collections.sort(rankPlayers, new RankComparator());
		
		if(gameMode.isFirendlyPlayer(rankPlayers.get(0), rankPlayers.get(1))){
			//检查前面的2个玩家是否是同一组,如果是,则是双关
			//bothCatchGroup = rankPlayers.get(0).groupIndex;
			//双关则是+200分
			TichuPlayerGroup group = gameMode.getPlayerGroup(rankPlayers.get(0));
			group.bothCatchScore = 200;
			//res.setPublicParameter("bothCatch" + bothCatchGroup, 200);
		}else{
			//如果不是双关,则需要将最后1名的手牌中的分数给对家
			TichuPlayer lastPlayer = rankPlayers.get(3);
			TichuPlayerGroup group = gameMode.getOppositeGroup(lastPlayer);
			group.addScore = lastPlayer.getHandScore();
			
			//最后1名的分数给头家
			rankPlayers.get(0).score += lastPlayer.score;
			lastPlayer.score = 0;
		}
		//计算所有玩家的分数
		List<Map<String, Object>> playerResults = new ArrayList<Map<String,Object>>();
		for(TichuPlayer player : gameMode.getGame().getValidPlayers()){
			//如果叫了大地主,则200分,小地主100分
			if(player.tichuType==TichuType.BIG_TICHU){
				player.tichuScore = 200 * (player.rank==1?1:-1);
			}else if(player.tichuType==TichuType.SMALL_TICHU){
				player.tichuScore = 100 * (player.rank==1?1:-1);
			}
			Map<String, Object> map = player.toMap();
			playerResults.add(map);
		}
		
		//计算回合得分
		for(TichuPlayerGroup group : gameMode.getGroups()){
			group.calculateRoundScore();
		}
		List<Map<String, Object>> groupResults = BgUtils.toMapList(gameMode.getGroups());
		
		res.setPublicParameter("playerResults", playerResults);
		res.setPublicParameter("groupResults", groupResults);
		
		this.resultResponse = res;
	}
	
	@Override
	protected int getValidCode() {
		return TichuGameCmd.GAME_CODE_ROUND_RESULT;
	}
	
	@Override
	protected void sendPlayerListeningInfo(TichuGameMode gameMode,
			Player receiver) {
		super.sendPlayerListeningInfo(gameMode, receiver);
		//发送回合得分的指令
		gameMode.getGame().sendResponse(receiver, resultResponse);
	}
	
	@Override
	protected void doAction(TichuGameMode gameMode, BgAction action)
			throws BoardGameException {
		TichuPlayer player = action.getPlayer();
		this.setPlayerResponsed(gameMode, player);
	}

	/**
	 * 排名排序对象
	 * 
	 * @author F14eagle
	 *
	 */
	class RankComparator implements Comparator<TichuPlayer>{

		@Override
		public int compare(TichuPlayer o1, TichuPlayer o2) {
			//rank=0则被双关,往后排
			int i1 = o1.rank!=0?o1.rank:5;
			int i2 = o2.rank!=0?o2.rank:5;
			if(i1>i2){
				return 1;
			}else if(i1<i2){
				return -1;
			}else{
				return 0;
			}
		}
		
	}
}
