package com.phm.dao;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;

public abstract class GenericDAO<T> implements IDAO<T> {

	EntityManager em;
	protected Class<T> c;

	public GenericDAO() {
		em = Persistence.createEntityManagerFactory("bank-services").createEntityManager();
	}

	@Override
	public void create(T t) {
		try {
			em.getTransaction().begin();
			em.persist(t);
			em.getTransaction().commit();
	    } catch (RollbackException ex) {
	    	throw new EntityExistsException(ex);
	    }
	}

	@Override
	public void update(T t) {
		em.getTransaction().begin();
		em.merge(t);
		em.getTransaction().commit();
	}

	@Override
	public void delete(T t) {
		em.getTransaction().begin();
		em.remove(t);
		em.getTransaction().commit();
	}

	@Override
	public T fetch(Object id) {
		T var;
		em.getTransaction().begin();
		var = (T) em.find(c, id);
		em.getTransaction().commit();
		return var;
	}
}
