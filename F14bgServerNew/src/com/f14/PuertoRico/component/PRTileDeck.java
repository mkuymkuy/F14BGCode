package com.f14.PuertoRico.component;

import com.f14.PuertoRico.consts.GoodType;

public class PRTileDeck extends PRDeck<PRTile> {

	/**
	 * 按照goodType取得板块
	 * 
	 * @param goodType
	 * @return
	 */
	public PRTile takeTileByGoodType(GoodType goodType){
		for(PRTile tile : this.cards){
			if(tile.goodType==goodType){
				this.cards.remove(tile);
				return tile;
			}
		}
		return null;
	}
}
