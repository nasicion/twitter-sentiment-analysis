package uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Tweet implements Comparable<Tweet> {
	private Long id;
	private String text;
	private Date created_at;
	private boolean truncated;
	// Indica si el tweet es un retweet, si not null => retweet
	private RetweetedStatus retweeted_status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public boolean isTruncated() {
		return truncated;
	}

	public void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}

	@Override
	public boolean equals(Object obj) {
		Tweet t = (Tweet) obj;
		return this.getId().equals(t.getId());
	}

	public int compareTo(Tweet o) {
		return this.id.compareTo(o.getId());
	}

	public RetweetedStatus getRetweeted_status() {
		return retweeted_status;
	}

	public void setRetweeted_status(RetweetedStatus retweeted_status) {
		this.retweeted_status = retweeted_status;
	}

	public boolean isRetweet() {
		return this.retweeted_status != null;
	}
	
}
