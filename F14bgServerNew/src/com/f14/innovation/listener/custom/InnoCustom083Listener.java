package com.f14.innovation.listener.custom;

import com.f14.bg.action.BgAction;
import com.f14.bg.exception.BoardGameException;
import com.f14.innovation.InnoGameMode;
import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.ability.InnoAbility;
import com.f14.innovation.component.ability.InnoAbilityGroup;
import com.f14.innovation.consts.InnoColor;
import com.f14.innovation.consts.InnoGameCmd;
import com.f14.innovation.consts.InnoSplayDirection;
import com.f14.innovation.listener.InnoInterruptListener;
import com.f14.innovation.listener.InnoSplayListener;
import com.f14.innovation.param.InnoInitParam;
import com.f14.innovation.param.InnoParamFactory;
import com.f14.innovation.param.InnoResultParam;
import com.f14.innovation.utils.InnoUtils;

/**
 * #083-经验主义 监听器
 * 
 * @author F14eagle
 *
 */
public class InnoCustom083Listener extends InnoInterruptListener {

	public InnoCustom083Listener(InnoPlayer trigPlayer,
			InnoInitParam initParam, InnoResultParam resultParam,
			InnoAbility ability, InnoAbilityGroup abilityGroup) {
		super(trigPlayer, initParam, resultParam, ability, abilityGroup);
	}
	
	@Override
	protected int getValidCode() {
		return InnoGameCmd.GAME_CODE_083;
	}

	@Override
	protected void confirmCheck(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		
	}

	@Override
	protected void doConfirm(InnoGameMode gameMode, BgAction action)
			throws BoardGameException {
		InnoPlayer player = action.getPlayer();
		String colorString = action.getAsString("colors");
		String[] colorStrings = colorString.split(",");
		if(colorStrings.length!=2){
			throw new BoardGameException("你必须指定两种颜色!");
		}
		InnoColor[] colors = new InnoColor[colorStrings.length];
		for(int i=0;i<colorStrings.length;i++){
			colors[i] = InnoColor.valueOf(colorStrings[i]);
		}
		//首先指定两种颜色,抓一张[9]展示,若是你指定颜色中的
		//一种,便将之融合并可以将该颜色的牌向上展开!
		InnoResultParam resultParam = gameMode.getGame().playerDrawCardAction(player, 9, 1, true);
		if(!resultParam.getCards().isEmpty()){
			if(InnoUtils.hasColor(resultParam.getCards().getCards(), colors)){
				gameMode.getGame().playerMeldCard(player, resultParam);
				//创建一个将该颜色的牌向上展开的询问监听器
				InnoInitParam initParam = InnoParamFactory.createInitParam();
				initParam.color = resultParam.getCards().getCards().get(0).color;
				initParam.splayDirection = InnoSplayDirection.UP;
				initParam.canPass = true;
				initParam.msg = "你可以将你的"+InnoColor.getDescr(initParam.color)+"牌向上展开!";
				InnoSplayListener al = new InnoSplayListener(player, initParam, this.getResultParam(), this.getAbility(), this.getAbilityGroup());
				this.getCommandList().insertInterrupteListener(al, gameMode);
			}else{
				gameMode.getGame().playerAddHandCard(player, resultParam);
			}
		}
		this.setPlayerResponsed(gameMode, player);
	}

}
