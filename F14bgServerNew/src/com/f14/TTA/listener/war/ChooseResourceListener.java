package com.f14.TTA.listener.war;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.component.ability.EventAbility;
import com.f14.TTA.component.card.WarCard;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.listener.TTAWarListener;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.common.ParamSet;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.CommonUtil;

/**
 * 选择资源的侵略/战争监听器
 * 
 * @author F14eagle
 *
 */
public class ChooseResourceListener extends TTAWarListener {

	public ChooseResourceListener(WarCard warCard, TTAPlayer trigPlayer, TTAPlayer winner, TTAPlayer loser,
			ParamSet warParam) {
		super(warCard, trigPlayer, winner, loser, warParam);
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_CHOOSE_RESOURCE;
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		// 为所有玩家创建选择参数
		for (Player player : gameMode.getGame().getValidPlayers()) {
			ChooseParam param = new ChooseParam();
			this.setParam(player.position, param);
		}
	}

	@Override
	protected BgResponse createStartListenCommand(TTAGameMode gameMode, Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		// 参数中传递需要选择的资源数量和类型
		// 是由战胜方选择战败方的
		EventAbility ability = this.warCard.loserEffect;
		res.setPublicParameter("amount", this.getNeedAmount());
		res.setPublicParameter("singleSelection", ability.singleSelection);
		res.setPublicParameter("availableFood", this.loser.getTotalFood());
		res.setPublicParameter("availableResource", this.loser.getTotalResource());
		return res;
	}

	@Override
	protected String getMsg(Player player) {
		EventAbility ability = this.warCard.loserEffect;
		String msg = "你能夺取玩家{0}总数{1}的食物{2}资源,请选择!";
		// 改事件由战胜方选择,所以显示为得到食物
		int amount = Math.abs(this.getNeedAmount());
		msg = CommonUtil.getMsg(msg, this.loser.getReportString(), amount, (ability.singleSelection) ? "或" : "和");
		return msg;
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player p) {
		// 该事件总是检查战败方的资源损失情况
		// 检查玩家是否可以自动选择资源
		TTAPlayer player = this.loser;
		EventAbility ability = this.warCard.loserEffect;
		// 如果无需选择资源,则不需玩家回应
		int amount = this.getNeedAmount();
		if (amount == 0) {
			return false;
		}
		// 如果是玩家失去资源,则需要进行一些判断
		if (amount != 0) {
			int food = player.getTotalFood();
			int resource = player.getTotalResource();
			if (food + resource == 0) {
				// 如果玩家没有任何资源,则不需回应
				return false;
			}
			ChooseParam param = this.getParam(player.position);
			if (ability.singleSelection) {
				// 在只能选择一种资源的情况下
				// 如果玩家任一资源总数为0,则自动扣除另一种资源
				if (food == 0) {
					param.setResource(Math.min(resource, Math.abs(amount)));
					return false;
				}
				if (resource == 0) {
					param.setFood(Math.min(food, Math.abs(amount)));
					return false;
				}
			} else {
				// 在多选的情况下
				// 如果玩家的资源总数少于等于要扣除的总数,则自动扣光全部资源
				if ((food + resource) <= Math.abs(amount)) {
					param.setResource(resource);
					param.setFood(food);
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		int food = action.getAsInt("food");
		int resource = action.getAsInt("resource");
		// 不能选择负数
		if (food < 0 || resource < 0) {
			throw new BoardGameException("数量选择错误!");
		}
		EventAbility ability = this.warCard.loserEffect;
		int amount = this.getNeedAmount();
		if (ability.singleSelection) {
			// 判断是否只选择一种资源
			if (food != 0 && resource != 0) {
				throw new BoardGameException("不能同时选择食物和资源!");
			}
		}
		// 检查选择数量是否和需求的数量相等
		if ((food + resource) != Math.abs(amount)) {
			throw new BoardGameException("数量选择错误!");
		}
		// 需要检查战败方玩家是否拥有足够的食物或资源
		TTAPlayer player = this.loser;
		if (amount < 0) {
			if (food > player.getTotalFood()) {
				throw new BoardGameException("食物数量不足!");
			}
			if (resource > player.getTotalResource()) {
				throw new BoardGameException("资源数量不足!");
			}
		}
		// 设置选择的食物和资源
		ChooseParam param = this.getParam(player.position);
		param.setFood(food);
		param.setResource(resource);
		// 完成选择
		this.setPlayerResponsed(gameMode, action.getPlayer().position);
	}

	/**
	 * 取得实际需要选择的数量
	 * 
	 * @return
	 */
	protected int getNeedAmount() {
		return this.warCard.loserEffect.getRealAmount(warParam);
	}

	@Override
	protected void processWinnerEffect(TTAGameMode gameMode) {
		// 扣除战败方玩家的资源
		ChooseParam param = this.getParam(this.loser.position);
		TTAProperty resprop = gameMode.getGame().playerAddPoint(this.loser, param.property, -1);
		gameMode.getReport().printCache(this.loser);
		resprop.multi(-1);
		// 设置关联结果参数
		this.warParam.set("property", resprop);
		// 结算战胜方玩家的效果
		super.processWinnerEffect(gameMode);
	}

	/**
	 * 选择的参数
	 * 
	 * @author F14eagle
	 *
	 */
	class ChooseParam {
		TTAProperty property = new TTAProperty();

		/**
		 * 设置选择的食物数量
		 * 
		 * @param food
		 */
		void setFood(int food) {
			property.setProperty(CivilizationProperty.FOOD, food);
		}

		/**
		 * 设置选择的资源数量
		 * 
		 * @param resource
		 */
		void setResource(int resource) {
			property.setProperty(CivilizationProperty.RESOURCE, resource);
		}
	}

}
