package com.f14.TTA.component.ability;

import org.apache.log4j.Logger;

import com.f14.TTA.component.Chooser;
import com.f14.TTA.component.TTAProperty;
import com.f14.TTA.consts.CivilizationProperty;
import com.f14.TTA.consts.EventTrigType;
import com.f14.TTA.consts.EventType;
import com.f14.bg.common.ParamSet;
import com.f14.utils.StringUtils;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 * 事件能力
 * 
 * @author F14eagle
 *
 */
public class EventAbility extends CardAbility {
	protected Logger log = Logger.getLogger(this.getClass());
	/**
	 * 事件的触发类型
	 */
	public EventTrigType trigType;
	/**
	 * 文明选择器
	 */
	public Chooser chooser;
	/**
	 * 事件类型
	 */
	public EventType eventType;
	/**
	 * 总数
	 */
	public int amount;
	/**
	 * 选择资源/食物时用,是否只能选择一种
	 */
	public boolean singleSelection;
	/**
	 * 参照属性
	 */
	public CivilizationProperty byProperty;
	public boolean produceFood;
	public boolean ignoreFood;
	public boolean produceResource;
	public boolean ignoreResource;
	public boolean byResult;
	public String expression;
	public boolean winnerSelect;

	public EventTrigType getTrigType() {
		return trigType;
	}

	public void setTrigType(EventTrigType trigType) {
		this.trigType = trigType;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Chooser getChooser() {
		return chooser;
	}

	public void setChooser(Chooser chooser) {
		this.chooser = chooser;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public boolean isSingleSelection() {
		return singleSelection;
	}

	public void setSingleSelection(boolean singleSelection) {
		this.singleSelection = singleSelection;
	}

	public CivilizationProperty getByProperty() {
		return byProperty;
	}

	public void setByProperty(CivilizationProperty byProperty) {
		this.byProperty = byProperty;
	}

	public boolean isProduceFood() {
		return produceFood;
	}

	public void setProduceFood(boolean produceFood) {
		this.produceFood = produceFood;
	}

	public boolean isIgnoreFood() {
		return ignoreFood;
	}

	public void setIgnoreFood(boolean ignoreFood) {
		this.ignoreFood = ignoreFood;
	}

	public boolean isProduceResource() {
		return produceResource;
	}

	public void setProduceResource(boolean produceResource) {
		this.produceResource = produceResource;
	}

	public boolean isIgnoreResource() {
		return ignoreResource;
	}

	public void setIgnoreResource(boolean ignoreResource) {
		this.ignoreResource = ignoreResource;
	}

	public boolean isByResult() {
		return byResult;
	}

	public void setByResult(boolean byResult) {
		this.byResult = byResult;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public boolean isWinnerSelect() {
		return winnerSelect;
	}

	public void setWinnerSelect(boolean winnerSelect) {
		this.winnerSelect = winnerSelect;
	}

	/**
	 * 表达式中可能出现的参数名称
	 */
	protected static final String[] PROPS = new String[] { "level", "totalCost", "decrease_pop", "advantage" };

	/**
	 * 按照入参和表达式,取得真实的属性/资源调整值对象
	 * 
	 * @param param
	 * @return
	 */
	public TTAProperty getRealProperty(ParamSet param) {
		if (this.byResult) {
			// 如果和结果关联
			if (StringUtils.isEmpty(this.expression)) {
				// 如果表达式为空,则返回param中的property值
				return param.get("property");
			} else {
				// 否则计算表达式中的值
				Evaluator eval = new Evaluator();
				for (String prop : PROPS) {
					if (this.expression.indexOf(prop) != -1) {
						eval.putVariable(prop, param.getInteger(prop).toString());
					}
				}
				try {
					// 表达式值 x property为最终结果
					int amount = (int) eval.getNumberResult(this.expression);
					TTAProperty res = new TTAProperty();
					res.addProperties(this.property, amount);
					return res;
				} catch (EvaluationException e) {
					log.error(e, e);
					return null;
				}
			}
		} else {
			// 如果和结果无关联,则直接返回property
			return this.property;
		}
	}

	/**
	 * 按照入参和表达式,取得真实的amount值
	 * 
	 * @param param
	 * @return
	 */
	public int getRealAmount(ParamSet param) {
		if (this.byResult) {
			// 如果和结果关联
			if (StringUtils.isEmpty(this.expression)) {
				// 如果表达式为空,则返回1
				return 1;
			} else {
				// 否则计算表达式中的值
				Evaluator eval = new Evaluator();
				for (String prop : PROPS) {
					if (this.expression.indexOf(prop) != -1) {
						eval.putVariable(prop, param.getInteger(prop).toString());
					}
				}
				try {
					// 表达式值为最终结果
					int amount = (int) eval.getNumberResult(this.expression);
					return amount;
				} catch (EvaluationException e) {
					log.error(e, e);
					return 0;
				}
			}
		} else {
			// 如果和结果无关联,则直接返回amount
			return this.amount;
		}
	}
}
