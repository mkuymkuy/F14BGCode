package com.f14.TTA.component;

import com.f14.TTA.consts.ChooserType;
import com.f14.TTA.consts.CivilizationProperty;

/**
 * 文明选择器,按照设定的条件取得文明
 * 
 * @author F14eagle
 *
 */
public class Chooser {
	public ChooserType type;
	public int num;
	public CivilizationProperty byProperty;
	public boolean weakest;

	public ChooserType getType() {
		return type;
	}

	public void setType(ChooserType type) {
		this.type = type;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public CivilizationProperty getByProperty() {
		return byProperty;
	}

	public void setByProperty(CivilizationProperty byProperty) {
		this.byProperty = byProperty;
	}

	public boolean isWeakest() {
		return weakest;
	}

	public void setWeakest(boolean weakest) {
		this.weakest = weakest;
	}

}