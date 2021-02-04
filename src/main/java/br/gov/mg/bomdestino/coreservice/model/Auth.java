package br.gov.mg.bomdestino.coreservice.model;

import java.io.Serializable;

public class Auth implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5988667930758933024L;
	private String email;
	private String senha;
	
	public Auth() {}

	public Auth(String email, String senha) {
		super();
		this.email = email;
		this.senha = senha;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the senha
	 */
	public String getSenha() {
		return senha;
	}

	/**
	 * @param senha the senha to set
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}

}
