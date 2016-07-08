package com.f14.TTA.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f14.F14bg.network.CmdFactory;
import com.f14.TTA.TTAGameMode;
import com.f14.TTA.TTAPlayer;
import com.f14.TTA.component.ability.CivilCardAbility;
import com.f14.TTA.component.card.BonusCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.component.card.TacticsCard.TacticsResult;
import com.f14.TTA.consts.CardType;
import com.f14.TTA.consts.CivilAbilityType;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.component.Convertable;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;

/**
 * 选择部队的监听器
 * 
 * @author F14eagle
 *
 */
public abstract class ChooseArmyListener extends TTAOrderInterruptListener {

	public ChooseArmyListener(TTAPlayer trigPlayer) {
		super(trigPlayer);
	}

	/**
	 * 是否看的是殖民点数
	 * 
	 * @return
	 */
	protected boolean isColony() {
		return false;
	}

	@Override
	protected void beforeStartListen(TTAGameMode gameMode) throws BoardGameException {
		super.beforeStartListen(gameMode);
		// 监听开始前,为所有玩家创建拍卖参数
		for (TTAPlayer player : gameMode.getGame().getValidPlayers()) {
//			if (!player.resigned){
				AuctionParam param = new AuctionParam(player);
				this.setParam(player.position, param);
//			}
		}
	}

	@Override
	protected void sendPlayerListeningInfo(TTAGameMode gameMode, Player r) {
		super.sendPlayerListeningInfo(gameMode, r);
		TTAPlayer receiver = (TTAPlayer) r;
		// 发送玩家的部队及拍卖信息
		BgResponse res = this.createAuctionInfoResponse(gameMode, receiver);
		// 向receiver发送指令
		gameMode.getGame().sendResponse(receiver, res);
	}

	/**
	 * 创建玩家选择部队信息的指令
	 * 
	 * @param gameMode
	 * @param receiver
	 * @return
	 */
	protected BgResponse createAuctionInfoResponse(TTAGameMode gameMode, TTAPlayer receiver) {
		// 发送玩家的部队及拍卖信息
		BgResponse res = CmdFactory.createGameResponse(this.getValidCode(), -1);
		res.setPublicParameter("subact", "loadParam");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (TTAPlayer player : gameMode.getGame().getValidPlayers()) {
//			if (!player.resigned){
				Map<String, Object> map = this.createPlayerAuctionParam(player, receiver);
				list.add(map);
//			}
		}
		res.setPublicParameter("playersInfo", list);
		// 设置触发器信息
		this.setListenerInfo(res);
		return res;
	}

	/**
	 * 创建玩家的拍卖参数
	 * 
	 * @param player
	 * @param receiver
	 * @return
	 */
	protected Map<String, Object> createPlayerAuctionParam(TTAPlayer player, TTAPlayer receiver) {
		Map<String, Object> map = new HashMap<String, Object>();
		AuctionParam param = this.getParam(player.position);
		List<Map<String, Object>> unitsInfo = player.getUnitsInfo();
		map.put("unitsInfo", unitsInfo);
		map.put("position", player.position);

		// 防御和殖民地加值卡应该设置为私有参数
		map.put("bonusCardIds", BgUtils.card2String(player.getBonusCards()));

		// 如果是receiver玩家正在输入,或者玩家已经输入完成,则向发送该玩家的输入信息
		if (player == receiver || !param.inputing) {
			map.put("auctionParam", param.toMap());
		}
		return map;
	}

	@Override
	protected BgResponse createStartListenCommand(TTAGameMode gameMode, Player player) {
		BgResponse res = super.createStartListenCommand(gameMode, player);
		// 设置玩家当前的拍卖信息
		AuctionParam param = this.getParam(player.position);
		res.setPublicParameter("auctionParam", param.toMap());
		return res;
	}

	@Override
	protected void onPlayerTurn(TTAGameMode gameMode, TTAPlayer player) throws BoardGameException {
		super.onPlayerTurn(gameMode, player);
		// 玩家回合开始时,将其输入状态设为true
		AuctionParam param = this.getParam(player.position);
		param.inputing = true;
	}

	@Override
	protected void doAction(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		String subact = action.getAsString("subact");
		if ("adjustUnit".equals(subact)) {
			// 调整部队
			this.adjustUnit(gameMode, action);
		} else if ("adjustBonusCard".equals(subact)) {
			// 调整加值卡
			this.adjustBonusCard(gameMode, action);
		} else if ("confirm".equals(subact)) {
			// 确认拍卖
			this.confirm(gameMode, action);
		} else if ("pass".equals(subact)) {
			// 放弃拍卖
			this.pass(gameMode, action);
		} else {
			throw new BoardGameException("无效的行动代码!");
		}
	}

