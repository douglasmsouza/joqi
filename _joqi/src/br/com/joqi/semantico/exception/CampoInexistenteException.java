package br.com.joqi.semantico.exception;

public class CampoInexistenteException extends Exception {

	public CampoInexistenteException() {
		super();
	}

	public CampoInexistenteException(String message, Throwable cause) {
		super(message, cause);
	}

	public CampoInexistenteException(String message) {
		super(message);
	}

	public CampoInexistenteException(Throwable cause) {
		super(cause);
	}

}
