package com.f14.F14bg.network;

import com.f14.F14bg.consts.CmdConst;
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
	 * 创建聊天指令
	 * 
	 * @param message
	 * @return
	 */
	public static BgResponse createChatResponse(Message message){
		BgResponse res = new BgResponse(CmdConst.CHAT_CMD, CmdConst.CHAT_CODE_MESSAGE, 0, false);
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
	 * 创建客户端相关操作的指令
	 * 
	 * @param code
	 * @param position
	 * @return
	 */
	public static BgResponse createClientResponse(int code){
		return new BgResponse(CmdConst.CLIENT_CMD, code, -1, false);
	}
	
}
