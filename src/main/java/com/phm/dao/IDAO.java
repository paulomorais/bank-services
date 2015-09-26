package com.phm.dao;

public interface IDAO<T> {
	
	public void create(T t);
	
	public void update(T t);
	
	public void delete(T t);
	
	public T fetch (T t);
}
