package com.f14.net.smartinvoke;

import cn.smartinvoke.RemoteObject;
import cn.smartinvoke.gui.FlashContainer;

public class QueryCommandHandler extends RemoteObject {
	public QueryCommandHandler(FlashContainer container) {
		super(container);
		this.createRemoteObject();
	}

	public void loadUserParam(String paramString) {
		this.call("loadUserParam", new Object[] { paramString });
	}
	
}
