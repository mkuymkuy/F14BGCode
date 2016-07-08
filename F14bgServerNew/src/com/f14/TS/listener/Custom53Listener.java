package com.f14.TS.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.AdjustParam;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.InitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.listener.InterruptParam;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;

/**
 * #53-南非动荡的监听器
 * 
 * @author F14eagle
 *
 */
public class Custom53Listener extends TSParamInterruptListener {
	
	public Custom53Listener(TSPlayer trigPlayer, TSGameMode gameMode, InitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam(){
		return super.getInitParam();
	}
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_ADJUST_INFLUENCE;
	}
	
	@Override
	protected String getActionString() {
		return TSCmdString.ACTION_SELECT_COUNTRY;
	}
	
	@Override
	protected void beforeStartListen(TSGameMode gameMode)
			throws BoardGameException {
		super.beforeStartListen(gameMode);
		//为监听的玩家创建参数
		for(Player p : this.getListeningPlayers()){
			InfluenceParam param = new InfluenceParam((TSPlayer)p);
			param.reset();
			this.setParam(p, param);
		}
	}
	
	@Override
	protected void sendStartListenCommand(TSGameMode gameMode, Player player,
			Player receiver) {
		super.sendStartListenCommand(gameMode, player, receiver);
		//只会向指定自己发送该监听信息
		if(receiver!=null && player==receiver){
			this.sendTemplateInfluenceInfo(gameMode, receiver);
			this.sendInfluenceParamInfo(gameMode, receiver);
		}
	}
	
	/**
	 * 发送玩家的调整参数信息
	 * 
	 * @param gameMode
	 * @param p
	 */
	protected void sendInfluenceParamInfo(TSGameMode gameMode, Player p){
		InfluenceParam param = this.getParam(p);
		BgResponse res = this.createSubactResponse(p, "influenceParam");
		res.setPublicParameter("countries", param.getInfluenceParam());
		gameMode.getGame().sendResponse(p, res);
	}
	
	/**
	 * 发送玩家调整的临时影响力的信息
	 * 
	 * @param gameMode
	 * @param p
	 */
	protected void sendTemplateInfluenceInfo(TSGameMode gameMode, Player p){
		InfluenceParam param = this.getParam(p);
		gameMode.getGame().sendCountriesInfo(param.getTemplateInfluence(), p);
	}
	
