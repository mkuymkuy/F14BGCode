package com.f14.brass.component;

import com.f14.brass.BrassPlayer;
import com.f14.brass.consts.BrassConnectType;

public class BrassConnection {
	public String id;
	public BrassLocation[] locations = new BrassLocation[2];
	public BrassConnectType connectType;
	public BrassPlayer owner;
	
	public BrassConnection(BrassConnectType connectType, BrassLocation l1, BrassLocation l2){
		this.connectType = connectType;
		locations[0] = l1;
		locations[1] = l2;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BrassLocation[] getLocations() {
		return locations;
	}

	public void setLocations(BrassLocation[] locations) {
		this.locations = locations;
	}

	public BrassConnectType getConnectType() {
		return connectType;
	}

	public void setConnectType(BrassConnectType connectType) {
		this.connectType = connectType;
	}

	public BrassPlayer getOwner() {
		return owner;
	}

	public void setOwner(BrassPlayer owner) {
		this.owner = owner;
	}
	
}
