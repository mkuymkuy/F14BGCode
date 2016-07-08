import java.util.ArrayList;
import java.util.List;

import com.f14.bg.VPCounter;


public class ScoreTest {
	
	public static void main(String[] args){
		int[] p = new int[]{2500, 1500, 1500};
		ScoreProcess proc = new ScoreProcess();
		List<VPCounter> cs = new ArrayList<VPCounter>();
		for(int i=0;i<p.length;i++){
			VPCounter vpc = new VPCounter(null);
			vpc.isWinner = (i==0)?true:false;
			vpc.rank = i+1;
			proc.addVPCounter(vpc, p[i]);
			cs.add(vpc);
		}
		proc.count();
		
		int i = 1;
		for(VPCounter o : cs){
			System.out.println(i + " : " + o.getRankPoint());
			i++;
		}
	}
}

class ScoreProcess{
	List<ScoreCounter> scoreCounters = new ArrayList<ScoreCounter>();
	
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
		double totalRankPoint = 25 * playerNum;
		//计算所有玩家的总积分
		double total = 0;
		for(ScoreCounter e : scoreCounters){
			total += e.orgRankPoint;
		}
		//计算所有玩家在本局游戏中得到的积分和胜利点数
		int rest = 0;
		for(ScoreCounter e : scoreCounters){
			e.rate = e.orgRankPoint/total;
			e.factor = getRankPointFactor(playerNum, e.vpCounter.rank);
			//e.vpCounter.score = ScoreUtil.getScore(playerNum, e.vpCounter.rank);
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
	
	double getRankPointFactor(int playerNum, int rank){
		double[][] d = new double[5][];
		d[0] = new double[]{1,0};
		d[1] = new double[]{0.65,0.35,0};
		d[2] = new double[]{0.65,0.25,0.1,0};
		d[3] = new double[]{0.65,0.25,0.1,0,0};
		return d[playerNum-2][rank-1];
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
