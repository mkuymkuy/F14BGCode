package com.f14.RFTG.listener;

import java.util.ArrayList;
import java.util.List;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.Ability;
import com.f14.RFTG.consts.GameState;
import com.f14.RFTG.consts.RaceActionType;
import com.f14.RFTG.mode.RaceGameMode;
import com.f14.RFTG.network.CmdConst;
import com.f14.RFTG.network.CmdFactory;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.utils.StringUtils;

/**
 * 银河竞逐选择行动的监听器基类
 * 
 * @author F14eagle
 *
 */
public class ChooseActionListener extends RaceActionListener {

	@Override
	protected int getValidCode() {
		return CmdConst.GAME_CODE_CHOOSE_ACTION;
	}
	
	@Override
	protected <A extends Ability> Class<A> getAbility() {
		return null;
	}
	
	@Override
	protected void doAction(RaceGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doAction(gameMode, action);
		RacePlayer player = action.getPlayer();
		//取得行动
		String actionCode = action.getAsString("actionCode");
		if(StringUtils.isEmpty(actionCode)){
			throw new BoardGameException("请选择行动!");
		}
		String[] actions = actionCode.split(",");
		//如果行动数大于1,则检查是否直接选择了第2张开发或扩张指令,如果是,则替换成第1张
		if(gameMode.getActionNum()>1){
			if(StringUtils.indexOfArray(actions, RaceActionType.DEVELOP.toString())<0){
				int i = StringUtils.indexOfArray(actions, RaceActionType.DEVELOP_2.toString());
				if(i>=0){
					actions[i] = RaceActionType.DEVELOP.toString();
				}
			}
			if(StringUtils.indexOfArray(actions, RaceActionType.SETTLE.toString())<0){
				int i = StringUtils.indexOfArray(actions, RaceActionType.SETTLE_2.toString());
				if(i>=0){
					actions[i] = RaceActionType.SETTLE.toString();
				}
			}
		}
		ChooseParam p = this.getParam(player.position);
		if(gameMode.getState()!=GameState.CHOOSE_ACTION || player.state!=GameState.CHOOSE_ACTION){
			throw new BoardGameException("状态错误,不能执行该行动!");
		}
		if(p!=null && !p.types.isEmpty()){
			throw new BoardGameException("不能重复选择行动!");
		}
		if(actions.length!=gameMode.getActionNum()){
			throw new BoardGameException("选择行动出错!");
		}
		for(String acode : actions){
			RaceActionType actionType = RaceActionType.getActionType(acode);
			//判断游戏状态是否允许执行该行动
			if(!gameMode.isActionValid(actionType)){
				throw new BoardGameException("无效的行动!");
			}
		}
		p = this.createChooseParam(player);
		//选择行动并通知客户端
		for(String acode : actions){
			RaceActionType actionType = RaceActionType.getActionType(acode);
			p.types.add(actionType);
		}
		//设置玩家行动完成
		this.setPlayerResponsed(gameMode, player.getPosition());
	}

	@Override
	public void onAllPlayerResponsed(RaceGameMode gameMode)
			throws BoardGameException {
		//将所有玩家选择的行动返回到客户端
		for(RacePlayer o : gameMode.getGame().getValidPlayers()){
			ChooseParam p = this.getParam(o.position);
			o.getActionTypes().addAll(p.types);
			BgResponse res = CmdFactory.createGameResultResponse(CmdConst.GAME_CODE_SHOW_ACTION, o.getPosition());
			res.setPublicParameter("actionTypes", StringUtils.list2String(o.getActionTypes()));
			gameMode.getGame().sendResponse(res);
		}
	}
	
	/**
	 * 创建玩家的选择行动参数
	 * 
	 * @param player
	 * @param cards
	 * @return
	 */
	private ChooseParam createChooseParam(RacePlayer player){
		ChooseParam p = new ChooseParam();
		this.setParam(player.getPosition(), p);
		return p;
	}

	class ChooseParam{
		List<RaceActionType> types = new ArrayList<RaceActionType>();
	}
}
