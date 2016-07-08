package com.f14.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CollectionUtils {

	/**
	 * 将指定的集合分成blockNum个集合后打乱再进行重组
	 * 
	 * @param <E>
	 * @param coll
	 * @param blockNum
	 * @return
	 */
	public static <E> void shuffle(List<E> coll, int blockNum){
		if(coll.size()<blockNum*3){
			Collections.shuffle(coll);
			return;
		}
		List<E> list = new ArrayList<E>();
		list.addAll(coll);
		Collections.shuffle(list);
		coll.clear();
		int blockSize = (int)Math.ceil((double)list.size()/blockNum);
		List<List<E>> ll = new ArrayList<List<E>>();
		for(int i=0;i<blockNum;i++){
			int endIndex = Math.min((i+1)*blockSize, list.size());
			List<E> sublist = list.subList(i*blockSize, endIndex);
			Collections.shuffle(sublist);
			ll.add(sublist);
		}
		for(int i=0;i<blockSize;i++){
			for(List<E> sublist : ll){
				if(i<sublist.size()){
					coll.add(sublist.get(i));
				}
			}
		}
	}
	
	/**
	 * 随机打乱指定的集合
	 * 
	 * @param <E>
	 * @param coll
	 * @return
	 */
	public static <E> void shuffle(List<E> coll){
		List<E> tmp = new ArrayList<E>(coll);
		coll.clear();
		Random ran = new Random();
		Random ran2 = new Random();
		Collections.shuffle(tmp, ran);
		while(!tmp.isEmpty()){
			coll.add(tmp.remove(ran2.nextInt(tmp.size())));
		}
	}
	
	/**
	 * 随机抽取一个对象
	 * 
	 * @param <E>
	 * @param coll
	 * @return
	 */
	public static <E> E randomDraw(List<E> coll){
		if(coll!=null && !coll.isEmpty()){
			String d = "d" + coll.size();
			int num = DiceUtils.roll(d);
			return coll.get(num-1);
		}else{
			return null;
		}
	}
	
	public static void main(String[] args){
		List<Integer> list = new ArrayList<Integer>();
		for(int i=0;i<100;i++){
			list.add(i);
		}
		shuffle(list);
		for(Integer i : list){
			System.out.print(i+",");
		}
	}
	
}
