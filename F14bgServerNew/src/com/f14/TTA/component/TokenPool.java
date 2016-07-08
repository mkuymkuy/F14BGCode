package com.f14.TTA.component;

import com.f14.TTA.consts.Token;
import com.f14.bg.component.PartPool;

/**
 * TTA玩家的指示物
 * 
 * @author F14eagle
 *
 */
public class TokenPool {

	/**
	 * 配件池
	 */
	private PartPool parts;

	public TokenPool() {
		this.init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		// 设置指示物数量
		this.parts = new PartPool();
		this.parts.setPart(Token.AVAILABLE_WORKER, 18);
		this.parts.setPart(Token.UNUSED_WORKER, 1);
		this.parts.setPart(Token.AVAILABLE_BLUE, 18);
		this.parts.setPart(Token.UNHAPPY_WORKER, 0);
	}

	/**
	 * 取得所有可用的工人数
	 * 
	 * @return
	 */
	public int getAvailableWorkers() {
		return this.parts.getAvailableNum(Token.AVAILABLE_WORKER);
	}

	/**
	 * 取得所有空闲的工人数
	 * 
	 * @return
	 */
	public int getUnusedWorkers() {
		return this.parts.getAvailableNum(Token.UNUSED_WORKER);
	}

	/**
	 * 设置不愉快的工人数
	 */
	public void setUnhappyWorkers(int num) {
		this.parts.setPart(Token.UNHAPPY_WORKER, num);
	}

	/**
	 * 取得所有不愉快的工人数
	 * 
	 * @return
	 */
	public int getUnhappyWorkers() {
		return this.parts.getAvailableNum(Token.UNHAPPY_WORKER);
	}

	/**
	 * 取得所有可用的蓝色指示物数量
	 * 
	 * @return
	 */
	public int getAvailableBlues() {
		return this.parts.getAvailableNum(Token.AVAILABLE_BLUE);
	}

	/**
	 * 调整蓝色指示物的数量
	 * 
	 * @param num
	 * @return
	 */
	public int addAvailableBlues(int num) {
		if (num > 0) {
			this.putAvailableBlues(num);
			return num;
		} else {
			return this.takeAvailableBlues(-num);
		}
	}

	/**
	 * 取出指定数量的蓝色指示物
	 * 
	 * @param num
	 * @return
	 */
	public int takeAvailableBlues(int num) {
		return this.parts.takePart(Token.AVAILABLE_BLUE, num);
	}

	/**
	 * 放入指定数量的蓝色指示物
	 * 
	 * @param num
	 */
	public void putAvailableBlues(int num) {
		this.parts.putPart(Token.AVAILABLE_BLUE, num);
	}

	/**
	 * 调整可用工人数量
	 * 
	 * @param num
	 * @return
	 */
	public int addAvailableWorker(int num) {
		return this.addToken(Token.AVAILABLE_WORKER, num);
	}

	/**
	 * 调整空闲工人数量
	 * 
	 * @param num
	 */
	public void addUnusedWorker(int num) {
		this.addToken(Token.UNUSED_WORKER, num);
	}

	/**
	 * 调整配件的数量
	 * 
	 * @param token
	 * @param num
	 * @return
	 */
	protected int addToken(Token token, int num) {
		if (num > 0) {
			this.parts.putPart(token, num);
			return num;
		} else {
			return this.parts.takePart(token, -num);
		}
	}

}
