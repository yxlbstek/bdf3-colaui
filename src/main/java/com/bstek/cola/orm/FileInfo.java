package com.bstek.cola.orm;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/** 
* 
* @author bob.yang
* @since 2017年12月28日
*
*/

@Entity
@Table(name = "BDF3_FILE_INFO")
public class FileInfo implements Serializable {
	
	private static final long serialVersionUID = -296854638224828657L;

	@Id	
	@Column(name = "ID_", length = 60)
	private String id;
		
	@Column(name = "NAME_", length = 512)
	private String name;
	
	@Column(name = "TYPE_", length = 512)
	private String type;
		
	@Column(name = "SIZE_")
	private long size;
		
	@Column(name = "PATH_", length = 512)
	private String path;
	
	@Column(name = "CREATE_TIME_")
	private Date createDate;
		
	@Column(name = "CREATOR_", length = 60)
	private String creator;
	
	@Column(name = "DESC_", length = 1024)
	private String desc;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
}
