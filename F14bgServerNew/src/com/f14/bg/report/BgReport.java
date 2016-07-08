package com.f14.bg.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.network.CmdFactory;
import com.f14.bg.BoardGame;
import com.f14.bg.VPCounter;
import com.f14.bg.VPResult;
import com.f14.bg.VPCounter.VpObj;
import com.f14.bg.action.BgResponse;
import com.f14.bg.player.Player;

/**
 * 战报记录
 * 
 * @author F14eagle
 *
 */
public class BgReport {
	/**
	 * 分割线
	 */
	protected static final String LINE = "--------------------";
	/**
	 * 发送最近战报信息的条数
	 */
	protected static final int RECENT_MESSAGE_NUMBER = 20;
	protected static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	protected static DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	
	protected Logger log = Logger.getLogger(this.getClass());
	protected List<MessageObject> messages = new ArrayList<MessageObject>();
	protected BoardGame<?, ?> game;
	protected Date startTime;
	protected Date endTime;
	protected GameType gameType;
	protected String playerList;
	protected int playerNumber;
	
	public BgReport(BoardGame<?, ?> bg){
		this.game = bg;
	}
	
	@SuppressWarnings("unchecked")
	public <B extends BoardGame<?, ?>> B getGame(){
		return (B)this.game;
	}
	
	/**
	 * 取得当前时间 时:分:秒
	 * 
	 * @return
	 */
	protected String getCurrentTime(){
		return TIME_FORMAT.format(new Date());
	}
	
	/**
	 * 取得当前日期 年-月-日
	 * 
	 * @return
	 */
	protected String getCurrentDate(){
		return DATE_FORMAT.format(new Date());
	}
	
	/**
	 * 记录系统信息
	 * 
	 * @param message
	 */
	public void system(String message){
		this.system(message, false);
	}
	
	/**
	 * 记录系统信息
	 * 
	 * @param message
	 */
	public void system(String message, boolean alert){
		this.line();
		this.addMessage(this.getCurrentTime(), message, alert);
		this.line();
	}
	
	/**
	 * 记录玩家信息
	 * 
	 * @param player
	 * @param message
	 */
	public void action(Player player, String message){
		this.action(player, message, false);
	}
	
	/**
	 * 记录玩家信息
	 * 
	 * @param player
	 * @param message
	 */
	public void action(Player player, String message, boolean alert){
		this.addMessage(this.getCurrentTime(), player.getReportString() + " " + message, alert);
	}
	
	/**
	 * 记录普通信息
	 * 
	 * @param message
	 */
	public void info(String message){
		this.info(message, false);
	}
	
	/**
	 * 记录普通信息
	 * 
	 * @param message
	 */
	public void info(String message, boolean alert){
		this.addMessage(this.getCurrentTime(), "@ " + message, alert);
	}
	
	/**
	 * 画横线
	 */
	public void line(){
		this.addMessage(LINE);
	}
	
	/**
	 * 游戏开始
	 */
	public void start(){
		this.startTime = new Date();
		this.system("游戏开始");
	}
	
	/**
	 * 游戏结束
	 */
	public void end(){
		//设置战报中游戏的一些数据
		this.endTime = new Date();
		this.gameType = game.getRoom().type;
		this.playerNumber = game.getCurrentPlayerNumber();
		this.playerList = "";
		for(Player player : game.getValidPlayers()){
			this.playerList += (player.position+1) + ":" + player.getName() + " | ";
		}
		this.system("游戏结束");
		//结束时记录标题信息
		this.addTitle();
	}
	
	/**
	 * 记录游戏结果
	 * 
	 * @param result
	 */
	public void result(VPResult result){
		this.system("玩家得分情况");
		for(VPCounter vpc : result.vpCounters){
			this.addMessage("玩家: " + vpc.getPlayer().getName());
			this.addMessage("名次: " + vpc.rank);
			this.addMessage("顺位: " + (vpc.getPlayer().position+1));
			this.addMessage("积分: " + vpc.score);
			this.addMessage("排名点: " + vpc.rankPoint);
			this.addMessage("得分: " + vpc.getTotalVP());
			this.addMessage("得分明细: ");
			for(VpObj obj : vpc.getAllVps()){
				this.addMessage("    " + obj.getLabel() + " : " + obj.getVp());
			}
			this.addMessage(LINE);
		}
	}
	
	/**
	 * 添加信息
	 * 
	 * @param time
	 * @param message
	 * @return
	 */
	protected MessageObject addMessage(String message){
		return this.addMessage(message, false);
	}
	
	/**
	 * 添加信息
	 * 
	 * @param time
	 * @param message
	 * @param alert
	 * @return
	 */
	protected MessageObject addMessage(String message, boolean alert){
		return this.addMessage(null, message, null, alert);
	}
	
