package uy.edu.ucu.ii.twitter_sentiment_analysis.db.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

import uy.edu.ucu.ii.twitter_sentiment_analysis.db.HibernateUtil;

public abstract class BaseDAO<T> {

	private Class entityClass;
	
	/**
	 * Lista todos los elementos de clase <T>
	 * @return
	 */
	public List<T> findAll() {
		Session s = HibernateUtil.getSessionFactory().openSession();
		Criteria c = s.createCriteria(entityClass);
		List<T> list = c.list();
		s.close();
		return list;
	}
	
	/**
	 * Salva el objeto en base de datos
	 * @param object
	 * @return
	 */
	public T save(T object) {
		Session s = HibernateUtil.getSessionFactory().openSession();
		Transaction tx= s.beginTransaction();
		s.saveOrUpdate(object);
		tx.commit();
		s.close();
		return object;
	}
	
	
	
}
