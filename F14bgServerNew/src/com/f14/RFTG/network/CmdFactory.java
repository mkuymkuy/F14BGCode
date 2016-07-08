package com.f14.RFTG.network;

import com.f14.bg.action.BgResponse;
import com.f14.bg.chat.Message;
import com.f14.net.socket.cmd.ByteCommand;
import com.f14.net.socket.cmd.CommandFactory;

/**
 * 指令工厂类
 * 
 * @author F14eagle
 *
 */
public class CmdFactory {

	/**
	 * 创建基本的指令
	 * 
	 * @param content
	 * @return
	 */
	public static ByteCommand createCommand(int roomId, String content){
		return CommandFactory.createCommand(CmdConst.APPLICATION_FLAG, roomId, content);
	}
	
	/**
	 * 创建请求输入的游戏指令
	 * 
	 * @param code
	 * @param position
	 * @return
	 */
	public static BgResponse createGameResponse(int code, int position){
		return new BgResponse(CmdConst.GAME_CMD, code, position, false);
	}
	
	/**
	 * 创建对输入回应的游戏指令
	 * 
	 * @param code
	 * @param position
	 * @return
	 */
	public static BgResponse createGameResultResponse(int code, int position){
		return new BgResponse(CmdConst.GAME_CMD, code, position, true);
	}
	
	/**
	 * 创建聊天指令
	 * 
	 * @param message
	 * @return
	 */
	public static BgResponse createChatResponse(Message message){
		BgResponse res = new BgResponse(CmdConst.CHAT_CMD, 0, 0, false);
		res.setPublicParameter("message", message);
		return res;
	}
	
	/**
	 * 创建请求输入的系统指令
	 * 
	 * @param code
	 * @param position
	 * @return
	 */
	public static BgResponse createSystemResponse(int code, int position){
		return new BgResponse(CmdConst.SYSTEM_CMD, code, position, false);
	}
	
	/**
	 * 创建对输入回应的系统指令
	 * 
	 * @param code
	 * @param position
	 * @return
	 */
	public static BgResponse createSystemResultResponse(int code, int position){
		return new BgResponse(CmdConst.SYSTEM_CMD, code, position, true);
	}
	
	/**
	 * 创建玩家摸牌的指令
	 * 
	 * @param position
	 * @param cardIds
	 * @return
	 */
	public static BgResponse createDrawCardResponse(int position, String cardIds){
		BgResponse res = new BgResponse(CmdConst.GAME_CMD, CmdConst.GAME_CODE_DRAW_CARD, position, true);
		String[] ids = cardIds.split(",");
		res.setPrivateParameter("cardIds", cardIds);
		res.setPublicParameter("cardNum", ids.length);
		return res;
	}
	
	/**
	 * 创建玩家摸牌的指令
	 * 
	 * @param position
	 * @param cardIds
	 * @return
	 */
	public static BgResponse createDiscardResponse(int position, String cardIds){
		BgResponse res = new BgResponse(CmdConst.GAME_CMD, CmdConst.GAME_CODE_DISCARD, position, true);
		String[] ids = cardIds.split(",");
		res.setPrivateParameter("cardIds", cardIds);
		res.setPublicParameter("cardNum", ids.length);
		return res;
	}
	
	/**
	 * 创建玩家从手牌中打出牌的指令
	 * 
	 * @param position
	 * @param cardIds
	 * @return
	 */
	public static BgResponse createPlayCardResponse(int position, String cardIds){
		BgResponse res = new BgResponse(CmdConst.GAME_CMD, CmdConst.GAME_CODE_PLAY_CARD, position, true);
		String[] ids = cardIds.split(",");
		res.setPublicParameter("cardIds", cardIds);
		res.setPublicParameter("cardNum", ids.length);
		return res;
	}
	
	/**
	 * 创建玩家直接打出牌的指令
	 * 
	 * @param position
	 * @param cardIds
	 * @return
	 */
	public static BgResponse createDirectPlayCardResponse(int position, String cardIds){
		BgResponse res = new BgResponse(CmdConst.GAME_CMD, CmdConst.GAME_CODE_DIRECT_PLAY_CARD, position, true);
		String[] ids = cardIds.split(",");
		res.setPublicParameter("cardIds", cardIds);
		res.setPublicParameter("cardNum", ids.length);
		return res;
	}
	
	/**
	 * 创建玩家弃掉货物的指令
	 * 
	 * @param position
	 * @param cardIds
	 * @return
	 */
	public static BgResponse createDiscardGoodResponse(int position, String cardIds){
		BgResponse res = new BgResponse(CmdConst.GAME_CMD, CmdConst.GAME_CODE_DISCARD_GOOD, position, true);
		String[] ids = cardIds.split(",");
		res.setPublicParameter("cardIds", cardIds);
		res.setPublicParameter("cardNum", ids.length);
		return res;
	}
	
	/**
	 * 创建玩家的卡牌能力生效的指令
	 * 
	 * @param position
	 * @param cardIds
	 * @return
	 */
	public static BgResponse createCardEffectResponse(int position, String cardIds){
		BgResponse res = new BgResponse(CmdConst.GAME_CMD, CmdConst.GAME_CODE_CARD_EFFECT, position, true);
		String[] ids = cardIds.split(",");
		res.setPublicParameter("cardIds", cardIds);
		res.setPublicParameter("cardNum", ids.length);
		return res;
	}
	
	/**
	 * 创建玩家弃掉已打出卡牌的指令
	 * 
	 * @param position
	 * @param cardIds
	 * @return
	 */
	public static BgResponse createDiscardPlayedCardResponse(int position, String cardIds){
		BgResponse res = new BgResponse(CmdConst.GAME_CMD, CmdConst.GAME_CODE_DISCARD_PLAYED_CARD, position, true);
		String[] ids = cardIds.split(",");
		res.setPublicParameter("cardIds", cardIds);
		res.setPublicParameter("cardNum", ids.length);
		return res;
	}
	
	/**
	 * 创建玩家使用卡牌的指令
	 * 
	 * @param position
	 * @param cardIds
	 * @return
	 */
	public static BgResponse createUseCardResponse(int position, String cardIds){
		BgResponse res = new BgResponse(CmdConst.GAME_CMD, CmdConst.GAME_CODE_USE_CARD, position, true);
		String[] ids = cardIds.split(",");
		res.setPublicParameter("cardIds", cardIds);
		res.setPublicParameter("cardNum", ids.length);
		return res;
	}
	
	/**
	 * 创建星球生产货物的指令
	 * 
	 * @param position
	 * @param cardIds
	 * @return
	 */
	public static BgResponse createProduceGoodResponse(int position, String cardIds){
		BgResponse res = new BgResponse(CmdConst.GAME_CMD, CmdConst.GAME_CODE_PRODUCE_GOOD, position, true);
		String[] ids = cardIds.split(",");
		res.setPublicParameter("cardIds", cardIds);
		res.setPublicParameter("cardNum", ids.length);
		return res;
	}
	
	/**
	 * 创建玩家得到VP的指令
	 * 
	 * @param position
	 * @param vp 得到的vp
	 * @param remainvp 游戏剩余的vp
	 * @return
	 */
	public static BgResponse createGetVPResponse(int position, int vp, int remainvp){
		BgResponse res = new BgResponse(CmdConst.GAME_CMD, CmdConst.GAME_CODE_GET_VP, position, true);
		res.setPublicParameter("vp", vp);
		res.setPublicParameter("remainvp", remainvp);
		return res;
	}
}
