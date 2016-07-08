package com.f14.TS.listener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.TSEffect;
import com.f14.TS.component.RealignmentAdjustParam;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.RealignmentAdjustParam.RealignmentInfo;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.RealignmentInitParam;
import com.f14.TS.utils.TSRoll;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;

/**
 * 调整阵营的监听器
 * 
 * @author F14eagle
 *
 */
public class TSRealignmentListener extends TSOpActionInterruptListener {
	
	public TSRealignmentListener(TSPlayer trigPlayer, TSGameMode gameMode, RealignmentInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	protected RealignmentInitParam getInitParam(){
		return (RealignmentInitParam)super.getInitParam();
	}
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_REALIGNMENT;
	}
	
	@Override
	protected String getActionString() {
		return TSCmdString.ACTION_SELECT_COUNTRY;
	}
	
	@Override
	protected String getMsg(Player player) {
		//InfluenceParam param = this.getParam(player);
		String res = this.getInitParam().getMsg().replaceAll("\\{num\\}", this.getOP((TSPlayer)player, null)+"");
		return res;
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//为监听的玩家创建参数
		for(Player p : this.getListeningPlayers()){
			RealignmentParam param = new RealignmentParam((TSPlayer)p);
			//param.leftNum = this.getInitParam().num;
			this.setParam(p, param);
		}
	}
	
	@Override
	protected void sendStartListenCommand(TSGameMode gameMode, Player player,
			Player receiver) {
		super.sendStartListenCommand(gameMode, player, receiver);
		//只会向指定自己发送该监听信息
		this.sendRealignmentParamInfo(gameMode, player);
	}
	
	/**
	 * 发送玩家的调整阵营参数信息
	 * 
	 * @param gameMode
	 * @param p
	 */
	protected void sendRealignmentParamInfo(TSGameMode gameMode, Player p){
		RealignmentParam param = this.getParam(p);
		BgResponse res = this.createSubactResponse(p, "realignmentParam");
		if(param.adjustParam!=null){
			TSCountry country = param.adjustParam.orgCountry;
			res.setPublicParameter("countryName", country.name);
			res.setPublicParameter("influence", country.getInfluenceString());
			res.setPublicParameter("ussrBonus", param.adjustParam.getRealignmentInfo(SuperPower.USSR).bonus);
			res.setPublicParameter("usaBonus", param.adjustParam.getRealignmentInfo(SuperPower.USA).bonus);
		}
		gameMode.getGame().sendResponse(p, res);
	}
	
	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("country".equals(subact)){
			//调整阵营
			this.realignment(gameMode, action);
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
		RealignmentParam param = this.getParam(player);
		if(param.adjustParam==null){
			throw new BoardGameException("请选择需要调整阵营的国家!");
		}
		param.realignment();
		//输出战报信息
		gameMode.getReport().playerDoAction(player, param.adjustParam);
		//确定影响力调整
		param.applyRealignment();
		//向所有玩家发送影响力的调整结果
		gameMode.getGame().sendCountryInfo(param.adjustParam.orgCountry, null);
		
		if(param.getLeftOP()<=0){
			//如果OP已经用光,则设置为已回应
			this.setPlayerResponsed(gameMode, player);
		}else{
			//否则刷新提示信息
			this.refreshMsg(gameMode, player);
		}
	}
	
	/**
	 * 调整阵营
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void realignment(TSGameMode gameMode, BgAction action) throws BoardGameException {
		TSPlayer player = action.getPlayer();
		RealignmentParam param = this.getParam(player);
		String country = action.getAsString("country");
		TSCountry cty = gameMode.getCountryManager().getCountry(country);
		//检查是否可以选择该国家
		if(!this.getInitParam().test(cty)){
			throw new BoardGameException("你不能选择这个国家!");
		}
		param.checkRealignment(cty);
		gameMode.getValidManager().checkRealignment(player, cty, gameMode.getTurnPlayer(), this.getInitParam().isFreeAction);
		//调整影响力
		param.setRealignmentTarget(cty);
		this.sendRealignmentParamInfo(gameMode, player);
	}
	
	@Override
	protected boolean canCancel(TSGameMode gameMode, BgAction action) {
		TSPlayer player = action.getPlayer();
		RealignmentParam param = this.getParam(player);
		//如果玩家已经用过了点数,则不能取消
		if(param.usedNum>0){
			return false;
		}
		return super.canCancel(gameMode, action);
	}
	
	/**
	 * 调整影响力的临时参数
	 * 
	 * @author F14eagle
	 *
	 */
	class RealignmentParam{
		TSPlayer player;
		//int leftNum;
		RealignmentAdjustParam adjustParam;
		int usedNum;
		Collection<TSCountry> countries = new HashSet<TSCountry>();
		
