package com.f14.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {
	public static final SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
	public static final DecimalFormat rateFormat = new DecimalFormat("##0.00");

	/**
	 * 用args中的值替换msg中的值{index}(index为下标)
	 * 
	 * @param msg
	 * @param args
	 * @return
	 */
	public static String getMsg(String msg, Object...args){
		for(int i=0;i<args.length;i++){
			if(args[i]==null){
				msg = msg.replaceAll("\\{"+i+"\\}", "-NULL-");
			}else{
				msg = msg.replaceAll("\\{"+i+"\\}", args[i].toString());
			}
		}
		return msg;
	}

	/**
	 * 取得当前时间字符串
	 * 
	 * @return
	 */
	public static String getCurrentTime(){
		return df.format(new Date());
	}
	
	/**
	 * 格式化比率到小数点后面2位
	 * 
	 * @param rate
	 * @return
	 */
	public static Double formatRate(Double rate){
		return Double.valueOf(rateFormat.format(rate));
	}
	
//	public static <T> T getEnumValue(Class<T> enumType, String str){
//		if(StringUtils.isEmpty(str)){
//			return null;
//		}else{
//			return Enum.valueOf(enumType, str);
//		}
//	}
}
