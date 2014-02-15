package de.amshaegar.economy;

public class Transfer {

	private boolean success;
	private String message;
	
	public Transfer(boolean success) {
		this.success = success;
	}
	
	public Transfer(boolean success, String message) {
		this(success);
		this.message = message;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public String getMessage() {
		return message;
	}
}
