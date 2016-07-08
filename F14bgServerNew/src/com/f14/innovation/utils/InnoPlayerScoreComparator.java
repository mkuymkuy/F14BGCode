package com.f14.innovation.utils;

import java.util.Comparator;

import com.f14.innovation.InnoPlayer;

/**
 * 玩家的得分比较器
 * 
 * @author F14eagle
 *
 */
public class InnoPlayerScoreComparator implements Comparator<InnoPlayer> {

	@Override
	public int compare(InnoPlayer o1, InnoPlayer o2) {
		int s1 = o1.getScore();
		int s2 = o2.getScore();
		if(s1>s2){
			return 1;
		}else if(s1<s2){
			return -1;
		}else{
			return 0;
		}
	}

}
