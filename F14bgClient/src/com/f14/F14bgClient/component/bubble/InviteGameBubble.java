package com.f14.F14bgClient.component.bubble;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.f14.F14bgClient.component.RoomShell;
import com.f14.F14bgClient.event.FlashShellEvent;
import com.f14.F14bgClient.event.FlashShellListener;
import com.f14.F14bgClient.manager.ManagerContainer;


public class InviteGameBubble extends Bubble {
	private static final long serialVersionUID = 1L;
	
	protected int roomId;

	@Override
	protected void initComponents() {
		super.initComponents();
		this._message.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		this._message.addMouseListener(new InviteGameMouseListener());
	}
	
	@Override
	public void setMessage(String message) {
		super.setMessage(message + "\n\n点我直接进入房间...");
	}
	
	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	
	/**
	 * 加入房间
	 */
	protected void joinRoom(){
		this.setVisible(false);
		this.setTimeout(999999);
		
		//检查玩家是否已经加入了其他房间
		RoomShell shell = ManagerContainer.shellManager.getCurrentRoomShell();
		if(shell==null){
			//没有加入房间则直接加入指定的房间
			ManagerContainer.actionManager.joinRoomCheck(this.roomId);
		}else{
			//否则需要先退出原先的房间后再加入
			shell.addFlashShellListener(new FlashShellListener(){
				@Override
				public void onShellDisposed(FlashShellEvent e) {
					ManagerContainer.actionManager.joinRoomCheck(InviteGameBubble.this.roomId);
				}
			});
			ManagerContainer.actionManager.leaveRequest(shell.getRoomId());
		}
	}

	class InviteGameMouseListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			//点击就加入房间
			InviteGameBubble.this.joinRoom();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}
		
	}
	
	
}
