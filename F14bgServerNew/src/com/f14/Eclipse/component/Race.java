package com.f14.Eclipse.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.f14.Eclipse.consts.ActionType;
import com.f14.Eclipse.consts.PlayerProperty;
import com.f14.Eclipse.consts.RaceType;
import com.f14.Eclipse.consts.UnitType;
import com.f14.Eclipse.consts.TechnologyType;
import com.f14.bg.component.Card;

/**
 * 种族
 *
 * @author f14eagle
 */
public class Race extends Card {
	protected RaceType race;
	protected EclipseActionProperty actions;
	protected EclipsePlayerProperty properties;
	protected EclipsePlayerProperty resources;
	protected EclipseCostProperty costs;
	protected ReputationTrack reputationTrack;
	protected List<UnitType> startShip;
	protected List<TechnologyType> technology;
	protected Map<UnitType, Blueprint> blueprints;
	
	public RaceType getRace() {
		return race;
	}
	public void setRace(RaceType race) {
		this.race = race;
	}
	public EclipseActionProperty getActions() {
		return actions;
	}
	public void setActionProperty(Map<String,Integer> actions) {
		this.actions = new EclipseActionProperty();
		for(String key : actions.keySet()){
			ActionType action = ActionType.valueOf(key);
			this.actions.setProperty(action, actions.get(key));
		}
	}
	public EclipsePlayerProperty getProperties() {
		return properties;
	}
	public void setPlayerProperty(Map<String,Integer> properties) {
		this.properties = new EclipsePlayerProperty();
		for(String key : properties.keySet()){
			PlayerProperty p = PlayerProperty.valueOf(key);
			this.properties.setProperty(p, properties.get(key));
		}
	}
	public EclipsePlayerProperty getResources() {
		return resources;
	}
	public void setResourceProperty(Map<String,Integer> resources) {
		this.resources = new EclipsePlayerProperty();
		for(String key : resources.keySet()){
			PlayerProperty p = PlayerProperty.valueOf(key);
			this.resources.setProperty(p, resources.get(key));
		}
	}
	public EclipseCostProperty getCosts() {
		return costs;
	}
	public void setCostProperty(Map<String,Integer> costs) {
		this.costs = new EclipseCostProperty();
		for(String key : costs.keySet()){
			UnitType p = UnitType.valueOf(key);
			this.costs.setProperty(p, costs.get(key));
		}
	}
	public ReputationTrack getReputationTrack() {
		return reputationTrack;
	}
	public void setReputationTrack(ReputationTrack reputationTrack) {
		this.reputationTrack = reputationTrack;
	}
	public List<UnitType> getStartShip() {
		return startShip;
	}
	public void setStartShip(List<UnitType> startShip) {
		this.startShip = new ArrayList<UnitType>();
		for(Object o : startShip){
			this.startShip.add(UnitType.valueOf(o.toString()));
		}
	}
	public List<TechnologyType> getTechnology() {
		return technology;
	}
	public void setTechnology(List<TechnologyType> technology) {
		this.technology = new ArrayList<TechnologyType>();
		for(Object o : technology){
			this.technology.add(TechnologyType.valueOf(o.toString()));
		}
	}
	public Map<UnitType, Blueprint> getBlueprints() {
		return blueprints;
	}
	public void setBlueprints(Map<UnitType, Blueprint> blueprints) {
		this.blueprints = new HashMap<UnitType, Blueprint>();
		if(blueprints!=null){
			for(Object key : blueprints.keySet()){
				UnitType type = UnitType.valueOf(key.toString());
				Object v = blueprints.get(key);
				Blueprint a = (Blueprint)JSONObject.toBean(JSONObject.fromObject(v), Blueprint.class);
				a.shipType = type;
				this.blueprints.put(type, a);
			}
		}
	}
	
}
