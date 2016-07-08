package com.f14.TS.component;

import com.f14.TS.consts.SuperPower;
import com.f14.TS.consts.TSDurationSession;
import com.f14.TS.consts.TSDurationType;

public class DurationResult {
	public TSDurationType durationType;
	public SuperPower target;
	public TSDurationSession durationSession;
	public TSDurationType getDurationType() {
		return durationType;
	}
	public void setDurationType(TSDurationType durationType) {
		this.durationType = durationType;
	}
	public SuperPower getTarget() {
		return target;
	}
	public void setTarget(SuperPower target) {
		this.target = target;
	}
	public TSDurationSession getDurationSession() {
		return durationSession;
	}
	public void setDurationSession(TSDurationSession durationSession) {
		this.durationSession = durationSession;
	}
}
