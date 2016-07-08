package com.f14.TS.consts;

public class TSGameCmd {

	/**
	 * 代码类型 - 游戏指令 - 刷新游戏基本信息
	 */
	public static final int GAME_CODE_BASE_INFO = 0x4000;
	/**
	 * 代码类型 - 游戏指令 - 刷新游戏牌堆信息
	 */
	public static final int GAME_CODE_DECK_INFO = 0x4001;
	/**
	 * 代码类型 - 游戏指令 - 刷新国家的信息
	 */
	public static final int GAME_CODE_COUNTRY_INFO = 0x4002;
	/**
	 * 代码类型 - 游戏指令 - 刷新玩家的基本信息
	 */
	public static final int GAME_CODE_PLAYER_INFO = 0x4003;
	/**
	 * 代码类型 - 游戏指令 - 玩家得到手牌
	 */
	public static final int GAME_CODE_ADD_HANDS = 0x4004;
	/**
	 * 代码类型 - 游戏指令 - 玩家失去手牌
	 */
	public static final int GAME_CODE_REMOVE_HANDS = 0x4005;
	/**
	 * 代码类型 - 游戏指令 - 刷新中国牌的信息
	 */
	public static final int GAME_CODE_CHINA_CARD = 0x4006;
	/**
	 * 代码类型 - 游戏指令 - 弃牌堆加入牌
	 */
	public static final int GAME_CODE_ADD_DISCARD = 0x4007;
	/**
	 * 代码类型 - 游戏指令 - 弃牌堆移除牌
	 */
	public static final int GAME_CODE_REMOVE_DISCARD = 0x4008;
	/**
	 * 代码类型 - 游戏指令 - 添加生效的卡牌
	 */
	public static final int GAME_CODE_ADD_ACTIVED_CARD = 0x4009;
	/**
	 * 代码类型 - 游戏指令 - 移除生效的卡牌
	 */
	public static final int GAME_CODE_REMOVE_ACTIVED_CARD = 0x400A;
	/**
	 * 代码类型 - 游戏指令 - 发送行动记录
	 */
	public static final int GAME_CODE_ACTION_RECORD = 0x400B;
	/**
	 * 代码类型 - 游戏指令 - 卡牌移出游戏
	 */
	public static final int GAME_CODE_TRASH_CARD = 0x400C;
	
	/**
	 * 代码类型 - 游戏指令 - 游戏设置影响力阶段
	 */
	public static final int GAME_CODE_SETUP_PHASE = 0x4400;
	/**
	 * 代码类型 - 游戏指令 - 玩家调整影响力
	 */
	public static final int GAME_CODE_ADJUST_INFLUENCE = 0x4401;
	/**
	 * 代码类型 - 游戏指令 - 玩家回合行动
	 */
	public static final int GAME_CODE_ROUND = 0x4402;
	/**
	 * 代码类型 - 游戏指令 - 使用OP放置影响力
	 */
	public static final int GAME_CODE_ADD_INFLUENCE = 0x4403;
	/**
	 * 代码类型 - 游戏指令 - 政变
	 */
	public static final int GAME_CODE_COUP = 0x4404;
	/**
	 * 代码类型 - 游戏指令 - 调整阵营
	 */
	public static final int GAME_CODE_REALIGNMENT = 0x4405;
	/**
	 * 代码类型 - 游戏指令 - 选择国家进行行动
	 */
	public static final int GAME_CODE_COUNTRY_ACTION = 0x4406;
	/**
	 * 代码类型 - 游戏指令 - 使用OP进行行动
	 */
	public static final int GAME_CODE_OP_ACTION = 0x4407;
	/**
	 * 代码类型 - 游戏指令 - 头条阶段
	 */
	public static final int GAME_CODE_HEAD_LINE = 0x4408;
	/**
	 * 代码类型 - 游戏指令 - 选择卡牌进行行动
	 */
	public static final int GAME_CODE_CARD_ACTION = 0x4409;
	/**
	 * 代码类型 - 游戏指令 - 界面选项
	 */
	public static final int GAME_CODE_CHOICE = 0x440A;
	/**
	 * 代码类型 - 游戏指令 - 查看手牌
	 */
	public static final int GAME_CODE_VIEW_HAND = 0x440B;
	/**
	 * 代码类型 - 游戏指令 - 查看弃牌堆
	 */
	public static final int GAME_CODE_VIEW_DISCARD_DECK = 0x440C;
	/**
	 * 代码类型 - 游戏指令 - #45-高峰会议事件
	 */
	public static final int GAME_CODE_45 = 0x440D;
	/**
	 * 代码类型 - 游戏指令 - #46-我如何学会不再担忧
	 */
	public static final int GAME_CODE_46 = 0x440E;
	/**
	 * 代码类型 - 游戏指令 - #77-“不要问你的祖国能为你做什么……”
	 */
	public static final int GAME_CODE_77 = 0x440F;
	/**
	 * 代码类型 - 游戏指令 - #67-向苏联出售谷物
	 */
	public static final int GAME_CODE_67 = 0x4410;
	/**
	 * 代码类型 - 游戏指令 - #50-“我们会埋葬你的”
	 */
	public static final int GAME_CODE_50 = 0x4411;
	/**
	 * 代码类型 - 游戏指令 - #94-切尔诺贝利
	 */
	public static final int GAME_CODE_94 = 0x4412;
	/**
	 * 代码类型 - 游戏指令 - #98-阿尔德里希·阿姆斯
	 */
	public static final int GAME_CODE_98 = 0x4413;
	/**
	 * 代码类型 - 游戏指令 - #40-古巴导弹危机
	 */
	public static final int GAME_CODE_40 = 0x4414;
	/**
	 * 代码类型 - 游戏指令 - 困境事件
	 */
	public static final int GAME_CODE_QUAGMIRE = 0x4415;
	/**
	 * 代码类型 - 游戏指令 - #49-导弹嫉妒
	 */
	public static final int GAME_CODE_49 = 0x4416;
	/**
	 * 代码类型 - 游戏指令 - #49-导弹嫉妒 执行
	 */
	public static final int GAME_CODE_49_ROUND = 0x4417;
	/**
	 * 代码类型 - 游戏指令 - 回合结束时弃牌
	 */
	public static final int GAME_CODE_ROUND_DISCARD = 0x4418;
	/**
	 * 代码类型 - 游戏指令 - #108-我们在伊朗有人
	 */
	public static final int GAME_CODE_108 = 0x4419;
	/**
	 * 代码类型 - 游戏指令 - #104-剑桥五杰
	 */
	public static final int GAME_CODE_104 = 0x441A;
	/**
	 * 代码类型 - 游戏指令 - 打计分牌
	 */
	public static final int GAME_CODE_PLAY_SCORE_CARD = 0x441B;
	/**
	 * 代码类型 - 游戏指令 - #106-北美防空司令部
	 */
	public static final int GAME_CODE_106 = 0x441C;
	/**
	 * 代码类型 - 游戏指令 - #100-战争游戏
	 */
	public static final int GAME_CODE_100 = 0x441D;
	
}
