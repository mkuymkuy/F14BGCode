package com.f14.tichu;

import com.f14.bg.report.BgReport;
import com.f14.tichu.componet.TichuCard;
import com.f14.tichu.componet.TichuCardGroup;
import com.f14.tichu.consts.CardValueUtil;
import com.f14.tichu.consts.TichuType;

public class TichuReport extends BgReport {

	public TichuReport(Tichu bg) {
		super(bg);
	}

	/**
	 * 玩家叫地主
	 * 
	 * @param player
	 * @param tichuType
	 */
	public void playerCallTichu(TichuPlayer player, TichuType tichuType){
		this.action(player, " 叫了 " + TichuType.getChinese(tichuType), true);
	}
	
	/**
	 * 玩家许愿
	 * 
	 * @param player
	 * @param point
	 */
	public void playerWishPoint(TichuPlayer player, double point){
		this.action(player, " 许愿了 " + CardValueUtil.getCardValue(point), true);
	}
	
	/**
	 * 玩家得到分数
	 * 
	 * @param player
	 * @param score
	 */
	public void playerGetScore(TichuPlayer player, int score){
		this.action(player, " 得到 " + score + " 分", true);
	}
	
	public void playerPlayCards(TichuPlayer player, TichuCardGroup group){
		StringBuffer sb = new StringBuffer();
		sb.append("打出了");
		switch(group.combination){
		case SINGLE:
			sb.append("单张");
			break;
		case PAIR:
			sb.append("一对");
			break;
		case GROUP_PAIRS:
			sb.append("姐妹对");
			break;
		case TRIO:
			sb.append("三条");
			break;
		case FULLHOUSE:
			sb.append("葫芦");
			break;
		case STRAIGHT:
			sb.append("顺子");
			break;
		case BOMBS:
			sb.append("炸弹");
			break;
		}
		for(TichuCard c : group.cards){
			sb.append(c.getReportString());
		}
		this.action(player, sb.toString());
	}
	
	public void playerPass(TichuPlayer player){
		this.action(player, "PASS");
	}
}
