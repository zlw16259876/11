package com.jw.oa.meetingmanage.controller;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gentlesoft.commons.util.UtilResource;
import com.gentlesoft.commons.util.UtilValidate;
import com.gentlesoft.dictionary.Dictionary;
import com.gentlesoft.framework.action.BaseControl;
import com.gentlesoft.framework.action.PageUtil;
import com.gentlesoft.organiseManage.domain.Organise;
import com.gentlesoft.organiseManage.proxy.OrganiseFactoryBean;
import com.gentlesoft.organiseManage.proxy.OrganiseServiceProxy;
import com.gentlesoft.organiseManage.proxy.PersonServiceProxy;
import com.gentlesoft.persistence.Page;

import org.springframework.web.servlet.ModelAndView;

import com.jw.oa.meetingManage.domain.JwNextAudit;
import com.jw.oa.meetingManage.domain.JwNextMeeting;
import com.jw.oa.meetingManage.domain.Meeting;
import com.jw.oa.meetingManage.service.JwNextAuditService;
import com.jw.oa.meetingManage.service.JwNextMeetingService;
import com.jw.oa.util.DateHelper;
import com.sz.common.util.DateUtil;
import com.sz.common.util.StringUtil;

@SuppressWarnings("unchecked")
public class JwNextMeetingController extends BaseControl  {
	private static final Logger logger=LoggerFactory.getLogger(JwNextMeetingController.class);
	private JwNextMeetingService jwNextMeetingService;

	private PersonServiceProxy personService=OrganiseFactoryBean.getInstance().getPersonProxy();
	private OrganiseServiceProxy organiseService = OrganiseFactoryBean.getInstance().getOrganiseProxy();
	private Dictionary dic;
	private JwNextAuditService jwNextAuditService;
	public void setJwNextAuditService(JwNextAuditService jwNextAuditService) {
		this.jwNextAuditService = jwNextAuditService;
	}
	public void setDic(Dictionary dic) {
		this.dic = dic;
	}
	public void setJwNextMeetingService(JwNextMeetingService jwNextMeetingService){
		this.jwNextMeetingService = jwNextMeetingService;
	}
	
