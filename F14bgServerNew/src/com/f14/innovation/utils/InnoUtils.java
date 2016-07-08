package com.f14.innovation.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoConsts;
import com.f14.innovation.consts.InnoIcon;

public class InnoUtils {

	/**
	 * 检查等级参数的合法性
	 * 
	 * @param level
	 * @throws BoardGameException
	 */
	public static void checkLevel(int level) throws BoardGameException{
		if(level<1 || level>InnoConsts.MAX_LEVEL){
			throw new BoardGameException("无效的等级参数!");
		}
	}
	
	/**
	 * 将卡牌的level转换成string
	 * 
	 * @param cards
	 * @return
	 */
	public static String cardLevel2String(Collection<InnoCard> cards){
		String res = "";
		for(InnoCard o : cards){
			res += o.level + ",";
		}
		return (res.length()>0) ? res.substring(0, res.length()-1) : res;
	}
	
	/**
	 * 取得这些牌中,有几种不同时期的牌的数量
	 * 
	 * @param cards
	 * @return
	 */
	public static int getDifferentLevelCardsNum(Collection<InnoCard> cards){
		int i = 0;
		Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
		for(InnoCard card : cards){
			if(map.get(card.level)==null){
				i += 1;
				map.put(card.level, true);
			}
		}
		return i;
	}
	
	/**
	 * 判断卡牌中是否有指定的颜色
	 * 
	 * @param cards
	 * @param colors
	 * @return
	 */
	public static boolean hasColor(Collection<InnoCard> cards, InnoColor...colors){
		for(InnoCard card : cards){
			for(InnoColor c : colors){
				if(card.color==c){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断这些卡牌中是否有相同的颜色
	 * 
	 * @param cards
	 * @return
	 */
	public static boolean hasSameColor(Collection<InnoCard> cards){
		Map<InnoColor, Integer> map = new HashMap<InnoColor, Integer>();
		for(InnoCard card : cards){
			Integer i = map.get(card.color);
			if(i==null){
				i = 0;
			}
			i += 1;
			map.put(card.color, i);
		}
		for(Integer i : map.values()){
			if(i>1){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 取得所有牌中最小等级的牌
	 * 
	 * @param cards
	 * @return
	 */
	public static int getMinLevel(Collection<InnoCard> cards){
		int i = 99;
		for(InnoCard c : cards){
			if(c.level<i){
				i = c.level;
			}
		}
		return i;
	}
	
	/**
	 * 取得所有牌中最高等级的牌
	 * 
	 * @param cards
	 * @return
	 */
	public static int getMaxLevel(Collection<InnoCard> cards){
		int i = 0;
		for(InnoCard c : cards){
			if(c.level>i){
				i = c.level;
			}
		}
		return i;
	}
	
	/**
	 * 取得指定牌中,num张最高级的牌中的最低等级
	 * 
	 * @param cards
	 * @param num
	 * @return
	 */
	public static int getMaxLevel(Collection<InnoCard> cards, int num){
		List<InnoCard> list = new ArrayList<InnoCard>(cards);
		Collections.sort(list, new InnoCardLevelComparator());
		int i = list.size()-num;
		if(i<0){
			i = 0;
		}
		return list.get(i).level;
	}
	
	/**
	 * 取得玩家人数对应的成就胜利条件
	 * 
	 * @param playerNumber
	 * @return
	 */
	public static int getVictoryAchieveNumber(InnoGameMode gameMode){
		int playerNumber = gameMode.getGame().getCurrentPlayerNumber();
		if(gameMode.getGame().isTeamMatch()){
			//组队赛时总是返回6
			return 6;
		}else{
			switch(playerNumber){
			case 2:
				return 6;
			case 3:
				return 5;
			case 4:
				return 4;
			}
		}
		return -1;
	}
	
	/**
	 * 取得所有得分最高的玩家
	 * 
	 * @param players
	 * @return
	 */
	public static List<InnoPlayer> getHighestScorePlayers(List<InnoPlayer> players){
		List<InnoPlayer> list = new ArrayList<InnoPlayer>(players);
		Collections.sort(list, new InnoPlayerScoreComparator());
		Collections.reverse(list);
		int highestScore = list.get(0).getScore();
		List<InnoPlayer> res = new ArrayList<InnoPlayer>();
		for(InnoPlayer p : list){
			if(p.getScore()==highestScore){
				res.add(p);
			}
		}
		return res;
	}
	
	/**
	 * 取得所有得分最低的玩家
	 * 
	 * @param players
	 * @return
	 */
	public static List<InnoPlayer> getLowestScorePlayers(List<InnoPlayer> players){
		List<InnoPlayer> list = new ArrayList<InnoPlayer>(players);
		Collections.sort(list, new InnoPlayerScoreComparator());
		int lowestScore = list.get(0).getScore();
		List<InnoPlayer> res = new ArrayList<InnoPlayer>();
		for(InnoPlayer p : list){
			if(p.getScore()==lowestScore){
				res.add(p);
			}
		}
		return res;
	}
	
	/**
	 * 取得所有指定符号最多的玩家
	 * 
	 * @param players
	 * @param icon
	 * @return
	 */
	public static List<InnoPlayer> getMostIconPlayers(List<InnoPlayer> players, InnoIcon icon){
		List<InnoPlayer> list = new ArrayList<InnoPlayer>(players);
		Collections.sort(list, new InnoPlayerIconComparator(icon));
		Collections.reverse(list);
		int highestValue = list.get(0).getIconCount(icon);
		List<InnoPlayer> res = new ArrayList<InnoPlayer>();
		for(InnoPlayer p : list){
			if(p.getIconCount(icon)==highestValue){
				res.add(p);
			}
		}
		return res;
	}
	
}
