package com.f14.net.socket.cmd;

public class CommandFactory {

	public static ByteCommand createCommand(int flag, int roomId, String content){
		ByteCommand cmd = new ByteCommand();
		cmd.head = ByteCommand.BYTE_HEAD;
		cmd.flag = flag;
		cmd.roomId = roomId;
		cmd.tail = ByteCommand.BYTE_TAIL;
		if(content==null || content.length()==0){
			content = "-";
		}
		cmd.setContent(content);
		return cmd;
	}
}
