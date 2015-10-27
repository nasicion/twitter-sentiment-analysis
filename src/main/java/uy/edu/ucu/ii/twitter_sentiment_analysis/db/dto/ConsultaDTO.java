package uy.edu.ucu.ii.twitter_sentiment_analysis.db.dto;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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

	public ConsultaDTO() {
	}

	public ConsultaDTO(String query) {
		this.query = query;
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

	
}
