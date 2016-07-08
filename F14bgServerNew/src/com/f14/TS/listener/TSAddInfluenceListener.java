package com.f14.TS.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.component.AdjustParam;
import com.f14.TS.component.TSCountry;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.listener.initParam.OPActionInitParam;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;

/**
 * 使用OP放置影响力的监听器
 * 
 * @author F14eagle
 *
 */
public class TSAddInfluenceListener extends TSOpActionInterruptListener {
	
	public TSAddInfluenceListener(TSPlayer trigPlayer, TSGameMode gameMode, OPActionInitParam initParam) {
		super(trigPlayer, gameMode, initParam);
	}

	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_ADD_INFLUENCE;
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
		this.sendTemplateInfluenceInfo(gameMode, player);
		this.sendInfluenceParamInfo(gameMode, player);
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
		TSPlayer player = action.getPlayer();
		InfluenceParam param = this.getParam(player);
		//判断点数是否用光
		if(param.getLeftOP()>0){
			throw new BoardGameException(this.getMsg(player));
		}
	}
	
	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
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
	 * 放置影响力
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
		//检查是否可以选择该国家
		if(!param.availableCountries.contains(cty.country)){
			throw new BoardGameException("你不能选择这个国家!");
		}
		if(!this.getInitParam().test(cty)){
			throw new BoardGameException("你不能选择这个国家!");
		}
		//检查玩家是否有禁止加点的区域
		if(player.hasEffect(EffectType._94_EFFECT)){
			TSCountryCondition condition = player.getForbiddenCondition();
			if(condition.test(cty)){
				throw new BoardGameException("你不能选择这个国家!");
			}
		}
		//调整影响力
		param.adjustInfluence(cty);
		//刷新调整后的国家影响力信息及调整列表
		this.sendTemplateInfluenceInfo(gameMode, player);
		this.sendInfluenceParamInfo(gameMode, player);
	}
	
	/**
	 * 调整影响力的临时参数
	 * 
	 * @author F14eagle
	 *
	 */
	class InfluenceParam{
		TSPlayer player;
		int usedNum;
		/**
		 * 每次可调整的数量
		 */
		int adjustValue = 1;
		Map<String, AdjustParam> adjustParams = new LinkedHashMap<String, AdjustParam>();
		/**
		 * 允许放置影响力的国家
		 */
		Set<Country> availableCountries;
		/**
		 * 放置过影响力的国家
		 */
		Collection<TSCountry> countries = new HashSet<TSCountry>();
		
		InfluenceParam(TSPlayer player){
			this.player = player;
			this.init();
		}
		
		/**
		 * 初始化参数
		 */
		void init(){
			//设置允许放置影响力的国家列表
			this.availableCountries = gameMode.getCountryManager().getAvailableCountries(this.player.superPower);
		}
		
		/**
		 * 重置调整参数
		 */
		void reset(){
			this.usedNum = 0;
			this.adjustParams.clear();
			this.countries.clear();
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
		 * 取得剩余的OP
		 * 
		 * @return
		 */
		int getLeftOP(TSCountry country){
			Collection<TSCountry> countries = new HashSet<TSCountry>(this.countries);
			countries.add(country);
			return getOP(player, countries) - usedNum;
		}
		
		/**
		 * 检查是否拥有剩余的op对country进行调整阵营的操作
		 * 
		 * @param country
		 * @return
		 */
		boolean hasLeftOP(TSCountry country){
			int leftOp = this.getLeftOP(country);
			if(leftOp<=0){
				return false;
			}else{
				return true;
			}
		}
		
		/**
		 * 调整影响力
		 * 
		 * @param country
		 * @throws BoardGameException
		 */
		void adjustInfluence(TSCountry country) throws BoardGameException{
			if(!this.hasLeftOP(country)){
				throw new BoardGameException("你没有多余的OP了!");
			}
			AdjustParam ap = this.getAdjustParam(country.id);
			if(ap==null){
				//如果不存在调整参数,则创建一个
				ap = getInitParam().createAdjustParam(country);
				this.adjustParams.put(country.id, ap);
			}
			int needop = 1;
			if(ap.tempCountry.isControlledByOpposite(this.player.superPower)){
				//如果要在被对方控制的国家放影响力,需要2个OP
				needop = 2;
			}
			int leftNum = this.getLeftOP(country);
			if(leftNum<needop){
				//如果没有使用过点数,则删除该参数
				if(ap.num==0){
					this.adjustParams.remove(country.id);
				}
				throw new BoardGameException("你的OP不够放置该影响力!");
			}
			//按照初始化参数中设定的值调整该国家的影响力
			ap.tempCountry.addInfluence(ap.adjustPower, this.adjustValue);
			ap.num += this.adjustValue;
			ap.op += needop;
			
			this.usedNum += needop;
			this.countries.add(ap.tempCountry);
			//this.leftNum -= needop;
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
				ap.orgCountry.setInfluence(SuperPower.USSR, ap.tempCountry.getInfluence(SuperPower.USSR));
				ap.orgCountry.setInfluence(SuperPower.USA, ap.tempCountry.getInfluence(SuperPower.USA));
			}
		}
	}
	
}
