package com.f14.TS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f14.TS.component.AdjustParam;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.TSCountry;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSProperty;
import com.f14.TS.consts.TrigType;
import com.f14.TS.manager.ScoreManager.ScoreCounter;
import com.f14.bg.report.BgReport;
import com.f14.bg.utils.BgUtils;

public class TSReport extends BgReport {
	protected List<ActionRecord> records = new ArrayList<ActionRecord>();

	public TSReport(TS bg) {
		super(bg);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TS getGame() {
		return super.getGame();
	}
	
	/**
	 * 执行行动提示
	 * 
	 * @param ap 调整的详细参数
	 */
	public void doAction(AdjustParam ap){
		this.playerDoAction(null, ap);
	}
	
	/**
	 * 玩家执行行动
	 * 
	 * @param player 调整的玩家(可以为空)
	 * @param ap 调整的详细参数
	 */
	public void playerDoAction(TSPlayer player, AdjustParam ap){
		if(player!=null){
			this.action(player, ap.getReportString());
		}else{
			this.info(ap.getReportString());
		}
	}
	
	/**
	 * 玩家调整影响力
	 * 
	 * @param player 调整的玩家
	 * @param aps 调整的详细参数
	 */
	public void playerDoAction(TSPlayer player, Collection<AdjustParam> aps){
		for(AdjustParam ap : aps){
			this.playerDoAction(player, ap);
		}
	}
	
	/**
	 * 玩家打出牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerPlayCard(TSPlayer player, TSCard card, TrigType type){
		StringBuffer sb = new StringBuffer();
		String act = "";
		if(type!=null){
			act += "以" + TrigType.getChinese(type) + "方式";
			//sb.append("以").append(TrigType.getChinese(type)).append("方式");
		}
		act += "打出";
		sb.append(act + card.getReportString());
		this.action(player, sb.toString());
		//记录行动
		this.printRecord(player, card, act);
	}
	
	/**
	 * 输出DEFCON的变化值
	 * 
	 * @param num
	 */
	public void adjustDefcon(int num){
		String str = null;
		if(num>0){
			str = "DEFCON改善了 " + num + "个等级";
		}else if(num<0){
			str = "DEFCON恶化了 " + (-num) + "个等级";
		}
		if(str!=null){
			this.info(str);
		}
	}
	
	/**
	 * 输出DEFCON等级
	 * 
	 * @param defcon
	 */
	public void printDefcon(int defcon){
		this.info("DEFCON变为 " + defcon);
	}
	
	/**
	 * 输出VP的变化值
	 * 
	 * @param num
	 */
	public void adjustVp(int num){
		String str = null;
		if(num>0){
			str = "苏联得到 " + num + "VP";
		}else if(num<0){
			str = "美国得到 " + (-num) + "VP";
		}
		if(str!=null){
			this.info(str);
		}
	}
	
	/**
	 * 输出VP
	 * 
	 * @param vp
	 */
	public void printVp(int vp){
		String str;
		if(vp>0){
			str = "苏联 " + vp + "VP";
		}else if(vp<0){
			str = "美国 " + (-vp) + "VP";
		}else{
			str = "0";
		}
		this.info("VP变为 " + str);
	}
	
	/**
	 * 玩家摸牌
	 * 
	 * @param player
	 * @param drawNum
	 */
	public void playerDrawCards(TSPlayer player, int drawNum){
		this.action(player, "摸了 " + drawNum + " 张牌");
	}
	
	/**
	 * 玩家得到牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerGetCard(TSPlayer player, TSCard card){
		this.action(player, "得到 " + card.getReportString());
		this.printRecord(player, card, "得到");
	}

	/**
	 * 玩家区域得分
	 * 
	 * @param player
	 * @param counter
	 */
	public void playerRegionScore(TSPlayer player, ScoreCounter counter){
		this.action(player, counter.getReportString());
	}
	
	/**
	 * 玩家进行太空竞赛
	 * 
	 * @param player
	 * @param card
	 * @param roll
	 * @param success
	 */
	public void playerSpaceRace(TSPlayer player, TSCard card, int roll, boolean success){
		StringBuffer sb = new StringBuffer();
		sb.append("用").append(card.getReportString()).append("进行太空竞赛,掷骰结果为 ")
		.append(roll).append(",尝试").append(success?"成功":"失败");
		this.action(player, sb.toString());
		this.printRecord(player, card, "进行太空竞赛");
	}
	
	/**
	 * 玩家调整太空竞赛等级
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAdvanceSpaceRace(TSPlayer player, int num){
		StringBuffer sb = new StringBuffer();
		sb.append("的太空竞赛等级前进了 ").append(num).append(" 格,变为 ")
		.append(player.getProperty(TSProperty.SPACE_RACE));
		this.action(player, sb.toString());
	}
	
	/**
	 * 调整玩家的军事行动力
	 * 
	 * @param player
	 * @param num
	 */
	public void playerAdjustMilitaryAction(TSPlayer player, int num){
		this.action(player, "得到 " + num + " 点军事行动");
	}
	
	/**
	 * 设置玩家的军事行动力
	 * 
	 * @param player
	 */
	public void playerSetMilitaryAction(TSPlayer player){
		this.action(player, "的军事行动变为 " + player.getProperty(TSProperty.MILITARY_ACTION));
	}
	
	/**
	 * 玩家发送战争
	 * 
	 * @param player
	 * @param country
	 * @param roll
	 * @param correctedValue
	 * @param success
	 */
	public void playerWar(TSPlayer player, TSCountry country, int roll, int correctedValue, boolean success){
		StringBuffer sb = new StringBuffer();
		sb.append("在").append(country.getReportString()).append("发动了战争,掷骰结果为 ")
		.append(roll).append(" - ").append(correctedValue).append(" = ").append(roll-correctedValue)
		.append(" , 战争").append(success?"胜利":"失败");
		this.action(player, sb.toString());
	}
	
	/**
	 * 玩家选择头条
	 * 
	 * @param player
	 * @param card
	 */
	public void playerHeadLine(TSPlayer player, TSCard card){
		this.action(player, "选择" + card.getReportString() + "作为头条");
		this.printRecord(player, card, "选择头条");
	}
	
	/**
	 * 执行头条
	 * 
	 * @param player
	 * @param card
	 */
	public void headLine(TSCard card){
		this.line();
		this.info(card.getReportString() + "生效");
	}
	
	/**
	 * 玩家弃牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerDiscardCard(TSPlayer player, TSCard card){
		this.playerDiscardCards(player, BgUtils.toList(card));
	}
	
	/**
	 * 玩家弃牌
	 * 
	 * @param player
	 * @param cards
	 */
	public void playerDiscardCards(TSPlayer player, List<TSCard> cards){
		if(!cards.isEmpty()){
			StringBuffer sb = new StringBuffer();
			sb.append("弃掉了 ");
			for(int i=0;i<cards.size();i++){
				sb.append(cards.get(i).getReportString());
				if(i<cards.size()-1){
					sb.append(", ");
				}
			}
			this.action(player, sb.toString());
			//记录行动
			for(TSCard card : cards){
				this.printRecord(player, card, "弃掉了");
			}
		}
	}
	
	/**
	 * 玩家失去牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRemoveCard(TSPlayer player, TSCard card){
		this.playerRemoveCards(player, BgUtils.toList(card));
	}
	
	/**
	 * 玩家失去牌
	 * 
	 * @param player
	 * @param cards
	 */
	public void playerRemoveCards(TSPlayer player, List<TSCard> cards){
		if(!cards.isEmpty()){
			StringBuffer sb = new StringBuffer();
			sb.append("失去了 ");
			for(int i=0;i<cards.size();i++){
				sb.append(cards.get(i).getReportString());
				if(i<cards.size()-1){
					sb.append(", ");
				}
			}
			this.action(player, sb.toString());
			//记录行动
			for(TSCard card : cards){
				this.printRecord(player, card, "失去了");
			}
		}
	}
	
	/**
	 * 玩家进行选择
	 * 
	 * @param player
	 * @param descr
	 */
	public void playerSelectChoice(TSPlayer player, String descr){
		this.action(player, "选择了 " + descr);
	}
	
	/**
	 * 玩家掷骰
	 * 
	 * @param player
	 * @param roll
	 * @param bonus
	 */
	public void playerRoll(TSPlayer player, int roll, int bonus){
		StringBuffer sb = new StringBuffer();
		sb.append("掷骰结果为 ").append(roll);
		if(bonus>0){
			sb.append(" + ").append(bonus).append(" = ").append(roll+bonus);
		}else if(bonus<0){
			sb.append(" - ").append(-bonus).append(" = ").append(roll+bonus);
		}
		this.action(player, sb.toString());
	}
	
	/**
	 * 取消头条
	 * 
	 * @param player
	 * @param power
	 */
	public void playerCancelHeadline(TSPlayer player, SuperPower power){
		this.action(player, "取消了 " + SuperPower.getChinese(power) + " 方的头条");
	}
	
	/**
	 * 玩家得到中国牌
	 * 
	 * @param player
	 * @param canUse
	 */
	public void playerGetChinaCard(TSPlayer player, boolean canUse){
		this.action(player, "得到了中国牌");
	}
	
	/**
	 * 玩家拥有中国牌
	 * 
	 * @param player
	 */
	public void playerOwnChinaCard(TSPlayer player){
		this.action(player, "拥有中国牌");
	}
	
	/**
	 * 打印行动记录
	 * 
	 * @param player
	 * @param card
	 * @param message
	 */
	public void printRecord(TSPlayer player, TSCard card, String message){
		ActionRecord rec = new ActionRecord(player, card, message);
		this.records.add(rec);
		this.getGame().sendActionRecord(rec);
	}
	
	/**
	 * 取得最近的N条记录
	 * 
	 * @param n
	 * @return
	 */
	public Collection<ActionRecord> getRecentRecords(int n){
		if(this.records.isEmpty()){
			return new ArrayList<ActionRecord>();
		}else{
			int i = this.records.size() - n;
			i = Math.max(0, i);
			return this.records.subList(i, records.size());
		}
	}
	
	/**
	 * 玩家掷骰
	 * 
	 * @param player
	 * @param roll
	 * @param modify
	 */
	public void roll(TSPlayer player, int roll, int modify){
		StringBuffer sb = new StringBuffer("掷骰结果: ");
		sb.append(roll).append("+").append(modify).append("=").append(roll+modify);
		this.action(player, sb.toString());
	}
	
	/**
	 * 玩家卡牌生效
	 * 
	 * @param player
	 * @param card
	 */
	public void playerActiveCard(TSPlayer player, TSCard card){
		this.action(player, "的"+card.getReportString()+"生效");
		this.printRecord(player, card, "卡牌生效");
	}
	
	/**
	 * 玩家移除生效的卡牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRemoveActiveCard(TSPlayer player, TSCard card){
		this.action(player, "移除了"+card.getReportString()+"的效果");
	}
	
	/**
	 * 玩家随机抽牌
	 * 
	 * @param player
	 * @param card
	 */
	public void playerRandowDrawCard(TSPlayer player, TSCard card){
		this.action(player, "抽到了 " + card.getReportString());
		this.printRecord(player, card, "抽到了");
	}
}
