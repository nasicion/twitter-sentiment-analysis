package uy.edu.ucu.ii.twitter_sentiment_analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa.TextProcessingAPI;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa.dto.SentimentResponse;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.TwitterClient;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto.Tweet;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.HibernateUtil;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dao.ConsultaDAO;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dto.ConsultaDTO;

public class App {

	private static final Integer HILOS_SENTIMENT_API = 50;
	private static final Integer HILOS_SENTIMENT_PROCESS = 50;

	public static void main(String[] args) {
		Long start = System.currentTimeMillis();
		String query = "#BatmanVSuperman";
		
		//Obtener los Tweets
		TwitterClient tc = new TwitterClient();
		ConcurrentHashMap<Long, Tweet> tweets = tc.getNTwitts(query, 1000, true);

		try {
			HibernateUtil.getSessionFactory();
			
			ConsultaDTO consulta = getConsulta(query);
			
			ConcurrentHashMap<Long, SentimentResponse> sentiments = 
					new ConcurrentHashMap<Long, SentimentResponse>(tweets.size()); 
			
			getSentiments(tweets,sentiments, HILOS_SENTIMENT_API);
			processTweetsAndSentiment(tweets, sentiments, consulta, HILOS_SENTIMENT_PROCESS);
			
			HibernateUtil.shutdown();
		}catch(Exception e){ 
			e.printStackTrace();
		} 
		Long end = System.currentTimeMillis();
		System.out.println("Tiempo total: " + (end-start) + "ms");
	}

	/**
	 * Inicia los hilos que procesa los sentiment y los twitss y los guarda en BD 
	 * @param tweets
	 * @param sentiments
	 * @param consulta
	 * @param hilosSentimentProcess 
	 */
	private static void processTweetsAndSentiment(ConcurrentHashMap<Long,Tweet> tweets,
			ConcurrentHashMap<Long, SentimentResponse> sentiments, ConsultaDTO consulta, Integer hilosSentimentProcess) {
		List<TweetsSentimentProcessor> threads = new ArrayList<TweetsSentimentProcessor>(10);
		for (int i = 0; i < hilosSentimentProcess; i++) {
			TweetsSentimentProcessor tsp = new TweetsSentimentProcessor(tweets, sentiments, consulta);
			threads.add(tsp);
			tsp.setName("TweetsAndSentiment Thread " + i);
			tsp.start();
		}
		for(TweetsSentimentProcessor thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		}
	}

	/**
	 * Obtiene los sentiments utilizando threads para mejorar la performance
	 * @param tweets
	 * @param hilosSentimentApi 
	 * @return
	 */
	private static void getSentiments(
			ConcurrentHashMap<Long, Tweet> tweets, ConcurrentHashMap<Long, SentimentResponse> sentiments, Integer hilosSentimentApi) {
		
		ConcurrentLinkedQueue<Tweet> tempTweetMap = new ConcurrentLinkedQueue<Tweet>(tweets.values());
		
		for(int i = 0; i < hilosSentimentApi; i++) {
			Thread t = new TextProcessingAPI(sentiments, tempTweetMap);
			t.setName("Sentiment Thread " + i);
			t.start();
		}
	}

	/**
	 * Obtiene la consulta y si no existe para la query la crea
	 * @param query
	 * @return
	 */
	private static ConsultaDTO getConsulta(String query) {
		ConsultaDAO consultaDAO = new ConsultaDAO();
		
		ConsultaDTO consulta = consultaDAO.getConsultaForQuery(query);
		if(consulta == null) {
			consulta = consultaDAO.save(new ConsultaDTO(query));
		}
		
		return consulta;
	}
}
