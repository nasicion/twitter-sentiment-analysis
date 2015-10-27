package uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto;

import java.util.List;

public class TwitterErrors {
	private List<TwitterError> errors;

	public List<TwitterError> getErrors() {
		return errors;
	}

	public void setErrors(List<TwitterError> errors) {
		this.errors = errors;
	}

	/**
	 * Se fija si el error se produjo por llegar al limite de request
	 * @return
	 */
	public boolean maxLimitExceeded() {
		for(TwitterError e : errors) {
			if(e.getCode().equals(88)) {
				return true;
			}
		}
		return false;
	}
	
}
