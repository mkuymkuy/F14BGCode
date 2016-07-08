package com.f14.f14bgdb.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.f14.bg.VPCounter;
import com.f14.bg.VPResult;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.CheckUtils;
import com.f14.f14bgdb.dao.BgInstanceDao;
import com.f14.f14bgdb.dao.BoardGameDao;
import com.f14.f14bgdb.dao.RankingListDao;
import com.f14.f14bgdb.dao.UserDao;
import com.f14.f14bgdb.model.BgInstance;
import com.f14.f14bgdb.model.BgInstanceRecord;
import com.f14.f14bgdb.model.BgReport;
import com.f14.f14bgdb.model.BoardGame;
import com.f14.f14bgdb.model.RankingList;
import com.f14.f14bgdb.model.User;
import com.f14.f14bgdb.service.BgInstanceManager;
import com.f14.f14bgdb.util.ScoreUtil;
import com.f14.framework.common.service.BaseManagerImpl;
import com.f14.utils.CommonUtil;

public class BgInstanceManagerImpl extends BaseManagerImpl<BgInstance, Long> implements BgInstanceManager {
	private BgInstanceDao bgInstanceDao;
	private UserDao userDao;
	private BoardGameDao boardGameDao;
	private RankingListDao rankingListDao;

	public void setBgInstanceDao(BgInstanceDao bgInstanceDao) {
		this.bgInstanceDao = bgInstanceDao;
		this.setDao(this.bgInstanceDao);
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setBoardGameDao(BoardGameDao boardGameDao) {
		this.boardGameDao = boardGameDao;
	}

	public void setRankingListDao(RankingListDao rankingListDao) {
		this.rankingListDao = rankingListDao;
	}

	/**
	 * 保存游戏结果
	 * 
	 * @param result
	 * @throws BoardGameException
	 */
	public BgInstance saveGameResult(VPResult result) throws BoardGameException{
		BoardGame bg = this.boardGameDao.get(result.boardGame.getRoom().type.toString());
		//this.boardGameDao.getBoardGameByCnname(result.boardGame.getRoom().type.toString());
		CheckUtils.checkNull(bg, "未取得游戏类型实例!");
		
		BgInstance o = new BgInstance();
		o.setConfig(JSONObject.fromObject(result.boardGame.getConfig()).toString());
		o.setBoardGame(bg);
		o.setPlayerNum(result.boardGame.getValidPlayers().size());
		o.setGameTime(System.currentTimeMillis() - result.boardGame.getStartTime().getTime());
		
		Map<VPCounter, RankingList> rankMap = new HashMap<VPCounter, RankingList>();
		ScoreProcess process = new ScoreProcess(result.boardGame);
		//设置明细信息
		for(VPCounter vpc : result.vpCounters){
			User u = this.userDao.get(vpc.player.user.id);
			CheckUtils.checkNull(u, "用户不存在! id:" + vpc.player.user.id);
			
			//取得玩家当前积分
			RankingList rank = this.rankingListDao.getRankingList(u.getId(), bg.getId());
			if(rank==null){
				rank = this.createRankingList(u, bg.getId());
			}
			process.addVPCounter(vpc, rank.getRankPoint());
			rankMap.put(vpc, rank);
		}
		
		//计算积分和排名信息
		process.count();
		for(VPCounter vpc : rankMap.keySet()){
			RankingList rank = rankMap.get(vpc);
			//计算胜率及积分
			if(vpc.isWinner){
				rank.setNumWins(rank.getNumWins()+1);
			}else{
				rank.setNumLoses(rank.getNumLoses()+1);
			}
			rank.setNumTotal(rank.getNumWins()+rank.getNumLoses());
			if(rank.getNumTotal()!=null && rank.getNumTotal()>0){
				double rate = ((double)rank.getNumWins()/rank.getNumTotal())*100;
				rank.setRate(CommonUtil.formatRate(rate));
			}
			rank.setScore(rank.getScore() + vpc.score);
			rank.setRankPoint(rank.getRankPoint() + vpc.rankPoint);
			//保存积分和排名信息
			this.rankingListDao.save(rank);
			
			//设置游戏记录明细
			User u = this.userDao.get(vpc.player.user.id);
			BgInstanceRecord r = new BgInstanceRecord();
			r.setUser(u);
			r.setRank(vpc.rank);
			r.setIsWinner(vpc.isWinner);
			r.setVp(vpc.getTotalVP());
			r.setScore((int)vpc.getScore());
			r.setRankPoint((int)vpc.getRankPoint());
			r.setDetailStr(vpc.toJSONString());
			o.addBgInstanceRecord(r);
		}
		//保存游戏记录
		this.save(o);
		return o;
	}
	
	/**
	 * 保存游戏战报
	 * 
	 * @param o
	 * @param descr
	 * @throws BoardGameException
	 */
	public void saveGameReport(BgInstance o, String descr) throws BoardGameException{
		BgReport report = new BgReport();
		report.setDescr(descr);
		o.addBgReport(report);
		this.update(o);
	}
	
	/**
	 * 创建默认的排行榜对象
	 * 
	 * @param user
	 * @return
	 */
	private RankingList createRankingList(User user, String boardGameId){
		RankingList rank = new RankingList();
		rank.setUserId(user.getId());
		rank.setBoardGameId(boardGameId);
		rank.setLoginName(user.getLoginName());
		rank.setUserName(user.getUserName());
		rank.setNumWins(0l);
		rank.setNumLoses(0l);
		rank.setScore(0l); //初始积分为0
		rank.setRankPoint(ScoreUtil.getDefaultRankPoint()); //初始排名点数
		return rank;
	}
	
	class ScoreProcess{
		List<ScoreCounter> scoreCounters = new ArrayList<ScoreCounter>();
		com.f14.bg.BoardGame<?, ?> bg;
		
		ScoreProcess(com.f14.bg.BoardGame<?, ?> bg){
			this.bg = bg;
		}
		
		/**
		 * 添加VPCounter
		 * 
		 * @param o
		 * @param orgRankPoint
		 */
		void addVPCounter(VPCounter o, long orgRankPoint){
			ScoreCounter s = new ScoreCounter();
			s.vpCounter = o;
			s.orgRankPoint = Math.round(Math.pow(orgRankPoint, 2));
			scoreCounters.add(s);
		}
		
		/**
		 * 计算所有VPCounter得到的积分和排名
		 */
		void count(){
			int playerNum = scoreCounters.size();
			//单局游戏总排名点数
			double totalRankPoint = ScoreUtil.getRoundRankPoint(playerNum);
			//计算所有玩家的总积分
			double total = 0;
			for(ScoreCounter e : scoreCounters){
				total += e.orgRankPoint;
			}
			//计算所有玩家在本局游戏中得到的积分和胜利点数
			int rest = 0;
			for(ScoreCounter e : scoreCounters){
				e.rate = 1/(double)playerNum;//e.orgRankPoint/total;
				e.factor = ScoreUtil.getRankPointFactor(playerNum, e.vpCounter.rank, this.bg.isTeamMatch());
				e.vpCounter.score = ScoreUtil.getScore(playerNum, e.vpCounter.rank);
				e.vpCounter.rankPoint = Math.round((e.factor-e.rate) * totalRankPoint);
				if(e.vpCounter.isWinner && e.vpCounter.rankPoint<=0){
					//胜者至少能得到1分...
					rest = 1 - (int)e.vpCounter.rankPoint;
					e.vpCounter.rankPoint = 1;
				}
			}
			//如果胜者分数有经过调整,则将其被调整的分数从其他玩家的得分中扣除
			if(rest!=0){
				int winNum = this.getWinnerNumber();
				//如果全部都是胜者,则不扣分
				if(winNum<playerNum){
					int each = rest / (playerNum - winNum);
					for(ScoreCounter e : scoreCounters){
						if(!e.vpCounter.isWinner){
							e.vpCounter.rankPoint -= each;
						}
					}
				}
			}
		}
		
		/**
		 * 得到胜者的数量
		 * 
		 * @return
		 */
		int getWinnerNumber(){
			int res = 0;
			for(ScoreCounter sc : this.scoreCounters){
				if(sc.vpCounter.isWinner){
					res += 1;
				}
			}
			return res;
		}
	}
	
	class ScoreCounter{
		VPCounter vpCounter;
		/**
		 * 原排名点数
		 */
		long orgRankPoint;
		double rate;
		double factor;
	}
	
}
