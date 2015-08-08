package com.cd.minecraft.mclauncher.entity;

import java.util.List;

public class TimelineVo {
	
	private String timelineId;
	private String title;
	private String uid;
	private String desc;
	private String nickName;
	private String picUrl;
	private String imageUrl;
	private String downloadUrl;
	private String shareType;
	private int like;
	private int comments;
	private int download;
	private long time;


	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getDownload() {
		return download;
	}
	public void setDownload(int download) {
		this.download = download;
	}
	private List<TimelineCommentVo> commentContents;

	public List<TimelineCommentVo> getCommentContents() {
		return commentContents;
	}
	public void setCommentContents(List<TimelineCommentVo> commentContents) {
		this.commentContents = commentContents;
	}

	
	public String getTimelineId() {
		return timelineId;
	}
	public void setTimelineId(String timelineId) {
		this.timelineId = timelineId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getShareType() {
		return shareType;
	}
	public void setShareType(String shareType) {
		this.shareType = shareType;
	}
	public int getLike() {
		return like;
	}
	public void setLike(int like) {
		this.like = like;
	}
	public int getComments() {
		return comments;
	}
	public void setComments(int comments) {
		this.comments = comments;
	}
	
	
	
}
