package uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TwitterSearchResponse {
	private SearchMetadata search_metadata;
	private List<Tweet> statuses;

	public List<Tweet> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<Tweet> statuses) {
		this.statuses = statuses;
	}

	public SearchMetadata getSearch_metadata() {
		return search_metadata;
	}

	public void setSearch_metadata(SearchMetadata search_metadata) {
		this.search_metadata = search_metadata;
	}


	public boolean hasMoreResults() {
		return this.search_metadata != null && StringUtils.isNotBlank(this.search_metadata.getNext_results());
	}
}
