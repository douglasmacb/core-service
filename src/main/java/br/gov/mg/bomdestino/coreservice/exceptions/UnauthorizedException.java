package br.gov.mg.bomdestino.coreservice.exceptions;

public class UnauthorizedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UnauthorizedException() {
		super("Unauthorized");
	}

}
