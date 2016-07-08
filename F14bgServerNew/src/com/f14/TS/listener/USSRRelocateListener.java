package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.consts.ActionType;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;

public class USSRRelocateListener extends TSParamInterruptListener {
	protected RelocatePhase phase;
	
	@Override
	protected int getValidCode() {
		return 0;
	}
	
	public USSRRelocateListener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected void onStartListen(TSGameMode gameMode) throws BoardGameException {
		super.onStartListen(gameMode);
		//开始为移除点数的阶段
		this.phase = RelocatePhase.REMOVE;
		//开始监听时,先为监听的玩家创建移除点数的监听器
		TSPlayer player = this.getListeningPlayer();
		ActionInitParam initParam = this.getInitParam().clone();
		//移除点数时不受条件影响
		initParam.clearConditionGroup();
		initParam.actionType = ActionType.ADJUST_INFLUENCE;
		initParam.num = -Math.abs(initParam.num);
		initParam.limitNum = 0;
		initParam.msg = "请先移除需要重新分配的影响力,最多可以选择 {num} 点!";
		initParam.canLeft = true;
		TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(player, gameMode, initParam);
		this.insertInterrupteListener(l, gameMode);
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	@Override
	protected void onInterrupteListenerOver(TSGameMode gameMode,
			InterruptParam param) throws BoardGameException {
		super.onInterrupteListenerOver(gameMode, param);
		if(this.phase==RelocatePhase.REMOVE){
			//移除点数阶段的回应
			Integer n = param.getInteger("adjustNum");
			if(n!=null && n>0){
				//只有当存在调整数量时,才会创建添加影响力的监听器
				this.phase = RelocatePhase.ADD;
				TSPlayer player = this.getListeningPlayer();
				ActionInitParam initParam = this.getInitParam().clone();
				//移除点数时不受条件影响
				initParam.actionType = ActionType.ADJUST_INFLUENCE;
				initParam.num = n;
				initParam.msg = "请在非美国控制的国家重新分配 {num} 点影响力,每个国家最多可以分配 {limitNum} 点!";
				TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(player, gameMode, initParam);
				this.insertInterrupteListener(l, gameMode);
			}
		}
		if(!this.isInterruped()){
			//设置回应字符串为确认状态
			this.confirmString = ConfirmString.CONFIRM;
			//当不被中断时,则完成回应
			this.setAllPlayerResponsed(gameMode);
		}
	}

	/**
	 * 重新分配点数的阶段
	 * 
	 * @author F14eagle
	 *
	 */
	enum RelocatePhase{
		/**
		 * 移除点数
		 */
		REMOVE,
		/**
		 * 添加点数
		 */
		ADD,
	}
}
