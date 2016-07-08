package com.f14.bg.component;

import java.util.Collection;

public abstract class AbstractCondition<P> implements ICondition<P>, Cloneable {
	
	@Override
	public boolean test(Collection<P> objects) {
		for(P object : objects){
			if(!this.test(object)){
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected AbstractCondition<P> clone() {
		try {
			return (AbstractCondition<P>)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
