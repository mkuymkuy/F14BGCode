package com.f14.f14bgdb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.f14.framework.common.model.BaseModel;

@Entity
@Table(name = "BG_INSTANCE", uniqueConstraints = {})
public class BgInstance extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String boardGameId;
	private String config;
	private Long gameTime;
	private Integer playerNum;
	
	private BoardGame boardGame;
	private List<BgInstanceRecord> bgInstanceRecords = new ArrayList<BgInstanceRecord>(0);
	private List<BgReport> bgReports = new ArrayList<BgReport>(0);
	
	@Id
	@TableGenerator(name = "PK_GEN", allocationSize = 100, table = "PK_GEN", valueColumnName = "VALUE", pkColumnName = "NAME", pkColumnValue = "SEQ_ID")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PK_GEN")
	@Column(name = "ID", unique = true, nullable = false, insertable = true, updatable = true)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "BOARDGAME_ID", insertable = false, updatable = false)
	public String getBoardGameId() {
		return boardGameId;
	}
	public void setBoardGameId(String boardGameId) {
		this.boardGameId = boardGameId;
	}
	@Column(name = "CONFIG")
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	@Column(name = "GAME_TIME")
	public Long getGameTime() {
		return gameTime;
	}
	public void setGameTime(Long gameTime) {
		this.gameTime = gameTime;
	}
	@Column(name = "PLAYER_NUM")
	public Integer getPlayerNum() {
		return playerNum;
	}
	public void setPlayerNum(Integer playerNum) {
		this.playerNum = playerNum;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATETIME")
	@Override
	public Date getCreateTime() {
		return super.getCreateTime();
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="UPDATETIME")
	@Override
	public Date getUpdateTime() {
		return super.getUpdateTime();
	}
	@Override
	public void setCreateTime(Date createTime) {
		super.setCreateTime(createTime);
	}
	@Override
	public void setUpdateTime(Date updateTime) {
		super.setUpdateTime(updateTime);
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BOARDGAME_ID")
	public BoardGame getBoardGame() {
		return boardGame;
	}
	public void setBoardGame(BoardGame boardGame) {
		this.boardGame = boardGame;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bgInstance")
	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	@JoinColumn(name = "BG_INSTANCE_ID")
	public List<BgInstanceRecord> getBgInstanceRecords() {
		return bgInstanceRecords;
	}
	public void setBgInstanceRecords(List<BgInstanceRecord> bgInstanceRecords) {
		this.bgInstanceRecords = bgInstanceRecords;
	}
	public void addBgInstanceRecord(BgInstanceRecord o) {
		this.bgInstanceRecords.add(o);
		o.setBgInstance(this);
	}
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "bgInstance")
	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	@JoinColumn(name = "BG_INSTANCE_ID")
	public List<BgReport> getBgReports() {
		return bgReports;
	}
	public void setBgReports(List<BgReport> bgReports) {
		this.bgReports = bgReports;
	}
	public void addBgReport(BgReport o) {
		this.bgReports.add(o);
		o.setBgInstance(this);
	}
	
}
