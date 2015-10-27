package uy.edu.ucu.ii.twitter_sentiment_analysis;

import java.util.concurrent.ConcurrentHashMap;

import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa.dto.SentimentResponse;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto.Tweet;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.HibernateUtil;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dao.TwittDAO;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dto.ConsultaDTO;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dto.TwittDTO;

public class TweetsSentimentProcessor extends Thread {

	private ConcurrentHashMap<Long, Tweet> twitts;
	private ConcurrentHashMap<Long, SentimentResponse> sentiments;
	private ConsultaDTO consulta;
	
	public TweetsSentimentProcessor(ConcurrentHashMap<Long, Tweet> twitts,
			ConcurrentHashMap<Long, SentimentResponse> sentiments, ConsultaDTO consulta) {
		this.twitts = twitts;
		this.sentiments = sentiments;
		this.consulta = consulta;
	}

	@Override
	public void run() {
		while(!this.twitts.isEmpty()) {
			
			if(!this.sentiments.isEmpty()) {
				for(int i = 0; i < this.sentiments.keySet().size(); i++) {
					try {
						Long tweetId = (Long)this.sentiments.keySet().toArray()[i];;
						SentimentResponse sentiment = this.sentiments.remove(tweetId);;
								
						Tweet t = this.twitts.remove(tweetId);
						if(t != null) {
							TwittDTO twitt = new TwittDTO(consulta.getId(), t, sentiment);
							TwittDAO.getInstance().save(twitt);
							System.out.println("Tweets remaining :" + this.twitts.size());
						}
					}catch(Exception e) {
						
					}
				}
			}
			
		}
//		try{
//			HibernateUtil.shutdown();
//		} catch (Exception e) {
//			//Por si 2 hilos no tratan de cerrar la sesión
//		}
	}

}
