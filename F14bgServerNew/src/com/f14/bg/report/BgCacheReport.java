package com.f14.bg.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.bg.BoardGame;
import com.f14.bg.player.Player;

/**
 * 带有缓存的战报记录
 * 
 * @author F14eagle
 *
 */
public class BgCacheReport extends BgReport {
	/**
	 * 所有玩家的战报缓存记录
	 */
	protected Map<Player, List<MessageObject>> messageCache = new HashMap<Player, List<MessageObject>>();

	public BgCacheReport(BoardGame<?, ?> bg) {
		super(bg);
	}
	
	/**
	 * 取得玩家的所有缓存信息
	 * 
	 * @param player
	 * @return
	 */
	protected List<MessageObject> getCacheMessages(Player player){
		List<MessageObject> messages = this.messageCache.get(player);
		if(messages==null){
			messages = new ArrayList<MessageObject>();
			this.messageCache.put(player, messages);
		}
		return messages;
	}
	
	/**
	 * 清空玩家的缓存信息
	 * 
	 * @param player
	 */
	protected void clearPlayerCache(Player player){
		this.getCacheMessages(player).clear();
	}
	
	/**
	 * 玩家执行动作,并输出该玩家所有缓存的信息
	 */
	@Override
	public void action(Player player, String message) {
		String text = message;
		for(MessageObject mo : this.getCacheMessages(player)){
			text += "," + mo.message;
		}
		this.clearPlayerCache(player);
		super.action(player, text);
	}
	
	/**
	 * 添加玩家行动到缓存中
	 * 
	 * @param player
	 * @param message
	 */
	public void addAction(Player player, String message){
		MessageObject mo = new MessageObject(null, message, null, false);
		this.getCacheMessages(player).add(mo);
	}
	
	/**
	 * 输出玩家当前缓存内容(如果没有缓存内容则不输出)
	 * 
	 * @param player
	 */
	public void printCache(Player player){
		if(!this.getCacheMessages(player).isEmpty()){
			this.action(player, "");
		}
	}

}
