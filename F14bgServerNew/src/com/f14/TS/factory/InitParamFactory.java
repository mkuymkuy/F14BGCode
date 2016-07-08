package com.f14.TS.factory;

import com.f14.TS.TSGameMode;
import com.f14.TS.TSPlayer;
import com.f14.TS.action.ActionParam;
import com.f14.TS.component.TSCard;
import com.f14.TS.component.ability.TSAbility;
import com.f14.TS.component.condition.TSCountryCondition;
import com.f14.TS.component.condition.TSCountryConditionGroup;
import com.f14.TS.consts.ActionType;
import com.f14.TS.consts.SubRegion;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TrigType;
import com.f14.TS.listener.initParam.ActionInitParam;
import com.f14.TS.listener.initParam.CardActionInitParam;
import com.f14.TS.listener.initParam.ChoiceInitParam;
import com.f14.TS.listener.initParam.ConditionInitParam;
import com.f14.TS.listener.initParam.ExecuterInitParam;
import com.f14.TS.listener.initParam.OPActionInitParam;
import com.f14.TS.listener.initParam.RealignmentInitParam;

/**
 * 初始化参数的工厂类
 * 
 * @author F14eagle
 *
 */
public class InitParamFactory {

	/**
	 * 创建游戏设置时放置影响力的初始化参数
	 * 
	 * @param power
	 * @return
	 */
	public static ActionInitParam createSetupInfluence(SuperPower power){
		ActionInitParam initParam = new ActionInitParam();
		switch(power){
		case USSR: //苏联在东欧分配6点影响力
			initParam.listeningPlayer = power;
			initParam.targetPower = power;
			initParam.actionType = ActionType.ADJUST_INFLUENCE;
			initParam.num = 6;
			initParam.msg = "请在东欧分配 {num} 点影响力!";
			TSCountryCondition c = new TSCountryCondition();
			c.subRegion = SubRegion.EAST_EUROPE;
			initParam.addWc(c);
			break;
		case USA: //美国在西欧分配7点影响力
			initParam.listeningPlayer = power;
			initParam.targetPower = power;
			initParam.actionType = ActionType.ADJUST_INFLUENCE;
			initParam.num = 7;
			initParam.msg = "请在西欧分配 {num} 点影响力!";
			c = new TSCountryCondition();
			c.subRegion = SubRegion.WEST_EUROPE;
			initParam.addWc(c);
			break;
		default:
			return null;
		}
		return initParam;
	}
	
	/**
	 * 创建游戏开始时苏联让点的初始化参数
	 * 
	 * @return
	 */
	public static ActionInitParam createGivenPointInfluence(int num){
		ActionInitParam initParam = new ActionInitParam();
		//由美国在已有影响力的国家分配点数
		initParam.listeningPlayer = SuperPower.USA;
		initParam.targetPower = SuperPower.USA;
		initParam.actionType = ActionType.ADJUST_INFLUENCE;
		initParam.num = num;
		initParam.msg = "请在已有自己影响力的国家分配 {num} 点影响力!";
		TSCountryCondition c = new TSCountryCondition();
		c.hasUsaInfluence = true;
		initParam.addWc(c);
		return initParam;
	}
	
	/**
	 * 创建回合行动时放置影响力的初始化参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param op
	 * @param trigType
	 * @return
	 */
	public static OPActionInitParam createAddInfluenceParam(TSGameMode gameMode, TSPlayer player, TSCard card, int op, TrigType trigType, boolean isFreeAction, TSCountryConditionGroup conditionGroup){
		OPActionInitParam initParam = new OPActionInitParam();
		initParam.listeningPlayer = player.superPower;
		initParam.targetPower = player.superPower;
		initParam.actionType = ActionType.ADD_INFLUENCE;
		initParam.card = card;
		initParam.trigType = trigType;
		initParam.canCancel = true;
		initParam.canPass = false;
		initParam.num = op;
		initParam.msg = "请用 {num}OP 放置影响力!";
		initParam.isFreeAction = isFreeAction;
		initParam.setConditionGroup(conditionGroup);
		return initParam;
	}
	
