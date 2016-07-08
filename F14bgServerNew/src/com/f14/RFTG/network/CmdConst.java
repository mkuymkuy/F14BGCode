package com.f14.RFTG.network;

/**
 * 指令常量
 * 
 * @author F14eagle
 *
 */
public class CmdConst {
	/**
	 * 应用程序代码
	 */
	public static final int APPLICATION_FLAG = 0x1401;
	
	/**
	 * 指令类型 - 系统指令
	 */
	public static final int SYSTEM_CMD = 0x39;
	/**
	 * 指令类型 - 聊天指令
	 */
	public static final int CHAT_CMD = 0x10;
	/**
	 * 指令类型 - 游戏指令
	 */
	public static final int GAME_CMD = 0x20;
	/**
	 * 指令类型 - 异常指令
	 */
	public static final int EXCEPTION_CMD = 0x00;
	
	/**
	 * 代码类型 - 系统指令 - 登录
	 */
	public static final int SYSTEM_CODE_LOGIN = 0x3900;
	/**
	 * 代码类型 - 系统指令 - 装载所有卡牌信息
	 */
	public static final int SYSTEM_CODE_INIT_CARD = 0x3901;
	/**
	 * 指令类型 - 系统指令 - 连接服务器
	 */
	public static final int SYSTEM_CODE_CONNECT = 0x3902;
	/**
	 * 指令类型 - 系统指令 - 刷新房间列表
	 */
	public static final int SYSTEM_CODE_ROOM_LIST = 0x3903;
	/**
	 * 指令类型 - 系统指令 - 创建房间
	 */
	public static final int SYSTEM_CODE_CREATE_ROOM = 0x3904;
	/**
	 * 指令类型 - 系统指令 - 刷新成员列表
	 */
	public static final int SYSTEM_CODE_PLAYER_LIST = 0x3905;
	/**
	 * 指令类型 - 系统指令 - 进入大厅
	 */
	public static final int SYSTEM_CODE_JOIN_HALL = 0x3906;
	/**
	 * 指令类型 - 系统指令 - 退出大厅
	 */
	public static final int SYSTEM_CODE_LEAVE_HALL = 0x3907;
	/**
	 * 指令类型 - 系统指令 - 进入房间
	 */
	public static final int SYSTEM_CODE_JOIN_ROOM = 0x3908;
	/**
	 * 指令类型 - 系统指令 - 退出房间
	 */
	public static final int SYSTEM_CODE_LEAVE_ROOM = 0x3909;
	/**
	 * 指令类型 - 系统指令 - 本地玩家进入房间
	 */
	public static final int SYSTEM_CODE_LOCAL_JOIN = 0x390A;
	/**
	 * 指令类型 - 系统指令 - 本地玩家退出房间
	 */
	public static final int SYSTEM_CODE_LOCAL_LEAVE = 0x390B;
	
