package com.f14.innovation.listener;

import java.util.List;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.player.Player;
import com.f14.bg.utils.CheckUtils;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoActiveType;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * 选择其他玩家牌堆的监听器
 * 
 * @author F14eagle
 *
 */
public class InnoChoosePlayerStackListener extends InnoChooseStackListener {

	public InnoChoosePlayerStackListener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_CHOOSE_STACK;
	}
	
	@Override
	protected String getActionString() {
		return "ACTION_CHOOSE_PLAYER_STACK";
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}
	
	@Override
	protected boolean beforeListeningCheck(InnoGameMode gameMode, Player player) {
		if(this.getInitParam()!=null){
			InnoPlayer p = (InnoPlayer)player;
			if(this.getAbilityGroup()!=null && this.getAbilityGroup().getActiveType()==InnoActiveType.DEMAND){
				//如果是DEMAND技能,则只要存在可以选的牌,就需要回应
				if(this.getAvailableCardNum(gameMode, p)==0){
					return false;
				}
			}else{
				//如果需要选择牌,并且牌数量不足时,则不需要回应
				if(this.getInitParam().num>0 && this.getAvailableCardNum(gameMode, p)<this.getInitParam().num){
					return false;
				}
			}
		}
		return true;
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
			if(this.canChoosePlayer(gameMode, player, p)){
				for(InnoCard card : p.getTopCards()){
					if(this.canChooseCard(p, card)){
						i += 1;
					}
				}
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
		CheckUtils.checkNull(target, "请选择目标玩家");
		this.checkChoosePlayer(gameMode, player, target);
		
		//选择置顶牌时只指定了牌堆的颜色,需要按照牌堆的颜色取得对应的置顶牌
		String colorString = action.getAsString("colors");
		CheckUtils.checkNull(colorString, "请选择置顶牌!");
		String[] colorStrings = colorString.split(",");
		InnoColor[] colors = new InnoColor[colorStrings.length];
		for(int i=0;i<colors.length;i++){
			colors[i] = InnoColor.valueOf(colorStrings[i]);
		}
		List<InnoCard> cards = target.getTopCards(colors);
		
		this.checkChooseCard(gameMode, player, target, cards);
		this.processChooseCard(gameMode, player, target, cards);
		this.afterProcessChooseCard(gameMode, player, cards);
		this.checkPlayerResponsed(gameMode, player);
	}
	
	/**
	 * 对所选的牌进行校验
	 * 
	 * @param gameMode
	 * @param player
	 * @param target
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void checkChooseCard(InnoGameMode gameMode, InnoPlayer player, InnoPlayer target, List<InnoCard> cards) throws BoardGameException{
		if(cards.isEmpty()){
			throw new BoardGameException("请选择置顶牌!");
		}
		if(this.getInitParam()!=null){
			if(this.getInitParam().maxNum>0){
				if(cards.size()>this.getInitParam().maxNum){
					throw new BoardGameException("你至多只能选择"+this.getInitParam().maxNum+"张置顶牌!");
				}
			}
		}
		if(!this.canChooseCard(player, cards)){
			throw new BoardGameException("你不能选择这张牌!");
		}
	}
	
	/**
	 * 处理玩家选择的牌
	 * 
	 * @param gameMode
	 * @param player
	 * @param cards
	 * @throws BoardGameException
	 */
	protected void processChooseCard(InnoGameMode gameMode, InnoPlayer player, InnoPlayer target, List<InnoCard> cards)
			throws BoardGameException {
		
	}
	
	/**
	 * 判断是否可以选择玩家
	 * 
	 * @param gameMode
	 * @param player
	 * @param target
	 * @return
	 */
	protected boolean canChoosePlayer(InnoGameMode gameMode, InnoPlayer player, InnoPlayer target){
		//判断能不能选择自己
		if(this.getInitParam()==null || !this.getInitParam().canChooseSelf){
			if(player==target){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 检查是否可以选择目标玩家
	 * 
	 * @param gameMode
	 * @param player
	 * @param target
	 * @throws BoardGameException
	 */
	protected void checkChoosePlayer(InnoGameMode gameMode, InnoPlayer player, InnoPlayer target) throws BoardGameException{
		if(!this.canChoosePlayer(gameMode, player, target)){
			throw new BoardGameException("不能选择该玩家!");
		}
	}
	
	/**
	 * 判断是否可以结束回应
	 * 
	 * @param gameMode
	 * @param player
	 * @return
	 */
	protected boolean canEndResponse(InnoGameMode gameMode, InnoPlayer player){
		if(this.getAvailableCardNum(gameMode, player)==0){
			return true;
		}
		if(this.getInitParam()!=null){
			if(this.selectedCards.size()<this.getInitParam().num){
				return false;
			}
		}
		return true;
	}

}
