package com.f14.TS.listener;

import java.util.ArrayList;
import java.util.Collection;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.TSEffect;
import com.f14.TS.component.AdjustParam;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.component.condition.TSCountryConditionGroup;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.Region;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.consts.TSVictoryType;
import com.f14.TS.listener.initParam.OPActionInitParam;
import com.f14.TS.utils.TSRoll;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;

/**
 * 政变的监听器
 * 
 * @author F14eagle
 *
 */
public class TSCoupListener extends TSOpActionInterruptListener {
	
	public TSCoupListener(TSPlayer trigPlayer, TSGameMode gameMode, OPActionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_COUP;
	}
	
	@Override
	protected String getActionString() {
		return TSCmdString.ACTION_SELECT_COUNTRY;
	}
	
	@Override
	protected String getMsg(Player player) {
		//CoupParam param = this.getParam(player);
		String res = this.getInitParam().getMsg().replaceAll("\\{num\\}", this.getOP((TSPlayer)player, null)+"");
		return res;
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//为监听的玩家创建参数
		for(Player p : this.getListeningPlayers()){
			CoupParam param = new CoupParam((TSPlayer)p);
			this.setParam(p, param);
		}
	}
	
	@Override
	protected void sendStartListenCommand(TSGameMode gameMode, Player player,
			Player receiver) {
		super.sendStartListenCommand(gameMode, player, receiver);
		//只会向指定自己发送该监听信息
		this.sendCoupParamInfo(gameMode, player);
	}
	
	/**
	 * 发送玩家的政变参数信息
	 * 
	 * @param gameMode
	 * @param p
	 */
	protected void sendCoupParamInfo(TSGameMode gameMode, Player p){
		CoupParam param = this.getParam(p);
		BgResponse res = this.createSubactResponse(p, "coupParam");
		if(param.adjustParam!=null){
			TSCountry country = param.adjustParam.orgCountry;
			res.setPublicParameter("countryName", country.name);
			res.setPublicParameter("influence", country.getInfluenceString());
			res.setPublicParameter("battleField", country.battleField);
			res.setPublicParameter("stabilization", country.stabilization);
		}
		gameMode.getGame().sendResponse(p, res);
	}
	
	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("country".equals(subact)){
			//政变
			this.coup(gameMode, action);
		}
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		CoupParam param = this.getParam(player);
		if(param.adjustParam==null){
			throw new BoardGameException("请选择需要政变的国家!");
		}
		//执行政变
		param.coup();
		//输出战报信息
		gameMode.getReport().playerDoAction(player, param.adjustParam);
		//应用政变结果
		param.applyCoup();
		//向所有玩家发送影响力的调整结果
		gameMode.getGame().sendCountryInfo(param.adjustParam.orgCountry, null);
		
		//非无偿的政变可以得到牌OP点数的军事行动
		if(!this.getInitParam().isFreeAction){
			gameMode.getGame().playerAdjustMilitaryAction(player, this.getInitParam().num);
		}
		
		//检查玩家是否在进行政变后会输掉游戏
		if(player.hasEffect(EffectType.COUP_TO_LOSE)){
			TSPlayer opposite = gameMode.getGame().getOppositePlayer(player.superPower);
			gameMode.getGame().playerWin(opposite, TSVictoryType.SPECIAL);
		}
		
		//如果调整的是战场国,则需要降低DEFCON等级
		if(param.adjustParam.orgCountry.battleField){
			//检查玩家是否有政变不改变DEFCON的能力
			if(!player.hasEffect(EffectType.FREE_DEFCON_COUP)){
				gameMode.getGame().adjustDefcon(-1);
			}
		}
		
		//检查玩家是否有#111-尤里和萨曼莎
		if(player.hasEffect(EffectType._109_EFFECT)){
			//如果有,则给对手+1VP
			TSPlayer opposite = gameMode.getGame().getOppositePlayer(player.superPower);
			gameMode.getGame().adjustVp(opposite, 1);
		}
		
		//设置已回应
		this.setPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 政变
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void coup(TSGameMode gameMode, BgAction action) throws BoardGameException {
		TSPlayer player = action.getPlayer();
		CoupParam param = this.getParam(player);
		String country = action.getAsString("country");
		TSCountry cty = gameMode.getCountryManager().getCountry(country);
		//检查是否可以选择该国家
		if(!this.getInitParam().test(cty)){
			throw new BoardGameException("你不能选择这个国家!");
		}
		param.checkCoup(cty);
		//检查是否可以政变该国家
		gameMode.getValidManager().checkCoup(player, cty, gameMode.getTurnPlayer(), this.getInitParam().isFreeAction);
		param.setCoupTarget(cty);
		//向玩家发送政变参数
		this.sendCoupParamInfo(gameMode, player);
	}
	
