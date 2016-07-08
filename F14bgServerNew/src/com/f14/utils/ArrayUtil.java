package com.f14.utils;

public class ArrayUtil {

	/**
	 * clone int array
	 * 
	 * @param in
	 * @return
	 */
	public static int[] cloneArray(int[] in){
		int[] out = new int[in.length];
		for(int i=0;i<in.length;i++){
			out[i] = in[i];
		}
		return out;
	}
}
