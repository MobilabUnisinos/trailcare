package br.unisinos.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe responsável por contrar atributos e construtores do objeto Recurso
 * 
 * @author Bruno Mota
 */
@XmlRootElement
public class Recurso implements Comparable<Recurso> {

	private Integer latitude;
	private Integer longitude;
	private Integer rfid;
	private Integer tipoRecurso;
	private String nomeRecurso;
	private String descricaoRecurso;
	private String numeroSOS;
	private String error;
	private Integer distancia;
	private String idPai;

	public Integer getLatitude() {
		return this.latitude;
	}

	public void setLatitude(Integer latitude) {
		this.latitude = latitude;
	}

	public Integer getLongitude() {
		return longitude;
	}

	public void setLongitude(Integer longitude) {
		this.longitude = longitude;
	}

	public Integer getRfid() {
		return rfid;
	}

	public void setRfid(Integer rfid) {
		this.rfid = rfid;
	}

	public Integer getTipoRecurso() {
		return tipoRecurso;
	}

	public void setTipoRecurso(Integer tipoRecurso) {
		this.tipoRecurso = tipoRecurso;
	}

	public String getNomeRecurso() {
		return nomeRecurso;
	}

	public void setNomeRecurso(String nomeRecurso) {
		this.nomeRecurso = nomeRecurso;
	}

	public String getDescricaoRecurso() {
		return descricaoRecurso;
	}

	public void setDescricaoRecurso(String descricaoRecurso) {
		this.descricaoRecurso = descricaoRecurso;
	}

	public String getNumeroSOS() {
		return numeroSOS;
	}

	public void setNumeroSOS(String numeroSOS) {
		this.numeroSOS = numeroSOS;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Integer getDistancia() {
		return distancia;
	}

	public void setDistancia(Integer distancia) {
		this.distancia = distancia;
	}

	public String getIdPai() {
		return idPai;
	}

	public void setIdPai(String idPai) {
		this.idPai = idPai;
	}

	public Recurso() {
	}

	@Override
	public int compareTo(Recurso r) {
		if (this.distancia < r.distancia) {
			return -1;
		}
		if (this.distancia > r.distancia) {
			return 1;
		}
		return 0;
		
	}
}