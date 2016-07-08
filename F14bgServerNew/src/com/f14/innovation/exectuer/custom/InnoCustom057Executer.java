package com.f14.innovation.exectuer.custom;

import java.util.List;

import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.exectuer.InnoActionExecuter;
import com.f14.innovation.listener.custom.InnoCustom057P1Listener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoResultParam;

/**
 * #057-分类学 执行器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom057Executer extends InnoActionExecuter {

	public InnoCustom057Executer(InnoGameMode gameMode, InnoPlayer player,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(gameMode, player, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	public void doAction() throws BoardGameException {
		//拿取所有其他玩家手中该颜色牌
		InnoPlayer player = this.getTargetPlayer();
		if(this.getResultCards()!=null && !this.getResultCards().isEmpty()){
			//只要取第一张牌的颜色即可
			InnoColor color = this.getResultCards().get(0).color;
			for(InnoPlayer p : gameMode.getGame().getValidPlayers()){
				if(p!=player && !gameMode.getGame().isTeammates(p, player)){
					//取得所有指定颜色的手牌
					List<InnoCard> cards = p.getHandsByColor(color);
					if(cards!=null && !cards.isEmpty()){
						for(InnoCard c : cards){
							//转移手牌
							InnoResultParam resultParam = gameMode.getGame().playerRemoveHandCard(p, c);
							gameMode.getGame().playerAddHandCard(player, resultParam);
						}
						
					}
				}
			}
			//创建一个实际执行效果的监听器
			InnoCustom057P1Listener al = new InnoCustom057P1Listener(this.getTargetPlayer(), this.getInitParam(), this.getResultParam(), this.getAbility(), this.getAbilityGroup());
			//将玩家所有指定颜色的手牌设为待选牌
			al.getSpecificCards().addCards(player.getHandsByColor(color));
			//插入监听器
			this.getCommandList().insertInterrupteListener(al, gameMode);
		}
	}

}
