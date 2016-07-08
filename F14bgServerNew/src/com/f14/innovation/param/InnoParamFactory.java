package com.f14.innovation.param;


public class InnoParamFactory {

	public static InnoInitParam createInitParam(){
		InnoInitParam res = new InnoInitParam();
		return res;
	}
	
	public static InnoInitParam createInitParam(int num, int level){
		InnoInitParam res = new InnoInitParam();
		res.num = num;
		res.level = level;
		return res;
	}
}
