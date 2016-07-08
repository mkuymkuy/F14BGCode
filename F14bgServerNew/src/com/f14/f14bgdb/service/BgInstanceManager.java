package com.f14.f14bgdb.service;

import com.f14.bg.VPResult;
import com.f14.bg.exception.BoardGameException;
import com.f14.f14bgdb.model.BgInstance;
import com.f14.framework.common.service.BaseManager;

public interface BgInstanceManager extends BaseManager<BgInstance, Long> {

	/**
	 * 保存游戏结果
	 * 
	 * @param result
	 * @throws BoardGameException
	 */
	public BgInstance saveGameResult(VPResult result) throws BoardGameException;
	
	/**
	 * 保存游戏战报
	 * 
	 * @param o
	 * @param descr
	 * @throws BoardGameException
	 */
	public void saveGameReport(BgInstance o, String descr) throws BoardGameException;
}
