package com.f14.bg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import com.f14.bg.component.Convertable;
import com.f14.bg.player.Player;

/**
 * VP计算器
 * 
 * @author F14eagle
 *
 */
public class VPCounter implements Comparable<VPCounter>, Convertable {
	public Player player;
	public List<VpObj> primaryVps = new ArrayList<VpObj>();
	public List<VpObj> secondaryVps = new ArrayList<VpObj>();
	public List<VpObj> displayVps = new ArrayList<VpObj>();
	public int rank;
	public boolean isWinner;
	public long score;
	public long rankPoint;
	
	public VPCounter(Player player){
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public List<VpObj> getPrimaryVps() {
		return primaryVps;
	}

	public void setPrimaryVps(List<VpObj> primaryVps) {
		this.primaryVps = primaryVps;
	}

	public List<VpObj> getSecondaryVps() {
		return secondaryVps;
	}

	public void setSecondaryVps(List<VpObj> secondaryVps) {
		this.secondaryVps = secondaryVps;
	}

	public List<VpObj> getDisplayVps() {
		return displayVps;
	}

	public void setDisplayVps(List<VpObj> displayVps) {
		this.displayVps = displayVps;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public boolean isWinner() {
		return isWinner;
	}

	public void setWinner(boolean isWinner) {
		this.isWinner = isWinner;
	}

	public long getScore() {
		return score;
	}

	public void setScore(long score) {
		this.score = score;
	}

	public long getRankPoint() {
		return rankPoint;
	}

	public void setRankPoint(long rankPoint) {
		this.rankPoint = rankPoint;
	}

	/**
	 * 添加VP
	 * 
	 * @param label
	 * @param vp
	 */
	public void addVp(String label, int vp){
		VpObj obj = new VpObj(label, vp);
		this.primaryVps.add(obj);
	}
	
	/**
	 * 添加次要的VP
	 * 
	 * @param label
	 * @param vp
	 */
	public void addSecondaryVp(String label, int vp){
		VpObj obj = new VpObj(label, vp);
		this.secondaryVps.add(obj);
	}
	
	/**
	 * 添加显示用的VP
	 * 
	 * @param label
	 * @param vp
	 */
	public void addDisplayVp(String label, int vp){
		VpObj obj = new VpObj(label, vp);
		this.displayVps.add(obj);
	}
	
	/**
	 * 清除所有的VP
	 */
	public void clearVps(){
		this.primaryVps.clear();
		this.secondaryVps.clear();
		this.displayVps.clear();
	}
	
	/**
	 * 取得所有的VP,包括次要VP,和显示用的VP
	 * @return
	 */
	public List<VpObj> getAllVps(){
		List<VpObj> res = new ArrayList<VpObj>(this.primaryVps);
		res.addAll(this.secondaryVps);
		res.addAll(this.displayVps);
		return res;
	}
	
	/**
	 * 取得总VP
	 * 
	 * @return
	 */
	public int getTotalVP(){
		int vp = 0;
		for(VpObj v : this.primaryVps){
			vp += v.vp;
		}
		return vp;
	}
	
	/**
	 * 取得总显示的VP
	 * 
	 * @return
	 */
	public int getTotalDisplayVP(){
		int vp = 0;
		for(VpObj v : this.displayVps){
			vp += v.vp;
		}
		return vp;
	}
	
	/**
	 * 生成JSON字符串
	 * 
	 * @return
	 */
	public String toJSONString(){
		JsonConfig cfg = new JsonConfig();
		cfg.setExcludes(new String[]{"player", "allVps", "totalVP"});
		return JSONObject.fromObject(this, cfg).toString();
	}
	
	public class VpObj{
		String label;
		int vp;
		VpObj(String label, int vp){
			this.label = label;
			this.vp = vp;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public int getVp() {
			return vp;
		}
		public void setVp(int vp) {
			this.vp = vp;
		}
	}

	@Override
	public int compareTo(VPCounter o) {
		int vp1 = this.getTotalVP();
		int vp2 = o.getTotalVP();
		if(vp1>vp2){
			return 1;
		}else if(vp1<vp2){
			return -1;
		}else{
			//总分相同的情况下,需要判断次要VP大小
			for(int i=0;i<this.secondaryVps.size();i++){
				VpObj v1 = this.secondaryVps.get(i);
				VpObj v2 = o.secondaryVps.get(i);
				if(v1.vp>v2.vp){
					return 1;
				}else if(v1.vp<v2.vp){
					return -1;
				}
			}
			return 0;
		}
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> res = new HashMap<String, Object>();
		res.put("userName", this.player.getName());
		res.put("position", this.player.position);
		res.put("vs", this.getAllVps());
		res.put("totalVP", this.getTotalVP());
		res.put("rank", this.rank);
		res.put("rankPoint", this.rankPoint);
		res.put("score", this.score);
		return res;
	}
}
