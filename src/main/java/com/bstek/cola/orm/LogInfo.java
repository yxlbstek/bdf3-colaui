package com.bstek.cola.orm;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.bstek.cola.excel.annotation.ColumnDesc;

/** 
* 
* @author bob.yang
* @since 2017年12月28日
*
*/

@Entity
@Table(name = "BDF3_LOG_INFO")
public class LogInfo implements Serializable {

	private static final long serialVersionUID = -7486058941071569980L;

	@Id
	@Column(name = "ID_", length = 36)
	@ColumnDesc(label = "ID")
	private String id;
	
	@Column(name = "MODULE_", length = 255)
	@ColumnDesc(label = "所属模块")
	private String module;
	
	@Lob
	@Column(name = "DESC_")
	@ColumnDesc(label = "描述")
	private String desc;
	
	@Column(name = "OPERATION_", length = 100)
	@ColumnDesc(label = "操作")
	private String operation;
	
	@Column(name = "OPERATION_USER_", length = 30)
	@ColumnDesc(label = "操作人")
	private String operationUser;
	
	@Column(name = "OPERATION_USER_NICKNAME_", length = 30)
	@ColumnDesc(label = "操作人昵称")
	private String operationUserNickname;
	
	@Column(name = "OPERATION_DATE_")
	@ColumnDesc(label = "操作时间")
	private Date operationDate;
	
	@Column(name = "CATEGORY_", length = 100)
	@ColumnDesc(label = "日志类型")
	private String category;
	
	@Column(name = "IP_", length = 20)
	@ColumnDesc(label = "IP地址")
	private String ip;
	
	@Column(name = "SOURCE_", length = 255)
	@ColumnDesc(label = "来源")
	private String source;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getOperationUser() {
		return operationUser;
	}

	public void setOperationUser(String operationUser) {
		this.operationUser = operationUser;
	}

	public String getOperationUserNickname() {
		return operationUserNickname;
	}

	public void setOperationUserNickname(String operationUserNickname) {
		this.operationUserNickname = operationUserNickname;
	}

	public Date getOperationDate() {
		return operationDate;
	}

	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	
}