	/**
	 * 添加信息
	 * 
	 * @param time
	 * @param message
	 * @return
	 */
	protected MessageObject addMessage(String time, String message){
		return this.addMessage(time, message, false);
	}
	
	/**
	 * 添加信息
	 * 
	 * @param time
	 * @param message
	 * @return
	 */
	protected MessageObject addMessage(String time, String message, boolean alert){
		return this.addMessage(time, message, null, alert);
	}
	
	/**
	 * 添加信息
	 * 
	 * @param time
	 * @param message
	 * @param param
	 * @param alert
	 * @return
	 */
	protected MessageObject addMessage(String time, String message, Map<String, Object> param, boolean alert){
		MessageObject mo = new MessageObject(time, message, param, alert);
		this.addMessage(mo);
		return mo;
	}
	
	/**
	 * 添加信息
	 * 
	 * @param mo
	 * @return
	 */
	protected void addMessage(MessageObject mo){
		log.info(mo);
		this.messages.add(mo);
		//发送指令到客户端
		this.sendMessageResponse(mo, null);
	}
	
	/**
	 * 发送信息的指令
	 * 
	 * @param receiver
	 * @param mos
	 */
	protected void sendMessageResponse(MessageObject mo, Player receiver){
		List<MessageObject> mos = new ArrayList<MessageObject>();
		mos.add(mo);
		this.sendMessageResponse(mos, receiver);
	}
	
	/**
	 * 发送信息的指令
	 * 
	 * @param receiver
	 * @param mos
	 */
	protected void sendMessageResponse(List<MessageObject> mos, Player receiver){
		BgResponse res = CmdFactory.createGameResponse(CmdConst.GAME_CODE_REPORT_MESSAGE, -1);
		res.setPublicParameter("messages", mos);
		this.game.sendResponse(receiver, res);
	}
	
	/**
	 * 发送最近的战报信息
	 * 
	 * @param receiver
	 */
	public void sendRecentMessages(Player receiver){
		if(!this.messages.isEmpty()){
			int toIndex = this.messages.size();
			int fromIndex = toIndex - RECENT_MESSAGE_NUMBER;
			fromIndex = Math.max(fromIndex, 0);
			List<MessageObject> mos = this.messages.subList(fromIndex, toIndex);
			this.sendMessageResponse(mos, receiver);
		}
	}
	
	/**
	 * 将游戏标题信息插入到消息列表最前面
	 */
	protected void addTitle(){
		List<MessageObject> titles = new ArrayList<MessageObject>();
		titles.add(new MessageObject(null, "游戏类型: " + this.gameType, null, false));
		titles.add(new MessageObject(null, "游戏人数: " + this.playerNumber, null, false));
		titles.add(new MessageObject(null, "玩家分布: " + this.playerList, null, false));
		titles.add(new MessageObject(null, "开始时间: " + TIME_FORMAT.format(this.startTime), null, false));
		titles.add(new MessageObject(null, "结束时间: " + TIME_FORMAT.format(this.endTime), null, false));
		this.messages.addAll(0, titles);
	}
	
	/**
	 * 转换成JSON String
	 * @return
	 */
	public String toJSONString(){
		return JSONArray.fromObject(this.messages).toString();
	}
	
	/**
	 * 输出所有信息
	 */
	@SuppressWarnings("unchecked")
	public void print(){
		String string = this.toJSONString();
		JSONArray array = JSONArray.fromObject(string);
		Iterator i = array.iterator();
		while(i.hasNext()){
			JSONObject obj = (JSONObject)i.next();
			MessageObject mo = (MessageObject)JSONObject.toBean(obj, MessageObject.class);
			System.out.println(mo.toString());
		}
	}
	
	/**
	 * 玩家回合开始
	 * 
	 * @param player
	 */
	public void playerRoundStart(Player player){
		this.line();
		this.action(player, "回合开始!");
	}
	
	/**
	 * 玩家回合结束
	 * 
	 * @param player
	 */
	public void playerRoundEnd(Player player){
		this.action(player, "回合结束!");
		this.line();
	}
	
	/**
	 * 游戏即将结束时的提醒
	 */
	public void gameOverWarning(){
		this.system("游戏即将结束!");
	}
	
	public static void main(String[] args){
		/*GameRoom<?> room = new GameRoom<PuertoRico>("123");
		room.type = GameType.波多黎各;
		PuertoRico game = new PuertoRico();
		game.room = room;
		BgReport rpt = new BgReport(game);
		rpt.start();
		rpt.system("第1回合");
		User u = new User(null);
		u.name = "F14eagle";
		PRPlayer p = new PRPlayer();
		p.user = u;
		
		p.position = 1;
		
		rpt.action(p, "得到1块钱");
		rpt.action(p, "建造了建筑.");
		rpt.info("清空了交易所");
		rpt.action(p, "卖出了玉米,得到1块钱");
		
		rpt.end();
		rpt.print();*/
	}
}