	/**
	 * 发送玩家调整过的国家的初始影响力的信息
	 * 
	 * @param gameMode
	 * @param p
	 */
	protected void sendOriginInfluenceInfo(TSGameMode gameMode, Player p){
		InfluenceParam param = this.getParam(p);
		gameMode.getGame().sendCountriesInfo(param.getOriginInfluence(), p);
	}
	
	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("country".equals(subact)){
			//调整影响力
			this.adjustInfluence(gameMode, action);
		}
	}

	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		//判断是否已经用足了点数
		TSPlayer player = action.getPlayer();
		InfluenceParam param = this.getParam(player);
		if(!param.isFinish()){
			throw new BoardGameException(getMsg(player));
		}
	}
	
	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		//判断点数是否用光
		TSPlayer player = action.getPlayer();
		InfluenceParam param = this.getParam(player);
		//输出战报信息
		gameMode.getReport().playerDoAction(player, param.getAdjustParams());
		//确定影响力调整
		param.applyAdjust();
		//向所有玩家发送影响力的调整结果
		gameMode.getGame().sendCountriesInfo(param.getOriginInfluence(), null);
		//设置已回应
		this.setPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 重置选择
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	@Override
	protected void doReset(TSGameMode gameMode, BgAction action) throws BoardGameException {
		TSPlayer player = action.getPlayer();
		InfluenceParam param = this.getParam(player);
		//刷新选择过的国家的影响力
		this.sendOriginInfluenceInfo(gameMode, player);
		//重置选择
		param.reset();
		//刷新临时的国家调整列表
		this.sendInfluenceParamInfo(gameMode, player);
	}
	
	/**
	 * 调整影响力
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void adjustInfluence(TSGameMode gameMode, BgAction action) throws BoardGameException {
		TSPlayer player = action.getPlayer();
		InfluenceParam param = this.getParam(player);
		String country = action.getAsString("country");
		TSCountry cty = gameMode.getCountryManager().getCountry(country);
		//检查是否可以选择该国家,该逻辑在param中的方法实现
//		if(!this.getInitParam().test(cty)){
//			throw new BoardGameException("你不能选择这个国家!");
//		}
		//调整影响力
		param.adjustInfluence(cty);
		//刷新调整后的国家影响力信息及调整列表
		this.sendTemplateInfluenceInfo(gameMode, player);
		this.sendInfluenceParamInfo(gameMode, player);
	}
	
	@Override
	public InterruptParam createInterruptParam() {
		InterruptParam res = super.createInterruptParam();
		//将玩家实际用掉的点数设置在返回参数中
		TSPlayer player = this.getListeningPlayer();
		//InfluenceParam param = this.getParam(player);
		//int adjustNum = Math.abs(this.getInitParam().num) - param.leftNum;
		//res.set("adjustNum", adjustNum);
		res.set("player", player);
		return res;
	}
	
	/**
	 * 调整影响力的临时参数
	 * 
	 * @author F14eagle
	 *
	 */
	class InfluenceParam {
		TSPlayer player;
		Map<String, AdjustParam> adjustParams = new LinkedHashMap<String, AdjustParam>();
		/**
		 * 加在南非的影响力
		 */
		int mainPoint = 0;
		/**
		 * 加在邻近国家的影响力
		 */
		int subPoint = 0;
		
		InfluenceParam(TSPlayer player){
			this.player = player;
		}
		
		/**
		 * 重置调整参数
		 */
		void reset(){
			this.adjustParams.clear();
			mainPoint = 0;
			subPoint = 0;
		}
		
		/**
		 * 判断点数是否用光
		 * 
		 * @return
		 */
		boolean isFinish(){
			if(mainPoint==2 || (mainPoint==1 && subPoint==2)){
				return true;
			}else{
				return false;
			}
		}
		
		/**
		 * 检查是否可以选择该国家
		 * 
		 * @param country
		 */
		void checkCountry(TSCountry country){
			
		}
		
		/**
		 * 调整影响力
		 * 
		 * @param country
		 * @throws BoardGameException
		 */
		void adjustInfluence(TSCountry country) throws BoardGameException{
			if(this.isFinish()){
				throw new BoardGameException("你没有多余的可操作点数了!");
			}
			AdjustParam ap = this.getAdjustParam(country.id);
			if(ap==null){
				//如果不存在调整参数,检查该国家是否可以选择
				//只能选择南非和南非的邻国
				boolean adj = country.isAdjacentTo(Country.RSA);
				if(country.country!=Country.RSA && !adj){
					throw new BoardGameException(getMsg(player));
				}
				if(adj){
					//如果是邻国,则需要判断,如果南非已经加了2点,则不能选择
					if(this.mainPoint==2){
						throw new BoardGameException(getMsg(player));
					}
					//可以两个邻国一个一点所以实现——如果已经有选择其他的邻国,也不能选择的代码注释掉
					/*if(this.subPoint>0){
						throw new BoardGameException(getMsg(player));
					}*/
				}
				//创建调整参数
				ap = getInitParam().createAdjustParam(country);
				this.adjustParams.put(country.id, ap);
			}
			
			if(country.country==Country.RSA){
				//如果是在南非加影响力,则先检查是否还能加
				if(mainPoint==2){
					throw new BoardGameException(getMsg(player));
				}
				//如果有在邻国添加过影响力,则只能在南非加1点
				if(this.adjustParams.size()>1 && mainPoint==1){
					throw new BoardGameException(getMsg(player));
				}
				mainPoint += 1;
			}else{
				//如果是邻国,则检查是否还能加
				if(subPoint==2){
					throw new BoardGameException(getMsg(player));
				}
				subPoint += 1;
			}
			//按照初始化参数中设定的值调整该国家的影响力
			ap.tempCountry.addInfluence(ap.adjustPower, 1);
			ap.num += 1;
		}
		
		/**
		 * 取得指定国家的调整参数
		 * 
		 * @param countryId
		 * @return
		 */
		AdjustParam getAdjustParam(String countryId){
			return this.adjustParams.get(countryId);
		}
		
		/**
		 * 取得指定国家的调整参数
		 * 
		 * @param country
		 * @return
		 */
		AdjustParam getAdjustParam(Country country){
			for(AdjustParam param : this.adjustParams.values()){
				if(param.orgCountry.country==country){
					return param;
				}
			}
			return null;
		}
		
		/**
		 * 取得所有调整参数
		 * 
		 * @return
		 */
		Collection<AdjustParam> getAdjustParams(){
			return this.adjustParams.values();
		}
		
		/**
		 * 取得临时调整影响力的国家信息
		 * 
		 * @return
		 */
		List<TSCountry> getTemplateInfluence(){
			List<TSCountry> res = new ArrayList<TSCountry>();
			for(AdjustParam ap : this.adjustParams.values()){
				res.add(ap.tempCountry);
			}
			return res;
		}
		
		/**
		 * 取得调整过影响力的国家的原始信息
		 * 
		 * @return
		 */
		List<TSCountry> getOriginInfluence(){
			List<TSCountry> res = new ArrayList<TSCountry>();
			for(AdjustParam ap : this.adjustParams.values()){
				res.add(ap.orgCountry);
			}
			return res;
		}
		
		/**
		 * 取得调整参数
		 * 
		 * @return
		 */
		List<Map<String, Object>> getInfluenceParam(){
			return BgUtils.toMapList(this.adjustParams.values());
		}
		
		/**
		 * 确定影响力调整
		 */
		void applyAdjust(){
			for(AdjustParam ap : this.adjustParams.values()){
				ap.apply();
			}
		}
	}


}
