package com.bstek.cola.excel.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/** 
* 
* @author bob.yang
* @since 2018年7月2日
*
*/

@Entity
@Table(name = "BDF3_ENTRY")
public class Entry implements Serializable {

	private static final long serialVersionUID = -8761841387298654333L;

	@Id
	@Column(name = "ID_", length = 36)
	private String id;
	
	@Column(name = "KEY_", length = 100)
	private String key;
	
	@Column(name = "VALUE_", length = 100)
	private String value;
	
	@Column(name = "MAPPING_RULE_ID_", length = 36)
	private String mappingRuleId;

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

	public String getMappingRuleId() {
		return mappingRuleId;
	}

	public void setMappingRuleId(String mappingRuleId) {
		this.mappingRuleId = mappingRuleId;
	}
	
}
