package com.f14.f14bgdb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.f14.framework.common.model.BaseModel;

@Entity
@Table(name = "BOARDGAME", uniqueConstraints = {})
public class BoardGame extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String cnname;
	private String enname;
	private Integer minPlayerNumber;
	private Integer maxPlayerNumber;
	private String gameClass;
	private String playerClass;
	private String resourceClass;
	
	@Id
	@Column(name = "ID")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Column(name = "CNNAME")
	public String getCnname() {
		return cnname;
	}
	public void setCnname(String cnname) {
		this.cnname = cnname;
	}
	@Column(name = "ENNAME")
	public String getEnname() {
		return enname;
	}
	public void setEnname(String enname) {
		this.enname = enname;
	}
	@Column(name = "MIN_PLAYER_NUMBER")
	public Integer getMinPlayerNumber() {
		return minPlayerNumber;
	}
	public void setMinPlayerNumber(Integer minPlayerNumber) {
		this.minPlayerNumber = minPlayerNumber;
	}
	@Column(name = "MAX_PLAYER_NUMBER")
	public Integer getMaxPlayerNumber() {
		return maxPlayerNumber;
	}
	public void setMaxPlayerNumber(Integer maxPlayerNumber) {
		this.maxPlayerNumber = maxPlayerNumber;
	}
	@Column(name = "GAME_CLASS")
	public String getGameClass() {
		return gameClass;
	}
	public void setGameClass(String gameClass) {
		this.gameClass = gameClass;
	}
	@Column(name = "PLAYER_CLASS")
	public String getPlayerClass() {
		return playerClass;
	}
	public void setPlayerClass(String playerClass) {
		this.playerClass = playerClass;
	}
	@Column(name = "RESOURCE_CLASS")
	public String getResourceClass() {
		return resourceClass;
	}
	public void setResourceClass(String resourceClass) {
		this.resourceClass = resourceClass;
	}
	
	
}
