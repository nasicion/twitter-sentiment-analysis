package uy.edu.ucu.ii.twitter_sentiment_analysis.db.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import uy.edu.ucu.ii.twitter_sentiment_analysis.db.HibernateUtil;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dto.TweetDTO;

public class TweetDAO extends BaseDAO<TweetDTO> {
	private static TweetDAO instance;

	private TweetDAO() {
	}

	public static TweetDAO getInstance() {
		if(instance == null) {
			instance = new TweetDAO();
		}
		return instance;
	}

	/**
	 * Obtiene el mayor tweet id para determinada consulta
	 * @param idConsulta
	 * @return
	 */
	public Long getMaxId(Integer idConsulta) {
		Session s = HibernateUtil.getSessionFactory().openSession();
		Criteria c = s.createCriteria(TweetDTO.class);
		c.add(Restrictions.eq("idConsulta", idConsulta)).
		setProjection(Projections.max("id"));
		
		Long maxId = (Long)c.uniqueResult(); 
		s.close();
		return maxId;
	}
}
