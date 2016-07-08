package com.f14.TS.listener;

import java.util.List;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.ability.TSAbility;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ChoiceInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 选择国家并执行相应行动的监听器
 * 
 * @author F14eagle
 *
 */
public class TSChoiceListener extends TSParamInterruptListener {
	
	public TSChoiceListener(TSPlayer trigPlayer, TSGameMode gameMode, ChoiceInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ChoiceInitParam getInitParam(){
		return super.getInitParam();
	}
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_CHOICE;
	}
	
	@Override
	protected void sendStartListenCommand(TSGameMode gameMode, Player player,
			Player receiver) {
		super.sendStartListenCommand(gameMode, player, receiver);
		//只会向指定自己发送该监听信息
		this.sendChoiceParamInfo(gameMode, player);
	}
	
	/**
	 * 发送选项参数
	 * 
	 * @param gameMode
	 * @param p
	 */
	protected void sendChoiceParamInfo(TSGameMode gameMode, Player p){
		BgResponse res = this.createSubactResponse(p, "choiceParam");
		TSCard card = this.getCard();
		res.setPublicParameter("choice1", card.abilityGroup.descr1);
		res.setPublicParameter("choice2", card.abilityGroup.descr2);
		gameMode.getGame().sendResponse(p, res);
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		for(TSPlayer player : gameMode.getGame().getValidPlayers()){
			ChoiceParam param = new ChoiceParam(player);
			this.setParam(player, param);
		}
	}
	
	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		int choice = action.getAsInt("choice");
		//暂时只能接受1或2选项
		if(choice!=1 && choice!=2){
			throw new BoardGameException("请选择行动!");
		}
		//执行所选的能力
		TSCard card = this.getCard();
		List<TSAbility> abilities = null;
		switch(choice){
		case 1:
			abilities = card.abilityGroup.getAbilitiesGroup1();
			gameMode.getReport().playerSelectChoice(player, card.abilityGroup.descr1);
			break;
		case 2:
			abilities = card.abilityGroup.getAbilitiesGroup2();
			gameMode.getReport().playerSelectChoice(player, card.abilityGroup.descr2);
			break;
		}
		if(abilities!=null){
			//处理所选择的能力,被插入的监听器为本监听器中断的监听器
			//执行的玩家为targetPower
			TSPlayer target = gameMode.getGame().getPlayer(this.getInitParam().targetPower);
			gameMode.getGame().processAbilities(abilities, target, card, this.getInterruptedListener());
		}
		//结束选择
		this.setPlayerResponsed(gameMode, player);
	}
	
//	@Override
//	protected void onInterrupteListenerOver(TSGameMode gameMode,
//			InterruptParam param) throws BoardGameException {
//		super.onInterrupteListenerOver(gameMode, param);
//		//如果中断处理完成,则设置所有玩家已回应
//		if(!this.isInterruped()){
//			this.setAllPlayerResponsed(gameMode);
//		}
//	}
	
	/**
	 * 选择参数
	 * 
	 * @author F14eagle
	 *
	 */
	class ChoiceParam{
		TSPlayer player;
		int choice;
		
		ChoiceParam(TSPlayer player){
			this.player = player;
		}
	}
	
}
