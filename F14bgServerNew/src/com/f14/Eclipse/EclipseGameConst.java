package com.f14.Eclipse;

public class EclipseGameConst {

	private static int[] resourceAddValues = new int[]{
		28, 24, 21, 18, 15, 12, 10, 8, 6, 4, 3, 2, 0
	};
	
	private static int[] upkeepValue = new int[]{
		-30, -25, -21, -17, -13, -10, -7, -5, -3, -2, -1, 0, 0
	};
	
	/**
	 * 按照人口数量取得资源增量
	 * 
	 * @param population
	 * @return
	 */
	public static int getResourceAddValue(int population){
		if(population>=resourceAddValues.length){
			return 0;
		}
		return resourceAddValues[population];
	}
	
	/**
	 * 按照影响力圆片数量取得维护费用
	 * 
	 * @param influenceDisc
	 * @return
	 */
	public static int getUpkeepValue(int influenceDisc){
		if(influenceDisc>=upkeepValue.length){
			return 0;
		}
		return upkeepValue[influenceDisc];
	}
}
