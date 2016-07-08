package com.f14.PuertoRico.consts;

public enum Ability {
	/**
	 * 交易时金钱+1
	 */
	SELL_1,
	/**
	 * 交易时金钱+2
	 */
	SELL_2,
	/**
	 * 运货后可以多保存1种货物
	 */
	SAVE_1,
	/**
	 * 运货后可以多保存2种货物
	 */
	SAVE_2,
	/**
	 * 拓荒者阶段可以多翻一块种植园
	 */
	PLANTATION,
	/**
	 * 拓荒阶段可以选择采石场
	 */
	QUARRY,
	/**
	 * 拓荒后可以得到1个移民
	 */
	COLONIST_SETTLE,
	/**
	 * 建造后可以得到1个移民
	 */
	COLONIST_BUILDER,
	/**
	 * 交易阶段可以卖同类货物
	 */
	SELL_SAME,
	/**
	 * 生产货物时得到金钱
	 */
	PRODUCE_DOUBLOON,
	/**
	 * 运货时得到VP
	 */
	VP_SHIP,
	/**
	 * 私船
	 */
	SELF_SHIP,
	
	//以下是第一扩充中的建筑能力
	/**
	 * 生产阶段,大型糖厂或者大型染料生产时得到额外的产品
	 */
	PRODUCE_ADDITION,
	/**
	 * 拓荒阶段,可以将种植园/采石场换成森林
	 */
	FOREST,
	/**
	 * 建筑阶段,可以用VP/货物/拓荒者各1个换取最多3金钱
	 */
	BLACK_TRADE,
	/**
	 * 船长阶段,可以保留任意3个货物
	 */
	SAVE_SINGLE_3,
	/**
	 * 任意阶段,分配宾馆中的拓荒者到其他的位置
	 */
	GUESTHOUSE,
	/**
	 * 交易阶段,可以直接卖货物,但是不能应用其他建筑的交易能力
	 */
	SELF_TRADE,
	/**
	 * 建筑阶段,造2,3级建筑时得到1VP,造4级建筑时得到2VP
	 */
	VP_BUILD,
	/**
	 * 船长阶段,可以将任意种类任意数量的货物运货,得到一半的VP
	 */
	VP_SHIP_HALF,
	/**
	 * 船长阶段,每次运货得到1金钱,如果是船长第一次运货时再得到1金钱
	 */
	DOUBLOON_SHIP,
	/**
	 * 生产阶段,得到生产数量最多的货物数量-1的金钱,除了玉米
	 */
	PRODUCE_SPECIAL,
	/**
	 * 任意阶段,可以执行2次特权
	 */
	DOUBLE_PRIVILEGE,
	/**
	 * 船长阶段,运货前,每2个相同的货物得到1VP
	 */
	VP_BEFORE_SHIP,
	
	//以下是自己设计的技能,因为原版的技能有些不容易实现
	/**
	 * 市长阶段,多得到一个移民
	 */
	COLONIST_1;
	
	public static final String CODE_TYPE = "PR_ABILITY";

}
