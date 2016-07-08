package com.f14.PuertoRico.utils;

import net.sf.json.JSONObject;

import com.f14.PuertoRico.component.PrPartPool;
import com.f14.PuertoRico.consts.GoodType;
import com.f14.bg.exception.BoardGameException;

public class PrUtils {

	/**
	 * 取得货物类型,如果不存在则抛出异常
	 * 
	 * @param goodType
	 * @return
	 * @throws BoardGameException
	 */
	public static GoodType getGoodType(String goodType) throws BoardGameException{
		try{
			return GoodType.valueOf(goodType);
		}catch(Exception e){
			throw new BoardGameException("未知的货物类型!");
		}
	}
	
	/**
	 * 按照partString解析成PrPartPool对象
	 * 
	 * @param partString
	 * @return
	 */
	public static PrPartPool getPartInfo(String partString) throws BoardGameException{
		try {
			JSONObject obj = JSONObject.fromObject(partString);
			PrPartPool part = new PrPartPool();
			for(Object o : obj.keySet()){
				String key = (String)o;
				int num = obj.getInt(key);
				GoodType goodType = GoodType.valueOf(key);
				part.putPart(goodType, num);
			}
			return part;
		} catch (Exception e) {
			throw new BoardGameException("转换PartPool对象时出错!");
		}
	}
	
	public static void main(String[] args) throws BoardGameException{
		String str = "{}";
		PrPartPool p = getPartInfo(str);
		System.out.println(p.getTotalNum());
	}
	
}
