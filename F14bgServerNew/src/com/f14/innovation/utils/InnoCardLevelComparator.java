package com.f14.innovation.utils;

import java.util.Comparator;

import com.f14.innovation.component.InnoCard;

/**
 * 卡牌的等级比较器
 * 
 * @author F14eagle
 *
 */
public class InnoCardLevelComparator implements Comparator<InnoCard> {

	@Override
	public int compare(InnoCard o1, InnoCard o2) {
		if(o1.level>o2.level){
			return 1;
		}else if(o1.level<o2.level){
			return -1;
		}else{
			return 0;
		}
	}

}
