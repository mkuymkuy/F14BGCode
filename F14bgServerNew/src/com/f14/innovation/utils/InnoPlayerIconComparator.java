package com.f14.innovation.utils;

import java.util.Comparator;

import com.f14.innovation.InnoPlayer;
import com.f14.innovation.consts.InnoIcon;

/**
 * 玩家的符号比较器
 * 
 * @author F14eagle
 *
 */
public class InnoPlayerIconComparator implements Comparator<InnoPlayer> {
	protected InnoIcon icon;
	
	public InnoPlayerIconComparator(InnoIcon icon){
		this.icon = icon;
	}

	@Override
	public int compare(InnoPlayer o1, InnoPlayer o2) {
		int s1 = o1.getIconCount(icon);
		int s2 = o2.getIconCount(icon);
		if(s1>s2){
			return 1;
		}else if(s1<s2){
			return -1;
		}else{
			return 0;
		}
	}

}
