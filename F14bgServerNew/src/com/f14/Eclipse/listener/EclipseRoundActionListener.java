package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.Eclipse.EclipsePlayer;
import com.f14.Eclipse.anim.EclipseAnimParamFactory;
import com.f14.Eclipse.consts.ActionType;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;

/**
 * Eclipse回合行动的监听器
 *
 * @author f14eagle
 */
public abstract class EclipseRoundActionListener extends EclipseInterruptListener {
	protected int subPhase = 0;
	protected boolean actived = false;
	protected int actionCount;

	public EclipseRoundActionListener(EclipsePlayer trigPlayer) {
		super(trigPlayer);
		this.initActionCount();
	}

	/**
	 * 初始化可执行的行动数量
	 */
	protected void initActionCount(){
		ActionType actionType = this.getActionType();
		if(actionType!=null){
			this.actionCount = this.trigPlayer.getActionCount(actionType);
		}
	}
	
	/**
	 * 取得行动类型
	 * 
	 * @return
	 */
	protected abstract ActionType getActionType();
	
	@Override
	protected BgResponse createStartListenCommand(EclipseGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		res.setPublicParameter("subPhase", subPhase);
		return res;
	}

	@Override
	protected void doConfirm(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.actived){
			//如果行动已经激活过,则结束行动
			EclipsePlayer player = action.getPlayer();
			this.checkActionCount(gameMode, player);
		}else{
			//否则等于选择取消
			this.doCancel(gameMode, action);
		}
	}
	
	@Override
	protected void doCancel(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		if(this.actived){
			//如果该行动已经激活过,则只能继续进行该行动
			this.doReset(gameMode, action);
			this.sendStartListenCommand(gameMode, trigPlayer, trigPlayer);
		}else{
			//如果没有,则可以关闭该监听器
			this.setPlayerResponsed(gameMode, trigPlayer);
		}
	}
	
	@Override
	protected void doReset(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		super.doReset(gameMode, action);
		this.subPhase = 0;
	}
	
	@Override
	protected void doSubact(EclipseGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		EclipsePlayer player = action.getPlayer();
		//这里会处理一些通用的行动指令
		if("DO_COLONY".equals(subact)){
			//殖民
			EclipseColonyListener l = new EclipseColonyListener(player);
			this.insertInterrupteListener(l, gameMode);
		}else{
			this.doRoundAction(gameMode, action);
		}
	}
	
	/**
	 * 执行回合中的行动
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected abstract void doRoundAction(EclipseGameMode gameMode, BgAction action) throws BoardGameException;

	/**
	 * 检查玩家是否已经激活该行动,如果没有则需要移除一个影响力圆片
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException
	 */
	protected void checkActived(EclipseGameMode gameMode, EclipsePlayer player) throws BoardGameException{
		if(!this.actived){
			gameMode.getGame().playerRemoveInfluenceDisc(player);
			//发送选择的行动标题
			gameMode.getGame().sendAnimationResponse(EclipseAnimParamFactory.createActionTitleParam(player, getActionType()));
			this.actived = true;
		}
	}
	
	/**
	 * 检查剩余行动数量
	 * 
	 * @param gameMode
	 * @param player
	 * @throws BoardGameException 
	 */
	protected void checkActionCount(EclipseGameMode gameMode, EclipsePlayer player) throws BoardGameException{
		this.actionCount -= 1;
		if(this.actionCount<=0){
			this.setPlayerResponsed(gameMode, player);
		}else{
			this.doReset(gameMode, null);
			this.sendStartListenCommand(gameMode, player, player);
		}
	}
	
	@Override
	public InterruptParam createInterruptParam() {
		InterruptParam param = super.createInterruptParam();
		param.set("actived", this.actived);
		param.set("player", this.trigPlayer);
		return param;
	}

}
