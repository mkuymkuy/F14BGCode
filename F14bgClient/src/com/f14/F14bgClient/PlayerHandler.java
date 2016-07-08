package com.f14.F14bgClient;

import java.io.IOException;
import java.net.Socket;

import org.eclipse.swt.widgets.Display;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bgClient.manager.ManagerContainer;
import com.f14.F14bgClient.update.DefaultUpdaterListener;
import com.f14.bg.action.BgAction;
import com.f14.net.socket.client.SocketHandler;
import com.f14.net.socket.cmd.ByteCommand;
import com.f14.net.socket.cmd.CommandSender;

public class PlayerHandler extends SocketHandler {
	protected CommandSender sender;

	public PlayerHandler(Socket socket) throws IOException, InterruptedException {
		super(socket);
		this.sender = new CommandSender(socket);
	}

	@Override
	protected void onSocketClose() throws IOException {
		//连接中断时,需要跳转到登录界面
		//需要使用UI线程进行界面操作
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				ManagerContainer.shellManager.showLoginShell();
			}
		});
	}

	@Override
	protected void onSocketConnect() throws IOException {

	}

	@Override
	protected void processCommand(ByteCommand cmd) throws IOException {
		switch(cmd.flag){
		case CmdConst.EXCEPTION_CMD: //错误信息提示
			F14bgClient.getInstance().sendErrorMessage(cmd.roomId, cmd.getContent());
			break;
		case CmdConst.CLIENT_CMD: //客户端指令
			this.processClientCommand(cmd);
			break;
		default: //处理其他指令
			F14bgClient.getInstance().sendCommand(cmd.roomId, cmd.getContent());
			break;
		}
	}

	@Override
	public void sendCommand(ByteCommand cmd) throws IOException {
		this.sender.sendCommand(cmd);
	}
	
	/**
	 * 处理客户端类型的行动
	 * 
	 * @param act
	 */
	protected void processClientCommand(ByteCommand cmd){
		final BgAction act = new BgAction(cmd.getContent());
		switch (act.getCode()) {
			case CmdConst.CLIENT_LOAD_CODE: //读取系统代码
				ManagerContainer.codeManager.loadCodeParam(act);
				break;
			case CmdConst.CLIENT_OPEN_ROOM: //打开游戏窗口
			{
				final int roomId = act.getAsInt("id");
				final String gameType = act.getAsString("gameType");
				//检查版本后再打开房间窗口
				ManagerContainer.updateManager.executeUpdate(gameType, new DefaultUpdaterListener(){
					@Override
					public void onUpdateSuccess(boolean updated) {
						//更新成功后,打开房间窗口
						Display.getDefault().asyncExec(new Runnable(){
							@Override
							public void run() {
								ManagerContainer.shellManager.createRoomShell(roomId, gameType);
							}
						});
					}
				});
			}
				break;
			case CmdConst.CLIENT_INIT_RESOURCE: //装载系统资源
			{
				String gameType = act.getAsString("gameType");
				String resString = act.getJSONString();
				ManagerContainer.resourceManager.setResourceString(gameType, resString);
				//继续检查文件更新
				ManagerContainer.updateManager.loadResouceSuccess(true);
				//解锁
				//ManagerContainer.resourceManager.notifyLock(gameType);
			}
				break;
			case CmdConst.CLIENT_CLOSE_ROOM: //关闭房间窗口
			{
				final int roomId = act.getAsInt("roomId");
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						ManagerContainer.shellManager.disposeRoomShell(roomId);
					}
				});
			}break;
			case CmdConst.CLIENT_LEAVE_ROOM_CONFIRM: //询问是否关闭窗口
			{
				final int roomId = act.getAsInt("roomId");
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						ManagerContainer.shellManager.disposeConfirmRoomShell(roomId);
					}
				});
			}
				break;
			case CmdConst.CLIENT_CHECK_UPDATE: //文件更新
			{
				String gameType = act.getAsString("gameType");
				String versionString = act.getAsString("versionString");
				String files = act.getAsString("files");
				ManagerContainer.updateManager.setUpdateFiles(gameType, versionString, files);
			}break;
			case CmdConst.CLIENT_USER_INFO: //查看用户信息
			{
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						ManagerContainer.shellManager.showUserShell(act.getJSONString());
						//ManagerContainer.shellManager.userShell.loadUserParam(act.getJSONString());
					}
				});
			}break;
			case CmdConst.CLIENT_BROADCAST: //广播消息
			{
				final String message = act.getAsString("message");
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						ManagerContainer.shellManager.alert(message);
					}
				});
			}break;
			case CmdConst.CLIENT_HALL_NOTICE: //打开大厅公告面板
			{
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						ManagerContainer.shellManager.hallShell.showHallNotice();
					}
				});
			}break;
			case CmdConst.CLIENT_BUBBLE_NOTIFY: //显示气泡通知
			{
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run() {
						ManagerContainer.notifyManager.showNotify(act);
					}
				});
			}break;
			default:
				break;
		}
	}

}
