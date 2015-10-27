package uy.edu.ucu.ii.twitter_sentiment_analysis.db.dao;

import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dto.TwittDTO;

public class TwittDAO extends BaseDAO<TwittDTO> {
	private static TwittDAO instance;

	private TwittDAO() {
	}

	public static TwittDAO getInstance() {
		if(instance == null) {
			instance = new TwittDAO();
		}
		return instance;
	}
}
