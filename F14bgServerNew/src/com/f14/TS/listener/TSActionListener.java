package com.f14.TS.listener;

import com.f14.TS.TSGameMode;
import com.f14.bg.listener.ActionListener;
import com.f14.bg.listener.ListenerType;

/**
 * TS的监听器基类
 * 
 * @author F14eagle
 *
 */
public abstract class TSActionListener extends ActionListener<TSGameMode> {

	public TSActionListener() {
		super();
	}

	public TSActionListener(ListenerType listenerType) {
		super(listenerType);
	}

}