	/**
	 * 创建回合行动时政变的初始化参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param op
	 * @param trigType
	 * @return
	 */
	public static OPActionInitParam createCoupParam(TSGameMode gameMode, TSPlayer player, TSCard card, int op, TrigType trigType, boolean isFreeAction, TSCountryConditionGroup conditionGroup){
		OPActionInitParam initParam = new OPActionInitParam();
		initParam.listeningPlayer = player.superPower;
		initParam.targetPower = SuperPower.getOppositeSuperPower(player.superPower);
		initParam.actionType = ActionType.COUP;
		initParam.card = card;
		initParam.trigType = trigType;
		initParam.canCancel = true;
		initParam.canPass = false;
		initParam.num = op;
		initParam.msg = "请用 {num}OP 进行政变!";
		initParam.isFreeAction = isFreeAction;
		initParam.setConditionGroup(conditionGroup);
		return initParam;
	}
	
	/**
	 * 创建回合行动时调整阵营的初始化参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param op
	 * @param trigType
	 * @return
	 */
	public static RealignmentInitParam createRealignmentParam(TSGameMode gameMode, TSPlayer player, TSCard card, int op, TrigType trigType, boolean isFreeAction, TSCountryConditionGroup conditionGroup){
		RealignmentInitParam initParam = new RealignmentInitParam();
		initParam.listeningPlayer = player.superPower;
		initParam.targetPower = SuperPower.getOppositeSuperPower(player.superPower);
		initParam.actionType = ActionType.REALIGNMENT;
		initParam.card = card;
		initParam.trigType = trigType;
		initParam.canCancel = true;
		initParam.canPass = false;
		initParam.num = op;
		initParam.msg = "请用 {num}OP 调整阵营!";
		initParam.isFreeAction = isFreeAction;
		initParam.setConditionGroup(conditionGroup);
		return initParam;
	}
	
	/**
	 * 创建行动参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @return
	 */
	public static ActionInitParam createActionInitParam(TSGameMode gameMode, TSPlayer player, TSCard card, TrigType trigType){
		ActionInitParam initParam = new ActionInitParam();
		initParam.card = card;
		initParam.listeningPlayer = player.superPower;
		initParam.trigType = trigType;
		return initParam;
	}
	
	/**
	 * 按照TS能力创建行动监听器初始化参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param ability
	 * @param trigType
	 * @return
	 */
	public static ActionInitParam createActionInitParam(TSGameMode gameMode, TSPlayer player, TSCard card, TSAbility ability, TrigType trigType){
		ActionInitParam initParam = new ActionInitParam();
		initParam.card = card;
		initParam.trigType = trigType;
		initParam.targetPower = ability.actionParam.targetPower;
		initParam.actionType = ability.actionParam.actionType;
		initParam.num = ability.actionParam.num;
		initParam.countryNum = ability.actionParam.countryNum;
		initParam.limitNum = ability.actionParam.limitNum;
		initParam.msg = ability.actionParam.descr;
		initParam.canCancel = ability.actionParam.canCancel;
		initParam.canPass = ability.actionParam.canPass;
		initParam.canLeft = ability.actionParam.canLeft;
		initParam.clazz = ability.actionParam.clazz;
		initParam.setConditionGroup(ability.getCountryCondGroup());
		//设置监听的目标玩家
		if(ability.trigPower!=null){
			initParam.listeningPlayer = gameMode.getGame().convertSuperPower(ability.trigPower, player);
		}
		return initParam;
	}
	
	/**
	 * 按照TS能力创建卡牌行动监听器初始化参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param ability
	 * @param trigType
	 * @return
	 */
	public static CardActionInitParam createCardActionInitParam(TSGameMode gameMode, TSPlayer player, TSCard card, TSAbility ability, TrigType trigType){
		CardActionInitParam initParam = new CardActionInitParam();
		initParam.card = card;
		initParam.trigType = trigType;
		initParam.targetPower = ability.actionParam.targetPower;
		initParam.num = ability.actionParam.num;
		initParam.msg = ability.actionParam.descr;
		initParam.canCancel = ability.actionParam.canCancel;
		initParam.canPass = ability.actionParam.canPass;
		initParam.setConditionGroup(ability.getCardCondGroup());
		//设置监听的目标玩家
		if(ability.trigPower!=null){
			initParam.listeningPlayer = gameMode.getGame().convertSuperPower(ability.trigPower, player);
		}
		return initParam;
	}
	
	/**
	 * 创建回合行动时使用OP的初始化参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param trigType
	 * @return
	 */
	public static OPActionInitParam createOpActionParam(TSGameMode gameMode, TSPlayer player, TSCard card, TrigType trigType){
		OPActionInitParam initParam = new OPActionInitParam();
		initParam.listeningPlayer = player.superPower;
		initParam.targetPower = player.superPower;
		initParam.card = card;
		initParam.trigType = trigType;
		initParam.canCancel = false;
		initParam.canPass = false;
		initParam.canAddInfluence = true;
		initParam.canCoup = true;
		initParam.canRealignment = true;
		initParam.isFreeAction = false;
		initParam.num = player.getOp(card);
		initParam.msg = "请用 {num}OP 进行行动!";
		return initParam;
	}
	
