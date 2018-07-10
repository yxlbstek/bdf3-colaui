package com.bstek.cola.excel.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * 
 * @author bob.yang
 * @since 2018年7月2日
 *
 */

@Entity
@Table(name = "BDF3_IMPORTER_SOLUTION")
public class ImporterSolution implements Serializable {

	private static final long serialVersionUID = 662038942924771519L;

	@Id
	@Column(name = "ID_", length = 64)
	private String id;

	@Column(name = "NAME_", length = 60)
	private String name;

	@Column(name = "EXCEL_SHEET_NAME_", length = 60)
	private String excelSheetName;

	@Column(name = "ENTITY_CLASS_NAME_", length = 255)
	private String entityClassName;

	@Column(name = "DESC_", length = 255)
	private String desc;

	@Column(name = "Entity_Manager_Factory_Name_", length = 60)
	private String entityManagerFactoryName;

	@Column(name = "CREATE_DATE_")
	private Date createDate;

	@Transient
	private List<MappingRule> mappingRules = new ArrayList<MappingRule>();

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

	public String getExcelSheetName() {
		return excelSheetName;
	}

	public void setExcelSheetName(String excelSheetName) {
		this.excelSheetName = excelSheetName;
	}

	public String getEntityClassName() {
		return entityClassName;
	}

	public void setEntityClassName(String entityClassName) {
		this.entityClassName = entityClassName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getEntityManagerFactoryName() {
		return entityManagerFactoryName;
	}

	public void setEntityManagerFactoryName(String entityManagerFactoryName) {
		this.entityManagerFactoryName = entityManagerFactoryName;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public List<MappingRule> getMappingRules() {
		return mappingRules;
	}

	public void setMappingRules(List<MappingRule> mappingRules) {
		this.mappingRules = mappingRules;
	}

}
