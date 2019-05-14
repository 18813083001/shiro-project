
package com.thorough.library.system.model.entity;


import com.thorough.library.mybatis.persistence.model.entity.IdEntity;
import com.thorough.library.utils.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;
import java.util.Map;

/**
 * 日志Entity
 */
@Table(name = "sys_log")
public class Log extends IdEntity<String> {

	private static final long serialVersionUID = 1L;

	@Column(name = "type")
	private String type; 		// 日志类型（1：接入日志；2：错误日志）

	@Column(name = "title")
	private String title;		// 日志标题

	@Column(name = "remote_addr")
	private String remoteAddr; 	// 操作用户的IP地址

	@Column(name = "user_agent")
	private String requestUri; 	// 操作的URI

	@Column(name = "method")
	private String method; 		// 操作的方式

	@Column(name = "params")
	private String params; 		// 操作提交的数据

	@Column(name = "user_agent")
	private String userAgent;	// 操作用户代理信息

	@Column(name = "exception")
	private String exception; 	// 异常信息

	@Column(name = "create_by")
	protected String createBy;    // 创建者

	@Column(name = "create_date")
	protected Date createDate;    // 创建日期
	
	private Date beginDate;		// 开始日期
	private Date endDate;		// 结束日期
	
	// 日志类型（1：接入日志；2：错误日志）
	public static final String TYPE_ACCESS = "1";
	public static final String TYPE_EXCEPTION = "2";
	
	public Log(){
		super();
	}
	
	public Log(String id){
		super(id);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRemoteAddr() {
		return remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getRequestUri() {
		return requestUri;
	}

	public void setRequestUri(String requestUri) {
		this.requestUri = requestUri;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
	
	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * 设置请求参数
	 * @param paramMap
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setParams(Map paramMap){
		if (paramMap == null){
			return;
		}
		StringBuilder params = new StringBuilder();
		for (Map.Entry<String, String[]> param : ((Map<String, String[]>)paramMap).entrySet()){
			params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
			String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
			params.append(StringUtils.abbr(StringUtils.endsWithIgnoreCase(param.getKey(), "password") ? "" : paramValue, 100));
		}
		this.params = params.toString();
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}