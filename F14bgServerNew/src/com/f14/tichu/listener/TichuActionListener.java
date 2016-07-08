package com.f14.tichu.listener;

import com.f14.bg.listener.ActionListener;
import com.f14.bg.listener.ListenerType;
import com.f14.tichu.TichuGameMode;

/**
 * tichu的行动监听器
 * 
 * @author F14eagle
 *
 */
public abstract class TichuActionListener extends ActionListener<TichuGameMode> {

	public TichuActionListener() {
		super();
	}

	public TichuActionListener(ListenerType listenerType) {
		super(listenerType);
	}

}
