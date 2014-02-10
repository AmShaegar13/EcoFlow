package de.amshaegar.economy;

public class Transaction {

	private boolean success;
	private String message;
	
	public Transaction(boolean success) {
		this.success = success;
	}
	
	public Transaction(boolean success, String message) {
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
