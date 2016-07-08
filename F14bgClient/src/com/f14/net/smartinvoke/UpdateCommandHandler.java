package com.f14.net.smartinvoke;

import java.util.Map;

import cn.smartinvoke.RemoteObject;
import cn.smartinvoke.gui.FlashContainer;

public class UpdateCommandHandler extends RemoteObject {
	public UpdateCommandHandler(FlashContainer container) {
		super(container);
		this.createRemoteObject();
	}

	public void loadParam(Map<String, String> param) {
		this.asyncCall("loadParam", new Object[] { param });
	}
	
	public void refreshSize(int currentSize) {
		this.asyncCall("refreshSize", new Object[] { currentSize });
	}
	
}
