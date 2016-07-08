package com.f14.TTA.listener.event;

import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.EventAbility;
import com.f14.TTA.component.card.EventCard;
import com.f14.TTA.consts.TTAGameCmd;
import com.f14.TTA.listener.TTAEventListener;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.utils.CommonUtil;

/**
 * 选择资源的监听器
 * 
 * @author F14eagle
 *
 */
public class ChooseResourceListener extends TTAEventListener {

	public ChooseResourceListener(EventCard eventCard, TTAPlayer trigPlayer) {
		super(eventCard, trigPlayer);
	}

	@Override
	protected int getValidCode() {
		return TTAGameCmd.GAME_CODE_CHOOSE_RESOURCE;
	}

	@Override
	protected BgResponse createStartListenCommand(TTAGameMode gameMode, Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		// 参数中传递需要选择的资源数量和类型
		EventAbility ability = this.eventCard.getAlternateAbility();
		res.setPublicParameter("amount", ability.amount);
		res.setPublicParameter("singleSelection", ability.singleSelection);
		res.setPublicParameter("availableFood", ((TTAPlayer) player).getTotalFood());
		res.setPublicParameter("availableResource", ((TTAPlayer) player).getTotalResource());
		return res;
	}

	@Override
	protected String getMsg(Player player) {
		EventAbility ability = this.eventCard.getAlternateAbility();
		String msg = "你{0}总数{1}的食物{2}资源,请选择!";
		msg = CommonUtil.getMsg(msg, (ability.amount < 0) ? "失去" : "得到", ability.amount,
				(ability.singleSelection) ? "或" : "和");
		return msg;
	}

	@Override
	protected boolean beforeListeningCheck(TTAGameMode gameMode, Player p) {
		// 检查玩家是否可以自动选择资源
		TTAPlayer player = (TTAPlayer) p;
		EventAbility ability = this.eventCard.getAlternateAbility();
		// 如果无需选择资源,则不需玩家回应
		if (ability.amount == 0) {
			return false;
		}
		// 如果是玩家失去资源,则需要进行一些判断
		if (ability.amount < 0) {
			int food = player.getTotalFood();
			int resource = player.getTotalResource();
			if (food + resource == 0) {
				// 如果玩家没有任何资源,则不需回应
				return false;
			}
			if (ability.singleSelection) {
				// 在只能选择一种资源的情况下
				// 如果玩家任一资源总数为0,则自动扣除另一种资源
				if (food == 0) {
					int num = -Math.min(resource, Math.abs(ability.amount));
					gameMode.getGame().playerAddResource(player, num);
					gameMode.getReport().printCache(player);
					return false;
				}
				if (resource == 0) {
					int num = -Math.min(food, Math.abs(ability.amount));
					gameMode.getGame().playerAddFood(player, num);
					gameMode.getReport().printCache(player);
					return false;
				}
			} else {
				// 在多选的情况下
				// 如果玩家的资源总数少于等于要扣除的总数,则自动扣光全部资源
				if ((food + resource) <= Math.abs(ability.amount)) {
					gameMode.getGame().playerAddFood(player, -food);
					gameMode.getGame().playerAddResource(player, -resource);
					gameMode.getReport().printCache(player);
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
		EventAbility ability = this.eventCard.getAlternateAbility();
		if (ability.singleSelection) {
			// 判断是否只选择一种资源
			if (food != 0 && resource != 0) {
				throw new BoardGameException("不能同时选择食物和资源!");
			}
		}
		// 检查选择数量是否和需求的数量相等
		if ((food + resource) != Math.abs(ability.amount)) {
			throw new BoardGameException("数量选择错误!");
		}
		// 如果是付出资源的情况,需要检查玩家是否拥有足够的食物或资源
		TTAPlayer player = action.getPlayer();
		if (ability.amount < 0) {
			if (food > player.getTotalFood()) {
				throw new BoardGameException("食物数量不足!");
			}
			if (resource > player.getTotalResource()) {
				throw new BoardGameException("资源数量不足!");
			}
		}
		// 得到或者扣掉对应的食物和资源
		if (ability.amount > 0) {
			// 得到食物/资源
			gameMode.getGame().playerAddFood(player, food);
			gameMode.getGame().playerAddResource(player, resource);
		} else {
			// 失去食物/资源
			gameMode.getGame().playerAddFood(player, -food);
			gameMode.getGame().playerAddResource(player, -resource);
		}
		gameMode.getReport().printCache(player);
		// 完成选择
		this.setPlayerResponsed(gameMode, player.position);
	}

}
