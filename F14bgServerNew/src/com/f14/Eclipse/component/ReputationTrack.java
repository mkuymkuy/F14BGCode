package com.f14.Eclipse.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.Eclipse.consts.ReputationSquareType;
import com.f14.bg.component.Convertable;
import com.f14.bg.utils.BgUtils;

public class ReputationTrack implements Convertable {
	protected List<ReputationSquare> squares = new ArrayList<ReputationSquare>();
	public int single;
	public int multiple;
	public int getSingle() {
		return single;
	}
	public void setSingle(int single) {
		this.single = single;
	}
	public int getMultiple() {
		return multiple;
	}
	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}
	
	/**
	 * 初始化影响力方格
	 */
	public void initReputationSquare(){
		int index = 0;
		for(int i=0;i<single;i++){
			ReputationSquare o = new ReputationSquare();
			o.index = index;
			o.reputationSquareType = ReputationSquareType.SINGLE;
			this.squares.add(o);
			index += 1;
		}
		for(int i=0;i<multiple;i++){
			ReputationSquare o = new ReputationSquare();
			o.index = index;
			o.reputationSquareType = ReputationSquareType.MULTIPLE;
			this.squares.add(o);
			index += 1;
		}
	}
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("squares", BgUtils.toMapList(this.squares));
		return res;
	}
}
