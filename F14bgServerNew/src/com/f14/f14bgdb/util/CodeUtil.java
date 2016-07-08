package com.f14.f14bgdb.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f14.f14bgdb.F14bgdb;
import com.f14.f14bgdb.dao.BoardGameDao;
import com.f14.f14bgdb.model.BoardGame;
import com.f14.f14bgdb.model.CodeDetail;
import com.f14.f14bgdb.service.CodeDetailManager;

/**
 * 代码缓存工具
 * 
 * @author F14eagle
 *
 */
public class CodeUtil {
	protected static Logger log = Logger.getLogger(CodeUtil.class);
	/**
	 * 代码类型 - 桌游
	 */
	public static final String CODE_BOARDGAME = "BOARDGAME";
	private static Map<String, Map<String, CodeDetail>> map;
	private static Map<String, List<CodeDetail>> list;
	
	private static Map<String, BoardGame> bgs;
	
	/**
	 * 装载所有代码,必须首先调用该方法才能使用该代码工具
	 */
	public static void loadAllCodes(){
		log.info("装载系统代码...");
		map = new HashMap<String, Map<String, CodeDetail>>();
		list = new HashMap<String, List<CodeDetail>>();
		bgs = new LinkedHashMap<String, BoardGame>();
		CodeDetailManager manager = F14bgdb.getBean("codeDetailManager");
		List<CodeDetail> list = manager.query((CodeDetail)null, "codeType,codeIndex");
		for(CodeDetail e : list){
			addCode(e);
		}
		loadOtherCodes();
		log.info("系统代码装载完成!");
	}
	
	/**
	 * 取得所有的代码
	 * 
	 * @return
	 */
	public static Map<String, List<CodeDetail>> getAllCodes(){
		return list;
	}
	
	/**
	 * 取得codeType对应的map
	 * 
	 * @param codeType
	 * @return
	 */
	private static Map<String, CodeDetail> getMap(String codeType){
		Map<String, CodeDetail> res = map.get(codeType);
		if(res==null){
			res = new HashMap<String,CodeDetail>();
			map.put(codeType, res);
		}
		return res;
	}
	
	/**
	 * 取得codeKey对应的map list
	 * 
	 * @param codeType
	 * @return
	 */
	private static List<CodeDetail> getList(String codeType){
		List<CodeDetail> res = list.get(codeType);
		if(res==null){
			res = new ArrayList<CodeDetail>();
			list.put(codeType, res);
		}
		return res;
	}
	
	/**
	 * 添加代码对象到缓存中
	 * 
	 * @param o
	 */
	private static void addCode(CodeDetail o){
		String codeType = o.getCodeType();
		Map<String, CodeDetail> codeMap = getMap(codeType);
		List<CodeDetail> codeList = getList(codeType);
		codeMap.put(o.getValue(), o);
		codeList.add(o);
	}
	
	/**
	 * 取得代码值
	 * 
	 * @param codeType
	 * @param value
	 * @return
	 */
	public static String getLabel(String codeType, String value){
		Map<String, CodeDetail> codes = getMap(codeType);
		if(codes==null){
			return null;
		}
		return codes.get(value).getLabel();
	}
	
	/**
	 * 取得指定类型的代码列表
	 * 
	 * @param codeType
	 * @return
	 */
	public static List<CodeDetail> getCodes(String codeType){
		List<CodeDetail> res = getList(codeType);
		return res;
	}
	
	/**
	 * 装载非标准的代码
	 * BOARDGAME - 取得游戏类型代码
	 * 
	 * @return
	 */
	private static void loadOtherCodes(){
		BoardGameDao dao = F14bgdb.getBean("boardGameDao");
		List<BoardGame> codes = dao.query(new BoardGame());
		int i = 0;
		for(BoardGame e : codes){
			CodeDetail c = new CodeDetail();
			c.setLabel(e.getCnname());
			c.setValue(e.getId());
			c.setCodeType(CODE_BOARDGAME);
			c.setCodeIndex(i++);
			addCode(c);
			
			bgs.put(e.getId(), e);
		}
	}
	
	/**
	 * 取得游戏属性对象
	 * 
	 * @param id
	 * @return
	 */
	public static BoardGame getBoardGame(String id){
		return bgs.get(id);
	}
	
	/**
	 * 取得所有的游戏属性对象
	 * 
	 * @return
	 */
	public static Collection<BoardGame> getBoardGames(){
		return bgs.values();
	}
}
