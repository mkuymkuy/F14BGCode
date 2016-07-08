package com.f14.innovation.param;

import com.f14.bg.anim.AnimType;
import com.f14.bg.consts.ConditionResult;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoPlayerTargetType;
import com.f14.innovation.consts.InnoSplayDirection;
import com.f14.utils.StringUtils;

public class InnoInitParam {
	public int num;
	public int maxNum;
	public int level;
	public String type;
	public InnoColor color;
	public InnoSplayDirection splayDirection;
	public InnoCard card;
	public InnoCard trigCard;
	public AnimType animType;
	public boolean setActived;
	public boolean setActiveAgain;
	/**
	 * 提示信息
	 */
	public String msg = "";
	/**
	 * 是否可以跳过执行
	 */
	public boolean canPass;
	/**
	 * 是否可以取消执行
	 */
	public boolean canCancel;	
	public ConditionResult conditionResult;
	/**
	 * 是否显示确认按钮,默认为true
	 */
	public boolean showConfrimButton = true;
	/**
	 * 目标玩家类型
	 */
	public InnoPlayerTargetType targetPlayer;
	/**
	 * 触发玩家类型
	 */
	public InnoPlayerTargetType trigPlayer;
	public boolean canChooseSelf;
	/**
	 * 是否检查成就
	 */
	public boolean checkAchieve;
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getMaxNum() {
		return maxNum;
	}
	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public InnoCard getCard() {
		return card;
	}
	public void setCard(InnoCard card) {
		this.card = card;
	}
	public InnoCard getTrigCard() {
		return trigCard;
	}
	public void setTrigCard(InnoCard trigCard) {
		this.trigCard = trigCard;
	}
	public AnimType getAnimType() {
		return animType;
	}
	public void setAnimType(AnimType animType) {
		this.animType = animType;
	}
	public boolean isSetActived() {
		return setActived;
	}
	public void setSetActived(boolean setActived) {
		this.setActived = setActived;
	}
	public boolean isSetActiveAgain() {
		return setActiveAgain;
	}
	public void setSetActiveAgain(boolean setActiveAgain) {
		this.setActiveAgain = setActiveAgain;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isCanPass() {
		return canPass;
	}
	public void setCanPass(boolean canPass) {
		this.canPass = canPass;
	}
	public boolean isCanCancel() {
		return canCancel;
	}
	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}
	/**
	 * 该方法将取得替换{num}值后的提示信息
	 * 
	 * @return
	 */
	public String getRealMsg() {
		if(StringUtils.isEmpty(msg)){
			return "";
		}else{
			String res = msg.replaceAll("\\{num\\}", Math.abs(this.num)+"");
			res = res.replaceAll("\\{maxNum\\}", Math.abs(this.maxNum)+"");
			return res;
		}
	}
	public ConditionResult getConditionResult() {
		return conditionResult;
	}
	public void setConditionResult(ConditionResult conditionResult) {
		this.conditionResult = conditionResult;
	}
	public boolean isShowConfrimButton() {
		return showConfrimButton;
	}
	public void setShowConfrimButton(boolean showConfrimButton) {
		this.showConfrimButton = showConfrimButton;
	}
	public InnoPlayerTargetType getTargetPlayer() {
		return targetPlayer;
	}
	public void setTargetPlayer(InnoPlayerTargetType targetPlayer) {
		this.targetPlayer = targetPlayer;
	}
	public InnoPlayerTargetType getTrigPlayer() {
		return trigPlayer;
	}
	public void setTrigPlayer(InnoPlayerTargetType trigPlayer) {
		this.trigPlayer = trigPlayer;
	}
	public InnoColor getColor() {
		return color;
	}
	public void setColor(InnoColor color) {
		this.color = color;
	}
	public InnoSplayDirection getSplayDirection() {
		return splayDirection;
	}
	public void setSplayDirection(InnoSplayDirection splayDirection) {
		this.splayDirection = splayDirection;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isCanChooseSelf() {
		return canChooseSelf;
	}
	public void setCanChooseSelf(boolean canChooseSelf) {
		this.canChooseSelf = canChooseSelf;
	}
	public boolean isCheckAchieve() {
		return checkAchieve;
	}
	public void setCheckAchieve(boolean checkAchieve) {
		this.checkAchieve = checkAchieve;
	}
}
