import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f14.TTA.component.card.CivilCard;
import com.f14.TTA.component.card.TTACard;
import com.f14.TTA.consts.CardSubType;
import com.f14.TTA.consts.CivilizationProperty;

/**
 * 提取资源和粮食用的类
 * 
 * @author F14eagle
 * 
 */
public class ResourceTaker {
	List<ResourceCounter> cs;

	ResourceTaker(CardSubType cardSubType) {
		cs = new ArrayList<ResourceCounter>();
		List<CivilCard> list = new ArrayList<CivilCard>();
		CivilCard c = new CivilCard();
		c.cardSubType = CardSubType.FARM;
		c.addBlues(0);
		c.property.addProperty(CivilizationProperty.FOOD, 1);
		list.add(c);
		c = new CivilCard();
		c.cardSubType = CardSubType.FARM;
		c.addBlues(0);
		c.property.addProperty(CivilizationProperty.FOOD, 2);
		//list.add(c);
		c = new CivilCard();
		c.cardSubType = CardSubType.FARM;
		c.addBlues(0);
		c.property.addProperty(CivilizationProperty.FOOD, 3);
		//list.add(c);
		c = new CivilCard();
		c.cardSubType = CardSubType.FARM;
		c.addBlues(1);
		c.property.addProperty(CivilizationProperty.FOOD, 5);
		list.add(c);
		
		for (TTACard c1 : list) {
			CivilCard card = (CivilCard) c1;
			cs.add(new ResourceCounter(card));
		}
		Collections.sort(cs);
	}

	/**
	 * 按照算法拿取资源
	 * 
	 * @param num
	 */
	void takeResource(int num) {
		int i = 0;
		int offset = 1;
		int rest = num;
		// 循环计算使用资源的数量
		while (rest != 0) {
			ResourceCounter c = this.cs.get(i);
			if (rest > 0) {
				// 拿取资源的逻辑
				int take = (int)Math.ceil((double)rest / (double)c.value);
				take = Math.min(take, c.availableNum);
				c.num += take;
				rest -= take * c.value;
			} else {
				// 找零的逻辑
				int take = -rest / c.value;
				if (take > 0) {
					take = Math.min(take, c.num);
					c.num -= take;
					rest += take * c.value;
				}
			}
			if (rest > 0) {
				// 如果还是不够资源,则继续检查值大的资源
				offset = 1;
			} else {
				// 如果需要找零,则检查值小的资源
				offset = -1;
			}
			i += offset;
			// 越界时跳出循环
			if (i < 0 || i >= cs.size()) {
				break;
			}
		}
		if(rest<0){
			//需要处理找零的情况
			i = this.cs.size()-1;
			offset = -1;
			while(rest!=0){
				ResourceCounter c = this.cs.get(i);
				int take = -rest / c.value;
				if (take > 0) {
					c.ret += take;
					rest += take * c.value;
				}
				i += offset;
				// 越界时跳出循环
				if (i < 0 || i >= cs.size()) {
					break;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		String str = "";
		for(ResourceCounter c : this.cs){
			str += c.value + " 可用:" + c.availableNum + " 使用:" + c.num + " 找回:" + c.ret + "\n";
		}
		return str;
	}

	class ResourceCounter implements Comparable<ResourceCounter> {
		CivilCard card;
		/**
		 * 每个单位表示的值
		 */
		int value;
		/**
		 * 可用的数量
		 */
		int availableNum;
		/**
		 * 使用的数量
		 */
		int num;
		/**
		 * 找回的数量
		 */
		int ret;

		ResourceCounter(CivilCard card) {
			this.card = card;
			if (this.card.cardSubType == CardSubType.FARM) {
				this.value = this.card.property
						.getProperty(CivilizationProperty.FOOD);
			} else if (this.card.cardSubType == CardSubType.MINE) {
				this.value = this.card.property
						.getProperty(CivilizationProperty.RESOURCE);
			}
			this.availableNum = this.card.getBlues();
		}

		@Override
		public int compareTo(ResourceCounter o) {
			if (value > o.value) {
				return 1;
			} else if (value < o.value) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	public static void main(String[] args){
		ResourceTaker taker = new ResourceTaker(null);
		taker.takeResource(1);
		System.out.println(taker);
	}
}
