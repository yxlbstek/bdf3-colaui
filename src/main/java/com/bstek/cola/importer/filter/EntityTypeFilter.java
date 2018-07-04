package com.bstek.cola.importer.filter;

import java.util.List;

import javax.persistence.metamodel.EntityType;

/** 
* 
* @author bob.yang
* @since 2018年7月3日
*
*/
public interface EntityTypeFilter {

	void filter(List<EntityType<?>> entityTypes);
	
}
