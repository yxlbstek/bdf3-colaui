package com.bstek.cola.importer.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;


/** 
* 
* @author bob.yang
* @since 2018年7月2日
*
*/
@Entity
@Table(name = "BDF3_MAPPING_RULE")
public class MappingRule implements Serializable {

	private static final long serialVersionUID = -5753033802489025058L;

	@Id
	@Column(name = "ID_", length = 64)
	private String id;
	
	@Column(name = "NAME_", length = 255)
	private String name;
	
	@Column(name = "IMPORTER_SOLUTION_ID_", length = 64)
	private String importerSolutionId;
	
	@Column(name = "EXCEL_COLUMN_")
	private int excelColumn;
	
	@Column(name = "IGNORE_ERROR_FORMAT_DATA_")
	private boolean ignoreErrorFormatData;
	
	@Column(name = "PROPERTY_NAME_", length = 60)
	private String propertyName;
	
	@Transient
	private ImporterSolution importerSolution;
	
	@Transient
	private List<Entry> entries;
	
	@Transient
	private Map<String, String> map;

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

	public String getImporterSolutionId() {
		return importerSolutionId;
	}

	public void setImporterSolutionId(String importerSolutionId) {
		this.importerSolutionId = importerSolutionId;
	}

	public int getExcelColumn() {
		return excelColumn;
	}

	public void setExcelColumn(int excelColumn) {
		this.excelColumn = excelColumn;
	}

	
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	
	public ImporterSolution getImporterSolution() {
		return importerSolution;
	}

	public void setImporterSolution(ImporterSolution importerSolution) {
		this.importerSolution = importerSolution;
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public boolean isIgnoreErrorFormatData() {
		return ignoreErrorFormatData;
	}

	public void setIgnoreErrorFormatData(boolean ignoreErrorFormatData) {
		this.ignoreErrorFormatData = ignoreErrorFormatData;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
	
	public String getMappingValueIfNeed(String key) {
		String value = key;
		if(entries != null && !entries.isEmpty() && StringUtils.isNotEmpty(key)) {
			if (map == null) {
				map = new HashMap<>();
				for (Entry entry : entries) {
					map.put(entry.getKey(), entry.getValue());
				}
			} 
			value = map.get(key);
			if (value == null) {
				throw new RuntimeException("[" + key+ "] 不在枚举范围内。");
			}
		}
		
		return value;
	}

	
}
