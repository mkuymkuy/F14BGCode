package com.f14.innovation.anim;

import com.f14.bg.anim.AnimObjectType;
import com.f14.bg.anim.AnimParam;
import com.f14.bg.anim.AnimType;
import com.f14.bg.anim.AnimVar;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.consts.InnoAnimPosition;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoSplayDirection;

public class InnoAnimParamFactory {
	
	/**
	 * 按照动画方式创建动画对象
	 * 
	 * @param card
	 * @param animType
	 * @return
	 */
	public static AnimVar createAnimObject(InnoCard card, AnimType animType){
		if(animType==AnimType.REVEAL){
			//如果动画类型是展示,则创建明牌
			return AnimVar.createAnimObjectVar(AnimObjectType.CARD, card.id);
		}else{
			//否则创建暗牌
			return AnimVar.createAnimObjectVar(AnimObjectType.CARD_BACK, card.level);
		}
	}

	/**
	 * 创建玩家合并卡牌的动画参数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	public static AnimParam createMeldCardParam(InnoPlayer player, InnoCard card, AnimVar from){
		AnimParam res = new AnimParam();
		res.animType = AnimType.REVEAL;
		res.from = from;
		res.to = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_STACKS, player.position, card.color);
		res.animObject = createAnimObject(card, AnimType.REVEAL);
		return res;
	}
	
	/**
	 * 创建玩家摸牌的动画参数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	public static AnimParam createDrawCardParam(InnoPlayer player, InnoCard card, AnimVar from, AnimType animType){
		AnimParam res = new AnimParam();
		res.animType = animType;
		res.from = from;
		res.to = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_HANDS, player.position);
		res.animObject = createAnimObject(card, animType);
		return res;
	}
	
	/**
	 * 创建归还卡牌的动画参数
	 * 
	 * @param card
	 * @return
	 */
	public static AnimParam createReturnCardParam(InnoCard card, AnimVar from, AnimType animType){
		AnimParam res = new AnimParam();
		res.animType = animType;
		res.from = from;
		res.to = AnimVar.createAnimVar(InnoAnimPosition.DRAW_DECK, "", card.level);
		res.animObject = createAnimObject(card, res.animType);
		return res;
	}
	
	/**
	 * 创建玩家拿成就牌的动画参数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	public static AnimParam createDrawAchieveCardParam(InnoPlayer player, InnoCard card){
		AnimParam res = new AnimParam();
		res.animType = AnimType.DIRECT;
		res.from = AnimVar.createAnimVar(InnoAnimPosition.ACHIEVE_DECK, "", card.level);
		res.to = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_ACHIEVES, player.position);
		res.animObject = createAnimObject(card, res.animType);
		return res;
	}
	
	/**
	 * 创建玩家拿特殊成就牌的动画参数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	public static AnimParam createDrawSpecialAchieveCardParam(InnoPlayer player, InnoCard card){
		AnimParam res = new AnimParam();
		res.animType = AnimType.DIRECT;
		res.from = AnimVar.createAnimVar(InnoAnimPosition.SPECIAL_ACHIEVE_DECK, "");
		res.to = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_ACHIEVES, player.position);
		res.animObject = createAnimObject(card, AnimType.REVEAL);
		return res;
	}
	
	/**
	 * 创建玩家计分的动画参数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	public static AnimParam createAddScoreParam(InnoPlayer player, InnoCard card, AnimVar from, AnimType animType){
		AnimParam res = new AnimParam();
		res.animType = animType;
		res.from = from;
		res.to = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_SCORES, player.position);
		res.animObject = createAnimObject(card, res.animType);
		return res;
	}
	
	/**
	 * 创建玩家展开牌堆的动画参数
	 * 
	 * @param player
	 * @param color
	 * @param splayDirection
	 * @return
	 */
	public static AnimParam createSplayCardParam(InnoPlayer player, InnoColor color, InnoSplayDirection splayDirection){
		AnimParam res = new AnimParam();
		res.animType = AnimType.DIRECT;
		res.from = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_STACKS, player.position, color);
		return res;
	}
	
	/**
	 * 创建玩家触发卡牌效果的动画参数
	 * 
	 * @param player
	 * @param card
	 * @return
	 */
	public static AnimParam createDogmaCardParam(InnoPlayer player, InnoCard card){
		AnimParam res = new AnimParam();
		res.animType = AnimType.REVEAL_FADEOUT;
		res.from = AnimVar.createAnimVar(InnoAnimPosition.PLAYER_STACKS, player.position, card.color);
		res.animObject = createAnimObject(card, AnimType.REVEAL);
		return res;
	}
	
}
