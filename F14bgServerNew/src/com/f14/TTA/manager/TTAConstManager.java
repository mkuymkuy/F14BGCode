package com.f14.TTA.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.consts.CivilAbilityType;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.TTACmdString;

/**
 * TTA一些参数的管理器
 * 
 * @author F14eagle
 *
 */
public class TTAConstManager {
	/**
	 * 扩张人口的费用参照标准
	 */
	private static List<CostValue> costs;
	/**
	 * 幸福度需求的参照标准
	 */
	private static List<CostValue> needHappiness;
	/**
	 * 食物供应的参照标准
	 */
	private static List<CostValue> foodSupply;
	/**
	 * 资源腐败的参照标准
	 */
	private static List<CostValue> resourceCorruption;
	/**
	 * 行动所消耗的行动点数
	 */
	private static Map<String, Integer> actionCost;

	static {
		init();
	}

	/**
	 * 初始化
	 */
	protected static void init() {
		// 设置扩张人口的费用
		costs = new ArrayList<CostValue>();
		costs.add(new CostValue(1, 4, 7));
		costs.add(new CostValue(5, 8, 5));
		costs.add(new CostValue(9, 12, 4));
		costs.add(new CostValue(13, 16, 3));
		costs.add(new CostValue(17, 99, 2));

		// 设置人口需要的幸福度
		needHappiness = new ArrayList<CostValue>();
		needHappiness.add(new CostValue(0, 0, 8));
		needHappiness.add(new CostValue(1, 2, 7));
		needHappiness.add(new CostValue(3, 4, 6));
		needHappiness.add(new CostValue(5, 6, 5));
		needHappiness.add(new CostValue(7, 8, 4));
		needHappiness.add(new CostValue(9, 10, 3));
		needHappiness.add(new CostValue(11, 12, 2));
		needHappiness.add(new CostValue(13, 16, 1));
		needHappiness.add(new CostValue(17, 99, 0));

		// 设置粮食供应的标准
		foodSupply = new ArrayList<CostValue>();
		foodSupply.add(new CostValue(0, 0, 6));
		foodSupply.add(new CostValue(1, 4, 4));
		foodSupply.add(new CostValue(5, 8, 3));
		foodSupply.add(new CostValue(9, 12, 2));
		foodSupply.add(new CostValue(13, 16, 1));
		foodSupply.add(new CostValue(17, 99, 0));

		// 设置资源腐败的标准
		resourceCorruption = new ArrayList<CostValue>();
		resourceCorruption.add(new CostValue(0, 0, 6));
		resourceCorruption.add(new CostValue(1, 4, 4));
		resourceCorruption.add(new CostValue(5, 8, 2));
		resourceCorruption.add(new CostValue(9, 99, 0));

		// 设置行动点数
		actionCost = new HashMap<String, Integer>();
		actionCost.put(TTACmdString.ACTION_BUILD, 1);
		actionCost.put(TTACmdString.ACTION_POPULATION, 1);
		actionCost.put(TTACmdString.ACTION_DESTORY, 1);
		actionCost.put(TTACmdString.ACTION_UPGRADE, 1);
		actionCost.put(TTACmdString.ACTION_PLAY_CARD, 1);

	}

	/**
	 * 取得玩家当前扩张人口需要的费用
	 * 
	 * @param player
	 * @return
	 */
	public static int getPopulationCost(TTAPlayer player) {
		int res = 0;
		int availableWorkers = player.tokenPool.getAvailableWorkers();
		for (CostValue o : costs) {
			if (o.min <= availableWorkers && availableWorkers <= o.max) {
				res = o.cost;
				break;
			}
		}
		// 计算扩张人口时调整费用的能力
		for (CivilCardAbility ability : player.abilityManager.getAbilitiesByType(CivilAbilityType.PA_POPULATION_COST)) {
			res += ability.property.getProperty(CivilizationProperty.FOOD);
		}
		res = Math.max(0, res);
		return res;
	}

	/**
	 * 取得当前需要的幸福度
	 * 
	 * @param availableWorkers
	 * @return
	 */
	public static int getNeedHappiness(int availableWorkers) {
		for (CostValue o : needHappiness) {
			if (o.min <= availableWorkers && availableWorkers <= o.max) {
				return o.cost;
			}
		}
		return 0;
	}

	/**
	 * 取得当前工人需要消耗的粮食数量
	 * 
	 * @param availableWorkers
	 * @return
	 */
	public static int getFoodSupply(int availableWorkers) {
		for (CostValue o : foodSupply) {
			if (o.min <= availableWorkers && availableWorkers <= o.max) {
				return o.cost;
			}
		}
		return 0;
	}

	/**
	 * 取得玩家当前需要腐败的资源数量
	 * 
	 * @param availableBlues
	 * @return
	 */
	public static int getResourceCorruption(int availableBlues) {
		for (CostValue o : resourceCorruption) {
			if (o.min <= availableBlues && availableBlues <= o.max) {
				return o.cost;
			}
		}
		return 0;
	}

	/**
	 * 判断玩家是否会引起暴动
	 * 
	 * @param player
	 * @return
	 */
	public static boolean isUprising(TTAPlayer player) {
		// 当玩家空闲的工人数量小于暴怒的人口,则发生暴动
		if (player.tokenPool.getUnhappyWorkers() > player.tokenPool.getUnusedWorkers()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 取得行动所消耗的行动点数
	 * 
	 * @param player
	 * @param action
	 * @return
	 */
	public static int getActionCost(TTAPlayer player, String action) {
		return actionCost.get(action);
	}

	/**
	 * 费用区间的对应关系
	 * 
	 * @author F14eagle
	 *
	 */
	static class CostValue {
		int min;
		int max;
		int cost;

		CostValue(int min, int max, int cost) {
			this.min = min;
			this.max = max;
			this.cost = cost;
		}
	}
}
