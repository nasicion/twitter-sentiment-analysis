package uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto;

public class SearchMetadata {

	private Long max_id;
	private Integer count;
	private String next_results;

	public Long getMax_id() {
		return max_id;
	}

	public void setMax_id(Long maxId) {
		this.max_id = maxId;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getNext_results() {
		return next_results;
	}

	public void setNext_results(String next_results) {
		this.next_results = next_results;
	}



}
