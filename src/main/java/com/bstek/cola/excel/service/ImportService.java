package com.bstek.cola.excel.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Id;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.malagu.linq.JpaUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bstek.cola.excel.model.ImporterSolution;
import com.bstek.cola.excel.model.MappingRule;
import com.bstek.cola.utils.DateUtils;


/**
 * 
 * @author bob.yang
 * @since 2018年7月4日
 *
 */
@RestController("cola.importController")
@RequestMapping("/importExcel")
public class ImportService {

	@SuppressWarnings("unchecked")
	@RequestMapping("/import")
	@ResponseBody
	@Transactional
	public String importExcel(@RequestParam(value = "file") MultipartFile file,
			@RequestParam(value = "importerSolutionId") String importerSolutionId) throws Exception {		
		String result = "";
		List<ImporterSolution> importerSolutions = JpaUtil
				.linq(ImporterSolution.class)
				.equal("id", importerSolutionId)
				.list();
		
		if (!importerSolutions.isEmpty()) {
			List<MappingRule> mappingRules = JpaUtil
				.linq(MappingRule.class)
				.equal("importerSolutionId", importerSolutionId)
				.list();
			
			if (!mappingRules.isEmpty()) {
				Map<String, MappingRule> mappingRuleMap = JpaUtil.index(mappingRules, "propertyName");
				Workbook workbook = WorkbookFactory.create(file.getInputStream());
				List<Map<Integer, String>> entityList = new ArrayList<Map<Integer, String>>();
				int sheetSize = workbook.getNumberOfSheets();
				for (int i = 0; i < sheetSize; i++) {
					Sheet sheet = workbook.getSheetAt(i);
					int rowSize = sheet.getLastRowNum() + 1;
					for (int j = 1; j < rowSize; j++) {	// 遍历行  从1开始略过第一行标题行
						Row row = sheet.getRow(j);
						if (row == null) {
							continue;
						}
						int cellSize = row.getLastCellNum();
						Map<Integer, String> entityMap = new HashMap<Integer, String>();// 对应一个数据行
						for (int key = 0; key < cellSize; key++) {	// 遍历列
							Cell cell = row.getCell(key);
							String value = null;
							if (cell != null) {
								value = getCellValue(cell);
							}
							entityMap.put(key, value);
						}
						entityList.add(entityMap);
					}
				}
				
				if (!entityList.isEmpty()) {
					String className = importerSolutions.get(0).getEntityClassName();
					Class<T> clazz = (Class<T>) Class.forName(className);
					Field[] fields = clazz.getDeclaredFields();
					for (Map<Integer, String> map : entityList) {
						Object obj = clazz.newInstance();
						for (Field field : fields) {
							Id idAn = field.getAnnotation(Id.class);
							MappingRule mappingRule = mappingRuleMap.get(field.getName());
							if (idAn != null) {
								String methodName = "set"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
								Method method = obj.getClass().getMethod(methodName, field.getType());
								if (mappingRule != null) {
									String cellValue = map.get(mappingRule.getExcelColumn());
									method.invoke(obj, convertType(field.getType(), cellValue));
								} else {
									method.invoke(obj, UUID.randomUUID().toString());
								}
							} else {
								if (mappingRule != null) {
									String methodName = "set"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
									Method method = obj.getClass().getMethod(methodName, field.getType());
									String cellValue = map.get(mappingRule.getExcelColumn());
									method.invoke(obj, convertType(field.getType(), cellValue));
								}
							}
						}
						JpaUtil.persist(obj);
					}
					result = "成功导入 " + entityList.size() + " 条数据！";
				} else {
					result = "Excel文件中无导入数据！";
				}
				
			} else {
				result = "该方案没有添加映射规则！";
			}
			
		} else {
			result = "导入方案不存在！";
		}
		return result;
	}

	private Object convertType(Class<?> type, String cellValue) {
		Object result = null;
		String strType = type.getSimpleName();
		if (strType.equals("long")) {
			result = Long.parseLong(cellValue);
			
		} else if (strType.equals("Long")) {
			result = Long.valueOf(cellValue);
			
		} else if (strType.equals("int")) {
			result = Integer.parseInt(cellValue);
			
		} else if (strType.equals("Integer")) {
			result = Integer.valueOf(cellValue);
			
		} else if (strType.equals("float")) {
			result = Float.parseFloat(cellValue);
			
		} else if (strType.equals("Float")) {
			result = Float.valueOf(cellValue);
			
		} else if (strType.equals("double")) {
			result = Double.parseDouble(cellValue);
			
		} else if (strType.equals("Double")) {
			result = Double.valueOf(cellValue);
			
		} else if (strType.equals("BigDecimal")) {
			result = new BigDecimal(cellValue);
			
		} else if (strType.equals("boolean") || strType.equals("Boolean")) {
			result = cellValue.equals("0") || cellValue.equals("true") ? true : false;
			
		} else if (strType.equals("Date")) {
			result = DateUtils.stringToDate(cellValue, "yyyy-MM-dd HH:mm:ss");
			
		} else {
			result = cellValue;
			
		}
		return result;
	}

	private String getCellValue(Cell cell) {
		String cellvalue = null;
		switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_NUMERIC: 
				short format = cell.getCellStyle().getDataFormat();
				if (format == 14 || format == 31 || format == 57 || format == 58) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					double value = cell.getNumericCellValue();
					Date date = DateUtil.getJavaDate(value);
					cellvalue = sdf.format(date);
				} else if (HSSFDateUtil.isCellDateFormatted(cell)) {
					Date date = cell.getDateCellValue();
					DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					cellvalue = formater.format(date);
				} else {
					cellvalue = NumberToTextConverter.toText(cell.getNumericCellValue());
				}	
			break;
		
			case HSSFCell.CELL_TYPE_STRING:
				cellvalue = cell.getStringCellValue().replaceAll("'", "''");
			break;
			
			case HSSFCell.CELL_TYPE_BLANK:
				cellvalue = null;
			break;
			default: {
				cellvalue = null;
			}
		}
		return cellvalue;
	}

}
