package uy.edu.ucu.ii.twitter_sentiment_analysis.db.dto;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="consulta")
public class ConsultaDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2235936297202764297L;
	
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(nullable=false, unique=true)
	private String query;

	@Column(nullable=false)
	private Date lastExecution;
	
	@Column(name="isActive", nullable=false, columnDefinition="boolean default 1")
	private boolean isActive;

	public ConsultaDTO() {
	}

	public ConsultaDTO(String query, Date lastExecution) {
		this.query = query;
		this.lastExecution = lastExecution;
		this.isActive = true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Date getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	
}