	/**
	 * 按照TS能力创建OP行动监听器初始化参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param ability
	 * @param trigType
	 * @return
	 */
	public static OPActionInitParam createOpActionParam(TSGameMode gameMode, TSPlayer player, TSCard card, TSAbility ability, TrigType trigType){
		OPActionInitParam initParam = new OPActionInitParam();
		initParam.card = card;
		initParam.trigType = trigType;
		initParam.targetPower = ability.actionParam.targetPower;
		initParam.actionType = ability.actionParam.actionType;
		initParam.msg = ability.actionParam.descr;
		initParam.canCancel = ability.actionParam.canCancel;
		initParam.canPass = ability.actionParam.canPass;
		initParam.canAddInfluence = ability.actionParam.canAddInfluence;
		initParam.canCoup = ability.actionParam.canCoup;
		initParam.canRealignment = ability.actionParam.canRealignment;
		initParam.isFreeAction = ability.actionParam.isFreeAction;
		initParam.setConditionGroup(ability.getCountryCondGroup());
		//设置监听的目标玩家
		if(ability.trigPower!=null){
			initParam.listeningPlayer = gameMode.getGame().convertSuperPower(ability.trigPower, player);
		}
		//设置可使用的OP数
		if(ability.actionParam.num>0){
			//如果在参数中设置了num,则使用num
			initParam.num = ability.actionParam.num;
		}/*else{
			TSPlayer p = gameMode.getGame().getPlayer(initParam.listeningPlayer);
			initParam.num = p.getOp(card);
		}*/
		
		//如果num的值和card的OP不同,则clone一个card,并设置card的op为num
		if(card.op!=initParam.num && initParam.num>0){
			TSCard tmpCard = card.clone();
			tmpCard.op = initParam.num;
			initParam.card = tmpCard;
		}
		return initParam;
	}
	
	/**
	 * 按照TS能力创建行动监听器初始化参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param ability
	 * @param trigType
	 * @return
	 */
	public static ChoiceInitParam createChoiceInitParam(TSGameMode gameMode, TSPlayer player, TSCard card, TrigType trigType){
		ChoiceInitParam initParam = new ChoiceInitParam();
		initParam.card = card;
		initParam.trigType = trigType;
		initParam.targetPower = player.superPower;
		//设置监听的目标玩家
		if(card.abilityGroup.trigPower!=null){
			initParam.listeningPlayer = gameMode.getGame().convertSuperPower(card.abilityGroup.trigPower, player);
		}
		return initParam;
	}
	
	/**
	 * 按照TS能力创建行动执行器的初始化参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param ability
	 * @param trigType
	 * @return
	 */
	public static ExecuterInitParam createExecuterInitParam(TSGameMode gameMode, TSPlayer player, TSCard card, TSAbility ability, TrigType trigType){
		ExecuterInitParam initParam = new ExecuterInitParam();
		initParam.card = card;
		initParam.trigType = trigType;
		initParam.clazz = ability.actionParam.clazz;
		initParam.targetPower = ability.actionParam.targetPower;
		initParam.num = ability.actionParam.num;
		initParam.canCancel = ability.actionParam.canCancel;
		initParam.canPass = ability.actionParam.canPass;
		//设置监听的目标玩家
		if(ability.trigPower!=null){
			initParam.listeningPlayer = gameMode.getGame().convertSuperPower(ability.trigPower, player);
		}
		return initParam;
	}
	
	/**
	 * 按照参数创建条件判断类的初始化参数
	 * 
	 * @param gameMode
	 * @param player
	 * @param card
	 * @param actionParam
	 * @return
	 */
	public static ConditionInitParam createConditionInitParam(TSGameMode gameMode, TSPlayer player, TSCard card, ActionParam actionParam){
		ConditionInitParam initParam = new ConditionInitParam();
		initParam.card = card;
		initParam.clazz = actionParam.clazz;
		//initParam.targetPower = actionParam.targetPower;
		//initParam.num = actionParam.num;
		//initParam.canCancel = actionParam.canCancel;
		//initParam.canPass = actionParam.canPass;
		//设置监听的目标玩家
		if(actionParam.trigPower!=null){
			initParam.listeningPlayer = gameMode.getGame().convertSuperPower(actionParam.trigPower, player);
		}
		return initParam;
	}
}