	/**
	 * 代码类型 - 游戏指令 - 进入游戏
	 */
	public static final int GAME_CODE_JOIN = 0x2000;
	/**
	 * 代码类型 - 游戏指令 - 开始游戏
	 */
	public static final int GAME_CODE_START = 0x2001;
	/**
	 * 代码类型 - 游戏指令 - 选择行动
	 */
	public static final int GAME_CODE_CHOOSE_ACTION = 0x2002;
	/**
	 * 代码类型 - 游戏指令 - 显示所有玩家选择的行动
	 */
	public static final int GAME_CODE_SHOW_ACTION = 0x2003;
	/**
	 * 代码类型 - 游戏指令 - 游戏开始时玩家弃牌
	 */
	public static final int GAME_CODE_STARTING_DISCARD = 0x2004;
	/**
	 * 代码类型 - 游戏指令 - 回合结束时弃牌
	 */
	public static final int GAME_CODE_ROUND_DISCARD = 0x2005;
	/**
	 * 代码类型 - 游戏指令 - 读取所有玩家信息
	 */
	public static final int GAME_CODE_LOAD_PLAYER = 0x2006;
	/**
	 * 代码类型 - 游戏指令 - 设置游戏起始信息
	 */
	public static final int GAME_CODE_SETUP = 0x2007;
	/**
	 * 代码类型 - 游戏指令 - 读取本地玩家信息
	 */
	public static final int GAME_CODE_LOCAL_PLAYER = 0x2008;
	/**
	 * 代码类型 - 游戏指令 - 探索阶段指令
	 */
	public static final int GAME_CODE_EXPLORE = 0x2010;
	/**
	 * 代码类型 - 游戏指令 - 开发阶段指令
	 */
	public static final int GAME_CODE_DEVELOP = 0x2011;
	/**
	 * 代码类型 - 游戏指令 - 扩张阶段指令
	 */
	public static final int GAME_CODE_SETTLE = 0x2012;
	/**
	 * 代码类型 - 游戏指令 - 消费阶段指令
	 */
	public static final int GAME_CODE_CONSUME = 0x2013;
	/**
	 * 代码类型 - 游戏指令 - 生产阶段指令
	 */
	public static final int GAME_CODE_PRODUCE = 0x2014;
	/**
	 * 代码类型 - 游戏指令 - 游戏结束
	 */
	public static final int GAME_CODE_END = 0x2015;
	/**
	 * 代码类型 - 游戏指令 - 结束时的分数统计
	 */
	public static final int GAME_CODE_VP_BOARD = 0x2016;
	/**
	 * 代码类型 - 游戏指令 - 离开游戏
	 */
	public static final int GAME_CODE_LEAVE = 0x2017;
	/**
	 * 代码类型 - 游戏指令 - 玩家被移除游戏
	 */
	public static final int GAME_CODE_REMOVE_PLAYER = 0x2018;
	/**
	 * 代码类型 - 游戏指令 - 玩家准备游戏
	 */
	public static final int GAME_CODE_PLAYER_READY = 0x2019;
	/**
	 * 代码类型 - 游戏指令 - 观战游戏
	 */
	public static final int GAME_CODE_AUDIENCE = 0x2020;
	/**
	 * 代码类型 - 游戏指令 - 退出观战
	 */
	public static final int GAME_CODE_EXIT_AUDIENCE = 0x2021;
	/**
	 * 代码类型 - 游戏指令 - 刷新游戏进行中的信息
	 */
	public static final int GAME_CODE_PLAYING_INFO = 0x2022;
	/**
	 * 代码类型 - 游戏指令 - 刷新所有当前信息
	 */
	public static final int GAME_CODE_REFRESH_ALL = 0x2100;
	/**
	 * 代码类型 - 游戏指令 - 摸牌
	 */
	public static final int GAME_CODE_DRAW_CARD = 0x2101;
	/**
	 * 代码类型 - 游戏指令 - 弃牌
	 */
	public static final int GAME_CODE_DISCARD = 0x2102;
	/**
	 * 代码类型 - 游戏指令 - 从手牌中打牌
	 */
	public static final int GAME_CODE_PLAY_CARD = 0x2103;
	/**
	 * 代码类型 - 游戏指令 - 直接打出牌
	 */
	public static final int GAME_CODE_DIRECT_PLAY_CARD = 0x2104;
	/**
	 * 代码类型 - 游戏指令 - 弃掉货物
	 */
	public static final int GAME_CODE_DISCARD_GOOD = 0x2105;
	/**
	 * 代码类型 - 游戏指令 - 卡牌生效
	 */
	public static final int GAME_CODE_CARD_EFFECT = 0x2106;
	/**
	 * 代码类型 - 游戏指令 - 弃掉已打出的卡牌
	 */
	public static final int GAME_CODE_DISCARD_PLAYED_CARD = 0x2107;
	/**
	 * 代码类型 - 游戏指令 - 使用卡牌
	 */
	public static final int GAME_CODE_USE_CARD = 0x2108;
	/**
	 * 代码类型 - 游戏指令 - 生产货物
	 */
	public static final int GAME_CODE_PRODUCE_GOOD = 0x2109;
	/**
	 * 代码类型 - 游戏指令 - 可以主动使用的卡牌列表
	 */
	public static final int GAME_CODE_ACTIVE_CARD_LIST = 0x210A;
	/**
	 * 代码类型 - 游戏指令 - 玩家得到VP
	 */
	public static final int GAME_CODE_GET_VP = 0x210B;
	/**
	 * 代码类型 - 游戏指令 - 刷新牌堆数量
	 */
	public static final int GAME_CODE_REFRESH_DECK = 0x210C;
	/**
	 * 代码类型 - 游戏指令 - 公共资源得到目标
	 */
	public static final int GAME_CODE_SUPPLY_GET_GOAL = 0x210D;
	/**
	 * 代码类型 - 游戏指令 - 公共资源失去目标
	 */
	public static final int GAME_CODE_SUPPLY_LOST_GOAL = 0x210E;
	/**
	 * 代码类型 - 游戏指令 - 玩家得到目标
	 */
	public static final int GAME_CODE_PLAYER_GET_GOAL = 0x210F;
	/**
	 * 代码类型 - 游戏指令 - 玩家失去目标
	 */
	public static final int GAME_CODE_PLAYER_LOST_GOAL = 0x2110;
	/**
	 * 代码类型 - 游戏指令 - 刷新公共区的目标
	 */
	public static final int GAME_CODE_SUPPLY_REFRESH_GOAL = 0x2111;
	/**
	 * 代码类型 - 游戏指令 - 刷新玩家的目标
	 */
	public static final int GAME_CODE_PLAYER_REFRESH_GOAL = 0x2112;
	/**
	 * 代码类型 - 游戏指令 - 监听器开始监听
	 */
	public static final int GAME_CODE_START_LISTEN = 0x2200;
	/**
	 * 代码类型 - 游戏指令 - 玩家输入回应
	 */
	public static final int GAME_CODE_PLAYER_RESPONSED = 0x2201;
	/**
	 * 代码类型 - 游戏指令 - 阶段开始
	 */
	public static final int GAME_CODE_PHASE_START = 0x2202;
	/**
	 * 代码类型 - 游戏指令 - 阶段结束
	 */
	public static final int GAME_CODE_PHASE_END = 0x2203;
	/**
	 * 代码类型 - 游戏指令 - 选择起始星球
	 */
	public static final int GAME_CODE_STARTING_WORLD = 0x2207;
	/**
	 * 代码类型 - 游戏指令 - 赌博
	 */
	public static final int GAME_CODE_GAMBLE = 0x2208;
}
