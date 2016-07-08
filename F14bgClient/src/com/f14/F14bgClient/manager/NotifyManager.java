package com.f14.F14bgClient.manager;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.Icon;

import com.f14.F14bgClient.component.bubble.Bubble;
import com.f14.F14bgClient.component.bubble.InviteGameBubble;
import com.f14.F14bgClient.consts.NotifyType;
import com.f14.bg.action.BgAction;

/**
 * 通知消息的管理器
 * 
 * @author F14eagle
 *
 */
public class NotifyManager {
	// 气泡提示宽
    public static int BUBBLE_WIDTH = 300;
    // 气泡提示高
    public static int BUBBLE_HEIGHT = 100;
    // 设定循环的步长
    public static int STEP = 30;

    // 每步时间
    public static int STEP_TIME = 30;

    // 显示时间
    public static int DISPLAY_TIME = 6000;

    // 目前申请的气泡提示数量
    public static int COUNT_OF_TOOLTIP = 0;

    // 当前最大气泡数
    public static int MAX_TOOLTIP = 0;

    // 在屏幕上显示的最大气泡提示数量
    public static int MAX_TOOLTIP_SCEEN;

    // 字体
    public static Font MESSAGE_FONT;

    // 边框颜色
    public static Color BG_COLOR;

    // 背景颜色
    public static Color BORDER;

    // 消息颜色
    public static Color MESSAGE_COLOR;

    // 差值设定
    public static int GAP;

    // 是否要求至顶（jre1.5以上版本方可执行）
    public static boolean USE_TOP = true;
    
    static{
    	// 设定字体
        MESSAGE_FONT = new Font("宋体", 0, 12);
        // 设定边框颜色
        BG_COLOR = new Color(255, 255, 225);
        BORDER = Color.BLACK;
        MESSAGE_COLOR = Color.BLACK;
        USE_TOP = true;
        // 通过调用方法，强制获知是否支持自动窗体置顶
//        try {
//            JWindow.class.getMethod("setAlwaysOnTop",
//                    new Class[] { Boolean.class });
//        } catch (Exception e) {
//        	USE_TOP = false;
//        }
    }
    
	protected NotifyHelper notifyHelper;
	
	public NotifyManager(){
		this.notifyHelper = new NotifyHelper();
	}

	/**
	 * 显示气泡通知
	 * 
	 * @param act
	 */
	public void showNotify(BgAction act){
		String nt = act.getAsString("notifyType");
		NotifyType notifyType = NotifyType.valueOf(nt);
		String message = act.getAsString("message");
		switch(notifyType){
		case CREATE_ROOM:
			int roomId = act.getAsInt("roomId");
			this.showCreateRoomNotiry(message, roomId);
			break;
		default:
			this.showNotiry(message);
			break;
		}
	}
	
	/**
	 * 显示气泡通知
	 * 
	 * @param message
	 */
	public void showNotiry(String message){
		notifyHelper.setToolTip(message);
	}
	
	/**
	 * 显示气泡通知
	 * 
	 * @param message
	 */
	public void showCreateRoomNotiry(String message, int roomId){
		InviteGameBubble b = new InviteGameBubble();
		b.setMessage(message);
		b.setRoomId(roomId);
		notifyHelper.setToolTip(b);
	}
	
	/**
	 * 
	 * 
	 * @author F14eagle
	 *
	 */
	class NotifyHelper {

	    /**
	     * 构造函数，初始化默认气泡提示设置
	     *
	     */
	    public NotifyHelper() {
	        
	    }

	    /**
	     * 此类处则动画处理
	     *
	     */
	    class Animation extends Thread {

	        Bubble _single;

	        public Animation(Bubble single) {
	            this._single = single;
	        }

