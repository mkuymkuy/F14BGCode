package com.f14.Eclipse.listener;

import com.f14.Eclipse.EclipseGameMode;
import com.f14.bg.listener.ActionListener;
import com.f14.bg.listener.ListenerType;

/**
 * Eclipse的行动监听器基类
 * 
 * @author F14eagle
 *
 */
public abstract class EclipseActionListener extends ActionListener<EclipseGameMode> {

	public EclipseActionListener() {
		super();
	}

	public EclipseActionListener(ListenerType listenerType) {
		super(listenerType);
	}

}