		RealignmentParam(TSPlayer player){
			this.player = player;
			this.init();
		}
		
		/**
		 * 初始化参数
		 */
		void init(){
			
		}
		
		/**
		 * 取得剩余的OP
		 * 
		 * @return
		 */
		int getLeftOP(){
			return getOP(player, countries) - usedNum;
		}
		
		/**
		 * 检查是否拥有剩余的op对country进行调整阵营的操作
		 * 
		 * @param country
		 * @return
		 */
		boolean hasLeftOP(TSCountry country){
			Collection<TSCountry> countries = new HashSet<TSCountry>(this.countries);
			countries.add(country);
			int op = getOP(player, countries);
			if(this.usedNum>=op){
				return false;
			}else{
				return true;
			}
		}
		
		/**
		 * 检查该国家是否可以调整阵营
		 * 
		 * @param country
		 * @throws BoardGameException 
		 */
		void checkRealignment(TSCountry country) throws BoardGameException{
			if(!country.hasInfluence(getInitParam().targetPower)){
				throw new BoardGameException("该国家没有对方的影响力,不能调整阵营!");
			}
		}
		
		/**
		 * 设置调整阵营的目标
		 * 
		 * @param country
		 * @throws BoardGameException
		 */
		void setRealignmentTarget(TSCountry country) throws BoardGameException{
			this.adjustParam = getInitParam().createAdjustParam(country);
			//计算调整加值
			Map<SuperPower, Integer> bonus = gameMode.getCountryManager().getRealignmentBonus(country);
			this.adjustParam.setRealignmentBonus(bonus);
		}
		
		/**
		 * 调整阵营
		 * 
		 * @param country
		 * @throws BoardGameException
		 */
		void realignment() throws BoardGameException{
			if(!this.hasLeftOP(adjustParam.tempCountry)){
				throw new BoardGameException("你没有多余的OP了!");
			}
			
			RealignmentInfo ussr = this.adjustParam.getRealignmentInfo(SuperPower.USSR);
			RealignmentInfo usa = this.adjustParam.getRealignmentInfo(SuperPower.USA);
			ussr.roll = TSRoll.roll();
			usa.roll = TSRoll.roll();
			//调整值修正
			ussr.modify = this.checkRealigmentModifier(SuperPower.USSR, adjustParam.tempCountry);
			usa.modify = this.checkRealigmentModifier(SuperPower.USA, adjustParam.tempCountry);
			
			int offset = ussr.getTotal() - usa.getTotal();
			if(offset>0){
				//苏联调整成功,移除美国影响力
				adjustParam.tempCountry.addInfluence(SuperPower.USA, -offset);
			}else if(offset<0){
				//美国调整成功,移除苏联影响力
				adjustParam.tempCountry.addInfluence(SuperPower.USSR, offset);
			}
			this.usedNum += 1;
			this.countries.add(adjustParam.tempCountry);
		}
		
		/**
		 * 确定调整阵营
		 */
		void applyRealignment(){
			this.adjustParam.apply();
		}
		
		/**
		 * 检查调整阵营的修正值
		 * 
		 * @return
		 */
		int checkRealigmentModifier(SuperPower superPower, TSCountry country){
			int res = 0;
			TSPlayer player = gameMode.getGame().getPlayer(superPower);
			Collection<TSEffect> effects = player.getEffects(EffectType.REALIGMENT_ROLL_MODIFIER);
			for(TSEffect e : effects){
				res += e.num;
			}
			return res;
		}
	}
	
}
