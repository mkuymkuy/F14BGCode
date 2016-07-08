package com.f14.utils;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class ExcelUtils {

	/**
	 * 取得字符串
	 * 
	 * @param row
	 * @param cellNum
	 * @return
	 */
	public static String getString(HSSFRow row, int cellNum){
		HSSFCell cell = row.getCell(cellNum);
		if(cell==null){
			return null;
		}
		switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_BLANK:
				return null;
			case HSSFCell.CELL_TYPE_STRING:
				return cell.getRichStringCellValue().getString();
			case HSSFCell.CELL_TYPE_NUMERIC:
				return cell.getNumericCellValue() + "";
			default:
				return null;
		}
	}
	
	/**
	 * 取得double
	 * 
	 * @param row
	 * @param cellNum
	 * @return
	 */
	public static double getDouble(HSSFRow row, int cellNum){
		HSSFCell cell = row.getCell(cellNum);
		if(cell==null){
			return 0;
		}else{
			return cell.getNumericCellValue();
		}
	}
	
	/**
	 * 取得int
	 * 
	 * @param row
	 * @param cellNum
	 * @return
	 */
	public static int getInteger(HSSFRow row, int cellNum){
		HSSFCell cell = row.getCell(cellNum);
		if(cell==null){
			return 0;
		}else{
			return (int)cell.getNumericCellValue();
		}
	}
	
	/**
	 * 取得boolean
	 * 
	 * @param row
	 * @param cellNum
	 * @return
	 */
	public static boolean getBoolean(HSSFRow row, int cellNum){
		HSSFCell cell = row.getCell(cellNum);
		if(cell==null){
			return false;
		}else{
			return cell.getBooleanCellValue();
		}
	}
	
	/**
	 * 将row转换成json字符串
	 * 
	 * @param row
	 * @param head 表头字段名
	 * @return
	 */
	public static String rowToJsonStr(HSSFRow row, String[] head){
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		int cellNum = row.getLastCellNum();
		HSSFCell cell;
		String str;
		for(int i=0;(i<head.length&&i<cellNum);i++){
			cell = row.getCell(i);
			//如果列名为"[X]"的格式,则跳过该列
			if(!StringUtils.isEmpty(head[i]) && (cell)!=null && !(head[i].startsWith("[") && head[i].endsWith("]"))){
				sb.append("\"").append(head[i]).append("\":");
				switch(cell.getCellType()){
				case HSSFCell.CELL_TYPE_STRING:
					str = getString(row, i);
					if(str.startsWith("{") && str.endsWith("}") || str.startsWith("[") && str.endsWith("]")){
						//该字符串为json字符串
						sb.append(str);
					}else{
						sb.append("\"").append(str).append("\"");
					}
					//sb.append("\"").append(str.replaceAll("\\\"", "\\\\\"")).append("\"");
					break;
				default:
					sb.append(getString(row, i));
					break;
				}
				sb.append(",");
			}
		}
		if(sb.charAt(sb.length()-1)==','){
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append("}");
		return sb.toString();
	}
	
	/**
	 * 将row转换成string数组
	 * 
	 * @param row
	 * @return
	 */
	public static String[] rowToStringArray(HSSFRow row){
		List<String> strs = new ArrayList<String>();
		String str;
		for(int i=0;i<row.getLastCellNum();i++){
			str = getString(row, i);
			//if(!(str.startsWith("[") && str.endsWith("]"))){
			strs.add(str);
			//}
		}
		return strs.toArray(new String[strs.size()]);
	}
	
	/**
	 * 将row转换成object
	 * 
	 * @param row
	 * @param head 表头字段名
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <A> A rowToObject(HSSFRow row, String[] head, Class<A> clazz){
		JSONObject obj = JSONObject.fromObject(rowToJsonStr(row, head));
		return (A)JSONObject.toBean(obj, clazz);
	}
	
}
