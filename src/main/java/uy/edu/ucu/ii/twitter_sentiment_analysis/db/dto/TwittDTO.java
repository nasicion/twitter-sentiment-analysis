package uy.edu.ucu.ii.twitter_sentiment_analysis.db.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.sa.dto.SentimentResponse;
import uy.edu.ucu.ii.twitter_sentiment_analysis.clients.twitter.dto.Tweet;

@Entity
@Table(name = "twitt")
public class TwittDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4730828085479612168L;

	@Id
	private Long id;
	private String text;
	@Column(name = "created_at")
	private Date cratedAt;
	@Column(name = "fecha_consulta")
	private Date fechaCosnulta;
	private Integer idConsulta;

	private float neg;
	private float neutral;
	private float pos;
	private String label;

	public TwittDTO() {
		
	}
	public TwittDTO(Integer idConsulta, Tweet t, SentimentResponse sentiment) {
		this.id = t.getId();
		this.text = t.getText();
		this.cratedAt = t.getCratedAt();
		this.fechaCosnulta = new Date();
		this.idConsulta = idConsulta;
		this.neg = sentiment.getNeg();
		this.neutral = sentiment.getNeutral();
		this.pos = sentiment.getPos();
		this.label = sentiment.getLabel();
	}

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

	public Date getCratedAt() {
		return cratedAt;
	}

	public void setCratedAt(Date cratedAt) {
		this.cratedAt = cratedAt;
	}

	public Date getFechaCosnulta() {
		return fechaCosnulta;
	}

	public void setFechaCosnulta(Date fechaCosnulta) {
		this.fechaCosnulta = fechaCosnulta;
	}

	public Integer getIdConsulta() {
		return idConsulta;
	}

	public void setIdConsulta(Integer idConsulta) {
		this.idConsulta = idConsulta;
	}

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
