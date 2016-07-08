package com.f14.f14bgdb.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.f14.f14bgdb.model.CodeDetail;

/**
 * 积分和排名相关辅助类
 * 
 * @author F14eagle
 *
 */
public class ScoreUtil {
	protected static Logger log = Logger.getLogger(ScoreUtil.class);
	/**
	 * 积分和排名默认参数的系统代码
	 */
	private static final String SYS_POINT = "SYS_POINT";
	/**
	 * 排名点数的系统代码
	 */
	private static final String SYS_RANK_POINT = "SYS_RANK_POINT";
	/**
	 * 积分的系统代码
	 */
	private static final String SYS_SCORE_POINT = "SYS_SCORE_POINT";
	
	private static int DEFAULT_RANK_POINT = 0;
	private static int ROUND_RANK_POINT = 0;
	private static Map<Integer, Double[]> rankMap;
	private static Map<Integer, Integer[]> scoreMap;
	
	/**
	 * 初始化
	 */
	public static void init() throws Exception{
		log.info("初始化积分模块...");
		rankMap = new HashMap<Integer, Double[]>();
		scoreMap = new HashMap<Integer, Integer[]>();
		DEFAULT_RANK_POINT = Integer.valueOf(CodeUtil.getLabel(SYS_POINT, "DEFAULT_RANK_POINT"));
		ROUND_RANK_POINT = Integer.valueOf(CodeUtil.getLabel(SYS_POINT, "DEFAULT_ROUND_POINT"));
		
		List<CodeDetail> codes = CodeUtil.getCodes(SYS_RANK_POINT);
		for(CodeDetail e : codes){
			String[] strs = e.getLabel().split(",");
			Double[] factors = new Double[strs.length];
			for(int i=0;i<strs.length;i++){
				factors[i] = Double.valueOf(strs[i]);
			}
			rankMap.put(strs.length, factors);
		}
		
		codes = CodeUtil.getCodes(SYS_SCORE_POINT);
		for(CodeDetail e : codes){
			String[] strs = e.getLabel().split(",");
			Integer[] scores = new Integer[strs.length];
			for(int i=0;i<strs.length;i++){
				scores[i] = Integer.valueOf(strs[i]);
			}
			scoreMap.put(strs.length, scores);
		}
		log.info("积分模块初始化完成!");
	}

	/**
	 * 取得初始排名点数
	 * 
	 * @return
	 */
	public static long getDefaultRankPoint(){
		return DEFAULT_RANK_POINT;
	}
	
	/**
	 * 取得每局的排名点数
	 * 
	 * @param playerNum 玩家数
	 * @return
	 */
	public static int getRoundRankPoint(int playerNum){
		return ROUND_RANK_POINT * playerNum;
	}
	
	/**
	 * 取得排名点数因数
	 * 
	 * @param playerNum 游戏中的玩家数
	 * @param roundRank 游戏中的排名
	 * @return
	 */
	public static double getRankPointFactor(int playerNum, int roundRank, boolean isTeamMatch){
		if(isTeamMatch){
			if(roundRank==1){
				return 0.5;
			}else{
				return 0;
			}
		}else{
			return rankMap.get(playerNum)[roundRank-1];
		}
	}
	
	/**
	 * 取得积分
	 * 
	 * @param playerNum 游戏中的玩家数
	 * @param roundRank 游戏中的排名
	 * @return
	 */
	public static int getScore(int playerNum, int roundRank){
		return scoreMap.get(playerNum)[roundRank-1];
	}
}
