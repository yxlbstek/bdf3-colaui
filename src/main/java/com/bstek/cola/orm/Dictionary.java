package com.bstek.cola.orm;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.bstek.cola.importer.annotation.ColumnDesc;


/** 
* 
* @author bob.yang
* @since 2017年12月28日
*
*/

@Entity
@Table(name = "BDF3_DICTIONARY")
public class Dictionary implements Serializable {

	private static final long serialVersionUID = -8952157582967054658L;

	@Id
	@Column(name = "ID_", length = 64)
	@ColumnDesc(label = "ID")
	private String id;
	
	@Column(name = "CODE_", length = 64)
	@ColumnDesc(label = "编码")
	private String code;
	
	@Column(name = "NAME_", length = 64)
	@ColumnDesc(label = "名称")
	private String name;
	
	@Column(name = "ORDER_", length = 64)
	@ColumnDesc(label = "排序号")
	private Integer order;
	
	@Transient
	private List<DictionaryItem> dictionaryItems;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DictionaryItem> getDictionaryItems() {
		return dictionaryItems;
	}

	public void setDictionaryItems(List<DictionaryItem> dictionaryItems) {
		this.dictionaryItems = dictionaryItems;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	
}
