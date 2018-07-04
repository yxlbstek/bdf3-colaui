package com.bstek.cola.importer.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Transient;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Type;

import org.apache.commons.lang.ClassUtils;
import org.malagu.linq.JpaUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bstek.cola.importer.filter.EntityManagerFactoryFilter;
import com.bstek.cola.importer.filter.EntityTypeFilter;
import com.bstek.cola.importer.model.Entry;
import com.bstek.cola.importer.model.ImporterSolution;
import com.bstek.cola.importer.model.MappingRule;
import com.bstek.cola.log.annotation.Log;
import com.bstek.cola.utils.FieldUtils;

/** 
* 
* @author bob.yang
* @since 2017年12月18日
*
*/
@RestController("cola.excelController")
@RequestMapping("/api")
public class ExcelController implements ApplicationContextAware {

	private Collection<String> entityManagerFactoryNames;
	
	private Map<String, List<String>> entityClassNameMap = new HashMap<>();
	
	@RequestMapping(path = "/excel/loadImporterSolutions", method = RequestMethod.GET)
	public List<ImporterSolution> loadImporterSolutions(@RequestParam(name = "searchKey", required = false) String searchKey) {
		return JpaUtil
			.linq(ImporterSolution.class)
			.addIf(searchKey)
				.or()
					.like("id", "%" + searchKey + "%")
					.like("name", "%" + searchKey + "%")
			.endIf()
			.desc("createDate")
			.list();
	}
	
	@RequestMapping(path = "/excel/loadMappingRules", method = RequestMethod.GET)
	public List<MappingRule> loadMappingRules(@RequestParam(name = "importerSolutionId", required = false) String importerSolutionId) {
		return JpaUtil
			.linq(MappingRule.class)
			.equal("importerSolutionId", importerSolutionId)
			.asc("excelColumn")
			.list();
	}
	
	@RequestMapping(path = "/excel/loadEntityManagerFactoryNames", method = RequestMethod.GET)
	public Collection<String> loadEntityManagerFactoryNames() {
		return entityManagerFactoryNames;
	}
	
	@RequestMapping(path = "/excel/loadEntityClassNames/{managerFactoryName}", method = RequestMethod.GET)
	public Collection<String> loadEntityClassNames(@PathVariable String managerFactoryName) {
		return entityClassNameMap.get(managerFactoryName);
	}
	
	@RequestMapping(path = "/excel/addImporterSolution", method = RequestMethod.POST)
	@Log(module = "导入管理", category = "系统日志", operation = "新增方案", source = "/excel")
	@Transactional
	public void addImporterSolution(@RequestBody ImporterSolution importerSolution) {
		importerSolution.setCreateDate(new Date());
		JpaUtil.persist(importerSolution);
	}
	
	@RequestMapping(path = "/excel/modifyImporterSolution", method = RequestMethod.PUT)
	@Log(module = "导入管理", category = "系统日志", operation = "修改方案", source = "/excel")
	@Transactional
	public void modifyImporterSolution(@RequestBody ImporterSolution importerSolution) {
		JpaUtil.merge(importerSolution);
	}
	
	@RequestMapping(path = "/excel/removeImporterSolution", method = RequestMethod.POST)
	@Log(module = "导入管理", category = "系统日志", operation = "删除方案", source = "/excel")
	@Transactional
	public void removeImporterSolution(@RequestParam String id) {
		ImporterSolution importerSolution = JpaUtil.getOne(ImporterSolution.class, id);
		JpaUtil.remove(importerSolution);
		List<MappingRule> mappingRules = JpaUtil
			.linq(MappingRule.class)
			.equal("importerSolutionId", id)
			.list();
		if (!mappingRules.isEmpty()) {
			Set<String> ids = JpaUtil.collectId(mappingRules);
			for (MappingRule mr : mappingRules) {
				JpaUtil.remove(mr);
			}
			List<Entry> entrys = JpaUtil
				.linq(Entry.class)
				.in("mappingRuleId", ids)
				.list();
			if (!entrys.isEmpty()) {
				for (Entry entry : entrys) {
					JpaUtil.remove(entry);
				}
			}
		}
	}
	
	@RequestMapping(path = "/excel/addMappingRule", method = RequestMethod.POST)
	@Log(module = "导入管理", category = "系统日志", operation = "新增映射关系", source = "/excel")
	@Transactional
	public void addMappingRule(@RequestBody MappingRule mappingRule) {
		mappingRule.setId(UUID.randomUUID().toString());
		mappingRule.setIgnoreErrorFormatData(false);
		JpaUtil.persist(mappingRule);
	}
	
