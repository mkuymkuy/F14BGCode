package com.f14.innovation.listener.custom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f14.bg.action.BgAction;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.BgUtils;
import com.f14.bg.utils.CheckUtils;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.consts.InnoIcon;
import com.f14.innovation.listener.InnoChooseCardListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #081-火箭技术 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom081Listener extends InnoChooseCardListener {
	protected int chooseNum = 0;
	protected List<InnoCard> chooseCards = new ArrayList<InnoCard>();

	public InnoCustom081Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
		this.setNum();
	}
	
	private void setNum(){
		InnoPlayer player = this.getTargetPlayer();
		int num = player.getIconCount(InnoIcon.CLOCK)/2;
		this.getInitParam().num = num;
	}

	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_081;
	}
	
	@Override
	protected String getActionString() {
		return "";
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		if(this.getAvailableCardNum(gameMode, (InnoPlayer)player)==0 
				|| this.getInitParam().num==0){
			return false;
		}
		return true;
	}
	
	@Override
	protected BgResponse createStartListenCommand(InnoGameMode gameMode,
			Player player) {
		InnoPlayer p = (InnoPlayer)player;
		BgResponse res = super.createStartListenCommand(gameMode, player);
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		for(InnoPlayer o : gameMode.getGame().getValidPlayers()){
			if(o!=p){
				String cardIds = BgUtils.card2String(o.getScores().getCards());
				map.put(o.position, cardIds);
			}
		}
		res.setPrivateParameter("playerScoreCards", map);
		res.setPrivateParameter("num", this.getInitParam().num);
		return res;
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	/**
	 * 取得所有可供选择牌的数量
	 * 
	 * @param player
	 * @return
	 */
	protected int getAvailableCardNum(InnoPlayer player){
		return 0;
	}
	
	/**
	 * 取得所有可供选择牌的数量
	 * 
	 * @param player
	 * @return
	 */
	protected int getAvailableCardNum(InnoGameMode gameMode, InnoPlayer player){
		int i = 0;
		for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
			if(p!=player && !gameMode.getGame().isTeammates(p, player)){
				i += p.getScores().size();
			}
		}
		return i;
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		int targetPosition = action.getAsInt("choosePosition");
		InnoPlayer target = gameMode.getGame().getPlayer(targetPosition);
		CheckUtils.checkNull(target, "请选择目标玩家!");
		if(target==player || gameMode.getGame().isTeammates(target, player)){
			throw new BoardGameException("不能选择自己或队友的牌!");
		}
		String cardIds = action.getAsString("cardIds");
		List<InnoCard> cards = target.getScores().getCards(cardIds);
		
		this.checkChooseCard(gameMode, player, cards);
		this.beforeProcessChooseCard(gameMode, player, target, cards);
		this.processChooseCard(gameMode, player, target, cards);
		this.afterProcessChooseCard(gameMode, player, cards);
		this.checkPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 对所选的牌进行校验
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void checkChooseCard(InnoGameMode gameMode, InnoPlayer player, List<InnoCard> cards) throws BoardGameException{
		if(cards.isEmpty()){
			throw new BoardGameException("请选择计分区的牌!");
		}
	}
	
	protected void beforeProcessChooseCard(InnoGameMode gameMode,
			InnoPlayer player, InnoPlayer target, List<InnoCard> cards) throws BoardGameException {
		//发送移除牌的指令
		gameMode.getGame().sendPlayerRemoveChooseScoreCardsResponse(player, target, cards);
		this.chooseNum += cards.size();
		this.chooseCards.addAll(cards);
	}
	
	/**
	 * 处理玩家选择的牌
	 * 
	 * @param gameMode
	 * @param player
	 * @param target
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player, InnoPlayer target, List<InnoCard> cards)
			throws BoardGameException {
		//从目标玩家计分区中归还牌
		for(InnoCard card : cards){
			InnoResultParam resultParam = gameMode.getGame().playerRemoveScoreCard(target, card);
			gameMode.getGame().playerReturnCard(target, resultParam);
		}
	}
	
	/**
	 * 检查玩家的回应情况
	 * 
	 * @param gameMode
	 * @param player
	 */
	protected void checkPlayerResponsed(InnoGameMode gameMode, InnoPlayer player){
		//如果达到选择数量,则结束回应
		if(this.canEndResponse(gameMode, player)){
			this.setPlayerResponsed(gameMode, player);
		}
	}
	
	/**
	 * 判断是否可以结束行动
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	protected boolean canEndResponse(InnoGameMode gameMode, InnoPlayer player){
		//选够牌,就可以结束行动
		if(this.getInitParam().num<=this.chooseNum){
			return true;
		}
		//如果其他玩家都没有计分牌,则可以结束行动
		if(this.getAvailableCardNum(gameMode, player)==0){
			return true;
		}
		return false;
	}
	
}
