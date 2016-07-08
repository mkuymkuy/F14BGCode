package com.f14.tichu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.f14.F14bg.consts.GameType;
import com.f14.F14bg.manager.ResourceManager;
import com.f14.F14bg.network.PlayerHandler;
import com.f14.bg.action.BgResponse;
import com.f14.bg.exception.BoardGameException;
import com.f14.bg.utils.BgUtils;
import com.f14.tichu.componet.TichuCard;
import com.f14.utils.ExcelUtils;
import com.f14.utils.SequenceUtils;

public class TichuResourceManager extends ResourceManager {
	protected List<TichuCard> cards = new ArrayList<TichuCard>();

	@Override
	public GameType getGameType() {
		return GameType.Tichu;
	}

	@Override
	public void init() throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(BgUtils.getFileInputStream("./game/Tichu.xls"));
		HSSFSheet sheet = wb.getSheetAt(0);
		String[] head = ExcelUtils.rowToStringArray(sheet.getRow(0));
		for(int i=1;i<=sheet.getLastRowNum();i++){
			TichuCard card = ExcelUtils.rowToObject(sheet.getRow(i), head, TichuCard.class);
			card.id = SequenceUtils.generateId(Tichu.class);
			cards.add(card);
		}
	}
	
	/**
	 * 取得所有卡牌的副本
	 * 
	 * @return
	 */
	public Collection<TichuCard> getAllCardsInstance(){
		return BgUtils.cloneList(this.cards);
	}
	
	@Override
	public void sendResourceInfo(PlayerHandler handler)
			throws BoardGameException {
		BgResponse res = this.createResourceResponse();
		res.setPublicParameter("cards", this.cards);
		handler.sendResponse(res);
	}

}