	@RequestMapping(path = "/excel/modifyMappingRule", method = RequestMethod.PUT)
	@Log(module = "导入管理", category = "系统日志", operation = "修改映射关系", source = "/excel")
	@Transactional
	public void modifyMappingRule(@RequestBody MappingRule mappingRule) {
		JpaUtil.merge(mappingRule);
	}
	
	@RequestMapping(path = "/excel/removeMappingRule", method = RequestMethod.POST)
	@Log(module = "导入管理", category = "系统日志", operation = "删除映射关系", source = "/excel")
	@Transactional
	public void removeMappingRule(@RequestParam String id) {
		MappingRule mappingRule = JpaUtil.getOne(MappingRule.class, id);
		JpaUtil.remove(mappingRule);
		List<Entry> entries = JpaUtil
			.linq(Entry.class)
			.equal("mappingRuleId", id)
			.list();
		if (!entries.isEmpty()) {
			for (Entry entry : entries) {
				JpaUtil.remove(entry);
			}
		}
	}
	
	@RequestMapping(path = "/excel/autoCreateMappingRules", method = RequestMethod.POST)
	@Transactional
	public void autoCreateMappingRules(@RequestParam String importerSolutionId) {
		try {
			ImporterSolution importerSolution = JpaUtil.getOne(ImporterSolution.class, importerSolutionId);
			Class<?> entityClass = ClassUtils.getClass(importerSolution.getEntityClassName());
			List<Field> fields = FieldUtils.getFields(entityClass);
			List<String> propertyNames = getPropertyNames(importerSolution);
			int col = propertyNames.size();

			for (Field field : fields) {
				Transient t = field.getAnnotation(Transient.class);
				String propertyName = field.getName();
				if (t != null 
						|| BeanUtils.getPropertyDescriptor(entityClass, propertyName) == null
						|| contains(propertyNames, propertyName)) {
					continue;
				}
				MappingRule mappingRule = new MappingRule();
				mappingRule.setId(UUID.randomUUID().toString());
				mappingRule.setImporterSolutionId(importerSolution.getId());
				mappingRule.setName(propertyName);
				mappingRule.setPropertyName(propertyName);
				mappingRule.setExcelColumn(col);
				mappingRule.setIgnoreErrorFormatData(false);
				
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					
				}
				JpaUtil.persist(mappingRule);
				col++;
			}
					
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		Map<String, EntityManagerFactory> entityManagerFactoryMap = 
				new HashMap<String, EntityManagerFactory>(applicationContext.getBeansOfType(EntityManagerFactory.class));
				
		Collection<EntityManagerFactoryFilter> sessionFactoryFilters = applicationContext
				.getBeansOfType(EntityManagerFactoryFilter.class).values();
		
		for (EntityManagerFactoryFilter sessionFactoryFilter : sessionFactoryFilters) {
			sessionFactoryFilter.filter(entityManagerFactoryMap);
		}
		
		Collection<EntityTypeFilter> entityTypeFilters = applicationContext
				.getBeansOfType(EntityTypeFilter.class).values();
		
		for (java.util.Map.Entry<String, EntityManagerFactory> entry : entityManagerFactoryMap.entrySet()) {
			List<EntityType<?>> entityTypes = new ArrayList<EntityType<?>>(entry.getValue().getMetamodel().getEntities());
			for (EntityTypeFilter entityTypeFilter : entityTypeFilters) {
				entityTypeFilter.filter(entityTypes);
			}
			List<String> entityClassNames = new ArrayList<String>();
			for (EntityType<?> entityType : entityTypes) {
				if (entityType instanceof Type) {
					entityClassNames.add(entityType.getJavaType().getName());
				}
			}
			entityClassNameMap.put(entry.getKey(), entityClassNames);
		}
		entityManagerFactoryNames = new ArrayList<String>(entityManagerFactoryMap.keySet());		
		
	}
	
	private List<String> getPropertyNames(ImporterSolution importerSolution) {
		return JpaUtil
			.linq(MappingRule.class, String.class)
			.select("propertyName")
			.equal("importerSolutionId", importerSolution.getId())
			.list();
	}
	
	private boolean contains(List<String> propertyNames, String propertyName) {
		for (String p : propertyNames) {
			if (p.equals(propertyName)) {
				return true;
			}
		}
		return false;
	}

}
