package com.f14.f14bgdb.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.f14.framework.common.model.BaseModel;

@Entity
@Table(name = "RANKING_LIST", uniqueConstraints = {})
public class RankingList extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long userId;
	private String boardGameId;
	private String loginName;
	private String userName;
	private Long numWins;
	private Long numLoses;
	private Long numTotal;
	private Double rate;
	private Long score;
	private Long rankPoint;
	
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
	@Column(name = "USER_ID")
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	@Column(name = "BOARDGAME_ID")
	public String getBoardGameId() {
		return boardGameId;
	}
	public void setBoardGameId(String boardGameId) {
		this.boardGameId = boardGameId;
	}
	@Column(name = "LOGINNAME")
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	@Column(name = "USERNAME")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@Column(name = "NUM_WINS")
	public Long getNumWins() {
		return numWins;
	}
	public void setNumWins(Long numWins) {
		this.numWins = numWins;
	}
	@Column(name = "NUM_LOSES")
	public Long getNumLoses() {
		return numLoses;
	}
	public void setNumLoses(Long numLoses) {
		this.numLoses = numLoses;
	}
	@Column(name = "NUM_TOTAL")
	public Long getNumTotal() {
		return numTotal;
	}
	public void setNumTotal(Long numTotal) {
		this.numTotal = numTotal;
	}
	@Column(name = "RATE")
	public Double getRate() {
		return rate;
	}
	public void setRate(Double rate) {
		this.rate = rate;
	}
	@Column(name = "SCORE")
	public Long getScore() {
		return score;
	}
	public void setScore(Long score) {
		this.score = score;
	}
	@Column(name = "RANK_POINT")
	public Long getRankPoint() {
		return rankPoint;
	}
	public void setRankPoint(Long rankPoint) {
		this.rankPoint = rankPoint;
	}
}
