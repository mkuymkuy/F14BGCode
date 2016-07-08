package com.f14.TS.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.ActionParam;
import com.f14.TS.action.TSGameAction;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.Country;
import com.f14.TS.consts.EffectType;
import com.f14.TS.consts.Region;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSCmdString;
import com.f14.TS.consts.TSGameCmd;
import com.f14.TS.factory.GameActionFactory;
import com.f14.TS.listener.initParam.ActionInitParam;
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
public class TSCountryActionListener extends TSParamInterruptListener {
	protected List<ActionParam> actionParams;
	
	public TSCountryActionListener(TSPlayer trigPlayer, TSGameMode gameMode, ActionInitParam initParam, ActionParam actionParam) {
		this(trigPlayer, gameMode, initParam, new ArrayList<ActionParam>());
		actionParams.add(actionParam);
	}
	
	public TSCountryActionListener(TSPlayer trigPlayer, TSGameMode gameMode, ActionInitParam initParam, List<ActionParam> actionParams) {
		super(trigPlayer, gameMode, initParam);
		this.actionParams = actionParams;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ActionInitParam getInitParam(){
		return super.getInitParam();
	}
	
	@Override
	protected int getValidCode() {
		return TSGameCmd.GAME_CODE_COUNTRY_ACTION;
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
			CountryParam param = new CountryParam((TSPlayer)p);
			param.reset();
			this.setParam(p, param);
		}
	}
	
	@Override
	protected void sendStartListenCommand(TSGameMode gameMode, Player player,
			Player receiver) {
		super.sendStartListenCommand(gameMode, player, receiver);
		//只会向指定自己发送该监听信息
		this.sendCountryParamInfo(gameMode, player);
	}
	
	/**
	 * 发送玩家选择国家的参数信息
	 * 
	 * @param gameMode
	 * @param p
	 */
	protected void sendCountryParamInfo(TSGameMode gameMode, Player p){
		CountryParam param = this.getParam(p);
		BgResponse res = this.createSubactResponse(p, "countryParam");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(TSCountry o : param.getSelectedCountries()){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("countryName", o.name);
			map.put("influence", o.getInfluenceString());
			list.add(map);
		}
		res.setPublicParameter("countries", list);
		gameMode.getGame().sendResponse(p, res);
	}
	
	@Override
	protected void doSubact(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		String subact = action.getAsString("subact");
		if("country".equals(subact)){
			//选择国家
			this.chooseCountry(gameMode, action);
		}
	}
	
	@Override
	protected void confirmCheck(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		//判断选择的国家数量是否匹配
		TSPlayer player = action.getPlayer();
		CountryParam param = this.getParam(player);
		int availnum = gameMode.getCountryManager().getAvailableCountryNum(this.getInitParam());
		int neednum = this.getInitParam().countryNum;
		//如果游戏中可选国家的数量少于需要选择的国家数量,则只需要取得可选国家的数量即可
		neednum = Math.min(availnum, neednum);
		if(param.getSelectedCountries().size()<neednum){
			throw new BoardGameException(this.getMsg(player));
		}
	}
	
	@Override
	protected void doConfirm(TSGameMode gameMode, BgAction action)
			throws BoardGameException {
		TSPlayer player = action.getPlayer();
		CountryParam param = this.getParam(player);
		//对所选的国家遍历并执行行动
		for(TSCountry country : param.getSelectedCountries()){
			for(ActionParam ap : this.actionParams){
				ap.country = country.country;
				TSGameAction a = GameActionFactory.createGameAction(gameMode, player, this.getInitParam().card, ap);
				gameMode.getGame().executeAction(a);
			}
		}
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
		CountryParam param = this.getParam(player);
		//重置选择
		param.reset();
		//刷新选择的国家列表
		this.sendCountryParamInfo(gameMode, player);
	}
	
	/**
	 * 选择国家
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void chooseCountry(TSGameMode gameMode, BgAction action) throws BoardGameException {
		TSPlayer player = action.getPlayer();
		CountryParam param = this.getParam(player);
		String country = action.getAsString("country");
		TSCountry cty = gameMode.getCountryManager().getCountry(country);
		//检查是否可以选择该国家
		if(!this.getInitParam().test(cty)){
			throw new BoardGameException("你不能选择这个国家!");
		}
		//如果北约生效,则苏联不能在美国控制的欧洲国家打局部战争
		if(this.getCard()!=null && this.getCard().tsCardNo==36){
			//36=局部战争
			if(player.superPower==SuperPower.USSR && player.hasEffect(EffectType.PROTECT_EUROPE)
					&& cty.getRegion()==Region.EUROPE && cty.isControlledByOpposite(SuperPower.USSR)){
				throw new BoardGameException("北约发生后不能在美国控制的欧洲国家进行局部战争!");
			}
		}
		
		//调整影响力
		param.chooseCountry(cty);
		//刷新选择后的国家列表
		this.sendCountryParamInfo(gameMode, player);
	}
	
	/**
	 * 选择国家的临时参数
	 * 
	 * @author F14eagle
	 *
	 */
	class CountryParam{
		TSPlayer player;
		Set<TSCountry> countries = new LinkedHashSet<TSCountry>();
		
		CountryParam(TSPlayer player){
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
			this.countries.clear();
		}
		
		/**
		 * 选择国家
		 * 
		 * @param country
		 * @throws BoardGameException
		 */
		void chooseCountry(TSCountry country) throws BoardGameException{
			if(!this.countries.contains(country)){
				int countryNum = getInitParam().countryNum;
				if(countryNum==1){
					//如果国家是单选的话,则移除原来选择的国家,选中新的国家
					this.countries.clear();
				}else if(this.getSelectedCountries().size()>=countryNum){
					throw new BoardGameException("你不能选择更多的国家了!");
				}
				this.countries.add(country);
			}
		}
		
		/**
		 * 取得选中的国家
		 * 
		 * @return
		 */
		Collection<TSCountry> getSelectedCountries(){
			return this.countries;
		}
		
	}
	
}
