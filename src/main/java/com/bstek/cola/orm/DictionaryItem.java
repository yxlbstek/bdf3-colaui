package com.bstek.cola.orm;

import java.io.Serializable;

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
@Table(name = "BDF3_DICTIONARY_ITEM")
public class DictionaryItem implements Serializable {

	private static final long serialVersionUID = 6911824641675616114L;

	@Id
	@Column(name = "ID_", length = 64)
	private String id;
	
	@Column(name = "KEY_", length = 64)
	private String key;
	
	@Column(name = "VALUE_")
	private String value;
	
	@Column(name = "ENABLED_")
	private boolean enabled;
	
	@Column(name = "ORDER_", length = 64)
	private Integer order;
	
	@Column(name = "DICTIONARY_ID_", length = 64)
	private String dictionaryId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(String dictionaryId) {
		this.dictionaryId = dictionaryId;
	}
	
}
