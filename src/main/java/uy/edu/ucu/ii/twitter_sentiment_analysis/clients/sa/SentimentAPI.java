package uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.text.html.parser.Entity;
import javax.ws.rs.core.MediaType;

import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa.dto.SentimentResponse;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto.Tweet;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SentimentAPI extends SentimentAnalysisAPI {

	private ConcurrentHashMap<Long, SentimentResponse> sentiments;
	private ConcurrentLinkedQueue<Tweet> tweets;

	
	
	
	public SentimentAPI(ConcurrentHashMap<Long, SentimentResponse> sentiments,
			ConcurrentLinkedQueue<Tweet> tweets) {
		super();
		this.sentiments = sentiments;
		this.tweets = tweets;
	}

	public SentimentAPI() {
	}

	public SentimentResponse getSentiment(String text) {
		String url = "http://sentiment.vivekn.com/api/text/";
		SentimentResponse analysis = null;
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		try {
			text = URLEncoder.encode(text, "UTF-8");
			
			String response = webResource
					.type(MediaType.APPLICATION_FORM_URLENCODED)
					.post(String.class, "txt="+text);
			

			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(SentimentResponse.class, new SentimentAPIDeserializer());
			analysis = gsonBuilder.create().fromJson(response, SentimentResponse.class);
		} catch (Exception e) {
			ClientResponse response = webResource.post(ClientResponse.class, "txt=" + text);
			System.err.println("Error contactando API de Sentiment");
			System.err.println(response);
			//llamar backup? dormir hilos? 
			try {
				Thread.sleep(10*1000);
			} catch (InterruptedException e1) {
				
			}
		}
 
		return analysis;
	}

	/**
	 * Custom deserealizer para Text-Processing
	 *
	 */
	private class SentimentAPIDeserializer implements
			JsonDeserializer<SentimentResponse> {

		public SentimentResponse deserialize(JsonElement json, Type arg1,
				JsonDeserializationContext arg2) throws JsonParseException {
			JsonObject jo = (JsonObject) json;
			SentimentResponse sr = new SentimentResponse();
			
			JsonObject result =  (JsonObject)jo.get("result");
			
			float confidence = result.get("confidence").getAsFloat(); 
			
			sr.setNeg((100-confidence)/100);
			
			sr.setPos(confidence/100);
			
			sr.setNeutral(new Float(0));
			
			String sentiment = result.get("sentiment").getAsString();
			if("Positive".equals(sentiment)) {
				sentiment = "pos";
			} else if("Negative".equals(sentiment)) {
				sentiment = "neg";
			} else {
				sentiment = "neutral";
			}
			sr.setLabel(sentiment);

			
			return sr;
		}

	}

	public void run() {
		while(!this.tweets.isEmpty()) {
			Tweet t = this.tweets.poll();
			if(t != null) {
				System.out.println("Procesando Tweet con SentimentAPI: TweetID: " + t.getId() + " Hilo: " + this.getName());
				SentimentResponse sentiment = getSentiment(t.getText());
				if(sentiment != null) {
					this.sentiments.put(t.getId(), sentiment);
				} else {
					//Si no se pudo analizar el tweet se lo coloca
					//nuevamente en la cola
					this.tweets.add(t);
				}
			}
		}
	}
}
