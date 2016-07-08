package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.consts.ActionType;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.factory.InitParamFactory;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.TS.listener.initParam.OPActionInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;

public class Custom112Listener extends TSParamInterruptListener {
	protected int times = 0;
	
	@Override
	protected int getValidCode() {
		return 0;
	}
	
	public Custom112Listener(TSPlayer trigPlayer, TSGameMode gameMode,
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
		TSPlayer player = this.getListeningPlayer();
		if(player.hasEffect(EffectType.COUP_TO_LOSE)){
			//只要有这个效果在,就会创建一个用来移除该效果的监听器
			TSCard card = gameMode.getCardManager().getCardByCardNo(40);
			ActionInitParam ip = InitParamFactory.createActionInitParam(gameMode, player, card, null);
			Custom40Listener l = new Custom40Listener(player, gameMode, ip);
			this.insertInterrupteListener(l, gameMode);
		}else{
			//否则就创建,创建政变监听器
			this.createCoupListener(gameMode, null);
		}
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	/**
	 * 创建政变监听器
	 * 
	 * @param gameMode
	 * @param targetCountry - 不能进行政变的国家
	 * @throws BoardGameException
	 */
	protected void createCoupListener(TSGameMode gameMode, TSCountry targetCountry) throws BoardGameException{
		//创建监听器参数
		TSPlayer player = this.getListeningPlayer();
		OPActionInitParam initParam = new OPActionInitParam();
		initParam.listeningPlayer = player.superPower;
		initParam.targetPower = SuperPower.getOppositeSuperPower(player.superPower);
		initParam.actionType = ActionType.COUP;
		initParam.card = this.getCard();
		initParam.trigType = this.getInitParam().trigType;
		initParam.canCancel = this.getInitParam().canCancel;
		initParam.canPass = this.getInitParam().canPass;
		initParam.num = this.getInitParam().num;
		initParam.msg = this.getInitParam().msg;
		initParam.isFreeAction = false;
		//取得条件组的副本,否则可能会出问题
		initParam.setConditionGroup(this.getInitParam().getConditionGroup().clone());
		if(targetCountry!=null){
			//如果存在不能政变的国家,则加入到条件中
			TSCountryCondition bc = new TSCountryCondition();
			bc.country = targetCountry.country;
			initParam.getConditionGroup().addBcs(bc);
		}
		//创建监听器
		TSCoupListener l = new TSCoupListener(player, gameMode, initParam);
		this.insertInterrupteListener(l, gameMode);
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
		String confirmString = param.getString("confirmString");
		Integer validCode = param.getInteger("validCode");
		if(validCode!=null && validCode==TSGameCmd.GAME_CODE_40){
			//古巴导弹危机的监听器执行完成时,如果时确认移除,则创建政变监听器
			if(ConfirmString.CONFIRM.equals(confirmString)){
				this.createCoupListener(gameMode, null);
			}else{
				//否则就直接结束行动
				this.setAllPlayerResponsed(gameMode);
			}
		}else{
			//其他情况就是政变监听器的执行结果了
			if(this.times==0 && ConfirmString.CONFIRM.equals(confirmString)){
				//如果是第一次执行政变,则检查是否政变成功
				Boolean success = param.getBoolean("success");
				if(success!=null && success){
					//如果成功可以再进行一次政变
					this.times += 1;
					
					TSCountry targetCountry = param.get("targetCountry");
					this.createCoupListener(gameMode, targetCountry);
				}else{
					//否则设置回应字符串为确认状态
					this.confirmString = ConfirmString.CONFIRM;
					this.setAllPlayerResponsed(gameMode);
				}
			}else{
				//否则设置回应字符串为确认状态
				this.confirmString = ConfirmString.CONFIRM;
				this.setAllPlayerResponsed(gameMode);
			}
		}
	}

}
