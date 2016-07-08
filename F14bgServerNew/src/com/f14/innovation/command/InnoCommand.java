package com.f14.innovation.command;

import com.f14.innovation.InnoPlayer;
import com.f14.innovation.component.InnoCard;
import com.f14.innovation.component.ability.InnoAbilityGroup;

public class InnoCommand {
	public InnoPlayer player;
	public InnoPlayer trigPlayer;
	public InnoAbilityGroup abilityGroup;
	public InnoCard trigCard;
	
	public InnoCommand(){
		
	}
	
	public InnoCommand(InnoPlayer player, InnoPlayer trigPlayer, InnoAbilityGroup abilityGroup, InnoCard trigCard){
		this.player = player;
		this.trigPlayer = trigPlayer;
		this.abilityGroup = abilityGroup;
		this.trigCard = trigCard;
	}
	
}
