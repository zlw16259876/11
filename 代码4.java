package com.jw.oa.meetingManage.service;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.HessianProxyFactory;
import com.gentlesoft.commons.util.UtilResource;
import com.gentlesoft.persistence.Page;
import com.gentlesoft.persistence.ibatis.IBatisGenericDao;
import com.jw.oa.meetingManage.domain.JwMeetingMsg;
import com.jw.oa.meetingManage.domain.JwPerson;
import com.jw.oa.meetingManage.domain.Meeting;
import com.jw.oa.smsUserTableManage.domain.SmsUserTable;
import com.jw.oa.util.StringHelper;
import com.oa.jw.ws.ISmsService;
import com.sz.common.util.StringUtil;
/**
 * 会议短信业务类
 * @author Administrator
 *
 */
public class JwMeetingMsgService {
	
	/**
	 * 新增会议短信
	 * @param msg
	 * @return
	 */
	public boolean add(JwMeetingMsg msg){
		boolean flag = false;
		if(msg != null){
			if(!StringUtil.isEmpty(msg.getOid())){
				dao.getSqlMapClientTemplate()
					.insert(JwMeetingMsg.class.getName() + ".insertJwMeetingMsg", msg);
				flag = true;
			}
		}
		return flag;
	}
	/**
	 * 更新会议短信
	 * @param msg
	 * @return
	 */
	public boolean update(JwMeetingMsg msg){
		boolean flag = false;
		if(msg != null){
			if(!StringUtil.isEmpty(msg.getOid())){
				dao.getSqlMapClientTemplate()
					.update(JwMeetingMsg.class.getName() + ".updateJwMeetingMsg", msg);
				flag = true;
			}
		}
		return flag;
	}
	/**
	 * 删除会议短信
	 * @param oid
	 * @return
	 */
	public boolean delete(String oid){
		boolean flag = false;
		if(StringUtil.isEmpty(oid)){
			dao.getSqlMapClientTemplate()
				.delete(JwMeetingMsg.class.getName() + ".deleteJwMeetingMsg", oid);
			flag = true;
		}
		return flag;
	}
	/**
	 * 批量删除会议短信
	 * @param oid
	 * @return
	 */
	public boolean delete(String[] oids){
		boolean flag = false;
		if(oids != null){
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("oids", oids);
			dao.getSqlMapClientTemplate()
				.delete(JwMeetingMsg.class.getName() + ".deleteByOids", param);
			flag = true;
		}
		return flag;
	}
	/**
	 * 查询会议短信
	 * @param oid
	 * @return
	 */
	public JwMeetingMsg queryByOid(String oid){
		JwMeetingMsg msg = null;
		if(!StringUtil.isEmpty(oid)){
			msg = (JwMeetingMsg) dao.getSqlMapClientTemplate()
				.queryForObject(JwMeetingMsg.class.getName() + ".queryJwMeetingMsgByOid", oid);
		}
		return msg;
	}
	/**
	 * 查询会议短信明细
	 * @param meetingOid
	 * @return
	 */
	public List<JwMeetingMsg> queryByMeetingOid(String meetingOid){
		List<JwMeetingMsg> list = null;
		if(!StringUtil.isEmpty(meetingOid)){
			list = dao.getSqlMapClientTemplate()
				.queryForList(JwMeetingMsg.class.getName() + ".queryJwMeetingMsgByMeetingOid", meetingOid);
		}
		return list;
	}
	
	/**
	 * 查询会议短信明细
	 * @param meetingOid 会议序列号
	 * @return
	 */
	public List<JwMeetingMsg> queryByMeetingOid(Map<String, Object> param){
		List<JwMeetingMsg> list = null;
		if(param != null){
			list = dao.getSqlMapClientTemplate()
				.queryForList(JwMeetingMsg.class.getName() + ".selectJwMeetingMsgs", param);
		}
		return list;
	}
	/**
	 * 预处理短信信息
	 * @param meetingOid
	 * @return
	 */
	public List<Map<String,Object>> selectMeetingInfo(String meetingOid){
		List<Map<String,Object>> list = null;
		if(!StringUtil.isEmpty(meetingOid)){
			list = dao.getSqlMapClientTemplate()
						.queryForList(JwMeetingMsg.class.getName() + ".selectMeetingInfo", meetingOid);
		}
		return list;
	}
	/**
	 * 分页查询会议短信明细
	 * @param param
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public Page<Map<String, Object>> selectJwMeetingMsg(Map<String,Object> param ,int start ,int pageSize){
		logger.debug("selectJwMeetingMsg {}",param);
		if(pageSize<0||pageSize>200)
			pageSize=Page.DEFAULT_PAGE_SIZE;
		if(start!=-1){
			return dao.queryPage(JwMeetingMsg.class.getName() + ".selectJwMeetingMsg", param, start, pageSize);
		}else{
			List<Map<String,Object>> list =  dao.getSqlMapClientTemplate()
												.queryForList(JwMeetingMsg.class.getName() + ".selectJwMeetingMsg", param);
			if(list != null){
				return new Page<Map<String,Object>>(list,list.size());
			}else{
				return new Page<Map<String,Object>>();
			}
		}
	}
	/**
	 * 获取会议相关机构
	 * @param meetingOid
	 * @return
	 */
	public List<Map<String, Object>> getPartiOrg(String meetingOid){
		List<Map<String, Object>>  list = null;
		if(StringUtil.isEmpty(meetingOid)){
			list = dao.getSqlMapClientTemplate()
						.queryForList(JwMeetingMsg.class.getName() + ".selectOrg", meetingOid );
		}
		return list;
	}
	/**
	 * 获取会议相关机构
	 * @param meetingOid
	 * @return
	 */
	public List<Map<String, Object>> getPartiPerson(String meetingOid){
		List<Map<String, Object>>  list = null;
		if(!StringUtil.isEmpty(meetingOid)){
			list = dao.getSqlMapClientTemplate()
						.queryForList(JwMeetingMsg.class.getName() + ".selectParti", meetingOid );
		}
		return list;
	}
	/**
	 * 获取领导简称信息
	 * @param meetingOid
	 * @return
	 */
	public List<Map<String, Object>> getLeader(){
		List<Map<String, Object>>  list = null;
		list = dao.getSqlMapClientTemplate()
					.queryForList(JwMeetingMsg.class.getName() + ".selectLeader");
		return list;
	}
	