	@Override
	public InterruptParam createInterruptParam() {
		InterruptParam param = super.createInterruptParam();
		//设置是否政变成功的参数
		CoupParam cp = this.getParam(this.getListeningPlayer());
		param.set("success", cp.success);
		if(cp.adjustParam!=null){
			param.set("targetCountry", cp.adjustParam.orgCountry);
		}
		return param;
	}
	
	/**
	 * 政变的临时参数
	 * 
	 * @author F14eagle
	 *
	 */
	class CoupParam{
		TSPlayer player;
		AdjustParam adjustParam;
		boolean success;
		
		CoupParam(TSPlayer player){
			this.player = player;
			this.init();
		}
		
		/**
		 * 初始化参数
		 */
		void init(){
			
		}
		
		/**
		 * 重置调整参数
		 */
		void reset(){
			this.adjustParam = null;
		}
		
		/**
		 * 检查该国家是否可以进行政变
		 * 
		 * @param country
		 * @throws BoardGameException 
		 */
		void checkCoup(TSCountry country) throws BoardGameException{
			if(!country.hasInfluence(getInitParam().targetPower)){
				throw new BoardGameException("该国家没有对方的影响力,不能发动政变!");
			}
		}
		
		/**
		 * 设置政变目标
		 * 
		 * @param country
		 * @throws BoardGameException
		 */
		void setCoupTarget(TSCountry country) throws BoardGameException{
			this.adjustParam = getInitParam().createAdjustParam(country);
		}
		
		/**
		 * 执行政变
		 */
		void coup(){
			int r = TSRoll.roll();
			//检查效果对掷骰结果的修正
			int modify = this.checkModify();
			this.adjustParam.num = r;
			this.adjustParam.modify = modify;
			//this.adjustParam.op = this.player.getOp(getInitParam().card);
			//this.adjustParam.op = getInitParam().num;
			Collection<TSCountry> countries = new ArrayList<TSCountry>();
			countries.add(this.adjustParam.orgCountry);
			this.adjustParam.op = getOP(this.player, countries);
			//掷骰结果总数,不可小于0
			int res = r + modify + this.adjustParam.op - 2*this.adjustParam.orgCountry.stabilization;
			res = Math.max(0, res);
			//计算掷骰结果与对方影响力的差值
			int oppoi = this.adjustParam.orgCountry.getInfluence(this.adjustParam.adjustPower);
			int offset = oppoi - res;
			if(offset<0){
				//如果差值有余,则将对方影响力调到0,再给自己加上差值的影响力
				this.adjustParam.tempCountry.setInfluence(this.adjustParam.adjustPower, 0);
				this.adjustParam.tempCountry.addInfluence(SuperPower.getOppositeSuperPower(this.adjustParam.adjustPower), Math.abs(offset));
			}else{
				this.adjustParam.tempCountry.setInfluence(this.adjustParam.adjustPower, offset);
			}
			//如果res>0,则表示政变成功
			if(res>0){
				this.success = true;
			}
		}
		
		/**
		 * 应用政变结果
		 */
		void applyCoup(){
			this.adjustParam.apply();
		}
		
		/**
		 * 检查修正效果
		 * 
		 * @return
		 */
		int checkModify(){
			int res = 0;
			//如果政变目标是在中美洲或者南美洲,则检查是否有拉美暗杀队的效果
			TSCountryConditionGroup conditions = new TSCountryConditionGroup();
			TSCountryCondition c = new TSCountryCondition();
			c.region = Region.CENTRAL_AMERICA;
			conditions.addWcs(c);
			c = new TSCountryCondition();
			c.region = Region.SOUTH_AMERICA;
			conditions.addWcs(c);
			if(conditions.test(this.adjustParam.orgCountry)){
				Collection<TSEffect> effects = player.getEffects(EffectType._69_EFFECT);
				for(TSEffect e : effects){
					res += e.num;
				}
			}
			//检查政变掷骰修正的效果
			Collection<TSEffect> effects = player.getEffects(EffectType.COUP_ROLL_MODIFIER);
			for(TSEffect e : effects){
				res += e.num;
			}
			return res;
		}
	}
	
}
