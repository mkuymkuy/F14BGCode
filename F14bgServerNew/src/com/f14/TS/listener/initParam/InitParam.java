package com.f14.TS.listener.initParam;

import com.f14.TS.component.TSCard;
import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TrigType;
import com.f14.utils.StringUtils;

/**
 * 初始化参数的基类
 * 
 * @author F14eagle
 *
 */
public abstract class InitParam implements Cloneable {
	/**
	 * 执行该监听器的玩家
	 */
	public SuperPower listeningPlayer;
	/**
	 * 目标的超级大国
	 */
	public SuperPower targetPower;
	/**
	 * 数量
	 */
	public int num;
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
	/**
	 * 是否可以不完全选择数量
	 */
	public boolean canLeft;
	/**
	 * 使用到的相关卡牌
	 */
	public TSCard card;
	/**
	 * 触发类型
	 */
	public TrigType trigType;
	public String clazz;

	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public SuperPower getListeningPlayer() {
		return listeningPlayer;
	}
	public void setListeningPlayer(SuperPower listeningPlayer) {
		this.listeningPlayer = listeningPlayer;
	}
	public SuperPower getTargetPower() {
		return targetPower;
	}
	public void setTargetPower(SuperPower targetPower) {
		this.targetPower = targetPower;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
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
	public boolean isCanLeft() {
		return canLeft;
	}
	public void setCanLeft(boolean canLeft) {
		this.canLeft = canLeft;
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
			return msg.replaceAll("\\{num\\}", Math.abs(this.num)+"");
		}
	}
	public TSCard getCard() {
		return card;
	}
	public void setCard(TSCard card) {
		this.card = card;
	}
	public TrigType getTrigType() {
		return trigType;
	}
	public void setTrigType(TrigType trigType) {
		this.trigType = trigType;
	}
	@Override
	public InitParam clone() {
		try {
			return (InitParam) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
