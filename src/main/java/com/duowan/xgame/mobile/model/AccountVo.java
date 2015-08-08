package com.duowan.xgame.mobile.model;

import java.io.Serializable;
import java.util.List;


public class AccountVo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String roleName;
	private String uid;
	private String token;
	private String nickName;
	private String gameId;
	private String picUrl;
	private long currentScore;
	private long maxScore;
	private int refundCounter;
	private int shared;
	private int level;
		
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public int getShared() {
		return shared;
	}
	public void setShared(int shared) {
		this.shared = shared;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getRefundCounter() {
		return refundCounter;
	}
	public void setRefundCounter(int refundCounter) {
		this.refundCounter = refundCounter;
	}
	public long getCurrentScore() {
		return currentScore;
	}
	public void setCurrentScore(long currentScore) {
		this.currentScore = currentScore;
	}
	public long getMaxScore() {
		return maxScore;
	}
	public void setMaxScore(long maxScore) {
		this.maxScore = maxScore;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getGameId() {
		return gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}
	
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public long getTopScore(){
		return (shared * 1000 + currentScore);
	}
	
}

