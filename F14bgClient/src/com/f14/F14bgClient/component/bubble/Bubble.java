package com.f14.F14bgClient.component.bubble;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.border.EtchedBorder;

import com.f14.F14bgClient.manager.NotifyManager;

/**
 * 重构JWindow用于显示单一气泡提示框
 *
 */
public class Bubble extends JWindow {
    private static final long serialVersionUID = 1L;

    protected JLabel _iconLabel = new JLabel();
    protected JTextArea _message = new JTextArea();
    
    private long timeout = 0;
    private boolean onFocus = false;
    
    public Bubble() {
        initComponents();
    }

    protected void initComponents() {
        setSize(NotifyManager.BUBBLE_WIDTH, NotifyManager.BUBBLE_HEIGHT);
        _message.setFont(NotifyManager.MESSAGE_FONT);
        JPanel externalPanel = new JPanel(new BorderLayout(1, 1));
        externalPanel.setBackground(NotifyManager.BG_COLOR);
        // 通过设定水平与垂直差值获得内部面板
        JPanel innerPanel = new JPanel(new BorderLayout(NotifyManager.GAP, NotifyManager.GAP));
        innerPanel.setBackground(NotifyManager.BG_COLOR);
        _message.setBackground(NotifyManager.BG_COLOR);
        _message.setMargin(new Insets(4, 4, 4, 4));
        _message.setLineWrap(true);
        _message.setWrapStyleWord(true);
        // 创建具有指定高亮和阴影颜色的阴刻浮雕化边框
        EtchedBorder etchedBorder = (EtchedBorder) BorderFactory
                .createEtchedBorder();
        // 设定外部面板内容边框为风化效果
        externalPanel.setBorder(etchedBorder);
        // 加载内部面板
        externalPanel.add(innerPanel);
        _message.setForeground(NotifyManager.MESSAGE_COLOR);
        //_message.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        innerPanel.add(_iconLabel, BorderLayout.WEST);
        innerPanel.add(_message, BorderLayout.CENTER);
        getContentPane().add(externalPanel);
        
        _message.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				Bubble.this.onFocus = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				Bubble.this.onFocus = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
        	
        });
    }

	public void setIcon(Icon icon) {
		this._iconLabel.setIcon(icon);
	}

	public void setMessage(String message) {
		this._message.setText(message);
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void refreshTimeout(long timeout){
		if(this.onFocus){
			this.timeout = 0;
		}else{
			this.timeout += timeout;
		}
	}

}