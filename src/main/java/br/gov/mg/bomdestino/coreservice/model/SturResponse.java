package br.gov.mg.bomdestino.coreservice.model;

import java.io.Serializable;

public class SturResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3460413802528949408L;
	private String token;
	private Boolean valid;

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

}
