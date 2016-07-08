package com.f14.bg.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.f14.F14bg.network.F14bgServer;
import com.f14.bg.component.Card;
import com.f14.bg.component.Convertable;

/**
 * 银河竞逐专用的工具类
 * 
 * @author F14eagle
 *
 */
public class BgUtils {

	/**
	 * 将卡牌的id转换成string
	 * 
	 * @param cards
	 * @return
	 */
	public static <C extends Card> String card2String(Collection<C> cards){
		String res = "";
		for(C o : cards){
			res += o.id + ",";
		}
		return (res.length()>0) ? res.substring(0, res.length()-1) : res;
	}
	
	/**
	 * 检查列表中是否存在相同cardNo的卡牌
	 * 
	 * @param cards
	 * @return
	 */
	public static <C extends Card> boolean checkDuplicate(List<C> cards){
		for(C c1 : cards){
			for(C c2 : cards){
				if(c1!=c2 && c1.cardNo.equals(c2.cardNo)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 将list转换成map对象的list
	 * 
	 * @param <C>
	 * @param list
	 * @return
	 */
	public static <C extends Convertable> List<Map<String, Object>> toMapList(Collection<C> list){
		List<Map<String, Object>> res = new ArrayList<Map<String,Object>>();
		for(C c : list){
			res.add(c.toMap());
		}
		return res;
	}
	
	/**
	 * 将array转换成map对象的list
	 * 
	 * @param <C>
	 * @param array
	 * @return
	 */
	public static <C extends Convertable> List<Map<String, Object>> toMapList(C[] array){
		List<Map<String, Object>> res = new ArrayList<Map<String,Object>>();
		for(C c : array){
			res.add(c.toMap());
		}
		return res;
	}
	
	/**
	 * clone card列表
	 * 
	 * @param <C>
	 * @param cards
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <C extends Card> Collection<C> cloneList(Collection<C> cards){
		List<C> res = new ArrayList<C>();
		for(C c : cards){
			res.add((C)c.clone());
		}
		return res;
	}
	
	/**
	 * 将对象转换成list
	 * 
	 * @param <C>
	 * @param o
	 * @return
	 */
	public static <C> List<C> toList(C o){
		List<C> list = new ArrayList<C>();
		list.add(o);
		return list;
	}
	
	/**
	 * 取得文件输入流
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream getFileInputStream(String path) throws FileNotFoundException{
		return F14bgServer.class.getClassLoader().getResourceAsStream(path);
	}
	
	/**
	 * 取得文件
	 * 
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public static File getFile(String path) throws FileNotFoundException{
		return new File(F14bgServer.class.getClassLoader().getResource(path).getFile());
	}
	
}
