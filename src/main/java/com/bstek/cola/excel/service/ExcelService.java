package com.bstek.cola.excel.service;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Id;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
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
@RestController("cola.excelService")
@RequestMapping("/excel")
public class ExcelService {

	@SuppressWarnings("unchecked")
	@RequestMapping("/importExcel")
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
					List<String> ids = new ArrayList<String>();
					List<Object> objs = new ArrayList<Object>();
					String fieldId = null;
					for (Map<Integer, String> map : entityList) {
						Object obj = clazz.newInstance();
						for (Field field : fields) {
							field.setAccessible(true);
							Id idAn = field.getAnnotation(Id.class);
							MappingRule mappingRule = mappingRuleMap.get(field.getName());
							if (idAn != null) {
								//String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
								//Method method = obj.getClass().getMethod(methodName, field.getType());
								if (mappingRule != null) {
									String cellValue = map.get(mappingRule.getExcelColumn());
									field.set(obj, convertType(field.getType(), cellValue));
									//method.invoke(obj, convertType(field.getType(), cellValue));
									ids.add(cellValue);
									fieldId = field.getName();
								} else {
									//method.invoke(obj, UUID.randomUUID().toString());
									field.set(obj, UUID.randomUUID().toString());
								}
							} else {
								if (mappingRule != null) {
									//String methodName = "set"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
									//Method method = obj.getClass().getMethod(methodName, field.getType());
									String cellValue = map.get(mappingRule.getExcelColumn());
									field.set(obj, convertType(field.getType(), cellValue));
									//method.invoke(obj, convertType(field.getType(), cellValue));
								}
							}
						}
						objs.add(obj);
					}
					Set<String> rs = validateId(clazz, fieldId, ids);
					if (!rs.isEmpty()) {
						result = "导入数据中存在冲突主键【 " + StringUtils.join(rs.toArray(), ",") + " 】";
					} else {
						for (Object obj : objs) {
							JpaUtil.persist(obj);
						}
						result = "成功导入 " + objs.size() + " 条数据！";
					}
					
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

	private Set<String> validateId(Class<T> clazz, String fieldId, List<String> ids) {
		Set<String> result = new HashSet<String>();
		if (!ids.isEmpty()) {
			Set<String> sIds = new HashSet<String>(ids);
			if (sIds.size() != ids.size()) {
				StringBuilder builder = new StringBuilder();
				for (String id : ids) {
					if(builder.indexOf("," + id + ",") > -1) {
						result.add(id);
		            } else {
		                builder.append(",").append(id).append(",");
		            }
				}
			}
			List<Object> objs = JpaUtil
					.linq(clazz)
					.in(fieldId, sIds)
					.list();
			if (!objs.isEmpty()) {
				result = JpaUtil.collect(objs, fieldId);
			}
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
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/exportExcel")
	public void exportExcel(@RequestParam String importerSolutionId, @RequestParam String fileName, @RequestParam List<String> keys, HttpServletResponse response) throws Exception {				
		List<ImporterSolution> importSolutions = JpaUtil
			.linq(ImporterSolution.class)
			.equal("id", importerSolutionId)
			.list();
		if (!importSolutions.isEmpty()) {
			List<MappingRule> mappingRules = JpaUtil
				.linq(MappingRule.class)
				.equal("importerSolutionId", importSolutions.get(0).getId())
				.list();
			if (!mappingRules.isEmpty()) {
				Map<Integer, MappingRule> indexMap = JpaUtil.index(mappingRules, "excelColumn");
				if (fileName == null || "".equals(fileName)) {
					fileName = "Excel数据导出.xls";
				} else {
					fileName += ".xls";
				}
				String sheetName = importSolutions.get(0).getExcelSheetName();
				
				String className = importSolutions.get(0).getEntityClassName();
				Class<T> clazz = (Class<T>) Class.forName(className);
				Field[] fields = clazz.getDeclaredFields();
				String fieldId = null;
				for (Field field : fields) {
					Id idAn = field.getAnnotation(Id.class);
					if (idAn != null) {
						fieldId = field.getName();
					}
				}
				List<Object> objs = JpaUtil.linq(clazz).in(fieldId, keys).list();
				if (!objs.isEmpty()) {
					HSSFWorkbook workbook = new HSSFWorkbook();
					HSSFSheet sheet = workbook.createSheet(sheetName);
					sheet.setDefaultColumnWidth(20);
					HSSFRow row = sheet.createRow(0);
					row.setHeightInPoints(25);
					HSSFCellStyle titleStyle = createStyle(workbook);
					
					for (Entry<Integer, MappingRule> map : indexMap.entrySet()) {
						HSSFCell cell = row.createCell(map.getKey());
						cell.setCellValue(map.getValue().getName());
						cell.setCellStyle(titleStyle);
					}
					
					for (int i = 0; i < objs.size(); i++) {
						row = sheet.createRow(i + 1);
						row.setHeightInPoints(20);
						HSSFCellStyle cellStyle = createCellStyle(workbook);
						for (Entry<Integer, MappingRule> map : indexMap.entrySet()) {
							HSSFCell dataCell = row.createCell(map.getKey());
							dataCell.setCellStyle(cellStyle);
							Field[] fs = objs.get(i).getClass().getDeclaredFields();
							for (Field field : fs) {
								field.setAccessible(true);
								if (map.getValue().getPropertyName().equals(field.getName())) {
									dataCell.setCellValue(field.get(objs.get(i)).toString());
								}
							}
						}
						
					}
					
					String fName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
					response.setContentType("application/x-msdownload");  
		        	response.setCharacterEncoding("UTF-8");
		        	response.setHeader("Content-Disposition", "attachment; filename=\"" + fName + "\"");
		        	OutputStream out = response.getOutputStream();
		        	try {
		        		workbook.write(out);
					} catch (Exception e) {
						String param = new String("数据导出失败".getBytes("UTF-8"), "iso-8859-1");
						response.sendRedirect("http://localhost:8080/bdf3-colaui/exportError?" + param);
					} finally {
						out.close();
					}
				} else {
					String param = new String("没有查询到所需要的导出数据".getBytes("UTF-8"), "iso-8859-1");
					response.sendRedirect("http://localhost:8080/bdf3-colaui/exportError?" + param);
				}
				
			} else {
				String param = new String("该方案没有添加映射关系".getBytes("UTF-8"), "iso-8859-1");
				response.sendRedirect("http://localhost:8080/bdf3-colaui/exportError?" + param);
			}
			
		} else {
			String param = new String("导入方案不存在".getBytes("UTF-8"), "iso-8859-1");
			response.sendRedirect("http://localhost:8080/bdf3-colaui/exportError?" + param);
		}
	}

	private HSSFCellStyle createCellStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		return style;
	}

	private HSSFCellStyle createStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);

		HSSFFont headerFont = (HSSFFont) workbook.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setFontName("黑体");
		headerFont.setFontHeightInPoints((short) 10);
		style.setFont(headerFont);
		
		return style;
	}

}
