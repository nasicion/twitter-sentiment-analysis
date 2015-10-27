package uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa.dto;
public class SentimentResponse {
	private float neg;
	private float neutral;
	private float pos;
	private String label;

	public float getNeg() {
		return neg;
	}

	public void setNeg(float neg) {
		this.neg = neg;
	}

	public float getNeutral() {
		return neutral;
	}

	public void setNeutral(float neutral) {
		this.neutral = neutral;
	}

	public float getPos() {
		return pos;
	}

	public void setPos(float pos) {
		this.pos = pos;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	
}
