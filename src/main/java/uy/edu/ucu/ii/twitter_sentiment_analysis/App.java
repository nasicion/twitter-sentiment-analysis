package uy.edu.ucu.ii.twitter_sentiment_analysis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;

import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa.SentimentAPI;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa.dto.SentimentResponse;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.TwitterClient;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto.Tweet;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.HibernateUtil;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dao.ConsultaDAO;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dao.TweetDAO;
import uy.edu.ucu.ii.twitter_sentiment_analysis.db.dto.ConsultaDTO;

public class App {

	private static final Integer HILOS_SENTIMENT_API = 50;
	private static final Integer HILOS_SENTIMENT_PROCESS = 10;

	public static void pentahoQuery(String query) {
		main(new String[]{query});
	}
	
	public static void main(String[] args) {
		
		
//		if(args.length == 0 || StringUtils.isBlank(args[0])) {
//			System.out.println("Uso: java -jar <jar_twitter_sentiment_analysis>.jar [query]");
//			return;
//		}
		String query = null;
		if(args.length > 0) {
			 query = args[0];
		}
		
		try {
			HibernateUtil.getSessionFactory();
			if(StringUtils.isNoneBlank(query))
			processQuery(query, 2000);
			
			boolean processQuerys = Boolean.getBoolean("processQuerys");
			if(processQuerys) {
				List<ConsultaDTO> consultas = getConsultas();
				ConsultaDAO consultaDAO = new ConsultaDAO();
				for(ConsultaDTO consulta : consultas) {
					consulta.setLastExecution(new Date());
					consultaDAO.save(consulta);
					processQuery(consulta.getQuery(), 2000);
				}
			}
			
			HibernateUtil.shutdown();
		}catch(Exception e){ 
			e.printStackTrace();
		} 

		
	}

	/**
	 * Obtiene todas las consultas activas en la base
	 * @return
	 */
	private static List<ConsultaDTO> getConsultas() {
		return new ConsultaDAO().getConsultasActivas();
	}

	/**
	 * Procesa tweets para la query
	 * @param query
	 */
	private static void processQuery(String query, Integer cantidad) {
		Long start = System.currentTimeMillis();
		System.out.println("====> Obteniendo Tweets para " + query);
		//Obtener los Tweets
		ConsultaDTO consulta = getConsulta(query);

		TwitterClient tc = new TwitterClient();
		
		boolean evitarRTs = false;
		Long sinceId = getMaxId(consulta.getId());
		
		ConcurrentHashMap<Long, Tweet> tweets = tc.getNTwitts(query, cantidad, sinceId, evitarRTs);

		
		ConcurrentHashMap<Long, SentimentResponse> sentiments = 
				new ConcurrentHashMap<Long, SentimentResponse>(tweets.size()); 
		
		getSentiments(tweets,sentiments, HILOS_SENTIMENT_API);
		processTweetsAndSentiment(tweets, sentiments, consulta, HILOS_SENTIMENT_PROCESS);
		System.out.println("Tiempo total para " + query + ": " + (System.currentTimeMillis()-start) + "ms");
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
			//Thread t = new TextProcessingAPI(sentiments, tempTweetMap);
			Thread t = new SentimentAPI(sentiments, tempTweetMap);
			t.setName("Sentiment Thread " + i);
			t.start();
		}
	}

	/**
	 * Obtiene la consulta y actualiza la fecha de ultima ejecución
	 * Si no existe para la query la crea y guarda
	 * @param query
	 * @return
	 */
	private static ConsultaDTO getConsulta(String query) {
		ConsultaDAO consultaDAO = new ConsultaDAO();
		
		ConsultaDTO consulta = consultaDAO.getConsultaForQuery(query);
		if(consulta == null) {
			consulta = consultaDAO.save(new ConsultaDTO(query, new Date()));
		} else {
			consulta.setLastExecution(new Date());
			consultaDAO.save(consulta);
		}
		
		return consulta;
	}
	
	/**
	 * Obtiene el id mas grande de tweet para una consulta
	 * @param idConsulta
	 * @return
	 */
	private static Long getMaxId(Integer idConsulta) {
		return TweetDAO.getInstance().getMaxId(idConsulta);
	}
}