	/**
	 * 调整拍卖所用的部队
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void adjustUnit(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		TTACard card = player.getBuildings().getCard(cardId);
		if (card.cardType != CardType.UNIT) {
			throw new BoardGameException("只能选择部队牌!");
		}
		int num = action.getAsInt("num");
		AuctionParam param = this.getParam(player.position);
		// 检查现有部队数量是否超出出价的部队数量
		if (num < 0 || num > card.getAvailableCount()) {
			throw new BoardGameException("部队数量错误,不能进行调整!");
		}
		param.setUnitNum(card, num);
		// 向操作的玩家刷新当前出价的总值
		this.sendPlayerAuctionValue(gameMode, player, player);
	}

	/**
	 * 调整拍卖所用的加值卡
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected void adjustBonusCard(TTAGameMode gameMode, BgAction action) throws BoardGameException {
		TTAPlayer player = action.getPlayer();
		String cardId = action.getAsString("cardId");
		TTACard c = player.getCard(cardId);
		if (c.cardType != CardType.DEFENSE_BONUS) {
			throw new BoardGameException("只能选择防御/殖民地加值卡!");
		}
		boolean selected = action.getAsBoolean("selected");
		BonusCard card = (BonusCard) c;
		AuctionParam param = this.getParam(player.position);
		param.setBonusCard(card, selected);
		// 向操作的玩家刷新当前出价的总值
		this.sendPlayerAuctionValue(gameMode, player, player);
	}

	/**
	 * 玩家确认
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected abstract void confirm(TTAGameMode gameMode, BgAction action) throws BoardGameException;

	/**
	 * 玩家结束
	 * 
	 * @param gameMode
	 * @param action
	 * @throws BoardGameException
	 */
	protected abstract void pass(TTAGameMode gameMode, BgAction action) throws BoardGameException;

	/**
	 * 向receiver发送player的拍卖总值,receiver为空则向所有玩家发送
	 * 
	 * @param gameMode
	 * @param player
	 * @param reciever
	 */
	protected void sendPlayerAuctionValue(TTAGameMode gameMode, TTAPlayer player, TTAPlayer receiver) {
		AuctionParam param = this.getParam(player.position);
		BgResponse res = CmdFactory.createGameResponse(this.getValidCode(), player.position);
		res.setPublicParameter("subact", "auctionValue");
		res.setPublicParameter("auctionValue", param.getTotalValue());
		gameMode.getGame().sendResponse(receiver, res);
	}

	/**
	 * 向receiver发送player的拍卖信息,receiver为空则向所有玩家发送
	 * 
	 * @param gameMode
	 * @param player
	 * @param reciever
	 */
	protected void sendPlayerAuctionInfo(TTAGameMode gameMode, TTAPlayer player, TTAPlayer receiver) {
		AuctionParam param = this.getParam(player.position);
		BgResponse res = CmdFactory.createGameResponse(this.getValidCode(), player.position);
		Map<String, Object> auctionInfo = param.toMap();
		if (this.isColony() && player != receiver) {
			auctionInfo.remove("units");
			auctionInfo.put("units", new ArrayList<Map<String, Object>>());
			auctionInfo.remove("bonusCards");
			auctionInfo.put("bonusCards", new ArrayList<Map<String, Object>>());
		}
		res.setPublicParameter("subact", "auctionParam");
		res.setPublicParameter("auctionParam", auctionInfo);
		gameMode.getGame().sendResponse(receiver, res);
	}

	/**
	 * 取得玩家的拍卖值
	 * 
	 * @param player
	 * @return
	 */
	protected int getPlayerAuctionValue(TTAPlayer player) {
		AuctionParam param = this.getParam(player.position);
		return param.getTotalValue();
	}

	/**
	 * 拍卖参数
	 * 
	 * @author F14eagle
	 *
	 */
	class AuctionParam implements Convertable {
		TTAPlayer player;
		int military = 0;
		int colonyBonus = 0;
		boolean pass = false;
		boolean inputing = false;
		Map<TTACard, Integer> units = new HashMap<TTACard, Integer>();
		Map<BonusCard, Boolean> bonusCards = new HashMap<BonusCard, Boolean>();

		AuctionParam(TTAPlayer player) {
			this.player = player;
			this.military = player.getProperty(CivilizationProperty.MILITARY);
			this.colonyBonus = player.getProperty(CivilizationProperty.COLONIZING_BONUS);
		}

		/**
		 * 清除拍卖参数
		 */
		void clear() {
			this.units.clear();
			this.bonusCards.clear();
		}

		/**
		 * 检查是否拥有部队
		 * 
		 * @return
		 */
		boolean hasUnit() {
			for (Integer i : units.values()) {
				if (i > 0) {
					return true;
				}
			}
			return false;
		}

		/**
		 * 取得总数
		 * 
		 * @return
		 */
		int getTotalValue() {
			if (isColony()) {
				return this.getTotalColonyValue();
			} else {
				return this.getTotalMilitaryValue();
			}
		}

