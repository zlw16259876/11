package com.jw.oa.meetingManage.domain;

import java.io.Serializable;
import java.util.List;
/**
 * 会议信息
 * @author hsk
 *
 */
public class Meeting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//会议登记序列号
	private String oid;
	//会议名称
	private String meetingName;
	//会议主题
	private String title;
	//会议主持
	private String compere;
	//会议开始时间
	private String startDateTime;
	//会议结束时间
	private String endDateTime;
	//会议地点
	private String meetinfSite;
	//会议报名（申请）时间
	private String applyDateTime;
	//会议通知单位
	private String notificationOrgId;
	//会议通知方式
	private String notificationWay;
	//主持领导
	private String compereManagerOid;
	//会议要求
	private String meetingDemand;
	//会议性质（0:委内,1：委外）
	private Integer meetingNature;
	//是否短信（0：否,1：是）  2014-12-05改为是否下发会议通知标识 hsk
	private Integer isShortMsg; 
	//短信内容
	private String shortMsg;
	//是否已安排（0：否,1：是）
	private Integer isArrange;
	//会议状态
	private String status;
	//会议创建人
	private String createUserId;
	//联系人
	private String contacts;
	//联系电话
	private String phone;
	//时段
	private String period;
	//会议通知时间
	private String notificationDate;
	//创建单位
	private String createOrg;
	//创建用户姓名
	private String createUserName;
	//参会人员
	private String participants;
	//预警日期
	private String warningDate;
	//最后更新时间
	private String lastUpdateTime;
	
	private String meetingTag;//会议安排标志 add taw 2015-02-06
	
	private String secretary; //秘书 add by chyf 2015-9-24
	private String secretarypid; //秘书id
	private String receivetime; //接收时间 
	private String receivestatus;//接收状态
	private String fromDept; //来文单位名称
	private String instructions; //委领导批示
	private String opinion; //办公室负责人审核意见
	private String remarks; //摘要及拟办意见
	private String joinMeeting; //参会人员
	
	private String jbr;  //经办人
	private String shr;  //审核人
	
	public Meeting(){}
	public Meeting(String oid){
		this.oid=oid;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCompere() {
		return compere;
	}
	public void setCompere(String compere) {
		this.compere = compere;
	}
	public String getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}
	public String getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}
	public String getMeetinfSite() {
		return meetinfSite;
	}
	public void setMeetinfSite(String meetinfSite) {
		this.meetinfSite = meetinfSite;
	}
	public String getApplyDateTime() {
		return applyDateTime;
	}
	public void setApplyDateTime(String applyDateTime) {
		this.applyDateTime = applyDateTime;
	}
	public String getNotificationOrgId() {
		return notificationOrgId;
	}
	public void setNotificationOrgId(String notificationOrgId) {
		this.notificationOrgId = notificationOrgId;
	}
	public String getNotificationWay() {
		return notificationWay;
	}
	public void setNotificationWay(String notificationWay) {
		this.notificationWay = notificationWay;
	}
	public String getCompereManagerOid() {
		return compereManagerOid;
	}
	public void setCompereManagerOid(String compereManagerOid) {
		this.compereManagerOid = compereManagerOid;
	}
	public String getMeetingDemand() {
		return meetingDemand;
	}
	public void setMeetingDemand(String meetingDemand) {
		this.meetingDemand = meetingDemand;
	}
	public Integer getIsShortMsg() {
		return isShortMsg;
	}
	public void setIsShortMsg(Integer isShortMsg) {
		this.isShortMsg = isShortMsg;
	}
	public Integer getIsArrange() {
		return isArrange;
	}
	public void setIsArrange(Integer isArrange) {
		this.isArrange = isArrange;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMeetingName() {
		return meetingName;
	}
	public void setMeetingName(String meetingName) {
		this.meetingName = meetingName;
	}
	public String getShortMsg() {
		return shortMsg;
	}
	public void setShortMsg(String shortMsg) {
		this.shortMsg = shortMsg;
	}
	public Integer getMeetingNature() {
		return meetingNature;
	}
	public void setMeetingNature(Integer meetingNature) {
		this.meetingNature = meetingNature;
	}
	public String getNotificationDate() {
		return notificationDate;
	}
	public void setNotificationDate(String notificationDate) {
		this.notificationDate = notificationDate;
	}
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public String getContacts() {
		return contacts;
	}
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getCreateOrg() {
		return createOrg;
	}
	public void setCreateOrg(String createOrg) {
		this.createOrg = createOrg;
	}
	public String getParticipants() {
		return participants;
	}
	public void setParticipants(String participants) {
		this.participants = participants;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getWarningDate() {
		return warningDate;
	}
	public void setWarningDate(String warningDate) {
		this.warningDate = warningDate;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getMeetingTag() {
		return meetingTag;
	}
	public void setMeetingTag(String meetingTag) {
		this.meetingTag = meetingTag;
	}
	public String getSecretary() {
		return secretary;
	}
	public void setSecretary(String secretary) {
		this.secretary = secretary;
	}
	public String getSecretarypid() {
		return secretarypid;
	}
	public void setSecretarypid(String secretarypid) {
		this.secretarypid = secretarypid;
	}
	public String getReceivetime() {
		return receivetime;
	}
	public void setReceivetime(String receivetime) {
		this.receivetime = receivetime;
	}
	public String getReceivestatus() {
		return receivestatus;
	}
	public void setReceivestatus(String receivestatus) {
		this.receivestatus = receivestatus;
	}
	public String getFromDept() {
		return fromDept;
	}
	public void setFromDept(String fromDept) {
		this.fromDept = fromDept;
	}
	public String getInstructions() {
		return instructions;
	}
	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}
	public String getOpinion() {
		return opinion;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getJoinMeeting() {
		return joinMeeting;
	}
	public void setJoinMeeting(String joinMeeting) {
		this.joinMeeting = joinMeeting;
	}
	public String getJbr() {
		return jbr;
	}
	public void setJbr(String jbr) {
		this.jbr = jbr;
	}
	public String getShr() {
		return shr;
	}
	public void setShr(String shr) {
		this.shr = shr;
	}		
}
