package com.silita.biaodaa.common.jdbc;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.concurrent.ConcurrentHashMap;


public class MapperFactory {
	@SuppressWarnings("rawtypes")
	private static ConcurrentHashMap<String,BeanPropertyRowMapper> table=new ConcurrentHashMap<String,BeanPropertyRowMapper>();
	@SuppressWarnings("unchecked")
	public static <T> BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass){
		BeanPropertyRowMapper<T> newInstance=null;
		
		if(table.containsKey(mappedClass.getName())){
			newInstance=table.get(mappedClass.getName());
		}else{
			newInstance = new BeanPropertyRowMapper<T>();
			newInstance.setMappedClass(mappedClass);
			table.put(mappedClass.getName(), newInstance);
		}

		return newInstance;
	}
}