		/**
		 * 取得殖民点数总值
		 * 
		 * @return
		 */
		private int getTotalColonyValue() {
			int res = this.colonyBonus;
			// 附加加值卡的数值
			int bonusCardNum = 0;
			for (BonusCard card : this.bonusCards.keySet()) {
				if (this.bonusCards.get(card)) {
					res += card.colo;
					bonusCardNum += 1;
				}
			}
			// 检查加值卡增加能力
			for (CivilCardAbility a : this.player.abilityManager
					.getAbilitiesByType(CivilAbilityType.PA_ENHANCE_BONUS_CARD)) {
				res += bonusCardNum * a.property.getProperty(CivilizationProperty.COLONIZING_BONUS);
			}
			// 附加部队的基本军事力
			for (TTACard card : this.units.keySet()) {
				res += card.property.getProperty(CivilizationProperty.MILITARY) * this.units.get(card);
			}
			// 如果玩家拥有战术牌,并且没有忽略战术牌的能力,则计算部队组成的军队提供的军事力
			if (this.player.getTactics() != null
					&& !this.player.abilityManager.hasAbilitiy(CivilAbilityType.PA_IGNORE_TACTICS)) {
				TacticsResult result = this.player.getTactics().getTacticsResult(units);
				res += result.getTotalMilitaryBonus();
			}
			return res;
		}

		/**
		 * 取得军事点数总值
		 * 
		 * @return
		 */
		private int getTotalMilitaryValue() {
			int res = this.military;
			// 检查玩家是否有加强防御卡的能力
			int multi = 1; // 加强倍数
			for (CivilCardAbility a : this.player.abilityManager
					.getAbilitiesByType(CivilAbilityType.PA_ENHANCE_DEFENSE_CARD)) {
				multi += a.amount;
			}
			// 附加加值卡的数值
			int bonusCardNum = 0;
			for (BonusCard card : this.bonusCards.keySet()) {
				if (this.bonusCards.get(card)) {
					res += (card.defense * multi); // 乘以加强倍数
					bonusCardNum += 1;
				}
			}
			// 检查加值卡增加能力
			for (CivilCardAbility a : this.player.abilityManager
					.getAbilitiesByType(CivilAbilityType.PA_ENHANCE_BONUS_CARD)) {
				res += bonusCardNum * a.property.getProperty(CivilizationProperty.MILITARY);
			}
			// 附加部队的基本军事力
			for (TTACard card : this.units.keySet()) {
				res += card.property.getProperty(CivilizationProperty.MILITARY) * this.units.get(card);
			}
			// 如果玩家拥有战术牌,并且没有忽略战术牌的能力,则计算部队组成的军队提供的军事力
			if (this.player.getTactics() != null
					&& !this.player.abilityManager.hasAbilitiy(CivilAbilityType.PA_IGNORE_TACTICS)) {
				TacticsResult result = this.player.getTactics().getTacticsResult(units);
				res += result.getTotalMilitaryBonus();
			}
			return res;
		}

		/**
		 * 取得unit的数量
		 * 
		 * @param unit
		 * @return
		 */
		int getUnitNum(TTACard unit) {
			Integer i = this.units.get(unit);
			if (i == null) {
				return 0;
			} else {
				return i;
			}
		}

		/**
		 * 设置unit的数量
		 * 
		 * @param unit
		 * @param num
		 */
		void setUnitNum(TTACard unit, int num) {
			this.units.put(unit, num);
		}

		/**
		 * 取得牺牲部队的信息
		 * 
		 * @return
		 */
		List<Map<String, Object>> getUnitsInfo() {
			// 只返回部队的cardId和牺牲的数量
			List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
			for (TTACard unit : this.units.keySet()) {
				Map<String, Object> o = new HashMap<String, Object>();
				o.put("cardId", unit.id);
				o.put("num", this.units.get(unit));
				res.add(o);
			}
			return res;
		}

		/**
		 * 设置加值卡是否选中
		 * 
		 * @param card
		 * @param selected
		 */
		void setBonusCard(BonusCard card, boolean selected) {
			this.bonusCards.put(card, selected);
		}

		/**
		 * 取得所有选中的加值卡
		 * 
		 * @return
		 */
		List<TTACard> getSelectedBonusCards() {
			List<TTACard> res = new ArrayList<TTACard>();
			for (BonusCard card : this.bonusCards.keySet()) {
				Boolean selected = this.bonusCards.get(card);
				if (selected != null && selected) {
					res.add(card);
				}
			}
			return res;
		}

		/**
		 * 取得使用加值卡的信息
		 * 
		 * @return
		 */
		List<Map<String, Object>> getBonusCardInfo() {
			// 只返回选中的加值卡
			List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
			for (TTACard card : this.bonusCards.keySet()) {
				Map<String, Object> o = new HashMap<String, Object>();
				o.put("cardId", card.id);
				o.put("selected", this.bonusCards.get(card));
				res.add(o);
			}
			return res;
		}

		@Override
		public Map<String, Object> toMap() {
			Map<String, Object> res = new HashMap<String, Object>();
			res.put("pass", this.pass);
			res.put("totalValue", this.getTotalValue());
			res.put("units", this.getUnitsInfo());
			res.put("bonusCards", this.getBonusCardInfo());
			return res;
		}
	}

}
