package uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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

public class TextProcessingAPI extends SentimentAnalysisAPI {

	private ConcurrentHashMap<Long, SentimentResponse> sentiments;
	private ConcurrentLinkedQueue<Tweet> tweets;

	
	
	
	public TextProcessingAPI(ConcurrentHashMap<Long, SentimentResponse> sentiments,
			ConcurrentLinkedQueue<Tweet> tweets) {
		super();
		this.sentiments = sentiments;
		this.tweets = tweets;
	}

	public TextProcessingAPI() {
	}

	public SentimentResponse getSentiment(String text) {
		String url = "http://text-processing.com/api/sentiment/";
		SentimentResponse analysis = null;
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		try {
			String response = webResource.type("application/json").post(
					String.class, "text=" + text);
			
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(SentimentResponse.class, new TextProcessingAPIDeserializer());
			
			analysis = gsonBuilder.create().fromJson(response, SentimentResponse.class);
		} catch (Exception e) {
			ClientResponse response = webResource.post(ClientResponse.class, "text=" + text);
			System.err.println("Error contactando API de TextProcessing");
			System.err.println(response);
			//llamar backup? dormir hilos? 
			try {
				Thread.sleep(900*1000);
			} catch (InterruptedException e1) {
				
			}
		}
 
		return analysis;
	}

	/**
	 * Custom deserealizer para Text-Processing
	 *
	 */
	private class TextProcessingAPIDeserializer implements
			JsonDeserializer<SentimentResponse> {

		public SentimentResponse deserialize(JsonElement json, Type arg1,
				JsonDeserializationContext arg2) throws JsonParseException {
			JsonObject jo = (JsonObject) json;
			SentimentResponse sr = new SentimentResponse();
			
			JsonObject probability =  (JsonObject)jo.get("probability");
			
			sr.setNeg(probability.get("neg").getAsFloat());
			sr.setPos(probability.get("pos").getAsFloat());
			sr.setNeutral(probability.get("neutral").getAsFloat());
			sr.setLabel(jo.get("label").getAsString());
			
			return sr;
		}

	}

	public void run() {
		while(!this.tweets.isEmpty()) {
			Tweet t = this.tweets.poll();
			if(t != null) {
				System.out.println("Procesando Tweet " + t.getId() + " Hilo " + this.getName());
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
