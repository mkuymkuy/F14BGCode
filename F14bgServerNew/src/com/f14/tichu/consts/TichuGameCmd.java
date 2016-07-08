package com.f14.tichu.consts;

/**
 * Tichu的游戏指令
 * 
 * @author F14eagle
 *
 */
public class TichuGameCmd {

	/**
	 * 代码类型 - 游戏指令 - 刷新游戏基本信息
	 */
	public static final int GAME_CODE_BASE_INFO = 0x4000;
	/**
	 * 代码类型 - 游戏指令 - 刷新玩家的基本信息
	 */
	public static final int GAME_CODE_PLAYER_INFO = 0x4001;
	/**
	 * 代码类型 - 游戏指令 - 刷新玩家的手牌
	 */
	public static final int GAME_CODE_PLAYER_HAND = 0x4002;
	/**
	 * 代码类型 - 游戏指令 - 刷新玩家打出的牌
	 */
	public static final int GAME_CODE_PLAYER_PLAY_CARD = 0x4003;
	/**
	 * 代码类型 - 游戏指令 - 刷新玩家的按键信息
	 */
	public static final int GAME_CODE_PLAYER_BUTTON = 0x4004;
	
	/**
	 * 代码类型 - 游戏指令 - 叫大地主阶段
	 */
	public static final int GAME_CODE_BIG_TICHU_PHASE = 0x4400;
	/**
	 * 代码类型 - 游戏指令 - 换牌阶段
	 */
	public static final int GAME_CODE_REGROUP_PHASE = 0x4401;
	/**
	 * 代码类型 - 游戏指令 - 正常回合阶段
	 */
	public static final int GAME_CODE_ROUND_PHASE = 0x4402;
	/**
	 * 代码类型 - 游戏指令 - 选择给对方分数
	 */
	public static final int GAME_CODE_GIVE_SCORE = 0x4403;
	/**
	 * 代码类型 - 游戏指令 - 玩家许愿
	 */
	public static final int GAME_CODE_WISH_POINT = 0x4404;
	/**
	 * 代码类型 - 游戏指令 - 回合确认阶段
	 */
	public static final int GAME_CODE_ROUND_RESULT = 0x4405;
	/**
	 * 代码类型 - 游戏指令 - 确认换牌阶段
	 */
	public static final int GAME_CODE_CONFIRM_EXCHANGE = 0x4406;
	/**
	 * 代码类型 - 游戏指令 - 选择炸弹的阶段
	 */
	public static final int GAME_CODE_BOMB_PHASE = 0x4407;
}