	/**
	 * 转向列表页面aa
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView  index(HttpServletRequest request,HttpServletResponse response) {
		request.setAttribute("startDate", DateUtil.formatYearMonthDay(DateHelper.getDateOfDay(new Date(), 1)));
		return new ModelAndView("app/jw/meeting/jwnextmeeting/index");
	
	}
	/**
	 * 转向审核列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView  auIndex(HttpServletRequest request,HttpServletResponse response) {
		request.setAttribute("startDate", DateUtil.formatYearMonthDay(DateHelper.getDateOfDay(new Date(), 1)));
		return new ModelAndView("app/jw/meeting/jwnextmeeting/auindex");
		
	}
	/**
	 * 转向已发布列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView  yfbIndex(HttpServletRequest request,HttpServletResponse response) {
		request.setAttribute("startDate", DateUtil.formatYearMonthDay(DateHelper.getDateOfDay(new Date(), 1)));
		return new ModelAndView("app/jw/meeting/jwnextmeeting/yfbindex");
		
	}
	
	
	public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }
	public static Date getThreeDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, +3);//+3今天的时间加3天
		date = calendar.getTime();
		return date;
	}
	
	/**
	 * 多条件查询结果
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException 
	 */
	public ModelAndView  query(HttpServletRequest request,HttpServletResponse response) throws ParseException {
		Map map = new HashMap();
		Date date=new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
		SimpleDateFormat datefm = new SimpleDateFormat("yyyy-MM-dd");
		String week=dateFm.format(date);
		String startstr = request.getParameter("start");
		String beginDate = filterBlank(request.getParameterMap(), request);
		Date date1 = datefm.parse(beginDate);
		String week1 = dateFm.format(date1);
		boolean isSame = isSameDate(date1, getNextDay(date));
		if(isSame){
			if("星期五".equals(week)){
				String d1 = datefm.format(getNextDay(date));
				String d2 = datefm.format(getThreeDay(date));
				map.put("startdatetime1", d1+" 00:00:00");
				map.put("startdatetime2", d2+" 23:59:59");
			}else{
				String d1 = datefm.format(getNextDay(date));
				map.put("startdatetime1", d1+" 00:00:00");
				map.put("startdatetime2", d1+" 23:59:59");
			}
		}else{
			String d1 = datefm.format(getNextDay(date1));
			map.put("startdatetime1", d1+" 00:00:00");
			map.put("startdatetime2", d1+" 23:59:59");
		}
		int start = 0;
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		Page<Map<String,Object>> page = jwNextMeetingService.findByMulitCondition(map,start);
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	
	private static boolean isSameDate(Date date1, Date date2) {
       Calendar cal1 = Calendar.getInstance();
       cal1.setTime(date1);

       Calendar cal2 = Calendar.getInstance();
       cal2.setTime(date2);

       boolean isSameYear = cal1.get(Calendar.YEAR) == cal2
               .get(Calendar.YEAR);
       boolean isSameMonth = isSameYear
               && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
       boolean isSameDate = isSameMonth
               && cal1.get(Calendar.DAY_OF_MONTH) == cal2
                       .get(Calendar.DAY_OF_MONTH);

       return isSameDate;
   }
	
	/**
	 * 多条件查询结果
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException 
	 */
	public ModelAndView  queryYfb(HttpServletRequest request,HttpServletResponse response) throws ParseException {
		Map map = new HashMap();
		Date date=new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
		SimpleDateFormat datefm = new SimpleDateFormat("yyyy-MM-dd");
		String week=dateFm.format(date);
		String startstr = request.getParameter("start");
		String beginDate = filterBlank(request.getParameterMap(), request);
		Date date1 = datefm.parse(beginDate);
		String week1 = dateFm.format(date1);
		boolean isSame = isSameDate(date1, getNextDay(date));
		if(isSame){
			if("星期五".equals(week)){
				String d1 = datefm.format(getNextDay(date));
				String d2 = datefm.format(getThreeDay(date));
				map.put("startdatetime1", d1+" 00:00:00");
				map.put("startdatetime2", d2+" 23:59:59");
			}else{
				String d1 = datefm.format(getNextDay(date));
				map.put("startdatetime1", d1+" 00:00:00");
				map.put("startdatetime2", d1+" 23:59:59");
			}
		}else{
			String d1 = datefm.format(getNextDay(date1));
			map.put("startdatetime1", d1+" 00:00:00");
			map.put("startdatetime2", d1+" 23:59:59");
		}
		map.put("status", "3");
		int start = 0;
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		Page<Map<String,Object>> page = jwNextMeetingService.findByMulitCondition(map,start);
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	
	/**
	 * 多条件查询结果
	 * @param request
	 * @param response
	 * @return
	 * @throws ParseException 
	 */
	public ModelAndView  queryAu(HttpServletRequest request,HttpServletResponse response) throws ParseException {
		Map map = new HashMap();
		Date date=new Date();
		String userId=super.getCuruserPartyId(request);
		map.put("current", userId);
		SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
		SimpleDateFormat datefm = new SimpleDateFormat("yyyy-MM-dd");
		String week=dateFm.format(date);
		String startstr = request.getParameter("start");
		String beginDate = filterBlank(request.getParameterMap(), request);
		Date date1 = datefm.parse(beginDate);
		String week1 = dateFm.format(date1);
		boolean isSame = isSameDate(date1, getNextDay(date));
		if(isSame){
			if("星期五".equals(week)){
				String d1 = datefm.format(getNextDay(date));
				String d2 = datefm.format(getThreeDay(date));
				map.put("startdatetime1", d1+" 00:00:00");
				map.put("startdatetime2", d2+" 23:59:59");
			}else{
				String d1 = datefm.format(getNextDay(date));
				map.put("startdatetime1", d1+" 00:00:00");
				map.put("startdatetime2", d1+" 23:59:59");
			}
		}else{
			String d1 = datefm.format(getNextDay(date1));
			map.put("startdatetime1", d1+" 00:00:00");
			map.put("startdatetime2", d1+" 23:59:59");
		}
		map.put("status", "1");
		int start = 0;
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		Page<Map<String,Object>> page = jwNextMeetingService.findByMulitCondition1(map,start);
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	
	/**
	 * 组装查询请求
	 * @param map 页面请求集合
	 * @param request
	 * @return
	 */
	private String filterBlank(Map<String,Object> map,HttpServletRequest request){
		Map<String,Object> returnMap=new HashMap<String,Object>();
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String[] array=(String[]) map.get(key);
			if(UtilValidate.isNotEmpty(array[0]))
				returnMap.put(key, array[0]);
		}
		String endDate="";
		endDate = (String) returnMap.get("sourceDate");
		if(UtilValidate.isEmpty(endDate))
			endDate = (String) returnMap.get("beginDate");
		return endDate;
	}	
	/**
	 * 提交审批
	 * @param au 审批人id
	 * @param response
	 * @return
	 */
	public ModelAndView  audit(HttpServletRequest request,HttpServletResponse response) {
		String reMsg = "";
		String userId=super.getCuruserPartyId(request);
		String name=getHandleUserLogin().getUserName(request);
		Map map = new HashMap();
		Date date=new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
		SimpleDateFormat datefm = new SimpleDateFormat("yyyy-MM-dd");
		String week=dateFm.format(date);
		String startstr = request.getParameter("start");
		if("星期五".equals(week)){
			String d1 = datefm.format(getNextDay(date));
			String d2 = datefm.format(getThreeDay(date));
			map.put("startdatetime1", d1+" 00:00:00");
			map.put("startdatetime2", d2+" 23:59:59");
		}else{
			String d1 = datefm.format(getNextDay(date));
			map.put("startdatetime1", d1+" 00:00:00");
			map.put("startdatetime2", d1+" 23:59:59");
		}
		map.put("isdel", "0");
		List<JwNextMeeting> list = jwNextMeetingService.findNext(map);
		String au = request.getParameter("au");
		if(UtilValidate.isNotEmpty(au))
			au = au.split(",")[0];
		try{
			if(list.size() != 0){
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					JwNextMeeting jwNextMeeting = (JwNextMeeting) iterator.next();
					jwNextMeeting.setStatus("1");
					jwNextMeetingService.update(jwNextMeeting);
					Map param = new HashMap();
					param.put("nmid", jwNextMeeting.getOid());
					param.put("currentauditid", userId);
					param.put("status", "open");
					JwNextAudit jwNextAudit = jwNextAuditService.selectAudit(param);
					jwNextAudit.setStatus("colsed");
					jwNextAuditService.update(jwNextAudit);
					JwNextAudit jwNextAudit2 = new JwNextAudit();
					jwNextAudit2.setOid(UUID.randomUUID().toString());
					jwNextAudit2.setNmid(jwNextMeeting.getOid());
					jwNextAudit2.setCurrentauditid(au);
					jwNextAudit2.setLastauditid(userId);
					jwNextAudit2.setLastauditname(name);
					jwNextAudit2.setStatus("open");
					jwNextAuditService.insert(jwNextAudit2);
				}
				reMsg = "提交成功!";
			}
		}catch(Exception e){
			e.printStackTrace();
			reMsg = "提交失败!";
		}	
		super.setReturnMsg(request, reMsg);
		return index(request, response);
	}
	/**
	 * 退回
	 * @param 
	 * @param response
	 * @return
	 */
	public ModelAndView  getBack(HttpServletRequest request,HttpServletResponse response) {
		String reMsg = "";
		String userId=super.getCuruserPartyId(request);
		String name=getHandleUserLogin().getUserName(request);
		String bgsId=UtilResource.getProperty("config/jw","jw.bgs");
		String bgsName=UtilResource.getProperty("config/jw","jw.bgs.name");
		Map map = new HashMap();
		Date date=new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
		SimpleDateFormat datefm = new SimpleDateFormat("yyyy-MM-dd");
		String week=dateFm.format(date);
		String startstr = request.getParameter("start");
		if("星期五".equals(week)){
			String d1 = datefm.format(getNextDay(date));
			String d2 = datefm.format(getThreeDay(date));
			map.put("startdatetime1", d1+" 00:00:00");
			map.put("startdatetime2", d2+" 23:59:59");
		}else{
			String d1 = datefm.format(getNextDay(date));
			map.put("startdatetime1", d1+" 00:00:00");
			map.put("startdatetime2", d1+" 23:59:59");
		}
		map.put("status", "1");
		List<JwNextMeeting> list = jwNextMeetingService.findNext(map);
		try{
			if(list.size() != 0){
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					JwNextMeeting jwNextMeeting = (JwNextMeeting) iterator.next();
					jwNextMeeting.setStatus("0");
					jwNextMeetingService.update(jwNextMeeting);
					Map param = new HashMap();
					param.put("nmid", jwNextMeeting.getOid());
					param.put("currentauditid", userId);
					param.put("status", "open");
					JwNextAudit jwNextAudit = jwNextAuditService.selectAudit(param);
					jwNextAudit.setStatus("colsed");
					jwNextAuditService.update(jwNextAudit);
					JwNextAudit jwNextAudit2 = new JwNextAudit();
					jwNextAudit2.setOid(UUID.randomUUID().toString());
					jwNextAudit2.setNmid(jwNextMeeting.getOid());
					jwNextAudit2.setCurrentauditid(bgsId);
					jwNextAudit2.setCurrentauditname(bgsName);
					jwNextAudit2.setLastauditid(userId);
					jwNextAudit2.setLastauditname(name);
					jwNextAudit2.setStatus("open");
					jwNextAuditService.insert(jwNextAudit2);
					reMsg = "退回成功！";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			reMsg = "退回失败！";
		}	
		super.setReturnMsg(request, reMsg);
		return auIndex(request, response);
	}
	/**
	 * 发布
	 * @param 
	 * @param response
	 * @return
	 */
	public ModelAndView  fabu(HttpServletRequest request,HttpServletResponse response) {
		String userId=super.getCuruserPartyId(request);
		String name=getHandleUserLogin().getUserName(request);
		Map map = new HashMap();
		Date date=new Date();
		SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");
		SimpleDateFormat datefm = new SimpleDateFormat("yyyy-MM-dd");
		String week=dateFm.format(date);
		String startstr = request.getParameter("start");
		if("星期五".equals(week)){
			String d1 = datefm.format(getNextDay(date));
			String d2 = datefm.format(getThreeDay(date));
			map.put("startdatetime1", d1+" 00:00:00");
			map.put("startdatetime2", d2+" 23:59:59");
		}else{
			String d1 = datefm.format(getNextDay(date));
			map.put("startdatetime1", d1+" 00:00:00");
			map.put("startdatetime2", d1+" 23:59:59");
		}
		map.put("status", "1");
		List<JwNextMeeting> list = jwNextMeetingService.findNext(map);
		try{
			if(list.size() != 0){
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					JwNextMeeting jwNextMeeting = (JwNextMeeting) iterator.next();
					jwNextMeeting.setStatus("3");
					jwNextMeetingService.update(jwNextMeeting);
					Map param = new HashMap();
					param.put("nmid", jwNextMeeting.getOid());
					param.put("currentauditid", userId);
					param.put("status", "open");
					JwNextAudit jwNextAudit = jwNextAuditService.selectAudit(param);
					jwNextAudit.setStatus("colsed");
					jwNextAuditService.update(jwNextAudit);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		return index(request, response);
	}
	/**
	 * 转向添加页
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView  add(HttpServletRequest request,HttpServletResponse response) {

		//添加页面
		request.setAttribute("zcld", dic.getDictList("hyzcld"));
		return new ModelAndView("app/jw/meeting/jwnextmeeting/add");
	}

	/**
	 * 添加信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView  save(HttpServletRequest request,HttpServletResponse response) {
		boolean flag = false;
		try{
			String userId=super.getCuruserPartyId(request);
			String name=getHandleUserLogin().getUserName(request);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String bgsId=UtilResource.getProperty("config/jw","jw.bgs");
			String bgsName=UtilResource.getProperty("config/jw","jw.bgs.name");
			JwNextMeeting jwNextMeeting = setNextMeeting(request);
			jwNextMeeting.setAddtime(format.format(new Date()));
			jwNextMeeting.setAdduserid(userId);
			jwNextMeeting.setAddusername(name);
			jwNextMeetingService.insert(jwNextMeeting);
			//新增环节
			JwNextAudit jwNextAudit = new JwNextAudit();
			jwNextAudit.setOid(UUID.randomUUID().toString());
			jwNextAudit.setCurrentauditid(userId);
			jwNextAudit.setCurrentauditname(name);
			jwNextAudit.setStatus("closed");
			jwNextAudit.setAudittime(format.format(new Date()));
			jwNextAudit.setNmid(jwNextMeeting.getOid());
			jwNextAuditService.insert(jwNextAudit);
			//值班室环节
			JwNextAudit jwNextAudit2 = new JwNextAudit();
			jwNextAudit2.setOid(UUID.randomUUID().toString());
			jwNextAudit2.setNmid(jwNextMeeting.getOid());
			jwNextAudit2.setCurrentauditid(bgsId);
			jwNextAudit2.setCurrentauditname(bgsName);
			jwNextAudit2.setLastauditid(userId);
			jwNextAudit2.setLastauditname(name);
			jwNextAudit2.setStatus("open");
			jwNextAuditService.insert(jwNextAudit2);
			flag = true;
		}catch(Exception e){
			e.printStackTrace();
			
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	
	/**
	 * 自动组装隔天委外会议请求数据
	 * @param request
	 * @return
	 */
	private JwNextMeeting setNextMeeting(HttpServletRequest request){
		JwNextMeeting jwNextMeeting = new JwNextMeeting();
		jwNextMeeting.setOid(UUID.randomUUID().toString());
		jwNextMeeting.setMeetingname(valueOf(request.getParameter("meetingName")));
		jwNextMeeting.setMeetinfsite(valueOf(request.getParameter("meetinfSite")));
		jwNextMeeting.setCompere(valueOf(request.getParameter("compere")));
		jwNextMeeting.setComperemanageroid(valueOf(request.getParameter("compereManagerOid")));
		jwNextMeeting.setStartdatetime(valueOf(request.getParameter("startDateTime")));
		jwNextMeeting.setEnddatetime(valueOf(request.getParameter("endDateTime")));
		jwNextMeeting.setParticipants(valueOf(request.getParameter("participants")));
		jwNextMeeting.setIsdel("0");//默认没有删除
		jwNextMeeting.setStatus("0");//默认值班室
		return jwNextMeeting;
	}
	/**
	 * 转向编辑页
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView  edit(HttpServletRequest request,HttpServletResponse response) {
		String oid=request.getParameter("oid");
		String returnUrl = request.getParameter("returnUrl");
		if(UtilValidate.isNotEmpty(returnUrl))
			request.setAttribute("returnUrl", returnUrl);
		if(UtilValidate.isNotEmpty(oid)){
			JwNextMeeting jwNextMeeting = jwNextMeetingService.editById(oid);
			request.setAttribute("obj", jwNextMeeting);
			request.setAttribute("zcld", dic.getDictList("hyzcld"));
			//编辑页面
			return new ModelAndView("app/jw/meeting/jwnextmeeting/edit");
		}
		//如果为空跳转到添加页面  是否提示未找到
		return add(request,response);
	}
	
	/**
	 * 更新信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView  update(HttpServletRequest request,HttpServletResponse response) {
		boolean flag = false;
		String returnUrl = request.getParameter("returnUrl");
		if(UtilValidate.isNotEmpty(returnUrl))
			request.setAttribute("returnUrl", returnUrl);
		String oid = request.getParameter("oid");
		JwNextMeeting jwNextMeeting = new JwNextMeeting();
		if(UtilValidate.isNotEmpty(oid))
			jwNextMeeting = jwNextMeetingService.editById(oid);
		jwNextMeeting.setMeetingname(valueOf(request.getParameter("meetingName")));
		jwNextMeeting.setMeetinfsite(valueOf(request.getParameter("meetinfSite")));
		jwNextMeeting.setCompere(valueOf(request.getParameter("compere")));
		jwNextMeeting.setComperemanageroid(valueOf(request.getParameter("compereManagerOid")));
		jwNextMeeting.setStartdatetime(valueOf(request.getParameter("startDateTime")));
		jwNextMeeting.setEnddatetime(valueOf(request.getParameter("endDateTime")));
		jwNextMeeting.setParticipants(valueOf(request.getParameter("participants")));
		try {
			jwNextMeetingService.update(jwNextMeeting);
			flag = true;
		} catch (Exception e) {
			flag = false;
			// TODO: handle exception
			e.printStackTrace();
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}

	
	/**
	 * 删除
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView  delete(HttpServletRequest request,HttpServletResponse response){
		boolean flag = false;
		String ids=request.getParameter("ids");
		String[] id = new String[0];
		if(UtilValidate.isNotEmpty(ids)) {
			try {
				id=ids.split(",");
				for (int i = 0; i < id.length; i++) {
					JwNextMeeting jwNextMeeting = jwNextMeetingService.editById(id[i]);
					jwNextMeeting.setIsdel("1");
					jwNextMeetingService.update(jwNextMeeting);
				}
			} catch (Exception e) {
				flag = true;
				logger.error("删除信息出错");
				e.printStackTrace();
			}
		}
		String success="{\n info:'操作成功!!',  success: true }";
		if(flag) {
			success = "{\n info:'操作失败!!', success: false}";
		}	
		super.outJson(success,response);
		return null;
	}
	
	/**
	 * Ajax提交，多记录删除
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView  removes (HttpServletRequest request,HttpServletResponse response) {
		String ids=request.getParameter("ids");
		boolean flag = false;
		if(UtilValidate.isNotEmpty(ids)){
			try {
				String[] id=ids.split(",");
				
			} catch (Exception e) {
				logger.error("删除出错");
				e.printStackTrace();
				flag = true;
			}
			
		}
		
		String success="{\n info:'操作成功!!',  success: true }";
		if(flag) {
			success = "{\n info:'操作失败!!', success: false}";
		}
		super.outJson(success,response);
		return null;
	}
	
	
	/**
	 * 明细表  重定向 /detail.jsp文件
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView  detail(HttpServletRequest request,HttpServletResponse response) {
		String oid=request.getParameter("oid");
		if(UtilValidate.isNotEmpty(oid)){
			JwNextMeeting jwNextMeeting = jwNextMeetingService.editById(oid);
			request.setAttribute("obj", jwNextMeeting);
			//编辑页面
			return new ModelAndView("app/jw/meeting/jwnextmeeting/detail");
		}
		return null;
	}
	
	/**
	 * 字符串去空处理方法
	 * @param str
	 * @return
	 */
	private String valueOf(String str){
		if(StringUtil.isEmpty(str)){
			str = "";
		}
		return str;
	}
}

package com.jw.oa.meetingmanage.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.gentlesoft.commons.util.UtilValidate;
import com.gentlesoft.commons.util.request.Files;
import com.gentlesoft.commons.util.request.MRequest;
import com.gentlesoft.commons.util.request.UpFile;
import com.gentlesoft.dictionary.Dictionary;
import com.gentlesoft.framework.action.BaseControl;
import com.gentlesoft.framework.action.PageUtil;
import com.gentlesoft.organiseManage.domain.Organise;
import com.gentlesoft.organiseManage.proxy.OrganiseFactoryBean;
import com.gentlesoft.organiseManage.proxy.PersonServiceProxy;
import com.gentlesoft.persistence.Page;
import com.jw.oa.meetingManage.domain.Meeting;
import com.jw.oa.meetingManage.domain.Participant;
import com.jw.oa.meetingManage.service.JwApplyService;
import com.jw.oa.meetingManage.service.MeetingService;
import com.soft.oa.common.Constants;
import com.soft.oa.meeting.domain.Meetingfile;
import com.sz.common.util.DateUtil;
import com.sz.common.util.StringUtil;

/**
 * 会议管理
 * @author hsk
 *
 */
public class MeetingController extends BaseControl {
	//会议业务处理层
	private MeetingService meetingService;
	
	private Dictionary dic;
	
	private PersonServiceProxy personService=OrganiseFactoryBean.getInstance().getPersonProxy();
	private JwApplyService jwApplyService;
	
	public ModelAndView reply(HttpServletRequest request,HttpServletResponse response){
		Organise org = getLoginUserOrg(request);
		request.setAttribute("list", jwApplyService.getOfficer(org.getPartyid()));
		return new ModelAndView("");
	}
	
	/**
	 * 获取当前登录用户所在的单位
	 * @param request
	 * @return
	 */
	private Organise getLoginUserOrg(HttpServletRequest request){
		Organise org = null;
		String partyId = getHandleUserLogin().getPartyId(request);
		if(!StringUtil.isEmpty(partyId)){
			org = personService.getDefaultOrgan(partyId);
		}
		return org;
	}
	/**
	 * 组装查询请求
	 * @param map 页面请求集合
	 * @param request
	 * @return
	 */
	private Map<String,Object> filterBlank(Map<String,Object> map,HttpServletRequest request){
		Map<String,Object> returnMap=new HashMap<String,Object>();
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			
			String[] array=(String[]) map.get(key);
			if(UtilValidate.isNotEmpty(array[0]))
				returnMap.put(key, array[0]);
		}
		//获取订制请求标识
		String flag = request.getParameter("flag");
		returnMap.put("flag", flag);
		return returnMap;
	}
	
	
	public void setMeetingService(MeetingService meetingService) {
		this.meetingService = meetingService;
	}
	

	public void setJwApplyService(JwApplyService jwApplyService) {
		this.jwApplyService = jwApplyService;
	}


	public void setDic(Dictionary dic) {
		this.dic = dic;
	}
	
	
}
package com.jw.oa.meetingmanage.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;

import com.gentlesoft.commons.util.UtilValidate;
import com.gentlesoft.framework.action.BaseControl;
import com.gentlesoft.framework.action.PageUtil;
import com.gentlesoft.persistence.Page;
import com.jw.oa.contact.domain.ContactOrgan;
import com.jw.oa.contact.domain.ContactPerson;
import com.jw.oa.contact.service.ContactService;
import com.jw.oa.meetingManage.domain.JwMeetingMsg;

import com.jw.oa.meetingManage.domain.Meeting;
import com.jw.oa.meetingManage.domain.MeetingComm;
import com.jw.oa.meetingManage.service.JwMeetingMsgService;
import com.jw.oa.meetingManage.service.MeetingService;
import com.jw.oa.util.JwComm;
import com.jw.oa.util.StringHelper;
import com.sz.common.util.DateUtil;
import com.sz.common.util.StringUtil;

public class MeetingMsgController extends BaseControl {
	/**
	 * 跳转--新增（人员）会议短信
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toAddPage(HttpServletRequest request,HttpServletResponse response){
		String meetingOid = request.getParameter("meetingOid");
		String url = "";
		if(!StringUtil.isEmpty(meetingOid)){
			Meeting meet = meetingService.queryByOid(meetingOid);
			JwMeetingMsg msg = null;
			if(meet != null){
				msg = new JwMeetingMsg();
				StringBuffer sb = new StringBuffer();
				sb.append(meet.getMeetingName()).append("于").append(meet.getStartDateTime()).append(meet.getPeriod())
					.append("在").append(meet.getMeetinfSite()).append("召开。");
				if(!StringUtil.isEmpty(meet.getContacts()) && !StringUtil.isEmpty(meet.getPhone())){
					sb.append("会议联系人:").append(meet.getContacts()).append("，联系电话:").append(meet.getPhone()).append("。");
				}
					
				msg.setContent(sb.toString());
			}
			request.setAttribute("obj", msg);
		}
		request.setAttribute("meetingOid", meetingOid);
		return new ModelAndView("app/jw/meeting/msg/add");
	}
	/**
	 * 跳转--会议短信编辑页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toEditPage(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		String url = "";
		JwMeetingMsg msg = null;
		if(!StringUtil.isEmpty(oid)){
			msg = jwMeetingMsgService.queryByOid(oid);
			request.setAttribute("obj", msg);
		}
//		setRuturnUrl(request);
		return new ModelAndView("app/jw/meeting/msg/edit");
	}
	
	public ModelAndView toChoosePage(HttpServletRequest request,HttpServletResponse response){
		String meetingOid = request.getParameter("meetingOid");
		if(!StringUtil.isEmpty(meetingOid)){
			Meeting meet = meetingService.queryByOid(meetingOid);
			JwMeetingMsg msg = null;
			if(meet != null){
				msg = new JwMeetingMsg();
				StringBuffer sb = new StringBuffer();
				sb.append(meet.getMeetingName()).append("于").append(meet.getStartDateTime()).append(meet.getPeriod())
					.append("在").append(meet.getMeetinfSite()).append("召开。");
				if(!StringUtil.isEmpty(meet.getContacts()) && !StringUtil.isEmpty(meet.getPhone())){
					sb.append("会议联系人:").append(meet.getContacts()).append("，联系电话:").append(meet.getPhone()).append("。");
				}
					
				msg.setContent(sb.toString());
			}
			request.setAttribute("obj", msg);
			request.setAttribute("personList", jwMeetingMsgService.getPartiPerson(meetingOid));
		}
		request.setAttribute("meetingOid", meetingOid);
		return new ModelAndView("app/jw/meeting/msg/choose");
	}
	/**
	 * 操作--会议短信保存并发送
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView chooseAndSend(HttpServletRequest request,HttpServletResponse response){
		String objs = request.getParameter("objs");
		String content = request.getParameter("content");
		String meetingOid = request.getParameter("meetingOid");
		boolean flag = false;
		if(!StringUtil.isEmpty(objs) && !StringUtil.isEmpty(content) && !StringUtil.isEmpty(meetingOid)){
			Map<String, Object> leaderMap = getLeader();
			List<JwMeetingMsg> msgs = new ArrayList<JwMeetingMsg>();
			JwMeetingMsg meetingMsg = null;
			String[] arr = objs.split(",");
			String[] array = null;
			String name = "";
			for (String str : arr) {
				meetingMsg = new JwMeetingMsg();
				array = str.split(":");
				meetingMsg.setMeetingOid(meetingOid);
				meetingMsg.setMobile(array[1]);
				meetingMsg.setRecipients(array[2]);
				name = (String) leaderMap.get(array[0]);
				if(StringUtil.isEmpty(name)){
					meetingMsg.setContent(content);
				}else{
					meetingMsg.setContent(name + "：" + content);
				}
				meetingMsg.setSendTime(DateUtil.getCurrentDateTime());
				meetingMsg.setSendUserId(super.getCuruserLoginId(request));
				meetingMsg.setSendUserName(super.getCuruserUserName(request));
				msgs.add(meetingMsg);
			}
			flag  = jwMeetingMsgService.sendMsg(msgs);
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	
	private Map<String, Object> getLeader(){
		Map<String, Object> map = null;
		List<Map<String, Object>> list = jwMeetingMsgService.getLeader();
		if(list != null && list.size() > 0){
			map = new HashMap<String, Object>();
			for (Map<String, Object> param : list) {
				map.put((String) param.get("OID"), param.get("REF_NAME"));
			}
		}
		return map;
	}
	/**
	 * 跳转--会议短信编辑页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toDetailPage(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		String url = "";
		JwMeetingMsg msg = null;
		if(!StringUtil.isEmpty(oid)){
			msg = jwMeetingMsgService.queryByOid(oid);
			request.setAttribute("obj", msg);
		}
//		setRuturnUrl(request);
		return new ModelAndView("app/jw/meeting/msg/detail");
	}
	/**
	 * 操作--会议短信保存
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView save(HttpServletRequest request,HttpServletResponse response){
		JwMeetingMsg meetingMsg = setRequestObj(request);
		boolean flag = false;
		if(meetingMsg != null){
			if(StringUtil.isEmpty(meetingMsg.getOid())){
				meetingMsg.setOid(StringHelper.getUUID32());
				flag = jwMeetingMsgService.add(meetingMsg);
			}else{
				flag = jwMeetingMsgService.update(meetingMsg);
			}
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	/**
	 * 操作--会议短信保存并发送
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveAndSend(HttpServletRequest request,HttpServletResponse response){
		List<JwMeetingMsg> list = getRequestList(request);
		boolean flag = jwMeetingMsgService.sendMsg(list);
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	private List<JwMeetingMsg> getRequestList(HttpServletRequest request){
		List<JwMeetingMsg> list = null;
		String recipients = StringHelper.ToString(request.getParameter("recipients"));
		String content = StringHelper.ToString(request.getParameter("content"));
		if(!StringUtil.isEmpty(recipients) && !StringUtil.isEmpty(content)){
			String[] recip = recipients.split(",");
			String date = DateUtil.getCurrentDateTime();
			String userId = super.getCuruserLoginId(request);
			String userName = super.getCuruserUserName(request);
			list = new ArrayList<JwMeetingMsg>();
			JwMeetingMsg msg = null;
			Map<String, Object> map = jwMeetingMsgService.getLeaderNames();
			String message = "";
			for (String str : recip) {
				if(!StringUtil.isEmpty(str)){
					String[] array = str.split(":");
					msg = new JwMeetingMsg();
					if(array.length >= 2){
						if(map.containsKey(array[0])){
							message = map.get(array[0]) + "：" + content;
						}else{
							message = content;
						}
						msg.setContent(message);
						msg.setOid("");
						msg.setRecipients(array[0]);
						msg.setMeetingOid(StringHelper.ToString(request.getParameter("meetingOid")));
						msg.setMobile(array[1]);
						msg.setSendTime(date);
						msg.setSendUserId(userId);
						msg.setSendUserName(userName);
						list.add(msg);
					}
				}
			}
		}
		return list;
	}
	/**
	 * 操作--批量发送会议短信
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView sendMsgs(HttpServletRequest request,HttpServletResponse response){
		String oids = request.getParameter("oids");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("oids", oids.split(","));
		List<JwMeetingMsg> list = jwMeetingMsgService.queryByMeetingOid(param);
		for (JwMeetingMsg meetingMsg : list) {
			meetingMsg.setSendTime(DateUtil.getCurrentDateTime());
			meetingMsg.setSendUserId(super.getCuruserLoginId(request));
			meetingMsg.setSendUserName(super.getCuruserUserName(request));
		}
		boolean flag = jwMeetingMsgService.sendMsg(list);
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	/**
	 * 操作--删除未发送成功的会议短信
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView del(HttpServletRequest request,HttpServletResponse response){
		String oids = request.getParameter("oids");
		boolean flag = false;
		if(!StringUtil.isEmpty(oids)){
			String[] array = oids.split(",");
			flag = jwMeetingMsgService.delete(array);
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	/**
	 * 跳转--短信发送列表
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView index(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
//		precompiled(oid);
		request.setAttribute("meetingOid", oid);
//		HttpSession session = request.getSession();
//		session.setAttribute("meetingOid", oid);
		return new ModelAndView("app/jw/meeting/msg/index");
	}
	/**
	 * 跳转--会议短信历史记录查询
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView history(HttpServletRequest request,HttpServletResponse response){
		request.setAttribute("sendResult", "发送成功");
		request.setAttribute("meetingOid",request.getParameter("oid"));
		return new ModelAndView("app/jw/meeting/msg/history");
	}
	/**
	 * 加载会议短信明细
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView query(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;

		Page<Map<String,Object>> page = jwMeetingMsgService.selectJwMeetingMsg(map, start, pageSize);
		
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	/**
	 * 预编译短信
	 * @param meetingOid
	 */
	private void precompiled(String meetingOid){
		if(StringUtil.isEmpty(meetingOid)) return;
		List<JwMeetingMsg> list = jwMeetingMsgService.queryByMeetingOid(meetingOid);
		if(list == null || list.size() == 0){
			List<Map<String, Object>>  lis = jwMeetingMsgService.selectMeetingInfo(meetingOid);
			if(lis != null){
				list = new ArrayList<JwMeetingMsg>();
				JwMeetingMsg msg = null;
				StringBuffer sb = null;
				for (Map<String, Object> map : lis) {
					sb = new StringBuffer();
					msg = new JwMeetingMsg();
					msg.setOid(StringHelper.getUUID32());
					msg.setRecipients((String) map.get("PERSONNAME"));
					msg.setMeetingOid(meetingOid);
					msg.setMobile((String) map.get("MOBILE"));
					if(map.get("REF_NAME") != null){
						sb.append(map.get("REF_NAME")).append("：");
					}
					sb.append(map.get("MEETINGNAME")).append("于")
						.append(map.get("STARTDATETIME")).append(map.get("PERIOD")).append("在").append(map.get("MEETINFSITE"))
						.append("召开。");
					if(map.get("CONTACTS") != null &&  map.get("PHONE") != null){
						sb.append("会议联系人:").append(map.get("CONTACTS")).append("，联系电话:").append(map.get("PHONE")).append("。");
					}
					msg.setContent(sb.toString());
					list.add(msg);
				}
				for (JwMeetingMsg meetingMsg : list) {
					jwMeetingMsgService.add(meetingMsg);
				}
			}
		}
	}
	/**
	 * 组装查询请求
	 * @param map 页面请求集合
	 * @param request
	 * @return
	 */
	private Map<String,Object> filterBlank(Map<String,Object> map,HttpServletRequest request){
		Map<String,Object> returnMap=new HashMap<String,Object>();
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String[] array=(String[]) map.get(key);
			if(UtilValidate.isNotEmpty(array[0]))
				returnMap.put(key, array[0]);
		}
		String endDate = (String) returnMap.get("endDate");
		if(!StringUtil.isEmpty(endDate)){
			endDate = endDate + " 23:59:59";
			returnMap.put("endDate", endDate);
		}
		return returnMap;
	}
	/**
	 * 自动封装请求对象数据
	 * @param request
	 * @param response
	 * @return
	 */
	private JwMeetingMsg setRequestObj(HttpServletRequest request){
		JwMeetingMsg msg = new JwMeetingMsg();
		msg.setOid(StringHelper.ToString(request.getParameter("oid")));
		msg.setRecipients(StringHelper.ToString(request.getParameter("recipients")));
		msg.setMeetingOid(StringHelper.ToString(request.getParameter("meetingOid")));
		msg.setMobile(StringHelper.ToString(request.getParameter("mobile")));
		msg.setContent(StringHelper.ToString(request.getParameter("content")));
		return msg;
	}
	/**
	 * 加载公共通讯录树
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getCommonTree(HttpServletRequest request, HttpServletResponse response){
		//获取上一级组织机构编码
		String parentId = request.getParameter("parentId");
		parentId = UtilValidate.isEmpty(parentId) ? "root" : parentId;
		
		StringBuilder sb = ReStrBuilder(parentId);
		outJson(sb.toString(), response);
		return null;
	}
	/**
	 * 加载个人通讯录树
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getPersonalTree(HttpServletRequest request, HttpServletResponse response){
		//获取上一级组织机构编码
		String parentId = request.getParameter("fatherId");
		StringBuilder sb = new StringBuilder("");
		if(!StringUtil.isEmpty(parentId)){
			parentId = "root";
//			String personId = super.getCuruserLoginId(request);
			String personId = super.getCuruserPartyId(request);
//			String personId = "6d0e6d54-fe43-488c-b2a4-c6a8f9e64598";
			List<Map<String, Object>> list = contactService.selectPersonal(personId);
			if(list != null && list.size() > 0){
				Map<String, Object> objMap = null;
				sb.append("[");
				for (Iterator iterator = list.iterator(); iterator.hasNext(); ){
					objMap = (Map<String, Object>) iterator.next();
					if(objMap != null && objMap.get("ID")!=null){
						sb.append("{")
						.append("id:'").append(parentId).append('-')
			            .append(objMap.get("ID")).append("',")
			            .append("value:'").append(objMap.get("ID"))
			            .append("',")
						.append("text:'").append(objMap.get("NAME")).append("',");
						sb.append("leaf:true,").append("href:'").append(objMap.get("MOBILE")).append("'");
			        }
					sb.append("}");
			        if (iterator.hasNext())
			          sb.append(",");
				}
			    sb.append("]");
			}
		}
		outJson(sb.toString(), response);
		return null;
	}
	/**
	 * 构造公共通讯录树
	 * @return
	 */
	public StringBuilder ReStrBuilder(String parentId){
		Map param = new HashMap();		//查询条件
		param.put("parentid", parentId);		
		//根据partyid作为下一级的parentid查询数据
		List<ContactOrgan> list = contactService.findAllSon(param);
		StringBuilder sb = new StringBuilder("[");
		if (list != null && !list.isEmpty()){
			//下一级组织
			for (Iterator iterator = list.iterator(); iterator.hasNext(); ){
				ContactOrgan co = (ContactOrgan)iterator.next();
				if(co != null && co.getPartyid()!=null){
					sb.append("{")
					.append("id:'").append(parentId).append('-')
		            .append(co.getPartyid()).append("',")
		            .append("value:'").append(co.getPartyid())
		            .append("',")
					.append("text:'").append(co.getName()).append("',");
					param.put("parentid", co.getPartyid());		
					List<ContactOrgan> organs = contactService.findAllSon(param);
					//无下级节点 
					if ((organs == null) || (organs.isEmpty())) {
						sb.append("leaf:false,");
						sb.append("href:'#")
			              .append("'");
					}else {
						//第一级节点
			            if("root".equals(co.getParentid())){
				            sb.append("leaf:false");
			            }else{
				            sb.append("leaf:false,");
				            sb.append("href:'#");
				            sb.append("")
				              .append("'");
			            }  
					}					
		        }
				sb.append("}");
		        if (iterator.hasNext())
		          sb.append(",");
			}
		    sb.append("]");				
		}else{
			Map map = new HashMap();		//查询条件
			map.put("partyid", parentId);//partyid		
			map.put("statusid", "0");	
			List<Map<String, Object>> personList = contactService.findPersonByOrgId(map);
			Map<String, Object> objMap = null;
			if(personList != null && personList.size() > 0){
				for (Iterator iterator = personList.iterator(); iterator.hasNext(); ){
					objMap = (Map<String, Object>) iterator.next();
					if(objMap != null && objMap.get("PARTYID")!=null){
						sb.append("{")
						.append("id:'").append(parentId).append('-')
			            .append(objMap.get("PARTYID")).append("',")
			            .append("value:'").append(objMap.get("PARTYID"))
			            .append("',")
						.append("text:'").append(objMap.get("NAME")).append("',");
						sb.append("leaf:true,").append("href:'").append(objMap.get("MOBILEPHONE")).append("'");
			        }
					sb.append("}");
			        if (iterator.hasNext())
			          sb.append(",");
				}
			    sb.append("]");
			}
		}		
		return sb;
	}
	/**
	 * 自动设置返回URL
	 * @param request
	 */
	private void setRuturnUrl(HttpServletRequest request){
		String url = request.getHeader("Referer");
		url = url.substring(url.lastIndexOf("/")+1);
		request.setAttribute("returnUrl", url);
	}
	
	public void setJwMeetingMsgService(JwMeetingMsgService jwMeetingMsgService) {
		this.jwMeetingMsgService = jwMeetingMsgService;
	}
	
	public void setMeetingService(MeetingService meetingService) {
		this.meetingService = meetingService;
	}
	
	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	private ContactService contactService;
	//会议短信接口
	private JwMeetingMsgService jwMeetingMsgService;
	private MeetingService meetingService;
}
package com.jw.oa.meetingmanage.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.gentlesoft.commons.util.UtilResource;
import com.gentlesoft.commons.util.UtilValidate;
import com.gentlesoft.framework.action.BaseControl;
import com.gentlesoft.framework.action.PageUtil;
import com.gentlesoft.organiseManage.domain.Organise;
import com.gentlesoft.organiseManage.proxy.OrganiseFactoryBean;
import com.gentlesoft.organiseManage.proxy.PersonServiceProxy;
import com.gentlesoft.persistence.Page;
import com.jw.oa.meetingManage.domain.JwRoomRegister;
import com.jw.oa.meetingManage.domain.MeetingRoom;
import com.jw.oa.meetingManage.service.JwRoomRegisterService;
import com.jw.oa.meetingManage.service.MeetingService;
import com.jw.oa.util.DateHelper;
import com.jw.oa.util.JwComm;
import com.jw.oa.util.StringHelper;
import com.oa.jw.ws.IJwSmsService;
import com.sz.common.util.DateUtil;
import com.sz.common.util.StringUtil;
/**
 * 会议室管理控制类
 * @author hushankun
 *
 */
public class RoomController extends BaseControl {
	//会议业务处理层
	private MeetingService meetingService;
	//会议室使用信息业务层
	private JwRoomRegisterService jwRoomRegisterService;
	
	private PersonServiceProxy personService=OrganiseFactoryBean.getInstance().getPersonProxy();
	
	private final String hysdjshr = UtilResource.getProperty("config/jw","jw.hysdjshr");  //会议室登记审核人
	private final String bygsPhone = UtilResource.getProperty("config/jw","jw.bygsPhone");  //北域公司电话
	
	private IJwSmsService jwSmsService;//建委短信发送接口
	
	public void setJwSmsService(IJwSmsService jwSmsService) {
		this.jwSmsService = jwSmsService;
	}
	public void setJwRoomRegisterService(JwRoomRegisterService jwRoomRegisterService) {
		this.jwRoomRegisterService = jwRoomRegisterService;
	}
	public void setMeetingService(MeetingService meetingService) {
		this.meetingService = meetingService;
	}
	/**
	 * 跳转到会议室信息编辑页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		MeetingRoom room = null;
		if(!StringUtil.isEmpty(oid)){
			room = meetingService.queryRoomByOid(oid);
		}
		request.setAttribute("obj", room);
		return new ModelAndView("app/jw/meeting/editRoom");
	}
	/**
	 * 调整到会议室信息浏览页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView detail(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		MeetingRoom room = null;
		if(!StringUtil.isEmpty(oid)){
			room = meetingService.queryRoomByOid(oid);
		}
		request.setAttribute("obj", room);
		return new ModelAndView("app/jw/meeting/detailRoom");
	}
	/**
	 * 新增或维护会议室信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveMeetingRoom(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		MeetingRoom room = new MeetingRoom();
		if(StringUtil.isEmpty(oid)){
			room.setCreateuserid(super.getCuruserPartyId(request));//设置创建人
			room.setCreatedatetime(DateUtil.getCurrentDateTime());//设置创建时间
		}
		
		String orderNum = request.getParameter("ordernum");
		if(StringUtil.isEmpty(orderNum)){
			orderNum = meetingService.queryMaxOrderNo();
			int num = Integer.parseInt(orderNum);
			num++;
			orderNum = num + "";
		}

		
		room.setOid(oid);
		room.setRoomname(request.getParameter("roomname"));
		room.setAddress(request.getParameter("address"));
		room.setDelflag("0");
		room.setRemark(request.getParameter("remark"));
		request.setAttribute("obj", room);
		room.setOrdernum(orderNum);
		
		meetingService.saveOrUpdateMeetingRoom(room);
		
		return new ModelAndView("redirect:index.do");
	}
	/**
	 * 改变会议室使用状态
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView changeRoomStatus(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		if(!StringUtil.isEmpty(oid)){
			MeetingRoom room = meetingService.queryRoomByOid(oid);
			if("0".equals(room.getDelflag())){
				room.setDelflag("1");
			}else{
				room.setDelflag("0");
			}
			meetingService.saveOrUpdateMeetingRoom(room);
		}
		return new ModelAndView("redirect:index.do");
	}
	/**
	 * 物理删除会议室信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView delRoom(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		meetingService.delRoom(oid);
		return new ModelAndView("redirect:index.do");
	}
	/**
	 * 查询会议室是否曾被使用--（删除前的验证方法）
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView isUsed(HttpServletRequest request,HttpServletResponse response){
		boolean flag = false;
		String oid = request.getParameter("oid");
		if(!StringUtil.isEmpty(oid)){
			//查看会议室是否被使用过？
			
			flag = true;
		}
		StringBuilder builder = new StringBuilder("{isTrue:");
		builder.append("'").append(flag).append("'}");
		super.outJson(builder.toString(), response);
		return null;
	}
	
	/**
	* @Title: checkRoom
	* @Description: 验证会议室是否被占用
	* @param @param request
	* @param @param response
	* @param @return    设定文件
	* @return ModelAndView    返回类型
	* @throws
	*/
	public void checkRoom(HttpServletRequest request,HttpServletResponse response){
		String roomOid = request.getParameter("roomOid");//会议室
		String beginDateTime = request.getParameter("beginDateTime");
		String endDateTime = request.getParameter("endDateTime");
		String oid = request.getParameter("oid");
		Map param = new HashMap();
		param.put("roomoid", roomOid);
		param.put("checkTime", beginDateTime);
		param.put("checkTime1", endDateTime);
		param.put("oid", oid);
		List<Map<String,Object>> list = jwRoomRegisterService.checkRoom(param);
		Map<String, Object> map = new HashMap<String, Object>();
		if(list != null && list.size() > 0){
			map.put("isTrue", "false");
		}else{
			map.put("isTrue", "true");
		}
		super.outJson(map, response);
	}
	
	public void checkJj(HttpServletRequest request,HttpServletResponse response){
		String roomOid = request.getParameter("roomOid");//会议室
		String beginDateTime = request.getParameter("beginDateTime");
		String endDateTime = request.getParameter("endDateTime");
		String oid = request.getParameter("oid");
		Map param = new HashMap();
		param.put("roomoid", roomOid);
		param.put("checkTime", beginDateTime);
		param.put("checkTime1", endDateTime);
		if(oid != null && oid.length() > 0){
			param.put("oid", oid);
		}
		List<Map<String,Object>> list = jwRoomRegisterService.checkJj(param);
		Map<String, Object> map = new HashMap<String, Object>();
		if(list != null && list.size() > 0){
			map.put("isTrue", "false");
		}else{
			map.put("isTrue", "true");
		}
		super.outJson(map, response);
	}
	
	/**
	 * 会议室管理查询
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView index(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("app/jw/meeting/roomIndex");
	}
	/**
	 * 加载会议室信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView query(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		
		Page<Map<String,Object>> page = meetingService.selectMeetingRoom(filterBlank(request.getParameterMap(),request) , start, pageSize);
		
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);return null;
	}
	
	/**
	 * 组装查询请求
	 * @param map 页面请求集合
	 * @param request
	 * @return
	 */
	private Map<String,Object> filterBlank(Map<String,Object> map,HttpServletRequest request){
		Map<String,Object> returnMap=new HashMap<String,Object>();
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			
			String[] array=(String[]) map.get(key);
			if(UtilValidate.isNotEmpty(array[0]))
				returnMap.put(key, array[0]);
		}
		String endDate = (String) returnMap.get("endDate");
		if(!StringUtil.isEmpty(endDate)){
			endDate = endDate + " 23:59:59";
			returnMap.put("endDate", endDate);
		}
		return returnMap;
	}
	/**
	 * 自动封装--会议室使用信息请求数据
	 * @param request
	 * @return
	 */
	public JwRoomRegister setJwRoomRegister(HttpServletRequest request){
		JwRoomRegister register = new JwRoomRegister();
		register.setOid(StringHelper.ToString(request.getParameter("oid")));
		register.setBeginDateTime(StringHelper.ToString(request.getParameter("beginDateTime")));
		register.setEndDateTime(StringHelper.ToString(request.getParameter("endDateTime")));
		register.setRoomOid(StringHelper.ToString(request.getParameter("roomOid")));
		register.setUseOrgId(StringHelper.ToString(request.getParameter("useOrgId")));
		register.setUseOrgName(StringHelper.ToString(request.getParameter("useOrgName")));
		register.setUseReason(StringHelper.ToString(request.getParameter("useReason")));
		register.setCreatorOid(StringHelper.ToString(request.getParameter("creatorOid")));
		register.setUseReason("无");
		register.setRemark(request.getParameter("remark"));
		
		register.setCslxr(request.getParameter("cslxr"));
		register.setCslxdh(request.getParameter("cslxdh"));
		register.setHymc(request.getParameter("hymc"));
		register.setChry(request.getParameter("chry"));
		register.setSfwld(request.getParameter("sfwld"));
		register.setChrs(request.getParameter("chrs"));
		register.setSfxyhb(request.getParameter("sfxyhb"));
		register.setHb(request.getParameter("hb"));
		register.setSfxyty(request.getParameter("sfxyty"));
		register.setSfxyzp(request.getParameter("sfxyzp"));
		register.setZp(request.getParameter("zp"));
		register.setHwlxr(request.getParameter("hwlxr"));
		register.setHwlxdh(request.getParameter("hwlxdh"));
		register.setStatus(request.getParameter("status"));
		register.setShyj(request.getParameter("shyj"));
		
		String kqs = request.getParameter("kqs");
		if(!"1".equals(kqs)){
			kqs = "0";
		}
		register.setKqs(kqs);
		String cha = request.getParameter("cha");
		if(!"1".equals(cha)){
			cha = "0";
		}
		register.setCha(cha);
		String bi = request.getParameter("bi");
		if(!"1".equals(bi)){
			bi = "0";
		}
		register.setBi(bi);
		return register;
	}
	/**
	 * 自动组装返回请求数据
	 * @param request
	 */
	private void setReturnJwRoomRegister(HttpServletRequest request){
		JwRoomRegister register = null;
		String oid = request.getParameter("oid");
		if(StringUtil.isEmpty(oid)){
			register = new JwRoomRegister();
			register.setCreateDateTime(DateUtil.getCurrentDateTime());
			Organise org = getLoginUserOrg(request);
			register.setUseOrgId(org.getPartyid());
			register.setUseOrgName(org.getShortName());
			register.setCreatorOid(super.getCuruserPartyId(request));
		}else{
			register = jwRoomRegisterService.queryByOid(oid);
		}
		request.setAttribute("obj", register);
		request.setAttribute("roomList", meetingService.queryByUsing());
	}
	/**
	 * 获取当前登录用户所在的单位
	 * @param request
	 * @return
	 */
	private Organise getLoginUserOrg(HttpServletRequest request){
		Organise org = null;
		String partyId = getHandleUserLogin().getPartyId(request);
		if(!StringUtil.isEmpty(partyId)){
			org = personService.getDefaultOrgan(partyId);
		}
		return org;
	}
	/**
	 * 跳转--会议室使用信息登记/修改界面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView editRegister(HttpServletRequest request,HttpServletResponse response){
		setReturnJwRoomRegister(request);
		return new ModelAndView("app/jw/meeting/room/edit");
	}
	
	public ModelAndView editTongguo(HttpServletRequest request,HttpServletResponse response){
		setReturnJwRoomRegister(request);
		return new ModelAndView("app/jw/meeting/room/zhoudan_edit");
	}
	
	public ModelAndView shenhe(HttpServletRequest request,HttpServletResponse response){
		setReturnJwRoomRegister(request);
		return new ModelAndView("app/jw/meeting/room/shenheEdit");
	}
	
	/**
	 * 跳转--会议室使用信息登记/修改界面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView detailRegister(HttpServletRequest request,HttpServletResponse response){
		setReturnJwRoomRegister(request);
		return new ModelAndView("app/jw/meeting/room/detail");
	}
	
	public ModelAndView bygsDetail(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		JwRoomRegister register = jwRoomRegisterService.queryByOid(oid);
		if(register != null && !"1".equals(register.getBygsstatus())){
			register.setBygsstatus("1");
			jwRoomRegisterService.saveOrUpdate(register);
		}
		request.setAttribute("obj", register);
		request.setAttribute("roomList", meetingService.queryByUsing());
		return new ModelAndView("app/jw/meeting/room/bygsDetail");
	}
	
	/**
	 * 操作--暂存/发布会议通知
	 * @param request
	 * @param response
	 * @return
	 */
	public void save(HttpServletRequest request,HttpServletResponse response){
		JwRoomRegister register = setJwRoomRegister(request);
		if(StringUtil.isEmpty(register.getOid())){
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
			register.setCreateDateTime(time);
			register.setStatus("1");
		}else{
			if("0".equals(register.getStatus())){
				register.setStatus("1");
			}else if("-1".equals(register.getStatus())){
				register.setStatus("0");
			}
		}
		boolean flag = jwRoomRegisterService.saveOrUpdate(register);
		
		try {
			//0:退回，1:审核中，2:通过，3:不通过
			if("1".equals(register.getStatus())){
				if(hysdjshr != null && !"".equals(hysdjshr)){
					String phone = jwRoomRegisterService.getPhone(hysdjshr);
					if(phone != null && !"".equals(phone)){
						String msg = "OA有会议室登记请求申请，请处理。";
						jwSmsService.sendSms(phone, msg, "会议室登记", register.getOid(),
								super.getCuruserUserName(request),
								super.getCuruserPartyId(request));
					}
				}
			}else if("2".equals(register.getStatus())){
				String phone = register.getCslxdh();
				if(phone != null && !"".equals(phone)){
					String msg = "会议室登记请求申请已审核，请登录OA查看。";  //发送处室
					jwSmsService.sendSms(phone, msg, "会议室登记", register.getOid(),
							super.getCuruserUserName(request),
							super.getCuruserPartyId(request));
				}
				
				if(bygsPhone != null && !"".equals(bygsPhone)){
					String msg2 = "OA有会议室登记请求申请，请及时处理。";  //发送北域公司
					jwSmsService.sendSms(bygsPhone, msg2, "会议室登记", register.getOid(),
							super.getCuruserUserName(request),
							super.getCuruserPartyId(request));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isTrue", flag);
		super.outJson(map, response);
	}
	
	public void zhoudanSave(HttpServletRequest request,HttpServletResponse response){
		JwRoomRegister register = setJwRoomRegister(request);
		if(StringUtil.isEmpty(register.getOid())){
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
			register.setCreateDateTime(time);
			register.setStatus("1");
		}
		boolean flag = jwRoomRegisterService.saveOrUpdate(register);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isTrue", flag);
		super.outJson(map, response);
	}
	
	/**
	 * 会议室使用情况
	 */
	public ModelAndView roomSyqk(HttpServletRequest request,HttpServletResponse response){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		request.setAttribute("nowTime", sdf.format(new Date()));
		
		request.setAttribute("roomList", meetingService.queryByUsing());
		return new ModelAndView("appForPhone/roomapp/syqk");
	}
	
	public ModelAndView adopt(HttpServletRequest request,HttpServletResponse response){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		request.setAttribute("nowTime", sdf.format(new Date()));
		
		return new ModelAndView("app/jw/meeting/room/adopt");
	}
	
	public void queryByStatus(HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String nowTime = request.getParameter("nowTime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			Calendar cal = Calendar.getInstance();
			if(nowTime == null || nowTime.equals("")){
				cal.setTime(new Date());
			}else{
				cal.setTime(sdf.parse(nowTime));
			}
			
			String status = request.getParameter("status");
			if("1".equals(status)){
				cal.add(Calendar.DATE, 7);  //下周
			}else if("-1".equals(status)){
				cal.add(Calendar.DATE, -7); //上周
			}
			
			cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);  //周一
			String beginDate = sdf.format(cal.getTime());
			
			List<Map<String, String>> li = new ArrayList<Map<String, String>>();
			Map<String, String> m = new HashMap<String, String>();
			m.put("date", sdf.format(cal.getTime()));
			m.put("day", "星期一");
			li.add(m);
			
			for(int i=1;i<7;i++){
				switch (i) {
					case 1:
						cal.add(Calendar.DATE, 1);
						m = new HashMap<String, String>();
						m.put("date", sdf.format(cal.getTime()));
				    	m.put("day", "星期二");
				    	li.add(m);
				    	break;
					case 2:
						cal.add(Calendar.DATE, 1);
						m = new HashMap<String, String>();
						m.put("date", sdf.format(cal.getTime()));
				    	m.put("day", "星期三");
				    	li.add(m);
				    	break;
					case 3:
						cal.add(Calendar.DATE, 1);
						m = new HashMap<String, String>();
						m.put("date", sdf.format(cal.getTime()));
				    	m.put("day", "星期四");
				    	li.add(m);
				    	break;
					case 4:
						cal.add(Calendar.DATE, 1);
						m = new HashMap<String, String>();
						m.put("date", sdf.format(cal.getTime()));
				    	m.put("day", "星期五");
				    	li.add(m);
				    	break;
					case 5:
						cal.add(Calendar.DATE, 1);
						m = new HashMap<String, String>();
						m.put("date", sdf.format(cal.getTime()));
				    	m.put("day", "星期六");
				    	li.add(m);
				    	break;
					case 6:
						cal.add(Calendar.DATE, 1);
						m = new HashMap<String, String>();
						m.put("date", sdf.format(cal.getTime()));
				    	m.put("day", "星期日");
				    	li.add(m);
				    	break;
				}
			}
			
			map.put("date", li);
			
			List<MeetingRoom> list = meetingService.queryByUsing();
			map.put("meetingRoom", list);
			
			Map param = new HashMap();
			param.put("beginDate", beginDate);
			List<Map<String, Object>> allData = jwRoomRegisterService.queryByStatus(param);
			map.put("allData", allData);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	super.outJson(map, response);
	}
	
	
	/**
	 * 操作--删除会议室使用信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView delete(HttpServletRequest request,HttpServletResponse response){
		String oids = request.getParameter("oids");
		boolean flag = false;
		if(!StringUtil.isEmpty(oids)){
			flag = jwRoomRegisterService.deleteByCreator(oids.split(","));
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	/**
	 * 跳转--会议室使用管理页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toIndexPage(HttpServletRequest request,HttpServletResponse response){
		List<MeetingRoom> list = meetingService.queryByUsing();
		StringBuffer sb = new StringBuffer();
		if(list != null ){
			int num = list.size();
			MeetingRoom room = null;
			for (int i = 0;i < num;i++) {
				room = list.get(i);
				if(i == num - 1){
					sb.append("['").append(room.getOid()).append("','").append(room.getRoomname()).append("']");
				}else{
					sb.append("['").append(room.getOid()).append("','").append(room.getRoomname()).append("'],");
				}
			}
		}
		request.setAttribute("today", DateUtil.getCurrentDate());
		request.setAttribute("person", sb.toString());
		request.setAttribute("isbgs", JwComm.isBgs(super.getCuruserPartyId(request)));
		return new ModelAndView("app/jw/meeting/room/index");
	}
	
	public ModelAndView toShenhe(HttpServletRequest request,HttpServletResponse response){
		List<MeetingRoom> list = meetingService.queryByUsing();
		StringBuffer sb = new StringBuffer();
		if(list != null ){
			int num = list.size();
			MeetingRoom room = null;
			for (int i = 0;i < num;i++) {
				room = list.get(i);
				if(i == num - 1){
					sb.append("['").append(room.getOid()).append("','").append(room.getRoomname()).append("']");
				}else{
					sb.append("['").append(room.getOid()).append("','").append(room.getRoomname()).append("'],");
				}
			}
		}
		request.setAttribute("today", DateUtil.getCurrentDate());
		request.setAttribute("person", sb.toString());
		request.setAttribute("isbgs", JwComm.isBgs(super.getCuruserPartyId(request)));
		return new ModelAndView("app/jw/meeting/room/shenhe");
	}
	
	public ModelAndView toBygs(HttpServletRequest request,HttpServletResponse response){
		List<MeetingRoom> list = meetingService.queryByUsing();
		StringBuffer sb = new StringBuffer();
		if(list != null ){
			int num = list.size();
			MeetingRoom room = null;
			for (int i = 0;i < num;i++) {
				room = list.get(i);
				if(i == num - 1){
					sb.append("['").append(room.getOid()).append("','").append(room.getRoomname()).append("']");
				}else{
					sb.append("['").append(room.getOid()).append("','").append(room.getRoomname()).append("'],");
				}
			}
		}
		request.setAttribute("today", DateUtil.getCurrentDate());
		request.setAttribute("person", sb.toString());
		request.setAttribute("isbgs", JwComm.isBgs(super.getCuruserPartyId(request)));
		return new ModelAndView("app/jw/meeting/room/bygs");
	}
	
	public ModelAndView toZhoudan(HttpServletRequest request,HttpServletResponse response){
		List<MeetingRoom> list = meetingService.queryByUsing();
		StringBuffer sb = new StringBuffer();
		if(list != null ){
			int num = list.size();
			MeetingRoom room = null;
			for (int i = 0;i < num;i++) {
				room = list.get(i);
				if(i == num - 1){
					sb.append("['").append(room.getOid()).append("','").append(room.getRoomname()).append("']");
				}else{
					sb.append("['").append(room.getOid()).append("','").append(room.getRoomname()).append("'],");
				}
			}
		}
		request.setAttribute("today", DateUtil.getCurrentDate());
		request.setAttribute("person", sb.toString());
		request.setAttribute("isbgs", JwComm.isBgs(super.getCuruserPartyId(request)));
		return new ModelAndView("app/jw/meeting/room/zhoudan");
	}
	
	/**
	 * 加载会议室使用信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryData(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> param = filterBlank(request.getParameterMap(),request);
		boolean	flag = JwComm.isBgs(super.getCuruserPartyId(request));
		if(flag){
			param.put("useOrgId", null);//办公室查看所有登记信息
		}else{
			Organise org = getLoginUserOrg(request);
			param.put("useOrgId",org.getPartyid());//处室查看本处室登记信息
		}
//		if(!flag){ // 非办公室查看没过期的所有登记信息，开始时间大于等于当前时间
//			param.put("endDateTime", DateUtil.getCurrentDateTime());
//		}
		Page<Map<String,Object>> page = jwRoomRegisterService.selectJwRoomRegisters(param, start, pageSize);
		
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);return null;
	}
	
	
	public ModelAndView queryAll(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> param = filterBlank(request.getParameterMap(),request);
		param.put("status", 1);

		Page<Map<String,Object>> page = jwRoomRegisterService.selectJwRoomRegisters(param, start, pageSize);
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);return null;
	}
	
	public ModelAndView queryBygs(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> param = filterBlank(request.getParameterMap(),request);
		param.put("status", 2);

		Page<Map<String,Object>> page = jwRoomRegisterService.selectJwRoomRegisters(param, start, pageSize);
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);return null;
	}
	
	/**
	 * 会议室使用公示
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView publicity(HttpServletRequest request,HttpServletResponse response){
		String startDate = request.getParameter("startDate");
		String add = request.getParameter("add");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		Map<String, Object> param = null;
		try {
			if(!StringUtil.isEmpty(startDate) && !StringUtil.isEmpty(add)){
				date = sdf.parse(startDate);
				Integer inc = Integer.valueOf(add);
				date = DateHelper.getDate(date, inc);
			}else{
				date = new Date();
			}
			param = DateHelper.getDayOfWeek(date);
			if(param != null){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("beginDate", param.get("monday") + " 00:00");
				map.put("endDate", param.get("endDate") + " 23:59");
				request.setAttribute("list", jwRoomRegisterService.queryForPublicity(map));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		request.setAttribute("weekday", param);
		request.setAttribute("roomList", meetingService.queryByUsing());
		return new ModelAndView("app/jw/meeting/room/publicity");
	}
	/****
	 *  验证当前会议室是否由当前用户创建
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView checkroombyCurrent(HttpServletRequest request,HttpServletResponse response){
		String oids = request.getParameter("oids");
		boolean flag = true;
		if(!StringUtil.isEmpty(oids)){
			List<JwRoomRegister> list = jwRoomRegisterService.getListByoids(oids.split(","), super.getCuruserPartyId(request));
			if(list != null && list.size() > 0){
				flag = false;
			}
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	} 
}
package com.jw.oa.meetingmanage.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.gentlesoft.archive.fileManage.domain.UploadManFile;
import com.gentlesoft.archive.fileManage.service.UploadManService;
import com.gentlesoft.commons.util.UtilResource;
import com.gentlesoft.commons.util.UtilValidate;
import com.gentlesoft.dictionary.Dictionary;
import com.gentlesoft.framework.action.BaseControl;
import com.gentlesoft.framework.action.PageUtil;
import com.gentlesoft.organiseManage.domain.Organise;
import com.gentlesoft.organiseManage.domain.Person;
import com.gentlesoft.organiseManage.proxy.OrganiseFactoryBean;
import com.gentlesoft.organiseManage.proxy.PersonServiceProxy;
import com.gentlesoft.persistence.Page;
import com.jw.oa.leaderPlan.domain.LeaderPlan;
import com.jw.oa.leaderPlan.service.LeaderPlanService;
import com.jw.oa.meetingManage.domain.JwApply;
import com.jw.oa.meetingManage.domain.JwMeetingMsg;
import com.jw.oa.meetingManage.domain.JwNextAudit;
import com.jw.oa.meetingManage.domain.JwNextMeeting;
import com.jw.oa.meetingManage.domain.JwPerson;
import com.jw.oa.meetingManage.domain.Meeting;
import com.jw.oa.meetingManage.domain.MeetingComm;
import com.jw.oa.meetingManage.domain.Participant;
import com.jw.oa.meetingManage.service.JwApplyService;
import com.jw.oa.meetingManage.service.JwMeetingMsgService;
import com.jw.oa.meetingManage.service.JwNextAuditService;
import com.jw.oa.meetingManage.service.JwNextMeetingService;
import com.jw.oa.meetingManage.service.JwPersonService;
import com.jw.oa.meetingManage.service.MeetingService;
import com.jw.oa.receivdLedgerManage.domain.ReceivdLedger;
import com.jw.oa.receivdLedgerManage.service.ReceivdLedgerService;
import com.jw.oa.util.DateHelper;
import com.jw.oa.util.ExcelHelper;
import com.jw.oa.util.FileUtil;
import com.jw.oa.util.StringHelper;
import com.jw.oa.workingDay.service.WorkingDayService;
import com.oa.jw.ws.IJwSmsService;
import com.oa.jw.ws.JwSmsComm;
import com.soft.oa.common.Constants;
import com.soft.oa.regingoingdoc.domain.RegInGoingDocWD;
import com.soft.oa.regingoingdoc.domain.RegInGoingDocWDAttach;
import com.soft.oa.regingoingdoc.service.RegInGoingDocService;
import com.sz.common.util.DateUtil;
import com.sz.common.util.StringUtil;

/**
 * 委外会议管理类
 * @author hushankun
 */
public class ExternalMeetingController extends BaseControl {
	/**
	 * 跳转到新增/编辑会议通知页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView edit(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		setRuturnUrl(request);

		setReturnData(request, oid);
		return new ModelAndView("app/jw/meeting/external/edit");
	}

	
	/**
	* @Title: editReadOnly
	* @Description: TODO(查看详情)
	* @param @param request
	* @param @param response
	* @param @return    设定文件
	* @return ModelAndView    返回类型
	* @throws
	*/
	public ModelAndView editReadOnly(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		setRuturnUrl(request);
		setReturnData(request, oid);
		return new ModelAndView("app/jw/meeting/external/editReadOnly");
	}
	/**
	 * 跳转--浏览会议通知
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView detail(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		setRuturnUrl(request);
		setReturnData(request, oid);
		return new ModelAndView("app/jw/meeting/external/detail");
	}
	
	public ModelAndView detail2(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		setRuturnUrl(request);
		setReturnData(request, oid);
		return new ModelAndView("app/jw/meeting/external/detail2");
	}
	
	/**
	 * 桌面跳转会议详情
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView detailZm(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		//setRuturnUrl(request);
		setReturnData(request, oid);
		return new ModelAndView("app/jw/meeting/external/detailZm");
	}
	
	/**
	 * 跳转--（参会人员）浏览会议通知
	 * @param request
	 * @param response
	 * @return
	 */
	
	private final String ms = UtilResource.getProperty("config/jw","jw.fawen.ms");//秘书
	
	public ModelAndView detailRec(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		Participant p = null;
		if(!StringUtil.isEmpty(oid)){
			p = meetingService.findParticipantByOid(oid);
			if(p != null){
				if(p.getExt1() !=null && (p.getExt1().equals("0") || p.getExt1().equals("1")) && (p.getReceptionStatus() == null || p.getReceptionStatus().equals(MeetingComm.STATUS_WJS))){
					p.setReceptionStatus(MeetingComm.STATUS_YJS);
					p.setReceptionDateTime(DateUtil.getCurrentDateTime());
					meetingService.updateParticipant(p);
				}
				setRuturnUrl(request);
				setReturnData(request, p.getMeetingOid());
				request.setAttribute("parti", p);
			}
			Organise org = getLoginUserOrg(request);
			request.setAttribute("partiOid", oid);
			request.setAttribute("offList", jwApplyService.getOfficer(org.getPartyid()));//获取处室人员名单
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("meetingOid", p.getMeetingOid());
			param.put("orgId", org.getPartyid());
			param.put("isNew", 0);
			List<Map<String,Object>> list = jwApplyService.getApply(param);
			if(list != null){
				int size = list.size();
				String objs = "";
				Map<String, Object> map = null;
				if(size > 0){
					for (int i = 0; i < size; i++) {
						map = list.get(i);
						if(i == size - 1){
							objs += (String) map.get("APPLYOID");
						}else{
							objs += (String) map.get("APPLYOID") + ",";
						}
					}
				}
				request.setAttribute("objs", objs);
			}
		}
		List list = leaderPlanService.findByLeader(super.getCuruserPartyId(request));
		if(list != null && list.size()>0)
			request.setAttribute("isWld", true);
		else
			request.setAttribute("isWld", false);
		
		String userId = super.getCuruserPartyId(request);
		if(ms != null && Arrays.asList(ms.split(",")).contains(userId)){
			request.setAttribute("isMs", "true");
		}
		return new ModelAndView("app/jw/meeting/external/detailRec");
	}
	public ModelAndView detailRecBgs(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		Participant p = null;
		if(!StringUtil.isEmpty(oid)){
			p = meetingService.findParticipantByOid(oid);
			if(p != null){
				setRuturnUrl(request);
				setReturnData(request, p.getMeetingOid());
				request.setAttribute("parti", p);
			}
			Organise org = getLoginUserOrg(request);
			request.setAttribute("partiOid", oid);
			request.setAttribute("offList", jwApplyService.getOfficer(org.getPartyid()));//获取处室人员名单
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("meetingOid", p.getMeetingOid());
			param.put("orgId", org.getPartyid());
			param.put("isNew", 0);
			List<Map<String,Object>> list = jwApplyService.getApply(param);
			if(list != null){
				int size = list.size();
				String objs = "";
				Map<String, Object> map = null;
				if(size > 0){
					for (int i = 0; i < size; i++) {
						map = list.get(i);
						if(i == size - 1){
							objs += (String) map.get("APPLYOID");
						}else{
							objs += (String) map.get("APPLYOID") + ",";
						}
					}
				}
				request.setAttribute("objs", objs);
			}
		}
		return new ModelAndView("app/jw/meeting/external/detailRecBgs");
	}
	private JwNextMeetingService jwNextMeetingService;
	public void setJwNextMeetingService(JwNextMeetingService jwNextMeetingService) {
		this.jwNextMeetingService = jwNextMeetingService;
	}
	private JwNextAuditService jwNextAuditService;
	public void setJwNextAuditService(JwNextAuditService jwNextAuditService) {
		this.jwNextAuditService = jwNextAuditService;
	}
	/**
	 * 操作--暂存/发布会议通知
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView save(HttpServletRequest request,HttpServletResponse response){
		String userId=super.getCuruserPartyId(request);
		String name=getHandleUserLogin().getUserName(request);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String bgsId=UtilResource.getProperty("config/jw","jw.bgs");
		String bgsName=UtilResource.getProperty("config/jw","jw.bgs.name");
		Meeting meet = setExternalMeeting(request);
		try{
		Map map = new HashMap();
		map.put("meetoid", meet.getOid());
		JwNextMeeting jwNextMeeting1 = jwNextMeetingService.check(map);
		if(jwNextMeeting1 == null){
			Map param = new HashMap();
			param.put("meetingname", meet.getMeetingName());
			param.put("startdatetime", meet.getStartDateTime());
//		param.put("enddatetime", meet.getEndDateTime());
			JwNextMeeting list = jwNextMeetingService.check(param);
			if(list == null){
				JwNextMeeting jwNextMeeting = new JwNextMeeting();
				jwNextMeeting.setOid(UUID.randomUUID().toString());
				jwNextMeeting.setMeetoid(meet.getOid());
				jwNextMeeting.setMeetingname(meet.getMeetingName());
				jwNextMeeting.setCompere(meet.getCompere());
				jwNextMeeting.setStartdatetime(meet.getStartDateTime());
				jwNextMeeting.setEnddatetime(meet.getEndDateTime());
				jwNextMeeting.setComperemanageroid(meet.getCompereManagerOid());
				jwNextMeeting.setMeetinfsite(meet.getMeetinfSite());
				jwNextMeeting.setParticipants(meet.getParticipants());
				jwNextMeeting.setIsdel("0");//默认没有删除
				jwNextMeeting.setStatus("0");//默认值班室
				jwNextMeeting.setAddtime(format.format(new Date()));
				jwNextMeeting.setAdduserid(userId);
				jwNextMeeting.setAddusername(name);
				jwNextMeetingService.insert(jwNextMeeting);
				//新增环节
				JwNextAudit jwNextAudit = new JwNextAudit();
				jwNextAudit.setOid(UUID.randomUUID().toString());
				jwNextAudit.setCurrentauditid(userId);
				jwNextAudit.setCurrentauditname(name);
				jwNextAudit.setStatus("closed");
				jwNextAudit.setAudittime(format.format(new Date()));
				jwNextAudit.setNmid(jwNextMeeting.getOid());
				jwNextAuditService.insert(jwNextAudit);
				//值班室环节
				JwNextAudit jwNextAudit2 = new JwNextAudit();
				jwNextAudit2.setOid(UUID.randomUUID().toString());
				jwNextAudit2.setNmid(jwNextMeeting.getOid());
				jwNextAudit2.setCurrentauditid(bgsId);
				jwNextAudit2.setCurrentauditname(bgsName);
				jwNextAudit2.setLastauditid(userId);
				jwNextAudit2.setLastauditname(name);
				jwNextAudit2.setStatus("open");
				jwNextAuditService.insert(jwNextAudit2);
			}else{
				if(UtilValidate.isEmpty(list.getMeetoid())){
					list.setMeetoid(meet.getOid());
				}
				list.setMeetingname(meet.getMeetingName());
				list.setCompere(meet.getCompere());
				list.setStartdatetime(meet.getStartDateTime());
				list.setEnddatetime(meet.getEndDateTime());
				list.setComperemanageroid(meet.getCompereManagerOid());
				list.setMeetinfsite(meet.getMeetinfSite());
				list.setParticipants(meet.getParticipants());
				jwNextMeetingService.update(list);
			}
		}else{
			jwNextMeeting1.setMeetingname(meet.getMeetingName());
			jwNextMeeting1.setCompere(meet.getCompere());
			jwNextMeeting1.setStartdatetime(meet.getStartDateTime());
			jwNextMeeting1.setEnddatetime(meet.getEndDateTime());
			jwNextMeeting1.setComperemanageroid(meet.getCompereManagerOid());
			jwNextMeeting1.setMeetinfsite(meet.getMeetinfSite());
			jwNextMeeting1.setParticipants(meet.getParticipants());
			jwNextMeetingService.update(jwNextMeeting1);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		//去掉， 模式变动，先发通知在由秘书确认参会人员
		//List<Participant> list = setParticipant(request);
		//修改以参会人员为标准来标识会议是否安排或者根据会议标识来判断是否安排
		if(!StringUtil.isEmpty(meet.getParticipants())){
			meet.setIsArrange(1);
		}else{
			meet.setIsArrange(0);
		}
		boolean flag = meetingService.saveOrUpdate(meet, null,request.getParameter("saveFlag"));
		// 保存成功时判断是否需要发送短信通知
		if(flag){
			String issms = valueOf(request.getParameter("issms"));
			if("1".equals(issms)){ // 
				String mobile = meetingService.queryMobilebyperid(meet.getSecretarypid());
				String smsmsg = "您好！ 【"+meet.getSecretary()+"】 OA有会议安排，请及时办理！";
				smsService.sendSms(mobile.split(","), smsmsg, JwSmsComm.JWSMS_TYPE_MEETING, meet.getOid(), 
						super.getCuruserUserName(request), super.getCuruserLoginId(request));
			}
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	/***
	 * 区分参会人行程是否冲突
	 * @param date
	 * @param endDate
	 * @param person
	 * @return
	 */
	public String[] dealParticipantsInfo(String date,String endDate,String person){
		String[] participantsInfo=new String[4];
		//处理参会人信息
		String [] arrParticipantsInfo=person.split(",");//参会人信息
		StringBuffer sbParticipantsName=new StringBuffer();
		StringBuffer sbUnConflictParticipantsId=new StringBuffer();//临时存放不冲突ID和姓名
		StringBuffer sbUnConflictParticipantsName=new StringBuffer();
		StringBuffer sbConflictParticipantsId=new StringBuffer();//临时存放冲突ID和姓名
		StringBuffer sbConflictParticipantsName=new StringBuffer();
		StringBuffer sbConflictIndex=new StringBuffer();//存放行程冲突的ID下标
		StringBuffer sbParticipantsId=new StringBuffer();
		for(String str:arrParticipantsInfo){
			sbParticipantsId.append(str.split(":")[0]).append(",");
			sbParticipantsName.append(str.split(":")[2]).append(",");
		}
		//得到参会人ID
		String participantsId=sbParticipantsId.toString();
		//得到参会人Name
		String participantsName=sbParticipantsName.toString();
		//获取行程有冲突的参会人员信息
		Map param2 = new HashMap();
		param2.put("ksrq", date);
		param2.put("jzrq", endDate);
		String [] arrParticipantsId=participantsId.substring(0, participantsId.length()-1).split(",");
		String [] arrParticipantsName=participantsName.substring(0, participantsName.length()-1).split(",");
		
		String unConflictParticipantsId="";//存放行程不冲突参会人ID信息
		String unConflictParticipantsName="";//存放行程不冲突参会人姓名信息
		String conflictParticipantsId="";//存放行程冲突参会人ID信息
		String conflictParticipantsName="";//存放行程冲突参会人姓名信息
		String conflictIndex="";//存放行程冲突的参会人下标
	
		for(int i=0;i<arrParticipantsId.length;i++){
			param2.put("partyid", arrParticipantsId[i]);
			long isConflict=leaderPlanService.selectConflict1(param2);
			if(isConflict==0){
				sbUnConflictParticipantsId.append(arrParticipantsId[i]).append(",");
				sbUnConflictParticipantsName.append(arrParticipantsName[i]).append(",");
			}else{
				sbConflictParticipantsId.append(arrParticipantsId[i]).append(",");
				sbConflictParticipantsName.append(arrParticipantsName[i]).append(",");
				sbConflictIndex.append(i).append(",");
			}
		}
		unConflictParticipantsId=sbUnConflictParticipantsId.toString();
		unConflictParticipantsName=sbUnConflictParticipantsName.toString();
		conflictParticipantsId=sbConflictParticipantsId.toString();
		conflictParticipantsName=sbConflictParticipantsName.toString();
		conflictIndex=sbConflictIndex.toString();
		//按行程是否冲突组装参会者信息
		participantsInfo[0]=unConflictParticipantsId;
		participantsInfo[1]=unConflictParticipantsName;
		participantsInfo[2]=conflictParticipantsId;
		participantsInfo[3]=conflictParticipantsName;
		return participantsInfo;
	}
	
	/***
	 * 对行程有冲突的参会人Id进行计划更新操作
	 * @param p
	 * @author hrl
	 */
	public void updateLeaderPlan(Map p){
		String date=(String) p.get("date");
		String endDate=(String) p.get("endDate");
		String conflictId=(String) p.get("conflictId");
		String conflictName=(String) p.get("conflictName");
		String period=(String) p.get("period");
		String hdnr=(String) p.get("hdnr");
		String whry=(String)p.get("Whry");
		Date d=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String whsj=sdf.format(d);

		
		//获取参会期间工作
		Map param = new HashMap();
		param.put("ksrq", date);
		param.put("jzrq", endDate);
 		List gzrList = workingDayService.findByRq(param);
 		//判断行程冲突
		String [] arrPartyid=conflictId.substring(0, conflictId.length()-1).split(",");//将领导ID存入数组
		String [] arrLeaderName=conflictName.substring(0, conflictName.length()-1).split(",");//将领导姓名存入数组
		for(int m=0;m<arrPartyid.length;m++){	
			String leaderId=arrPartyid[m];//领导ID
			String leaderName=arrLeaderName[m];//领导姓名
			for(int j=0;j<gzrList.size();j++){
				// 取出工作日
				Map map = (Map) gzrList.get(j);
				String gzr = (String) map.get("GZR");						
				//冲突判断
				Map param1=new HashMap<String,Object>();
				param1.put("partyid",leaderId);
				param1.put("hdrq", gzr);
				LeaderPlan lp=leaderPlanService.selectConf(param1);
				if(lp!=null){
					//更新活动类型 内容 和活动时段  add by hrl
					lp.setHdsd(period);
					lp.setHdlx(hdnr);
					lp.setWhry(whry);
					lp.setWhsj(whsj);
					try {
						leaderPlanService.update(lp);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	/***
	 * 对无行程冲突人员进行会议记录插入
	 * @param p
	 * @author hrl
	 */
	public void insertMeetingInfo(Map m){
		HttpServletRequest request=(HttpServletRequest) m.get("request");
		String unConflictParticipantsName=(String) m.get("unConflictParticipantsName");
		String [] arrParticipantsInfo=(String[]) m.get("arrParticipantsInfo");
		
		StringBuffer sbParticipantsId=new StringBuffer();
		StringBuffer sbParticipantsName=new StringBuffer();
		for(String str:arrParticipantsInfo){
			sbParticipantsId.append(str.split(":")[0]).append(",");
			sbParticipantsName.append(str.split(":")[2]).append(",");
		}
		
		
		String conflictParticipantsId=(String) m.get("conflictParticipantsId");
		Meeting meet = setExternalMeeting(request);
		meet.setParticipants(sbParticipantsName.toString());//修改meeting表会议参会人
		
		//修改PARTICIPANT表参会人信息
		List<Participant> list = setParticipant(request);
//		String[] arrConflictParticipantsId=conflictParticipantsId.substring(0, conflictParticipantsId.length()-1).split(",");
//		for(int k=0;k<arrConflictParticipantsId.length;k++){
//			for(int j=0;j<list.size();j++){
//				if(list.get(j).getPersonOid().equals(arrConflictParticipantsId[k])){
//					list.remove(j);
//				}
//			}
//		}		
		//修改以参会人员为标准来标识会议是否安排或者根据会议标识来判断是否安排
		if(!StringUtil.isEmpty(meet.getParticipants())){
			meet.setIsArrange(1);
		}else{
			meet.setIsArrange(0);
		}
		meetingService.saveOrUpdate(meet, list,request.getParameter("saveFlag"));
	}
	
	/***
	 * @Title insertOrUpdateLeaderPlan
	 * @Description 会议安排同步领导计划
	 * @param request
	 * @param response
	 * @author hrl
	 */
	public ModelAndView insertOrUpdateLeaderPlan(HttpServletRequest request,HttpServletResponse response){
		String date=request.getParameter("startDateTime");//获取会议开始时间
		String endDate=request.getParameter("endDateTime");//获取会议结束时间
		String period=request.getParameter("period");
		String hdnr=request.getParameter("meetingName");//获取会议名称
		String person=request.getParameter("person");//获取参会人信息
		
		//处理参会时间
		date=date.substring(0, 10);
		if(!"".equals(endDate)){
			endDate=endDate.substring(0, 10);
		}else{
			endDate=date;
		}
		String[] participantsInfo=dealParticipantsInfo(date,endDate,person);//获取判断过是否冲突后的参会人信息
		
		if(!"".equals(participantsInfo[2])){//对行程冲突的参会人员进行计划更新操作
			Map p=new HashMap();
			p.put("date", date);
			p.put("endDate", endDate);
			p.put("conflictId", participantsInfo[2]);
			p.put("conflictName", participantsInfo[3]);
			p.put("hdnr", hdnr);
			p.put("period", period);
			p.put("Whry", request.getParameter("createUserName"));
			updateLeaderPlan(p);	
		}
		
		String [] arrParticipantsInfo=person.split(",");//参会人信息
//		StringBuffer sbParticipantsId=new StringBuffer();
//		StringBuffer sbParticipantsId=new StringBuffer();
//		for(String str:arrParticipantsInfo){
//			sbParticipantsId.append(str.split(":")[0]).append(",");
//			sbParticipantsName.append(str.split(":")[2]).append(",");
//		}
		
	//	if(!"".equals(participantsInfo[0])){//对行程不冲突的参会人员进行会议及参会者相关信息插入操作
			Map m=new HashMap();
			m.put("request", request);
			m.put("arrParticipantsInfo", arrParticipantsInfo);
			insertMeetingInfo(m);
	//	}

		String msg = "{isTrue:true}";
		super.outJson(msg, response);
		return null;
	}
	
	
	
	
	
	/**
	 * Ajax操作--删除会议通知信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView delete(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		boolean flag = meetingService.delMeeting(oid);
		Map map = new HashMap();
		map.put("meetoid", oid);
		JwNextMeeting jwNextMeeting = jwNextMeetingService.check(map);
		if(jwNextMeeting != null){
			try {
				jwNextMeeting.setIsdel("1");
				jwNextMeetingService.update(jwNextMeeting);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				flag = false;
				e.printStackTrace();
			}
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	/**
	 * 操作--发送短信通知
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView sendMsg(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		String message = request.getParameter("message");
		String mobile = request.getParameter("mobile");
		boolean flag = false;
		if(!StringUtil.isEmpty(oid) && !StringUtil.isEmpty(mobile) && !StringUtil.isEmpty(message)){
			List<String> list = meetingService.getMobile(mobile.split(","));
			if(list != null){
				smsService.sendSms(mobile, message, JwSmsComm.JWSMS_TYPE_MEETING, oid, 
						super.getCuruserUserName(request), super.getCuruserLoginId(request));
				flag = true;
			}
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	/**
	 * 操作--Ajax查询是否有效工作日
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView isDays(HttpServletRequest request,HttpServletResponse response){
		String date = request.getParameter("startDateTime");
		boolean flag = meetingService.isDays(date);
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	/**
	 * 操作--导出（委领导参与的）会议信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView exportLeader(HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> param = filterBlank(request.getParameterMap(), request);
		List<Map<String, Object>> list = meetingService.exportLeader(param);
		request.setAttribute("title", "委领导参与的会议计划");
		request.setAttribute("lis", list);
		return new ModelAndView("app/jw/meeting/export/exportLeader");
	}
	/**
	 * 操作--导出（机关处室参与的）会议信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView exportOffices(HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> param = filterBlank(request.getParameterMap(), request);
		List<Map<String, Object>> list = meetingService.exportOffices(param);
		request.setAttribute("title", "委领导参与的会议计划");
		request.setAttribute("lis", list);
		return new ModelAndView("app/jw/meeting/export/exportOffices");
	}
	/**
	 * 操作--导出（未安排的）会议信息
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public ModelAndView exportNoArr(HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
//		Map<String, Object> param = filterBlank(request.getParameterMap(), request);
		String meetingName = request.getParameter("meetingName");
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		if(meetingName != null){
			meetingName = java.net.URLDecoder.decode(meetingName,"utf-8");
			map.put("meetingName", "%"+meetingName+"%");
		}
		Organise org = getLoginUserOrg(request);
//		map.put("personId", super.getCuruserPartyId(request));
		map.put("orgId", org.getPartyid());
		map.put("meetingTag", "0");
		map.put("isArrange", "0");
		List<Map<String, Object>> list = meetingService.exportMeeting(map);
//		request.setAttribute("title", "委领导参与的会议计划");
//		request.setAttribute("lis", list);
		
		 response.setCharacterEncoding("UTF-8"); 
		 boolean isOnLine = false;
		 OutputStream out=null;
		 response.reset(); //非常重要   
		 String title = "未安排会议";
//		 if(isOnLine){ //在线打开方式                 
//			 URL u = new URL(path);                 
//			 response.setContentType(u.openConnection().getContentType());                 
//			 response.setHeader("Content-Disposition", "inline; filename="+(file.getName()).getBytes("gbk"));             //文件名应该编码成UTF-8             
//		 }else{ //纯下载方式                  
//			 response.setContentType("application/x-msdownload");                                    
//			 response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(title,"UTF-8"));                                
//		 }
		                                     
		 try {
			String[] param = {"id:序号:1500","meetingName:会议名称:10000","compere:会议主持:4500"
					,"startDateTime:会议开始时间:5000","meetinfSite:会议地点:5500","applyDateTime:报名时间:5000","ISARRANGEANPAI:状态:5000"};
			response.setContentType("application/x-msdownload");
			response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(title + ".xls","UTF-8"));
			out = response.getOutputStream();
			ExcelHelper.exportExcel(list, param, title, out);
			if(out != null){
				out.close();
			}
		 } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		 } catch (IOException e) {
			e.printStackTrace();
		 }
		 return null;          
		 
//		return new ModelAndView("app/jw/meeting/export/exportNoArr");
	}
	/**
	 * 操作--导出（未安排的）会议信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView exportNext(HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		Organise org = getLoginUserOrg(request);
		map.put("orgId", org.getPartyid());
		List<Map<String, Object>> list = meetingService.exportMeeting(map);
		request.setAttribute("title", "委领导参与的会议计划");
		request.setAttribute("sourceDate", request.getParameter("beginDate"));
		request.setAttribute("lis", list);
		return new ModelAndView("app/jw/meeting/export/exportNext");
	}
	/**
	 * 操作--导出每日处理会议信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView exportDaily(HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		Organise org = getLoginUserOrg(request);
		map.put("orgId", org.getPartyid());
		List<Map<String, Object>> list = meetingService.exportMeeting(map);
		request.setAttribute("sourceDate", request.getParameter("beginUpdate"));
		request.setAttribute("lis", list);
		return new ModelAndView("app/jw/meeting/export/exportDaily");
	}
	/**
	 * 操作--导出每日处理会议信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView exportWeekly(HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		Organise org = getLoginUserOrg(request);
		map.put("orgId", org.getPartyid());
		List<Map<String, Object>> list = meetingService.exportMeeting(map);
		request.setAttribute("sourceDate", request.getParameter("beginUpdate") + "~" + request.getParameter("endUpdate"));
		request.setAttribute("lis", list);
		return new ModelAndView("app/jw/meeting/export/exportWeekly");
	}
	/**
	 * 获取当前登录用户所在的单位
	 * @param request
	 * @return
	 */
	private Organise getLoginUserOrg(HttpServletRequest request){
		Organise org = null;
		String partyId = getHandleUserLogin().getPartyId(request);
		if(!StringUtil.isEmpty(partyId)){
			org = personService.getDefaultOrgan(partyId);
		}
		return org;
	}
	/**
	 * 自动组装委外会议请求数据
	 * @param request
	 * @return
	 */
	private Meeting setExternalMeeting(HttpServletRequest request){
		Meeting meet = null;
		String oid = request.getParameter("oid");
		if(!StringUtil.isEmpty(oid)){
			meet = new Meeting();
			meet.setOid(oid);
			meet.setCompere(valueOf(request.getParameter("compere")));
			meet.setCompereManagerOid(valueOf(request.getParameter("compereManagerOid")));
			meet.setEndDateTime(valueOf(request.getParameter("endDateTime")));
			meet.setMeetinfSite(valueOf(request.getParameter("meetinfSite")));
			meet.setMeetingDemand(valueOf(request.getParameter("meetingDemand")));
			meet.setMeetingName(valueOf(request.getParameter("meetingName")));
			meet.setMeetingNature(1);
			meet.setNotificationDate(valueOf(request.getParameter("notificationDate")));
			meet.setNotificationOrgId(valueOf(request.getParameter("notificationOrgId")));
			meet.setNotificationWay(valueOf(request.getParameter("notificationWay")));
			meet.setStartDateTime(valueOf(request.getParameter("startDateTime")));
			meet.setShortMsg(valueOf(request.getParameter("shortMsg")));
			meet.setTitle(valueOf(request.getParameter("title")));
			meet.setCreateUserId(valueOf(request.getParameter("createUserId")));
			meet.setContacts(valueOf(request.getParameter("contacts")));
			meet.setPhone(valueOf(request.getParameter("phone")));
			//时段从会议开始时间中获取 mod by chyf at 2015-10-08
			if(meet.getStartDateTime()!=null && !"".equals(meet.getStartDateTime())){
				String hour = meet.getStartDateTime().substring(11,13);
				String _sd = "下午";
				if(Integer.parseInt(hour)<=12){
					_sd = "上午";
				}
				meet.setPeriod(_sd);
			}
			
			meet.setCreateOrg(valueOf(request.getParameter("createOrg")));
			meet.setCreateUserName(valueOf(request.getParameter("createUserName")));
			meet.setParticipants(valueOf(request.getParameter("personNames")));
			meet.setApplyDateTime(valueOf(request.getParameter("applyDateTime")));
			meet.setLastUpdateTime(valueOf(request.getParameter("lastUpdateTime")));
			//添加设置会议安排标志
			meet.setMeetingTag(valueOf(request.getParameter("meetingTag")));
			String str = valueOf(request.getParameter("isShortMsg"));
			if(!StringUtil.isEmpty(str)){
				meet.setIsShortMsg(Integer.valueOf(str));
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(StringUtil.isEmpty(meet.getApplyDateTime()) && !StringUtil.isEmpty(meet.getStartDateTime())){
				try {
					Date day = sdf.parse(meet.getStartDateTime());
					meet.setApplyDateTime(DateUtil.formatYearMonthDay(DateHelper.getDateOfDay(day, -1)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if(!StringUtil.isEmpty(meet.getApplyDateTime())){
				try {
					System.out.println(meet.getApplyDateTime().substring(0,10));
					Date day = sdf.parse(meet.getApplyDateTime().substring(0,10));
					meet.setWarningDate(DateUtil.formatYearMonthDay(DateHelper.getDateOfDay(day, -1)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			//add by chyf at 2015-09-24
			//增加秘书接收功能
			meet.setFromDept(valueOf(request.getParameter("fromDept")));
			meet.setSecretary(valueOf(request.getParameter("secretary")));
			meet.setSecretarypid(valueOf(request.getParameter("secretarypid")));
			//为空或者已退回，再次发送时设为未处理
			if("".equals(request.getParameter("receivestatus"))||"3".equals(request.getParameter("receivestatus"))){
				meet.setReceivestatus("0");
			}else{
				meet.setReceivestatus(valueOf(request.getParameter("receivestatus")));
			}
			meet.setReceivetime(valueOf(request.getParameter("receivetime")));
			meet.setInstructions(valueOf(request.getParameter("instructions")));
			meet.setOpinion(valueOf(request.getParameter("opinion")));
			meet.setRemarks(valueOf(request.getParameter("remarks")));
			meet.setJoinMeeting(valueOf(request.getParameter("joinMeeting")));
			meet.setJbr(valueOf(request.getParameter("jbr")));
			meet.setShr(valueOf(request.getParameter("shr")));
		}
		return meet;
	}
	
	/**
	 * 自动组装委外会议参会人员请求数据
	 * @param request
	 * @return
	 */
	private List<Participant> setParticipant(HttpServletRequest request){
		List<Participant> list = null;
		String oid = request.getParameter("oid");
	
		if(!StringUtil.isEmpty(oid)){
			String person = request.getParameter("person");
			String ext1 = request.getParameter("ext1");
			String bgsfzrid = request.getParameter("bgsfzrid");
			String bgsfzrname = request.getParameter("bgsfzrname");
			String bgszrid = request.getParameter("bgszrid");
			String bgszrname = request.getParameter("bgszrname");
		
			if(!StringUtil.isEmpty(person)){
				String[] array = person.split(",");
				list = new ArrayList<Participant>();
				Participant p = null;
				String dateTime = DateUtil.getCurrentDateTime();
				String status = "";
//				if(!StringUtil.isEmpty(request.getParameter("saveFlag"))){
//					status = MeetingComm.STATUS_WJS;
//				}
				for (String str : array) {
					p = new Participant();
					p.setOid(StringHelper.getUUID32());
					p.setMeetingOid(oid);
					p.setNoticeDateTime(dateTime);
					p.setReceptionStatus(status);
					p.setPersonOid(str.split(":")[0]);
					p.setIsParticipant(Integer.valueOf(str.split(":")[1]));
					p.setPersonName(str.split(":")[2]);
					if(str.split(":").length == 4){
						p.setExt2(str.split(":")[3]);
					}
					if(!StringUtil.isEmpty(ext1) && ext1.equals("2")){//副主任
						p.setBgsfzrid(bgsfzrid);
						p.setBgsfzrname(bgsfzrname);
					}
					if(!StringUtil.isEmpty(ext1) && ext1.equals("3")){//主任
						p.setBgszrid(bgszrid);
						p.setBgszrname(bgszrname);
					}
					
					list.add(p);
				}
			}
		}
		return list;
	}
	/**
	 * 自动组装返回数据
	 * @param request
	 * @param oid
	 */
	private void setReturnData(HttpServletRequest request,String oid){
		Meeting meet  = null;
		String saveFlag = "";
		if(StringUtil.isEmpty(oid)){
			meet = new Meeting();
			meet.setOid(StringHelper.getUUID32());
			Organise org = getLoginUserOrg(request);
			if(org != null){
				meet.setNotificationOrgId(org.getName());
			}
			meet.setNotificationDate(DateUtil.getCurrentDate());
			meet.setCreateUserId(super.getCuruserPartyId(request));
			meet.setCreateOrg(org.getPartyid());
			meet.setCreateUserName(super.getCuruserUserName(request));
			saveFlag = "add";
		}else{
			meet = meetingService.queryByOid(oid);
			if(!StringUtil.isEmpty(meet.getStatus()) && meet.getStatus().equals(MeetingComm.STATUS_WFB)){
				meet.setNotificationDate(DateUtil.getCurrentDate());
			}
			List<Participant> list = meetingService.getParticipants(oid);
			if(list != null){
				StringBuffer person = new StringBuffer();
				StringBuffer personOids = new StringBuffer();
				StringBuffer personNames = new StringBuffer();
				int num = list.size();
				Participant p = null;
				for (int i = 0;i < num;i++) {
					p = list.get(i);
					if(i == num - 1){
						person.append(p.getPersonOid()).append(":").append(p.getIsParticipant()).append(":").append(p.getPersonName());
						personOids.append(p.getPersonOid());
						personNames.append(p.getPersonOid()).append(":").append(p.getIsParticipant());
					}else{
						person.append(p.getPersonOid()).append(":").append(p.getIsParticipant()).append(":").append(p.getPersonName()).append(",");
						personOids.append(p.getPersonOid()).append(",");
						personNames.append(p.getPersonOid()).append(":").append(p.getIsParticipant()).append(",");
					}
				}
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("meetingOid", oid);
				param.put("isParticipant", 1);
				request.setAttribute("replayOrg", meetingService.queryParti(param));

				param.put("isParticipant", 0);
				param.put("ext1", 1);
				request.setAttribute("replayOrgBig", meetingService.queryParti(param));
				
				param.put("isNew", "0");
				request.setAttribute("replayPerson", jwApplyService.getApply(param));
				request.setAttribute("person", person.toString());
				request.setAttribute("personOids", personOids.toString());
				request.setAttribute("personNames", personNames.toString());
			}
			saveFlag = "edit";
		}
		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("parentId", MeetingComm.JW_WJG_ORGID);
//		map.put("orgId", MeetingComm.JW_WLD_ORGID);
		map.put("role", "机关处室");
//		List<Organise> list = meetingService.queryOrg(map);
		List<JwPerson> list = jwPersonService.queryJwPersons(map);
		request.setAttribute("orgList", list);
		//会议通知方式
		request.setAttribute("tzfs", dic.getDictList("notificationWay"));
		request.setAttribute("zcld", dic.getDictList("hyzcld"));
		request.setAttribute("obj", meet);
		request.setAttribute("today", DateUtil.getCurrentDate());
		request.setAttribute("saveFlag", saveFlag);
	}
	
	private void setRuturnUrl(HttpServletRequest request){
		String url = request.getHeader("Referer");
		if(url != null){
			url = url.substring(url.lastIndexOf("/")+1);
		}
		request.setAttribute("returnUrl", url);
	}
	/**
	 * 跳转--参会人员选择页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toSelectParticipants(HttpServletRequest request,HttpServletResponse response){
//		request.setAttribute("personList", meetingService.queryPersonByOrgId(MeetingComm.JW_WLD_ORGID));
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("parentId", MeetingComm.JW_WJG_ORGID);
//		map.put("orgId", MeetingComm.JW_WLD_ORGID);
//		request.setAttribute("orgList", meetingService.queryOrg(map));
		Map<String, Object> param = new HashMap<String, Object>();
//		param.put("role", "委领导");
//		request.setAttribute("personList", jwPersonService.queryJwPersons(param));
		request.setAttribute("personList", leaderPlanService.findByOrgLeaders());//委领导 查询 by add zhaodong
		param.put("role", "机关处室");
		request.setAttribute("orgList", jwPersonService.queryJwPersons(param));
		return new ModelAndView("app/jw/dic/selectParticipants");
	}
	/**
	 * 跳转委外会议管理列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView index(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("app/jw/meeting/external/index");
	}
	/**
	 * 跳转委外会议管理列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView inquirywld(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("app/jw/meeting/external/inquirywld");
	}
	
	/**
	 * 跳转委外会议管理列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView inquiry(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("app/jw/meeting/external/inquiry");
	}
	/**
	 * 跳转--未安排会议管理列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView noArrange(HttpServletRequest request,HttpServletResponse response){
		request.setAttribute("isArrange", 0);
		request.setAttribute("meetingTag", 0);
		//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		request.setAttribute("sourceDate",sdf.format(DateHelper.getDateOfDay(new Date(), -1)));
		request.setAttribute("today",DateUtil.getCurrentDate());
	
		return new ModelAndView("app/jw/meeting/external/noArrange");
		
	}
	/**
	* @Title: noArrangeReadOnly
	* @Description: TODO(跳转--未安排会议管理列表页面只读的)
	* @param @param request
	* @param @param response
	* @param @return    设定文件
	* @return ModelAndView    返回类型
	* @throws
	*/
	public ModelAndView noArrangeReadOnly(HttpServletRequest request,HttpServletResponse response){
		request.setAttribute("isArrange", 0);
		request.setAttribute("meetingTag", 0);
		//
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		request.setAttribute("sourceDate",sdf.format(DateHelper.getDateOfDay(new Date(), -1)));
		request.setAttribute("today",DateUtil.getCurrentDate());
	
		return new ModelAndView("app/jw/meeting/external/noArrangeReadOnly");
		
	}
	
	/**
	 * 跳转--隔天会议管理列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView next(HttpServletRequest request,HttpServletResponse response){
		request.setAttribute("startDate", DateUtil.formatYearMonthDay(DateHelper.getDateOfDay(new Date(), 1)));
		return new ModelAndView("app/jw/meeting/inquiry/next");
	}
	public ModelAndView nextReadOnly(HttpServletRequest request,HttpServletResponse response){
		request.setAttribute("startDate", DateUtil.formatYearMonthDay(DateHelper.getDateOfDay(new Date(), 1)));
		return new ModelAndView("app/jw/meeting/inquiry/nextReadOnly");
	}
	/**
	 * 时间
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getDate(HttpServletRequest request,HttpServletResponse response){
		String startDate = request.getParameter("startDate");
		String inc = request.getParameter("inc");
		if(!StringUtil.isEmpty(startDate) && !StringUtil.isEmpty(inc)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date day = sdf.parse(startDate);
				day = DateHelper.getDateOfDay(day, Integer.valueOf(inc));
				startDate = DateUtil.formatYearMonthDay(day);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String msg = "{startDate:\"" + startDate + "\"}";
		super.outJson(msg, response);
		return null;
	}
	/**
	 * 时间
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView getDateOfWeek(HttpServletRequest request,HttpServletResponse response){
		String startDate = request.getParameter("startDate");
		String inc = request.getParameter("inc");
		String msg = "";
		if(!StringUtil.isEmpty(startDate) && !StringUtil.isEmpty(inc)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Map<String, Object>  weekMap = null;
			try {
				Date day = sdf.parse(startDate);
				day = DateHelper.getDate(day, Integer.valueOf(inc));
				weekMap = DateHelper.getDayOfWeek(day);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			msg = "{startDate:\"" + weekMap.get("monday") + "\",endDate:\"" + weekMap.get("sunday") + "\"}";
		}
		
		super.outJson(msg, response);
		return null;
	}
	/**
	 * 加载委外会议信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView query(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		Organise org = getLoginUserOrg(request);
//		map.put("personId", super.getCuruserPartyId(request));
		map.put("orgId", org.getPartyid());
		Page<Map<String,Object>> page = meetingService.selectMeetingByCreatorAp(map, start, pageSize);
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	
	/**
	* @Title: queryReadOnly
	* @Description: TODO(未安排的会员的只读查询)
	* @param @param request
	* @param @param response
	* @param @return    设定文件
	* @return ModelAndView    返回类型
	* @throws
	*/
	public ModelAndView queryReadOnly(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		//Organise org = getLoginUserOrg(request);
		//map.put("personId", super.getCuruserPartyId(request));
		//map.put("orgId", org.getPartyid());
		Page<Map<String,Object>> page = meetingService.selectMeetingByCreatorAp(map, start, pageSize);
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	
	public ModelAndView querywld(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");

		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		

		Page<Map<String,Object>> page = meetingService.selectMeeting(map, start, pageSize);
		
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	
	/**
	 * 加载隔天会议
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryGt(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		Organise org = getLoginUserOrg(request);
//		map.put("personId", super.getCuruserPartyId(request));
		map.put("orgId", org.getPartyid());
		Page<Map<String,Object>> page = meetingService.selectMeetingByCreator(map, start, pageSize);
		
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	public ModelAndView queryGtReadOnly(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		//Organise org = getLoginUserOrg(request);
//		map.put("personId", super.getCuruserPartyId(request));
		//map.put("orgId", org.getPartyid());
		Page<Map<String,Object>> page = meetingService.selectMeetingByCreator(map, start, pageSize);
		
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	
	/**
	 * 跳转--我的会议管理列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView indexOfMine(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("app/jw/meeting/external/indexOfMine");
	}
	
	//办公室待审核会议页面
	public ModelAndView indexOfBgs(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("app/jw/meeting/external/indexOfBgs");
	}
	//办公室待审核会议页面
	public ModelAndView indexOfBgsYb(HttpServletRequest request,HttpServletResponse response){
		return new ModelAndView("app/jw/meeting/external/indexOfBgsYb");
	}
	/**
	 * 跳转--每日会议管理列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView daily(HttpServletRequest request,HttpServletResponse response){
		request.setAttribute("startDate", DateUtil.getCurrentDate());
		return new ModelAndView("app/jw/meeting/inquiry/daily");
	} 
	
	/**
	 * 跳转--每日会议管理列表页面
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView weekly(HttpServletRequest request,HttpServletResponse response){
		request.setAttribute("week", DateHelper.getDayOfWeek(new Date()));
		return new ModelAndView("app/jw/meeting/inquiry/weekly");
	}
	
	public ModelAndView leader(HttpServletRequest request,HttpServletResponse response){
		List<Person> list =  meetingService.queryPersonByOrgId(MeetingComm.JW_WLD_ORGID);
		StringBuffer sb = new StringBuffer();
		if(list != null ){
			int num = list.size();
			Person p = null;
			for (int i = 0;i < num;i++) {
				p = list.get(i);
				if(i == num - 1){
					sb.append("['").append(p.getPartyid()).append("','").append(p.getName()).append("']");
				}else{
					sb.append("['").append(p.getPartyid()).append("','").append(p.getName()).append("'],");
				}
			}
		}
		request.setAttribute("person", sb.toString());
		return new ModelAndView("app/jw/meeting/external/leader");
	}
	/**
	 * 加载委领导会议信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryLeader(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		Page<Map<String,Object>> page = meetingService.selectLeaderDef(map, start, pageSize);
		
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	/**
	 * 跳转--机关处室参会信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView offices(HttpServletRequest request,HttpServletResponse response){
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("parentId", MeetingComm.JW_WJG_ORGID);
//		map.put("orgId", MeetingComm.JW_WLD_ORGID);
//		List<Organise> list = meetingService.queryOrg(map);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("role", "机关处室");
		List<JwPerson> list = jwPersonService.queryJwPersons(param);
		StringBuffer sb = new StringBuffer();
		if(list != null ){
			int num = list.size();
//			Organise p = null;
//			for (int i = 0;i < num;i++) {
//				p = list.get(i);
//				if(i == num - 1){
//					sb.append("['").append(p.getPartyid()).append("','").append(p.getName()).append("']");
//				}else{
//					sb.append("['").append(p.getPartyid()).append("','").append(p.getName()).append("'],");
//				}
//			}
			JwPerson person = null;
			for (int i = 0;i < num;i++) {
				person = list.get(i);
				if(i == num - 1){
					sb.append("['").append(person.getOid()).append("','").append(person.getPersonName()).append("']");
				}else{
					sb.append("['").append(person.getOid()).append("','").append(person.getPersonName()).append("'],");
				}
			}
		}
		request.setAttribute("person", sb.toString());
		return new ModelAndView("app/jw/meeting/external/offices");
	}
	/**
	 * 加载机关处室会议信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryOffices(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
//		Page<Map<String,Object>> page = meetingService.selectOffices(map, start, pageSize);
		Page<Map<String,Object>> page = meetingService.selectRoleOff(map, start, pageSize);
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	/**
	 * 加载会议信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryOfPartiBgs(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		map.put("personId", super.getCuruserPartyId(request));
		Page<Map<String,Object>> page = meetingService.selectMeetingByParticipantBgs(map, start, pageSize);
		
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	public ModelAndView queryOfPartiBgsYb(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		map.put("personId", super.getCuruserPartyId(request));
		Page<Map<String,Object>> page = meetingService.selectMeetingByParticipantBgsYb(map, start, pageSize);
		
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	
	
	/**
	 * 加载会议信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryOfParti(HttpServletRequest request,HttpServletResponse response){
		String startstr = request.getParameter("start");
		
		String pageSizestr = request.getParameter("pageSize");
		int start = 0;
		int pageSize = Page.DEFAULT_PAGE_SIZE;
		
		if(UtilValidate.isNotEmpty(startstr))
			start = Integer.parseInt(startstr);
		if(UtilValidate.isNotEmpty(pageSizestr))
			pageSize = Integer.parseInt(pageSizestr);
		Map<String, Object> map = filterBlank(request.getParameterMap(),request) ;
		map.put("personId", super.getCuruserPartyId(request));
		Organise org = getLoginUserOrg(request);
		map.put("orgId", org.getPartyid());
		
		List list = leaderPlanService.findByLeader(super.getCuruserPartyId(request));
		if(list != null && list.size()>0){
			map.put("ext1",	"1");
		}
		
		Page<Map<String,Object>> page = meetingService.selectMeetingByParticipant(map, start, pageSize);
		
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	/**
	 * 跳转--参会人员信息页面
	 * @return
	 */
	public ModelAndView toParticipantsPage(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		if(!StringUtil.isEmpty(oid)){
			Meeting meet = meetingService.queryByOid(oid);
			StringBuffer sb = new StringBuffer();
			sb.append(meet.getMeetingName()).append("于").append(meet.getStartDateTime())
				.append("在").append(meet.getMeetinfSite()).append("召开。");
			request.setAttribute("msg", sb.toString());
		}
		request.setAttribute("oid", oid);
		return new ModelAndView("app/jw/meeting/external/participants");
	}
	/**
	 * 加载参会人员信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView queryParticipants(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		Page<Map<String,Object>> page = meetingService.queryParticipantsByMeetingOid(oid);
		PageUtil<Map<String,Object>> out=new PageUtil<Map<String,Object>>(page);		
		super.outJson(out, response);
		return null;
	}
	/**
	 * 跳转--发送会议通知
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView saveAndNotice(HttpServletRequest request,HttpServletResponse response){
		Meeting meet = setExternalMeeting(request);
		meet.setIsShortMsg(1);
		List<Participant> list = setParticipant(request);
		for (Participant participant : list) {
			if(participant.getIsParticipant()==1 || participant.getIsParticipant()==null){
				participant.setReceptionStatus(MeetingComm.STATUS_WJS);
			}
			participant.setExt1("0");//非委领导
		}
		if(StringUtil.isEmpty(meet.getParticipants()) && StringUtil.isEmpty(meet.getMeetingDemand())){
			meet.setIsArrange(0);
		}else{
			meet.setIsArrange(1);
		}
		// 删除之前的处室
		meetingService.delParticipants(meet.getOid());
		
		boolean flag = meetingService.saveOrUpdate(meet, list,request.getParameter("saveFlag"));
		if(flag){
			flag = sendMsg(request);
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	//委领导 的请示
	public ModelAndView saveAndNoticeBig(HttpServletRequest request,HttpServletResponse response){
		Meeting meet = setExternalMeeting(request);
		meet.setIsShortMsg(1);
		List<Participant> list = setParticipant(request);
		for (Participant participant : list) {
			if(participant.getIsParticipant()==0 || participant.getIsParticipant()==null){
				participant.setReceptionStatus(MeetingComm.STATUS_WJS);
			}
			String ext1 = request.getParameter("ext1");
			participant.setExt1(ext1);
		}
		if(StringUtil.isEmpty(meet.getParticipants()) && StringUtil.isEmpty(meet.getMeetingDemand())){
			meet.setIsArrange(0);
		}else{
			meet.setIsArrange(1);
		}
		
		boolean flag = meetingService.saveOrUpdateBig(meet, list,request.getParameter("saveFlag"));
		if(flag){
			flag = sendMsg(request);//TODO
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	/**
	 * 处室经办人发送短信通知
	 * @param request
	 * @return
	 */
	private boolean sendMsg(HttpServletRequest request){
		String message = request.getParameter("message");
		if(StringUtil.isEmpty(message)){
			return true;
		}
		String objs = request.getParameter("objs");
		String oid = request.getParameter("oid");
		boolean flag = false;
		if(!StringUtil.isEmpty(message) && !StringUtil.isEmpty(objs) && !StringUtil.isEmpty(oid)){
			String[] array = objs.split(",");
			List<String> list = new ArrayList<String>();
			for (String str : array) {
				list.add(str.split(":")[0]);
			}
			String[] deptIds = list.toArray(new String[list.size()]);
			List<Map<String, Object>> lis = jwMeetingMsgService.getContacts(deptIds);
			if(lis != null && lis.size() > 0){
				List<JwMeetingMsg> msgList = new ArrayList<JwMeetingMsg>();
				JwMeetingMsg msg = null;
				for (Map<String, Object> map : lis) {
					msg = new JwMeetingMsg();
					msg.setContent(message);
					msg.setMeetingOid(oid);
					msg.setMobile((String) map.get("MOBILE"));
					msg.setRecipients((String) map.get("NAME"));
					msg.setSendTime(DateUtil.getCurrentDateTime());
					msg.setSendUserId(super.getCuruserLoginId(request));
					msg.setSendUserName(super.getCuruserUserName(request));
					msgList.add(msg);
					smsService.sendSms(msg.getMobile(), message, JwSmsComm.JWSMS_TYPE_MEETING, oid, 
							super.getCuruserUserName(request), super.getCuruserLoginId(request));
				}
//				flag = jwMeetingMsgService.sendMsg(msgList);
				flag  = true;
			}
		}
		return flag;
	}
//	private List<Participant> getNoticeOrg(HttpServletRequest request){
//		List<Participant> list = null;
//		String oid = request.getParameter("oid");
//		String objs = request.getParameter("objs");
//		if(!StringUtil.isEmpty(oid) && !StringUtil.isEmpty(objs)){
//			String[] array = objs.split(",");
//			list = new ArrayList<Participant>();
//			Participant p = null;
//			String dateTime = DateUtil.getCurrentDateTime();
//			String status = MeetingComm.STATUS_WJS;
//			for (String str : array) {
//				p = new Participant();
//				p.setOid(StringHelper.getUUID32());
//				p.setMeetingOid(oid);
//				p.setNoticeDateTime(dateTime);
//				p.setReceptionStatus(status);
//				p.setPersonOid(str.split(":")[0]);
//				p.setPersonName(str.split(":")[1]);
//				p.setIsParticipant(1);
//				list.add(p);
//			} 
//		}
//		return list;
//	}
	/**
	 * 组装查询请求
	 * @param map 页面请求集合
	 * @param request
	 * @return
	 */
	private Map<String,Object> filterBlank(Map<String,Object> map,HttpServletRequest request){
		Map<String,Object> returnMap=new HashMap<String,Object>();
		for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			String[] array=(String[]) map.get(key);
			if(UtilValidate.isNotEmpty(array[0]))
				returnMap.put(key, array[0]);
		}
		String endDate = (String) returnMap.get("endDate");
		String beginDate = (String) returnMap.get("beginDate");
		String beginDateAp = (String) returnMap.get("beginDateAp");
		String endDateAp = (String) returnMap.get("endDateAp");
		String sourceDate = (String) returnMap.get("sourceDate");
		if(!StringUtil.isEmpty(sourceDate)){
			endDate = sourceDate;
			returnMap.put("beginDate", sourceDate + " 23:59:59");
		}
		if(!StringUtil.isEmpty(beginDate)){
			beginDate = beginDate + " 23:59:59";
			returnMap.put("beginDate", beginDate);
		}
		if(!StringUtil.isEmpty(endDate)){
			endDate = endDate + " 00:00:00";
			returnMap.put("endDate", endDate);
		}
		if(!StringUtil.isEmpty(beginDateAp)){
			beginDateAp = beginDateAp + " 00:00:00";
			returnMap.put("beginDateAp", beginDateAp);
		}
		if(!StringUtil.isEmpty(endDateAp)){
			endDateAp = endDateAp + " 23:59:59";
			returnMap.put("endDateAp", endDateAp);
		}
		returnMap.put("meetingNature", 1);//设置查询会议性质--委外会议
		return returnMap;
	}
	
	/**
	 * 重设请求参数
	 * @param request
	 * @param keys 参数列名
	 * @return
	 */
	private Map<String, Object> getRequestMap(HttpServletRequest request,String[] keys){
		Map<String, Object> param = null;
		if(keys != null && keys.length > 0){
			param = new HashMap<String, Object>();
			String value = "";
			for (String key : keys) {
				value = StringHelper.ToString(request.getParameter("exp_" + key));
				if(!StringUtil.isEmpty(value)){
					param.put(key, value);
				}
			}
		}
		return param;
	}
	
	/**
	 * 导出会议台账
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView exportByCreator(HttpServletRequest request,HttpServletResponse response){
		String[] keys = {"meetingName","compere","meetinfSite","participants","applyBegin","applyEnd","beginDateAp","endDateAp","isShortMsg","meetingTag"};
		Map<String, Object> param = getRequestMap(request, keys);
		Organise org = getLoginUserOrg(request);
		param.put("orgId", org.getPartyid());
		List<Map<String, Object>> list = meetingService.exportMeetingLedger(param);
		
		request.setAttribute("lis", list);
		return new ModelAndView("app/jw/meeting/export/exportMeetingLedger");
	}
	/**
	 * 添加收文扫描件
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toAddRec(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		String id = request.getParameter("processId") == null ? "": request.getParameter("processId");
		boolean flag = false;
		if(!StringUtil.isEmpty(oid) && !StringUtil.isEmpty(id)){
			ReceivdLedger rec = receivdLedgerService.getReceivdLedgerById(id);
			//根据流程id查找收文台账
			RegInGoingDocWD regInGoingDocWD = regInGoingDocService.queryByprocessId(id);
			
			List<RegInGoingDocWDAttach> list = regInGoingDocWD.getRegInGoingDocWDAttachs();
			//pdf文件路径
			String filepath="";
			if(list.size()>0){
				for(RegInGoingDocWDAttach attach : list) {
					if(attach.getFileType().equals(new Integer(1))) {//正文
						filepath = Constants.FILE_UPLOAD_PATH+attach.getFilePath();					
					}
				}
			}
			if(!StringUtil.isEmpty(filepath) && rec != null){
				String path = Constants.COMMON_TOP_FILE_UPLOAD_PATH;
				UploadManFile file = new UploadManFile();
				file.setId(StringHelper.getUUID32());
				file.setBizId(oid);
				file.setServerFile("\\sa\\"+file.getId()+".pdf");
				file.setSrcFile("\\meeting\\oid\\"+ file.getId() + "\\" + rec.getRlName() + ".pdf");
				file.setFileExt(".pdf");
				file.setFileId(file.getId());
				file.setFileName(rec.getRlName()+".pdf");
				file.setType("fd");
				file.setUptime(new Date().getTime());
				file.setUpperson(getHandleUserLogin().getUserName(request));
				file.setUppersonid(getHandleUserLogin().getLoginUserId(request));
				file.setUpmodule("meet");
				file.setUpmodulename("meet");
				file.setUpmoduletype("meet");
				file.setIsok("0");
				file.setExtattr2("0");
				file.setFileflag("0");
				File resourseFile = new File(filepath);
				File targetFile = new File(path,file.getServerFile());
				flag = FileUtil.copyFile(resourseFile, targetFile);
				if(flag){
					uploadManService.insert(file);
				}
			}
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	public ModelAndView replyBgs(HttpServletRequest request,HttpServletResponse response){
		boolean flag = false;
		String partiOid = request.getParameter("partiOid");
		String replyComments = request.getParameter("replyComments");
		String ext1 = request.getParameter("ext1");
		String ext2 = request.getParameter("ext2");
		String bj = request.getParameter("banjie"); // 直接当前环节办结---不发送委领导通知 2 办结
		if(!StringUtil.isEmpty(partiOid)){
			Participant parti = meetingService.findParticipantByOid(partiOid);
			if(parti != null){
				String _extq1= parti.getExt1();
				if(StringUtil.isEmpty(replyComments)){
					replyComments = "";
				}
				if(_extq1.equals("2")){
					parti.setFzryj(replyComments);
					parti.setFzrsj(DateUtil.getCurrentDateTime());
				}
				if(_extq1.equals("3")){
					parti.setZryj(replyComments);
					parti.setZrsj(DateUtil.getCurrentDateTime());
				}
				if(ext1.equals("3")){
					String bgszrid = request.getParameter("bgszrid");
					String bgszrname = request.getParameter("bgszrname");
					parti.setBgszrid(bgszrid);
					parti.setBgszrname(bgszrname);
				}
				if("2".equals(bj)){ // 直接当前环节办结---不发送委领导通知
					parti.setFzryj(replyComments);
					parti.setFzrsj(DateUtil.getCurrentDateTime());
					//parti.setReceptionStatus("已由其它渠道通知确认"); //字段长度为8个字节暂时注释
					ext1 = "4";
					
					try {
						String meetingOid = parti.getMeetingOid();
						Meeting meeting = meetingService.queryByOid(meetingOid);
						meeting.setReceivestatus("2");
						meetingService.update(meeting);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				parti.setExt2(ext2);
				parti.setExt1(ext1);
				meetingService.updateParticipant(parti);
			}
			flag = true;
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	public void xgyj(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		String nbyj = request.getParameter("nbyj");
		String fzryj = request.getParameter("fzryj");
		if(!StringUtil.isEmpty(oid)){
			Participant parti = meetingService.findParticipantByOid(oid);
			if(parti != null){
				parti.setExt2(nbyj);
				parti.setFzryj(fzryj == null ? "":fzryj);
				meetingService.updateParticipant(parti);
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", 1);
		super.outJson(map, response);
	}
	
	public ModelAndView updateQsxx(HttpServletRequest request,HttpServletResponse response){
		boolean flag = true;
		String oid = request.getParameter("oid");
		String ext2 = request.getParameter("ext2");
		Participant parti = meetingService.findParticipantByOid(oid);
		parti.setExt2(ext2);
		meetingService.updateParticipant(parti);
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	public ModelAndView reply(HttpServletRequest request,HttpServletResponse response){
		boolean flag = false;
		String partiOid = request.getParameter("partiOid");
		String oid = request.getParameter("oid");
		String objs = request.getParameter("objs");
//		String isWld = request.getParameter("isWld");
		if(!StringUtil.isEmpty(partiOid) && !StringUtil.isEmpty(oid)){
			Participant parti = meetingService.findParticipantByOid(partiOid);
			if(parti != null){
				parti.setReplyComments(StringHelper.ToString(request.getParameter("replyComments")));
				parti.setApplyDateTime(DateUtil.getCurrentDateTime());
				meetingService.updateParticipant(parti);
			}
			flag = true;
			if(!StringUtil.isEmpty(objs)){
				List<JwApply> list = new ArrayList<JwApply>();
				String[] arr = objs.split(",");
				JwApply app = null;
				Organise org = getLoginUserOrg(request);
				String dateTime = DateUtil.getCurrentDateTime();
				String userName = super.getCuruserUserName(request);
				for (String str: arr) {
					app = new JwApply();
					app.setName(str.split(":")[1]);
					app.setApplyOid(str.split(":")[0]);
					app.setMeetingOid(oid);
					app.setOrgId(org.getPartyid());
					app.setOrgName(org.getName());
					app.setApplyDateTime(dateTime);
					app.setIsNew(0);
					app.setCreateUser(userName);
					list.add(app);
				}
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("meetingOid", oid);
				param.put("orgId", org.getPartyid());
				flag = jwApplyService.saveAndUpdate(list, param);
			}
		}
		String msg = "{isTrue:" + flag + "}";
		super.outJson(msg, response);
		return null;
	}
	
	/**
	 * 查看通知及回复信息
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView toReplyInfo(HttpServletRequest request,HttpServletResponse response){
		String oid = request.getParameter("oid");
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("meetingOid", oid);
		param.put("isParticipant", 1);
		request.setAttribute("replayOrg", meetingService.queryParti(param));
		param.put("isNew", "0");
		request.setAttribute("replayPerson", jwApplyService.getApply(param));
		return  new ModelAndView("app/jw/meeting/external/replyInfo");
	}
	
	public void setMeetingService(MeetingService meetingService) {
		this.meetingService = meetingService;
	}
	
	public void setDic(Dictionary dic) {
		this.dic = dic;
	}
	/**
	 * 字符串去空处理方法
	 * @param str
	 * @return
	 */
	private String valueOf(String str){
		if(StringUtil.isEmpty(str)){
			str = "";
		}
		return str;
	}
	
	public void setSmsService(IJwSmsService smsService) {
		this.smsService = smsService;
	}
	
	
	public void setJwPersonService(JwPersonService jwPersonService) {
		this.jwPersonService = jwPersonService;
	}
	
	public void setRegInGoingDocService(RegInGoingDocService regInGoingDocService) {
		this.regInGoingDocService = regInGoingDocService;
	}
	
	public void setReceivdLedgerService(ReceivdLedgerService receivdLedgerService) {
		this.receivdLedgerService = receivdLedgerService;
	}
	
	public void setUploadManService(UploadManService uploadManService) {
		this.uploadManService = uploadManService;
	}
	
	public void setJwApplyService(JwApplyService jwApplyService) {
		this.jwApplyService = jwApplyService;
	}

	public void setJwMeetingMsgService(JwMeetingMsgService jwMeetingMsgService) {
		this.jwMeetingMsgService = jwMeetingMsgService;
	}
	
	//the next two function add by hrl
	public void setLeaderPlanService(LeaderPlanService leaderPlanService) {
		this.leaderPlanService = leaderPlanService;
	}
	public void setWorkingDayService(WorkingDayService workingDayService) {
		this.workingDayService = workingDayService;
	}


	private LeaderPlanService leaderPlanService;//add by hrl
	
	private WorkingDayService workingDayService;//add by hrl

	private JwApplyService jwApplyService;
	
	private JwMeetingMsgService jwMeetingMsgService;
	
	private UploadManService uploadManService;
	//收文service
	private ReceivdLedgerService receivdLedgerService;
	private RegInGoingDocService regInGoingDocService;
	private JwPersonService jwPersonService;
	//短信接口类
	private IJwSmsService smsService;
	//会议业务处理层
	private MeetingService meetingService;
		
	private Dictionary dic;	
	
	private PersonServiceProxy personService=OrganiseFactoryBean.getInstance().getPersonProxy();
}
