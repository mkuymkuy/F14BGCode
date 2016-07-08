import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.f14.RFTG.RacePlayer;
import com.f14.RFTG.card.Ability;
import com.f14.RFTG.card.BonusAbility;
import com.f14.RFTG.card.ConsumeAbility;
import com.f14.RFTG.card.DevelopAbility;
import com.f14.RFTG.card.ExploreAbility;
import com.f14.RFTG.card.ProduceAbility;
import com.f14.RFTG.card.RaceCard;
import com.f14.RFTG.card.SettleAbility;
import com.f14.RFTG.card.TradeAbility;
import com.f14.RFTG.consts.GameState;
import com.f14.RFTG.manager.RaceResourceManager;
import com.f14.bg.hall.User;


public class BonusTest {

	/**
	 * 按照阶段取得对应的能力类
	 * 
	 * @param state
	 * @return
	 */
	private static Class<? extends Ability> getPhaseClass(GameState state){
		switch(state){
		case ACTION_EXPLORE:
			return ExploreAbility.class;
		case ACTION_DEVELOP:
			return DevelopAbility.class;
		case ACTION_SETTLE:
			return SettleAbility.class;
		case ACTION_TRADE:
			return TradeAbility.class;
		case ACTION_CONSUME:
			return ConsumeAbility.class;
		case ACTION_PRODUCE:
			return ProduceAbility.class;
		default:
			return null;
		}
	}
	
	private static void printVP(RaceCard card, int vp){
		System.out.println(card.getName() + " , " + vp);
	}
	
	/**
	 * 取得指定卡牌能得到的额外VP
	 * 
	 * @param player
	 * @param ability
	 * @return
	 */
	public static int getBonus(RacePlayer player, RaceCard bonusCard){
		int vp = 0;
		//将玩家建造完成的卡牌保留一副本
		List<RaceCard> cards = new ArrayList<RaceCard>();
		cards.addAll(player.getBuiltCards());
		List<BonusAbility> abilities = bonusCard.getAbilitiesByType(BonusAbility.class);
		for(BonusAbility ability : abilities){
			System.out.println(ability.vp);
			if(ability.skill==null){
				//标准额外VP
				Iterator<RaceCard> it = cards.iterator();
				while(it.hasNext()){
					RaceCard card = it.next();
					if(ability.test(card)){
						if(ability.phase!=null){
							//拥有阶段能力的卡牌
							Class<? extends Ability> abilityClass = getPhaseClass(ability.phase);
							if(abilityClass!=null && card.hasAbility(abilityClass)){
								vp += ability.vp;
								//有符合条件的能力时,从列表中移除该卡牌
								//每个开发设施或卡牌在一个额外VP能力中只能取得1次VP
								it.remove();
								printVP(card, ability.vp);
							}
						}else{
							//未限定卡牌
							vp += ability.vp;
							//有符合条件的能力时,从列表中移除该卡牌
							//每个开发设施或卡牌在一个额外VP能力中只能取得1次VP
							it.remove();
							printVP(card, ability.vp);
						}
					}
				}
			}else{
				switch(ability.skill){
				case VP_BONUS_MILITARY:
					//额外VP - 军事力 - 计算所有星球的军事力
					for(RaceCard card : player.getBuiltCards()){
						vp += card.military;
						printVP(card, card.military);
					}
					break;
				case VP_BONUS_CHIP_PER_VP:
					//额外VP - VP筹码  - 每chip个筹码得1VP
					vp += (int)(player.vp/ability.chip);
					System.out.println("得到额外VP: " + (int)(player.vp/ability.chip));
					break;
				default:
				}
			}
		}
		return vp;
	}
	
	public static void main(String[] args) throws Exception{
		RaceResourceManager rm = new RaceResourceManager();
		rm.init();
		
		String cardNo = "36";
		int vp = 14;
		
		RaceCard card = rm.getByCardNo(cardNo);
		RacePlayer player = new RacePlayer();
		player.user = new User(null);
		player.vp = vp;
		player.addBuiltCards(rm.getCardList());
		
		vp = getBonus(player, card);
		printVP(card, vp);
	}
}
