package com.f14.TS;

import java.util.HashMap;
import java.util.Map;

import com.f14.TS.component.TSCard;
import com.f14.bg.component.Convertable;

/**
 * 行动记录
 * 
 * @author F14eagle
 *
 */
public class ActionRecord implements Convertable {
	public TSPlayer player;
	public TSCard card;
	public String message;
	
	public ActionRecord(TSPlayer player, TSCard card, String message){
		this.player = player;
		this.card = card;
		this.message = message;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("superPower", player.superPower);
		res.put("playerName", player.getName());
		res.put("cardId", card.id);
		res.put("message", message);
		return res;
	}

}
