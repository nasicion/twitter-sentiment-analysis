package uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa;

import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa.dto.SentimentResponse;

public abstract class SentimentAnalysisAPI extends Thread {

	abstract SentimentResponse getSentiment(String text);
}
