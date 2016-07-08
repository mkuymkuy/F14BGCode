package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCardDeck;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.consts.ActionType;
import com.f14.TS.consts.Region;
import com.f14.TS.consts.SubRegion;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.consts.ConfirmString;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.bg.utils.CheckUtils;

/**
 * #104-剑桥五杰的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom104Listener extends TSParamInterruptListener {
	protected TSCardDeck drawnCards = new TSCardDeck();
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_104;
	}
	
	@Override
	protected String getMsg(Player player) {
		return "请选择一张计分牌,你可以在该区域添加一点影响力!";
	}
	
	public Custom104Listener(TSPlayer trigPlayer, TSGameMode gameMode,
			InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam() {
		return super.getInitParam();
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//检查美国玩家手中的计分牌,苏联玩家只能从这些计分牌区域中选择国家
		TSPlayer player = gameMode.getGame().getUsaPlayer();
		this.drawnCards.addCards(player.getScoreCards());
	}
	
	@Override
	protected BgResponse createStartListenCommand(TSGameMode gameMode,
			Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		//将抽出的牌的信息发送给用户
		res.setPublicParameter("cardIds", BgUtils.card2String(this.drawnCards.getCards()));
		return res;
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}

	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		CheckUtils.checkNull(cardId, "请选择计分牌!");
		TSCard card = this.drawnCards.getCard(cardId);
		//有可能选到东南亚..这是个子区域
		String regionDesc = "";
		TSCountryCondition c = new TSCountryCondition();
		if("SOUTHEAST_ASIA".equals(card.scoreRegion)){
			SubRegion subregion = SubRegion.valueOf(card.scoreRegion);
			regionDesc = SubRegion.getChineseDescr(subregion);
			c.subRegion = subregion;
		}else{
			Region region = Region.valueOf(card.scoreRegion);
			regionDesc = Region.getChineseDescr(region);
			c.region = region;
		}
		
		//创建一个在该区域添加1点影响力的监听器
		ActionInitParam initParam = new ActionInitParam();
		initParam.listeningPlayer = player.superPower;
		initParam.targetPower = player.superPower;
		initParam.actionType = ActionType.ADJUST_INFLUENCE;
		initParam.num = 1;
		initParam.msg = "请在" + regionDesc + "分配 {num} 点影响力!";
		initParam.addWc(c);
		//创建监听器
		//ActionInitParam ip = InitParamFactory.createActionInitParam(gameMode, player, card, null);
		TSAdjustInfluenceListener l = new TSAdjustInfluenceListener(player, gameMode, initParam);
		this.insertInterrupteListener(l, gameMode);
	}

	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {

	}
	
	/**
	 * 中断监听器完成时回调的方法
	 * 
	 * @param gameMode
	 * @throws BoardGameException 
	 */
	@Override
	protected void onInterrupteListenerOver(TSGameMode gameMode, InterruptParam param)
			throws BoardGameException {
		String confirmString = param.getString("confirmString");
		if(ConfirmString.CONFIRM.equals(confirmString) || ConfirmString.PASS.equals(confirmString)){
			//检查是否存在中断监听器,如果有,则不予执行
			if(this.isInterruped()){
				return;
			}
			//无论如何都结束玩家行动
			this.setPlayerResponsed(gameMode, this.getListeningPlayer());
		}
	}
	
}
