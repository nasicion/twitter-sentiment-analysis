package uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto.RetweetedStatus;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto.Tweet;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto.TwitterErrors;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto.TwitterSearchResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class TwitterClient {
	private static final String TWITTER_API_URL = "https://api.twitter.com/1.1/search/tweets.json";
	private static final String TWITTER_API_KEY = "JUQy8XYdom9mnitAELB2pkJWy";
	private static final String TWITTER_API_SECRET = "AaXyddDCt9JI37ptSUIA0UzKK9S8xl2HD0H5zaJbhSxiISIM3N";
	private static final String TWITTER_ACCESS_TOKEN = "331110082-nv6CWJft1FMc8JAYvBY7F71vReM4lWNIcDBFKKeh";
	private static final String TWITTER_ACCESS_TOKEN_SECRET = "jt5p9l6zXbTgm4s6DBn1mP3QIz6urbCTAIu4A5exkHcdx";

	
	
	/**
	 * Obtiene los twitts para el parametro de busqueda y el sinceId provisto (100 twitts)
	 * @param query
	 * @param sinceId - el id minimo para los twitts
	 * @return
	 */
	public TwitterSearchResponse getTwitts(String query, Long sinceId) {
		TwitterSearchResponse tsr = null;
		try {
			OAuthService service = new ServiceBuilder().provider(TwitterApi.class)
					.apiKey(TWITTER_API_KEY).apiSecret(TWITTER_API_SECRET).build();

			
			String url = TWITTER_API_URL + 
					"?lang=en&count=100&q=" + 
					URLEncoder.encode(query, "UTF-8");
			
			if(sinceId != null) {
				url += "&since_id=" + sinceId;
				System.out.println(url);
			}
			OAuthRequest request = new OAuthRequest(Verb.GET, url);

			Token accessToken = new Token(TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_TOKEN_SECRET);
			service.signRequest(accessToken, request);

			Response response = request.send();

			Gson g = new Gson();
			tsr = g.fromJson(response.getBody(), TwitterSearchResponse.class);
		} catch (UnsupportedEncodingException e) {
			System.err.println("Error encoding query");
			e.printStackTrace();
		}
		return tsr;
	}

	/**
	 * Obtiene los twitts para la query provista (100 twitts)
	 * @param query
	 * @return
	 */
	public TwitterSearchResponse getTwitts(String query) {
		return getTwitts(query, null);
	}
	
	
	/**
	 * Obtiene <cantidad> de tweets para el parametro de busqueda
	 * @param query
	 * @param sinceId 
	 * @param sinceId - el id minimo para los twitts
	 * @return
	 */
	public ConcurrentHashMap<Long, Tweet> getNTwitts(String query, Integer cantidad, Long sinceId, boolean avoidRTs) {
		System.out.println("Obteniendo " + cantidad + " tweets...");
		ConcurrentHashMap<Long, Tweet> tweets = new ConcurrentHashMap<Long,Tweet>(cantidad);
		TwitterSearchResponse tsr = null;
		try {
			Integer count = cantidad > 100 ? 100 : cantidad;
			String url = TWITTER_API_URL + 
					"?lang=en&count=" + count  + "&q=" + 
					URLEncoder.encode(query, "UTF-8");
			
			if(sinceId != null) {
				url += "&since_id="+sinceId;
			}
			
			do{
				OAuthService service = new ServiceBuilder().provider(TwitterApi.class)
						.apiKey(TWITTER_API_KEY).apiSecret(TWITTER_API_SECRET).build();
				OAuthRequest request = new OAuthRequest(Verb.GET, url);
				Token accessToken = new Token(TWITTER_ACCESS_TOKEN, TWITTER_ACCESS_TOKEN_SECRET);
				service.signRequest(accessToken, request);
				Response response = request.send();
				
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(Tweet.class, new TweetDeserializer());
				
				Gson g = gsonBuilder.create();
				tsr = g.fromJson(response.getBody(), TwitterSearchResponse.class);
				
				
				if(tsr != null) {
					for (int i = 0; i < tsr.getStatuses().size() && tweets.size() < cantidad; i++) {
						Tweet t = tsr.getStatuses().get(i);
						
						if(avoidRTs && t.isRetweet()) {
							
						} else {
							//Evitar truncados ya que puede dar falsos valores al sentiment analysis
							if(!t.isTruncated()) {
								tweets.put(t.getId(), t);
							}
						}
					}
					
					if(tsr.hasMoreResults()) {
						url = TWITTER_API_URL + tsr.getSearch_metadata().getNext_results();
					} else {
						System.out.println("ATENCION!!: NO HAY MAS RESULTADOS");
					}
					
				} else {
					TwitterErrors error = g.fromJson(response.getBody(), TwitterErrors.class);
					if(error.maxLimitExceeded()) {
						System.out.println("Se excedió el limite de request, se debe esperar 15 minutos");
						System.out.println("Durmiendo hilo");
						try {
							Thread.sleep(900*1000);
						} catch (InterruptedException e) {
							System.err.println("Error pausando el fetch de tweets");
							e.printStackTrace();
						}
					}
				}
				System.out.println("Obtenidos " + tweets.size() + " tweets\r");
			}while(tweets.size() < cantidad && tsr.hasMoreResults());
			
		} catch (UnsupportedEncodingException e) {
			System.err.println("Error encoding query");
			e.printStackTrace();
		}
		return tweets;
	}
	public class TweetDeserializer implements JsonDeserializer<Tweet>{

		public Tweet deserialize(JsonElement jsonElement, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			Tweet t = new Tweet();
			JsonObject jsonObject = (JsonObject) jsonElement;
			
			t.setId(jsonObject.get("id").getAsLong());
			t.setText(jsonObject.get("text").getAsString());
			t.setTruncated(jsonObject.get("truncated").getAsBoolean());
			
			String createdAtStr = jsonObject.get("created_at").getAsString();
			//Fri Oct 30 02:53:02 +0000 2015
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d H:m:s Z yyyy");
			
			try {
				t.setCreated_at(sdf.parse(createdAtStr));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Gson gson = new Gson();
			t.setRetweeted_status(gson.fromJson(jsonObject.get("retweeted_status"), RetweetedStatus.class));
			return t;
		}
		
	}
}
