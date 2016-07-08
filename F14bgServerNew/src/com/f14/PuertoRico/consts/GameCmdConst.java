package com.f14.PuertoRico.consts;

import com.f14.F14bg.consts.CmdConst;

public class GameCmdConst extends CmdConst {
	/**
	 * 代码类型 - 游戏指令 - 选择角色
	 */
	public static final int GAME_CODE_CHOOSE_CHARACTER = 0x4000;
	/**
	 * 代码类型 - 游戏指令 - 显示所有玩家选择的行动
	 */
	public static final int GAME_CODE_SHOW_ACTION = 0x4001;
	/**
	 * 代码类型 - 游戏指令 - 读取所有玩家信息
	 */
	public static final int GAME_CODE_LOAD_PLAYER = 0x4002;
	/**
	 * 代码类型 - 游戏指令 - 设置游戏起始信息
	 */
	public static final int GAME_CODE_SETUP = 0x4003;
	/**
	 * 代码类型 - 游戏指令 - 市长阶段
	 */
	public static final int GAME_CODE_MAJOR = 0x4004;
	/**
	 * 代码类型 - 游戏指令 - 淘金者阶段
	 */
	public static final int GAME_CODE_PROSPECTOR = 0x4005;
	/**
	 * 代码类型 - 游戏指令 - 拓荒者阶段
	 */
	public static final int GAME_CODE_SETTLE = 0x4006;
	/**
	 * 代码类型 - 游戏指令 - 商人阶段
	 */
	public static final int GAME_CODE_TRADER = 0x4007;
	/**
	 * 代码类型 - 游戏指令 - 手工艺者阶段
	 */
	public static final int GAME_CODE_CRAFTSMAN = 0x4008;
	/**
	 * 代码类型 - 游戏指令 - 建筑师阶段
	 */
	public static final int GAME_CODE_BUILDER = 0x4009;
	/**
	 * 代码类型 - 游戏指令 - 船长阶段
	 */
	public static final int GAME_CODE_CAPTAIN = 0x400A;
	/**
	 * 代码类型 - 游戏指令 - 玩家得到配件
	 */
	public static final int GAME_CODE_GET_PART = 0x400B;
	/**
	 * 代码类型 - 游戏指令 - 公共资源得到配件
	 */
	public static final int GAME_CODE_GET_SUPPLY_PART = 0x4018;
	/**
	 * 代码类型 - 游戏指令 - 刷新玩家移民的分配情况
	 */
	public static final int GAME_CODE_COLONIST_INFO = 0x400C;
	/**
	 * 代码类型 - 游戏指令 - 刷新公开的种植园板块
	 */
	public static final int GAME_CODE_REFRESH_PLANTATIONS = 0x400D;
	/**
	 * 代码类型 - 游戏指令 - 移除公开的种植园板块
	 */
	public static final int GAME_CODE_REMOVE_PLANTATION = 0x4017;
	/**
	 * 代码类型 - 游戏指令 - 玩家得到种植园板块
	 */
	public static final int GAME_CODE_GET_PLANTATION = 0x400E;
	/**
	 * 代码类型 - 游戏指令 - 玩家得到VP
	 */
	public static final int GAME_CODE_GET_VP = 0x400F;
	/**
	 * 代码类型 - 游戏指令 - 玩家得到金钱
	 */
	public static final int GAME_CODE_GET_DOUBLOON = 0x4010;
	/**
	 * 代码类型 - 游戏指令 - 刷新配件信息
	 */
	public static final int GAME_CODE_REFRESH_PART = 0x4011;
	/**
	 * 代码类型 - 游戏指令 - 玩家得到采石场板块
	 */
	public static final int GAME_CODE_GET_QUARRY = 0x4012;
	/**
	 * 代码类型 - 游戏指令 - 交易所得到货物
	 */
	public static final int GAME_CODE_GET_TRADEHOUSE = 0x4013;
	/**
	 * 代码类型 - 游戏指令 - 刷新交易所信息
	 */
	public static final int GAME_CODE_REFRESH_TRADEHOUSE = 0x4014;
	/**
	 * 代码类型 - 游戏指令 - 玩家得到建筑
	 */
	public static final int GAME_CODE_GET_BUILDING = 0x4015;
	/**
	 * 代码类型 - 游戏指令 - 建筑版图移除建筑
	 */
	public static final int GAME_CODE_REMOVE_BUILDING_SUPPLY = 0x4016;
	/**
	 * 代码类型 - 游戏指令 - 货船得到货物
	 */
	public static final int GAME_CODE_GET_SHIP = 0x4019;
	/**
	 * 代码类型 - 游戏指令 - 刷新货船的信息
	 */
	public static final int GAME_CODE_REFRESH_SHIP = 0x401A;
	/**
	 * 代码类型 - 游戏指令 - 刷新角色的信息
	 */
	public static final int GAME_CODE_REFRESH_CHARACTER = 0x401B;
	/**
	 * 代码类型 - 游戏指令 - 刷新建筑的信息
	 */
	public static final int GAME_CODE_REFRESH_BUILDING = 0x401C;
	/**
	 * 代码类型 - 游戏指令 - 船长阶段结束后的弃货阶段
	 */
	public static final int GAME_CODE_CAPTAIN_END = 0x401D;
	/**
	 * 代码类型 - 游戏指令 - 刷新玩家的资源状况
	 */
	public static final int GAME_CODE_REFRESH_PLAYER_RESOURCE = 0x401E;
	/**
	 * 代码类型 - 游戏指令 - 装载货船容量的信息
	 */
	public static final int GAME_CODE_LOAD_SHIP = 0x401F;
	/**
	 * 代码类型 - 游戏指令 - 通用等待确认信息
	 */
	public static final int GAME_CODE_COMMON_CONFIRM = 0x4020;
	/**
	 * 代码类型 - 游戏指令 - 玩家选择所使用建筑的阶段
	 */
	public static final int GAME_CODE_CHOOSE_BUILDING_PHASE = 0x4021;
	/**
	 * 代码类型 - 游戏指令 - 玩家选择建筑
	 */
	public static final int GAME_CODE_CHOOSE_BUILDING = 0x4022;
}
