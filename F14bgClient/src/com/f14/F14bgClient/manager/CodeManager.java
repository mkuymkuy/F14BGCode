package com.f14.F14bgClient.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.f14.F14bg.consts.CmdConst;
import com.f14.F14bg.network.CmdFactory;
import com.f14.F14bgClient.vo.CodeDetail;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;


public class CodeManager {
	protected static Logger log = Logger.getLogger(CodeManager.class);
	private Object lock = new Object();
	private Map<String, Map<String, CodeDetail>> map = new HashMap<String, Map<String,CodeDetail>>();
	private Map<String, List<CodeDetail>> list = new HashMap<String, List<CodeDetail>>();

	public void clear(){
		map.clear();
		list.clear();
	}
	
	/**
	 * 取得codeType对应的map
	 * 
	 * @param codeType
	 * @return
	 */
	private Map<String, CodeDetail> getMap(String codeType){
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
	private List<CodeDetail> getList(String codeType){
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
	private void addCode(CodeDetail o){
		String codeType = o.getCodeType();
		Map<String, CodeDetail> codeMap = getMap(codeType);
		List<CodeDetail> codeList = getList(codeType);
		codeMap.put(o.getValue(), o);
		codeList.add(o);
	}
	
	/**
	 * 取得指定类型的代码列表
	 * 
	 * @param codeType
	 * @return
	 */
	public List<CodeDetail> getCodes(String codeType){
		List<CodeDetail> res = getList(codeType);
		return res;
	}
	
	/**
	 * 取得指定类型的代码值对应的显示值
	 * 
	 * @param codeType
	 * @param codeValue
	 * @return
	 */
	public String getCodeLabel(String codeType, String codeValue){
		CodeDetail o = this.getMap(codeType).get(codeValue);
		if(o!=null){
			return o.label;
		}else{
			return null;
		}
	}
	
	/**
	 * 从服务器装载所有代码,该方法会等待结果返回后继续执行
	 */
	public void loadAllCodes(){
		BgResponse res = CmdFactory.createClientResponse(CmdConst.CLIENT_LOAD_CODE);
		ManagerContainer.connectionManager.sendResponse(res);
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 装载代码参数
	 * 
	 * @param act
	 */
	@SuppressWarnings("unchecked")
	public void loadCodeParam(BgAction act){
		this.clear();
		JSONObject codes = act.getAsObject("codes");
		if(codes!=null && !codes.isEmpty()){
			Iterator<String> it = codes.keys();
			while(it.hasNext()){
				String codeType = it.next();
				JSONArray array = codes.getJSONArray(codeType);
				if(array!=null && !array.isEmpty()){
					Iterator<JSONObject> oit = array.iterator();
					while(oit.hasNext()){
						JSONObject code = oit.next();
						CodeDetail o = (CodeDetail)JSONObject.toBean(code, CodeDetail.class);
						this.addCode(o);
					}
				}
			}
		}
		this.notifyLock();
	}
	
	/**
	 * 解除等待
	 */
	public void notifyLock(){
		synchronized (lock) {
			lock.notifyAll();
		}
	}
	
}
