package com.f14.innovation.listener;

import com.f14.bg.listener.ActionListener;
import com.f14.bg.listener.ListenerType;
import com.f14.innovation.InnoGameMode;

/**
 * Innovation的行动监听器基类
 * 
 * @author F14eagle
 *
 */
public abstract class InnoActionListener extends ActionListener<InnoGameMode> {

	public InnoActionListener() {
		super();
	}

	public InnoActionListener(ListenerType listenerType) {
		super(listenerType);
	}

}