	/**
	 * 获取领导简称信息
	 * @param meetingOid
	 * @return
	 */
	public Map<String, Object> getLeaderNames(){
		Map<String, Object> map = null;
		List<Map<String, Object>>  list = null;
		list = dao.getSqlMapClientTemplate()
					.queryForList(JwMeetingMsg.class.getName() + ".selectLeaderNames");
		if(list != null && list.size() > 0){
			map = new HashMap<String, Object>();
			for (Map<String, Object> param : list) {
				map.put((String) param.get("PERSONNAME"), param.get("REF_NAME"));
			}
		}
		return map;
	}
	/**
	 * 发送短信并更新短信记录
	 * @param list
	 * @return
	 */
	public boolean sendMsg(List<JwMeetingMsg> list){
		boolean flag = false;
		if(list != null && list.size() > 0){
			 HessianProxyFactory factory = new HessianProxyFactory();
		     ISmsService service = null;
		     String bizOid = "";
		     int result = -100;//默认记录结果
		     try {
				service = (ISmsService) factory.create(ISmsService.class, jw_sms_url);
				for (JwMeetingMsg msg : list) {
					bizOid = StringHelper.getUUID32();
					result= service.sendSms(jw_sms_account, jw_sms_pwd, new String[]{msg.getMobile()}, msg.getContent(),bizOid);
					msg.setSendResult(getSendResultStr(result));//设置短信发送返回结果
					if(StringUtil.isEmpty(msg.getOid())){
						msg.setOid(bizOid);
						add(msg);
					}else{
						update(msg);//更新短信信息
					}
					
				}
				flag = true;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return flag;
			}
		}
		return flag;
	}
	/**
	 * 查询处室经办人信息
	 * @param deptIds
	 * @return
	 */
	public List<Map<String, Object>> getContacts(String[] deptIds){
		List<Map<String, Object>> list = null;
		if(deptIds != null){
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("deptIds", deptIds);
			list = dao.getSqlMapClientTemplate().queryForList(JwMeetingMsg.class.getName()+".queryMobileByDeptIds",param);
		}
		return list;
	}
	
	private String getSendResultStr(int result){
		if(result==0){
	       	return "发送成功";
       }else if(1==result){
    		return "号码格式错误";
       	}else if(2==result){
       		return "号码供应商未授权";
       	}else if(-1==result){
       		return "账号是空的";
       	}else if(-2==result){
       		return "密码是空的";
       	}else if(-3==result){
       		return "号码列表错误";
       	}else if(-4==result){
       		return "内容格式错误";
       	}else if(-5==result){
       		return "账号未授权";
       	}else if(-6==result){
       		return "系统故障";
       	}else if(-31==result){
       		return "号码列表格式错误";
       	}else if(-32==result){
       		return "号码列表未经授权";
       	}else if(-33==result){
       		return "业务序列号长度过长";
       	}else if(-51==result){
       		return "账号未开始生效";
       	}else if(-52==result){
       		return "IP未授权";
       	}else if(-53==result){
       		return "账号权限受限";
       	}else if(-55==result){
       		return "账号权限受限";
       	}
       	return "连接短信接口超时";
	}
	public void setDao(IBatisGenericDao dao) {
		this.dao = dao;
	}
	private static String jw_sms_url = UtilResource.getProperty("config/jw","jw.sms.url");
    private static String jw_sms_account = UtilResource.getProperty("config/jw","jw.sms.account");
    private static String jw_sms_pwd = UtilResource.getProperty("config/jw","jw.sms.pwd");
	private static final Logger logger = LoggerFactory.getLogger(MeetingService.class);
	private IBatisGenericDao dao;
}
