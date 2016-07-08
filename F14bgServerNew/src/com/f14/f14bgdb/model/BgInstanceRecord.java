package com.f14.f14bgdb.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.f14.framework.common.model.BaseModel;

@Entity
@Table(name = "BG_INSTANCE_RECORD", uniqueConstraints = {})
public class BgInstanceRecord extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long bgInstanceId;
	private Long userId;
	private Integer rank;
	private Integer score;
	private Integer vp;
	private String detailStr;
	private Boolean isWinner;
	private Integer rankPoint;
	
	private BgInstance bgInstance;
	private User user;
	
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
	@Column(name = "BG_INSTANCE_ID", insertable = false, updatable = false)
	public Long getBgInstanceId() {
		return bgInstanceId;
	}
	public void setBgInstanceId(Long bgInstanceId) {
		this.bgInstanceId = bgInstanceId;
	}
	@Column(name = "USER_ID", insertable = false, updatable = false)
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	@Column(name = "RANK")
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	@Column(name = "SCORE")
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	@Column(name = "VP")
	public Integer getVp() {
		return vp;
	}
	public void setVp(Integer vp) {
		this.vp = vp;
	}
	@Column(name = "DETAIL_STR")
	public String getDetailStr() {
		return detailStr;
	}
	public void setDetailStr(String detailStr) {
		this.detailStr = detailStr;
	}
	@Column(name = "IS_WINNER")
	public Boolean getIsWinner() {
		return isWinner;
	}
	public void setIsWinner(Boolean isWinner) {
		this.isWinner = isWinner;
	}
	@Column(name = "RANK_POINT")
	public Integer getRankPoint() {
		return rankPoint;
	}
	public void setRankPoint(Integer rankPoint) {
		this.rankPoint = rankPoint;
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
	@JoinColumn(name = "BG_INSTANCE_ID")
	public BgInstance getBgInstance() {
		return bgInstance;
	}
	public void setBgInstance(BgInstance bgInstance) {
		this.bgInstance = bgInstance;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
}