	        /**
	         * 调用动画效果，移动窗体坐标
	         *
	         * @param posx
	         * @param startY
	         * @param endY
	         * @throws InterruptedException
	         */
	        private void animateVertically(int posx, int startY, int endY)
	                throws InterruptedException {
	            _single.setLocation(posx, startY);
	            if (endY < startY) {
	                for (int i = startY; i > endY; i -= STEP) {
	                    _single.setLocation(posx, i);
	                    Thread.sleep(STEP_TIME);
	                }
	            } else {
	                for (int i = startY; i < endY; i += STEP) {
	                    _single.setLocation(posx, i);
	                    Thread.sleep(STEP_TIME);
	                }
	            }
	            _single.setLocation(posx, endY);
	        }

	        /**
	         * 开始动画处理
	         */
	        public void run() {
	            try {
	                boolean animate = true;
	                GraphicsEnvironment ge = GraphicsEnvironment
	                        .getLocalGraphicsEnvironment();
	                Rectangle screenRect = ge.getMaximumWindowBounds();
	                int screenHeight = (int) screenRect.height;
	                int startYPosition;
	                int stopYPosition;
	                if (screenRect.y > 0) {
	                    animate = false;
	                }
	                MAX_TOOLTIP_SCEEN = screenHeight / BUBBLE_HEIGHT;
	                int posx = (int) screenRect.width - BUBBLE_WIDTH - 1;
	                _single.setLocation(posx, screenHeight);
	                _single.setVisible(true);
	                if (USE_TOP) {
	                    _single.setAlwaysOnTop(true);
	                }
	                if (animate) {
	                    startYPosition = screenHeight;
	                    stopYPosition = startYPosition - BUBBLE_HEIGHT - 1;
	                    if (COUNT_OF_TOOLTIP > 0) {
	                        stopYPosition = stopYPosition
	                                - (MAX_TOOLTIP % MAX_TOOLTIP_SCEEN * BUBBLE_HEIGHT);
	                    } else {
	                        MAX_TOOLTIP = 0;
	                    }
	                } else {
	                    startYPosition = screenRect.y - BUBBLE_HEIGHT;
	                    stopYPosition = screenRect.y;

	                    if (COUNT_OF_TOOLTIP > 0) {
	                        stopYPosition = stopYPosition
	                                + (MAX_TOOLTIP % MAX_TOOLTIP_SCEEN * BUBBLE_HEIGHT);
	                    } else {
	                    	MAX_TOOLTIP = 0;
	                    }
	                }

	                COUNT_OF_TOOLTIP++;
	                MAX_TOOLTIP++;

	                animateVertically(posx, startYPosition, stopYPosition);
	                //当鼠标不在该气泡上超过DISPLAY_TIME,则气泡消失
	                while(_single.getTimeout()<DISPLAY_TIME){
	                	Thread.sleep(1000);
	                	_single.refreshTimeout(1000);
	                }
	                animateVertically(posx, stopYPosition, startYPosition);

	                COUNT_OF_TOOLTIP--;
	                _single.setVisible(false);
	                _single.dispose();
	            } catch (Exception e) {
	                throw new RuntimeException(e);
	            }
	        }
	    }

	    /**
	     * 设定显示的图片及信息
	     *
	     * @param icon
	     * @param msg
	     */
	    public void setToolTip(Icon icon, String msg) {
	        Bubble single = new Bubble();
	        if (icon != null) {
	            single.setIcon(icon);
	        }
	        single.setMessage(msg);
	        new Animation(single).start();
	    }

	    /**
	     * 设定显示的信息
	     *
	     * @param msg
	     */
	    public void setToolTip(String msg) {
	        setToolTip(null, msg);
	    }
	    
	    /**
	     * 显示指定的气泡
	     * 
	     * @param bubble
	     */
	    public void setToolTip(Bubble bubble){
	    	new Animation(bubble).start();
	    }

	}
	

    public static void main(String[] args) {
    	NotifyManager manager = new NotifyManager();
    	for(int i=0;i<5;i++){
    		manager.showCreateRoomNotiry("快加我"+i,i);
    	}
    }
}
