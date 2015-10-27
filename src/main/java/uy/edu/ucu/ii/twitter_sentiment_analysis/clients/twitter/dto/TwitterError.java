package uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto;

public class TwitterError {
	private String messagae;
	private Integer code;

	public String getMessagae() {
		return messagae;
	}

	public void setMessagae(String messagae) {
		this.messagae = messagae;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

}
