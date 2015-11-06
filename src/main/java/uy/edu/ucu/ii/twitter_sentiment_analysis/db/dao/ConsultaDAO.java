package uy.edu.ucu.ii.twitter_sentiment_analysis.db.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import uy.edu.ucu.ii.twitter_sentiment_analysis.db.HibernateUtil;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dto.ConsultaDTO;

public class ConsultaDAO extends BaseDAO<ConsultaDTO>{

	public ConsultaDTO getConsultaForQuery(String query) {
		ConsultaDTO cons = null;
		
		Session s = HibernateUtil.getSessionFactory().openSession();
		Criteria c = s.createCriteria(ConsultaDTO.class);
		c.add(Restrictions.eq("query", query));
	
		cons = (ConsultaDTO)c.uniqueResult();
	
		s.close();
		
		return cons;
	}

	public List<ConsultaDTO> getConsultasActivas() {
		Session s = HibernateUtil.getSessionFactory().openSession();
		Criteria c = s.createCriteria(ConsultaDTO.class);
		c.add(Restrictions.eq("isActive", true));
		List<ConsultaDTO> consultas = c.list();
		s.close();
		
		return consultas;
	}
}
