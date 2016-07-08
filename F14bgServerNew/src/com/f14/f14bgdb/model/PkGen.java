package com.f14.f14bgdb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.f14.framework.common.model.BaseModel;

@Entity
@Table(name = "PK_GEN", uniqueConstraints = {})
public class PkGen extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String name;
	private Long value;
	
	@Id
	@Column(name = "NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Column(name = "VALUE")
	public Long getValue() {
		return value;
	}
	public void setValue(Long value) {
		this.value = value;
	}
	
}
